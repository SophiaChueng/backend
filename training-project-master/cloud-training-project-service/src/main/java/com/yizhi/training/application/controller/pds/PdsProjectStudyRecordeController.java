package com.yizhi.training.application.controller.pds;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.training.application.domain.PdsProjectStudyRecorde;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.service.IPdsProjectStudyRecordeService;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.vo.PdsProjectStudyRecordeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * pds 自定义项目 学习记录 前端控制器
 * </p>
 *
 * @author fulan123
 * @since 2022-05-18
 */
@Slf4j
@RestController
@RequestMapping("/pdsProjectStudyRecorde")
public class PdsProjectStudyRecordeController {

    @Autowired
    private IPdsProjectStudyRecordeService pdsProjectStudyRecordeService;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private AccountClient accountClient;

    /**
     * 添加 暂存点的信息
     *
     * @return
     */
    @PostMapping("/point/add")
    public Boolean pointAdd(@RequestBody PdsProjectStudyRecordeVo projectStudyRecordeVo) {
        QueryWrapper<PdsProjectStudyRecorde> ew = QueryUtil.condition(new PdsProjectStudyRecorde());
        ew.eq("uid", projectStudyRecordeVo.getUid()).eq("pid", projectStudyRecordeVo.getPid());
        PdsProjectStudyRecorde psr = pdsProjectStudyRecordeService.getOne(ew);
        if (psr == null) {
            psr = new PdsProjectStudyRecorde();
            BeanUtils.copyProperties(projectStudyRecordeVo, psr);
            psr.setCreateTime(new Date());
            psr.setUpdateTime(new Date());
            return pdsProjectStudyRecordeService.save(psr);
        } else {
            psr.setUpdateTime(new Date());
            psr.setPointContext(projectStudyRecordeVo.getPointContext());
            return pdsProjectStudyRecordeService.update(psr, ew);
        }
    }

    /**
     * （根据用户id和项目id）获取保存的端点信息
     *
     * @param uid
     * @param pid
     * @return
     */
    @GetMapping("/point/get")
    public PdsProjectStudyRecordeVo pointGet(@RequestParam("uid") Long uid, @RequestParam("pid") Long pid) {
        QueryWrapper<PdsProjectStudyRecorde> ew = QueryUtil.condition(new PdsProjectStudyRecorde());
        ew.eq("uid", uid).eq("pid", pid);
        PdsProjectStudyRecorde data = pdsProjectStudyRecordeService.getOne(ew);
        if (data != null) {
            PdsProjectStudyRecordeVo vo = new PdsProjectStudyRecordeVo();
            BeanUtils.copyProperties(data, vo);
            return vo;
        }
        return null;
    }

    /**
     * 获取学时
     *
     * @return
     */
    @PostMapping("/study/period/get")
    public Float studyPeriodGet(@RequestBody PdsProjectStudyRecordeVo projectStudyRecordeVo) {

        // 获取项目关联的培训课程
        QueryWrapper<TpPlanActivity> ew = QueryUtil.condition(new TpPlanActivity());
        ew.eq("training_project_id", projectStudyRecordeVo.getPid()).eq("type", 0).eq("deleted", 0);
        List<TpPlanActivity> courseList = tpPlanActivityService.list(ew);
        if (CollectionUtils.isEmpty(courseList)) {
            return Float.valueOf(0);
        }

        // 获取学员课程学习时长
        String courseStr =
            courseList.stream().map(item -> item.getRelationId().toString()).collect(Collectors.joining("##"));
        log.info("courseStr={}" + courseStr);

        Integer period = courseClient.getCourseStudyStatisticSite(courseStr, projectStudyRecordeVo.getUid(),
            courseList.get(0).getSiteId());

        if (period == null) {
            period = 0;
        }
        log.info("period 学时总和={}" + period);
        BigDecimal a = new BigDecimal(period + "");
        BigDecimal b = new BigDecimal("3600");
        BigDecimal bigDecimal = a.divide(b, 1, RoundingMode.HALF_UP);

        QueryWrapper<PdsProjectStudyRecorde> ew1 = QueryUtil.condition(new PdsProjectStudyRecorde());
        ew1.eq("uid", projectStudyRecordeVo.getUid()).eq("pid", projectStudyRecordeVo.getPid());
        PdsProjectStudyRecorde existDb = pdsProjectStudyRecordeService.getOne(ew1);
        if (existDb == null) {
            com.yizhi.system.application.vo.AccountVO accountVO =
                accountClient.findById(projectStudyRecordeVo.getUid());
            PdsProjectStudyRecorde psr = new PdsProjectStudyRecorde();
            BeanUtils.copyProperties(projectStudyRecordeVo, psr);
            psr.setCreateTime(new Date());
            psr.setUpdateTime(new Date());
            psr.setPeriod(bigDecimal.floatValue());
            psr.setHeadPortrait(accountVO.getHeadPortrait());

            pdsProjectStudyRecordeService.save(psr);
        } else {
            existDb.setUpdateTime(new Date());
            if (StringUtils.isBlank(existDb.getHeadPortrait())) {
                com.yizhi.system.application.vo.AccountVO accountVO = accountClient.findById(existDb.getUid());
                existDb.setHeadPortrait(accountVO.getHeadPortrait());
            }
            existDb.setUserName(projectStudyRecordeVo.getUserName());
            existDb.setPeriod(bigDecimal.floatValue());
            pdsProjectStudyRecordeService.update(existDb, ew1);
        }

        return bigDecimal.floatValue();
    }

    /**
     * 获取 排行榜
     *
     * @return
     */
    @GetMapping("/study/period/ranking/list")
    public Page<PdsProjectStudyRecordeVo> studyPeriodRankingPage(@RequestParam("pageSize") Integer pageSize,
        @RequestParam("pageNum") Integer pageNum, @RequestParam("companyId") Long companyId,
        @RequestParam("siteId") Long siteId, @RequestParam("pid") Long pid) {
        return pdsProjectStudyRecordeService.studyPeriodRankingPage(pageSize, pageNum, companyId, siteId, pid, null);
    }

    /**
     * 获取自己的排行信息
     */
    @GetMapping("/study/period/ranking/get")
    public List<PdsProjectStudyRecordeVo> studyPeriodRankingList(@RequestParam("uid") Long uid,
        @RequestParam("companyId") Long companyId, @RequestParam("siteId") Long siteId, @RequestParam("pid") Long pid) {
        //return pdsProjectStudyRecordeService.studyPeriodRankingList(companyId, siteId, pid, uid);
        Page<PdsProjectStudyRecordeVo> data =
            pdsProjectStudyRecordeService.studyPeriodRankingPage(null, null, companyId, siteId, pid, uid);
        int total = (int)data.getTotal();
        if (total > 0) {
            return data.getRecords();
        }
        return null;
    }

}

