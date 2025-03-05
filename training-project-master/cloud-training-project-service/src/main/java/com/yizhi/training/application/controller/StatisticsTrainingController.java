package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.TpStudentActivityRecordMapper;
import com.yizhi.training.application.mapper.TpStudentPlanRecordMapper;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.api.StatisticsTrainingProjectParamVO;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.domain.TpStudentActivityRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.manage.StatisticsTrainingRecorde;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/trainingProject/statistics")
public class StatisticsTrainingController {

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private ITpStudentProjectRecordService tpStudentProjectRecordService;

    @Autowired
    private TpStudentPlanRecordMapper tpStudentPlanRecordMapper;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    /**
     * 得到所有培训项目
     *
     * @return
     */
    @GetMapping("/get/all/training")
    public List<TrainingProject> getAllTraining() {
        TrainingProject tp = new TrainingProject();
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<TrainingProject>(tp);
        List<Long> listSelect = new ArrayList<Long>();
        listSelect.add(1L);
        listSelect.add(2L);
        wrapper.in("status", listSelect);
        return trainingProjectService.list(wrapper);
    }

    @GetMapping("/getStatisticsTrainingRecorde/byid")
    public StatisticsTrainingRecorde getStatisticsTrainingRecordeById(@RequestParam("id") Long id,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate) {

        //项目和计划的对应关系  每天报表删除，重新插入
        List<TpPlanActivityVo> listTPA = new ArrayList<>();
        List<TpPlanActivity> listTpPlanActivity = tpPlanActivityService.listTpPlanActivityByTpId(id);
        //项目是否完成需要根据时间筛选
        List<TpStudentProjectRecordVo> listTSP = new ArrayList<>();
        List<TpStudentProjectRecord> listTpStudentProjectRecord =
            tpStudentProjectRecordService.getTpStudentProjectRecordByTpIdAndTime(id, startDate, endDate);
        //项目下的计划是否完成需要根据时间筛选
        List<TpStudentPlanRecordVo> listTSPR = new ArrayList<>();
        List<TpStudentPlanRecord> listTpStudentPlanRecord =
            tpStudentPlanRecordMapper.getTpStudentPlanRecordByTpIdAndTime(id, startDate, endDate);
        listTPA = BeanCopyListUtil.copyListProperties(listTpPlanActivity, TpPlanActivityVo::new);
        listTSP = BeanCopyListUtil.copyListProperties(listTpStudentProjectRecord, TpStudentProjectRecordVo::new);
        listTSPR = BeanCopyListUtil.copyListProperties(listTpStudentPlanRecord, TpStudentPlanRecordVo::new);
        StatisticsTrainingRecorde str = new StatisticsTrainingRecorde();
        str.setId(id);
        str.setListTpPlanActivity(listTPA);
        str.setListTpStudentProjectRecord(listTSP);
        str.setListTpStudentPlanRecord(listTSPR);
        return str;
    }

    @GetMapping("/getRecordeAllWorkId")
    public List<Long> getRecordeAllWorkId() {
        return tpStudentActivityRecordMapper.getRecordeAllWorkId();
    }

    @GetMapping("/getAllRecordeByTimeLimit")
    public List<TpStudentActivityRecord> getAllRecordeByTimeLimit(
        @RequestParam(value = "startDate", required = true) String startDate,
        @RequestParam(value = "endDate", required = true) String endDate,
        @RequestParam(value = "relationId", required = true) Long relationId) {

        RequestContext requestContext = ContextHolder.get();

        return tpStudentActivityRecordMapper.getAllRecordeByTimeLimit(startDate, endDate, relationId,requestContext.getCompanyId(),requestContext.getSiteId());
    }

    @GetMapping("/getPlanMessageByTpId")
    public List<TpPlan> getPlanMessageByTpId(@RequestParam(value = "tpId", required = true) Long tpId) {
        return tpPlanService.getListByStatistics(tpId);
    }

    @PostMapping("/getPlanMessageBySiteIds")
    public List<TpPlan> getPlanMessageBySiteIds(@RequestBody StatisticsTrainingProjectParamVO requestVO) {
        if (requestVO == null || CollectionUtils.isEmpty(requestVO.getSiteIds())) {
            return Collections.emptyList();
        }
        return tpPlanService.getListBySiteIds(requestVO.getSiteIds());
    }

    @PostMapping("/getStatisticsTrainingRecorde/bySiteIds")
    public StatisticsTrainingRecorde getStatisticsTrainingRecordeBySiteIds(
        @RequestBody StatisticsTrainingProjectParamVO requestVO) {
        List<Long> siteIds = requestVO.getSiteIds();
        String startDate = requestVO.getStartDateString();
        String endDate = requestVO.getEndDateString();
        if (CollectionUtils.isEmpty(siteIds) || StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)) {
            return null;
        }
        //项目和计划的对应关系  每天报表删除，重新插入
        List<TpPlanActivityVo> tpPlanActivities = tpPlanActivityService.getTpPlanActivitiesBySiteIds(siteIds);
        //项目下的计划是否完成需要根据时间筛选
        List<TpStudentPlanRecordVo> listTpStudentPlanRecord =
            tpStudentPlanRecordMapper.getTpStudentPlanRecordBySiteIdsAndTime(siteIds, startDate, endDate);
        //项目是否完成需要根据时间筛选
        List<TpStudentProjectRecordVo> listTpStudentProjectRecord =
            tpStudentProjectRecordService.getTpStudentProjectRecordBySiteIdsAndTime(siteIds, startDate, endDate);

        List<TpStudentActivityRecordVo> activityRecordVos =
            tpStudentActivityRecordMapper.getAllRecordeBySiteIdsTimeLimit(startDate, endDate, siteIds);
        StatisticsTrainingRecorde str = new StatisticsTrainingRecorde();
        str.setListTpPlanActivity(tpPlanActivities);
        str.setListTpStudentPlanRecord(listTpStudentPlanRecord);
        str.setListTpStudentProjectRecord(listTpStudentProjectRecord);
        str.setListActivityRecord(activityRecordVos);

        return str;
    }
}
