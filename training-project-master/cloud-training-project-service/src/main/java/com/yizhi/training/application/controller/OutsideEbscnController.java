package com.yizhi.training.application.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.api.TpStudentProjectRecordEbscnVO;
import com.yizhi.training.application.vo.api.UserTrainingProjectStatusVO;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外部系统光大证券的相关接口
 */
@RestController
@RequestMapping("/outside/ebscn")
public class OutsideEbscnController {

    public static final Logger LOGGER = LoggerFactory.getLogger(OutsideEbscnController.class);

    private static final Logger logger = LoggerFactory.getLogger(OutsideEbscnController.class);

    @Autowired
    ITrainingProjectService trainingProjectService;

    @Autowired
    ITpStudentProjectRecordService tpStudentProjectRecordService;

    @Autowired
    ITpPlanActivityService tpPlanActivityService;

    /**
     * 根据培训项目名称获取指定用户的完成状态
     */
    @PostMapping("/users/study/status")
    public TpStudentProjectRecordEbscnVO geUserTrainingProjectStatus(@RequestBody UserTrainingProjectStatusVO vo) {
        String tpName = vo.getTrainingProjectName();
        Long siteId = vo.getSiteId();
        List<Long> userIds = vo.getUserIds();

        logger.info("培训项目查看指定用户完成状态入参={}", JSON.toJSONString(vo));
        TpStudentProjectRecordEbscnVO vo1 = null;

        QueryWrapper<TrainingProject> ew = QueryUtil.condition(new TrainingProject());
        ew.eq("site_id", siteId).eq("name", tpName);
        TrainingProject trainingProject = trainingProjectService.getOne(ew);
        logger.info("培训项目数据库查询结果={}", JSON.toJSONString(trainingProject));
        TrainingProjectVo t = new TrainingProjectVo();

        if (trainingProject != null) {
            BeanUtils.copyProperties(trainingProject, t);
            QueryWrapper<TpStudentProjectRecord> ewTpSPR = QueryUtil.condition(new TpStudentProjectRecord());
            ewTpSPR.eq("site_id", siteId).eq("training_project_id", trainingProject.getId()).in("account_id", userIds);
            ewTpSPR.ge(ObjectUtils.isNotEmpty(vo.getStartTime()), "finish_date", vo.getStartTime())
                .le(ObjectUtils.isNotEmpty(vo.getEndTime()), "finish_date", vo.getEndTime());
            List<TpStudentProjectRecord> gData = tpStudentProjectRecordService.list(ewTpSPR);
            logger.info("查询到结果size={}", gData.size());
            List<TpStudentProjectRecordVo> gDateV = new ArrayList<>();
            for (TpStudentProjectRecord tps : gData) {
                TpStudentProjectRecordVo tpS = new TpStudentProjectRecordVo();
                BeanUtils.copyProperties(tps, tpS);
                gDateV.add(tpS);
            }
            if (!CollectionUtils.isEmpty(gData)) {
                vo1 = new TpStudentProjectRecordEbscnVO();
                vo1.setTrainingProject(t);
                vo1.setData(gDateV);
                QueryWrapper<TpPlanActivity> ewTpa = QueryUtil.condition(new TpPlanActivity());
                ewTpa.eq("training_project_id", trainingProject.getId()).eq("deleted", 0).eq("type", 0);
                List<TpPlanActivity> tpaList = tpPlanActivityService.list(ewTpa);
                // 获取项目关联的课程数
                List<Long> courseIds =
                    tpaList.parallelStream().map(courseId -> courseId.getRelationId()).collect(Collectors.toList());
                vo1.setCourseIds(courseIds);
            }

        }
        logger.info("培训项目返回结果={}", JSON.toJSONString(vo1));
        return vo1;
    }
}
