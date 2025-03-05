package com.yizhi.training.application.controller;

import com.yizhi.training.application.feign.ShenWanHongYuanProjectClient;
import com.yizhi.training.application.mapper.ShenWanHongYuanProjectMapper;
import com.yizhi.training.application.vo.manage.StudyCoursePlanProgressVo;
import com.yizhi.training.application.vo.manage.StudyExamPlanProjectVo;
import com.yizhi.training.application.vo.manage.StudyPlanProjectProgressVo;
import com.yizhi.training.application.vo.manage.StudyPlanProjectVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 申万宏源项目接口
 *
 * @author meicunzhi
 */
@RestController
@RequestMapping("/swhy/studyplan")
public class ShenWanHongYuanProjectController implements ShenWanHongYuanProjectClient {

    @Autowired
    ShenWanHongYuanProjectMapper shenWanHongYuanProjectMapper;

    @Override
    @GetMapping("/all")
    public List<StudyPlanProjectVo> swhyStudyplanAll(Long companyId) {
        return shenWanHongYuanProjectMapper.swhyStudyplanAll(companyId);
    }

    @Override
    @GetMapping("/record/account")
    public List<StudyPlanProjectVo> swhyRecordAccount(Long companyId, Long accountId, Long projectId) {
        return shenWanHongYuanProjectMapper.swhyRecordAccount(companyId, accountId, projectId);
    }

    @Override
    @GetMapping("/record/today")
    public List<StudyPlanProjectVo> swhyRecordAccountToday(Long companyId, Long projectId, Integer queryDays) {
        return shenWanHongYuanProjectMapper.swhyRecordAccountToday(companyId, projectId, queryDays);
    }

    @Override
    @GetMapping("/record/exam")
    public List<StudyExamPlanProjectVo> swhyExamRecordAccount(Long companyId, Long projectId, Long planId,
        Integer queryDays) {
        if (queryDays != null && -1 == queryDays) {
            queryDays = null;
        }
        return shenWanHongYuanProjectMapper.swhyExamRecordAccount(companyId, projectId, planId, queryDays);
    }

    @Override
    @GetMapping("/record/progress")
    public List<StudyPlanProjectProgressVo> swhyRecordAccountProgress(Long siteId, Long projectId, Integer queryDays) {
        if (queryDays != null && -1 == queryDays) {
            queryDays = null;
        }

        //获取项目总课程数量
        Integer totalCourse = shenWanHongYuanProjectMapper.getProjectCourseCount(projectId);
        if (totalCourse == null) {
            totalCourse = 0;
        }
        return shenWanHongYuanProjectMapper.swhyRecordAccountProgress(siteId, projectId, totalCourse, queryDays);
    }

    @Override
    @GetMapping("/record/course/progress")
    public List<StudyCoursePlanProgressVo> swhyRecordAccountCourseProgress(Long siteId, Long projectId, Long planId,
        Integer queryDays, Integer groupType) {
        if (queryDays != null && -1 == queryDays) {
            queryDays = null;
        }
        List<StudyCoursePlanProgressVo> courseProgress =
            shenWanHongYuanProjectMapper.swhyRecordAccountCourseProgress(siteId, projectId, planId, queryDays,
                groupType);

        if (CollectionUtils.isNotEmpty(courseProgress)) {
            //获取活动名称,并设置
            String planName = shenWanHongYuanProjectMapper.getPlanName(planId);
            if (planName != null) {
                String code = planId.toString();
                for (StudyCoursePlanProgressVo item : courseProgress) {
                    item.setPlanActivityCode(code);
                    item.setPlanActivityname(planName);
                }
            }
        }

        return courseProgress;
    }

    /**
     * 获取项目的课程ID
     */
    @Override
    @GetMapping("/record/project/courseids")
    public List<Long> getProjectCourseIds(Long projectId) {
        return shenWanHongYuanProjectMapper.getProjectCourseIds(projectId);
    }

    /**
     * 获取项目活动下的课程ID
     */
    @Override
    @GetMapping("/record/project/planId/courseids")
    public List<Long> getProjectPlanActivityCourseIds(Long projectId, Long planId) {
        return shenWanHongYuanProjectMapper.getProjectPlanActivityCourseIds(projectId, planId);
    }

}

