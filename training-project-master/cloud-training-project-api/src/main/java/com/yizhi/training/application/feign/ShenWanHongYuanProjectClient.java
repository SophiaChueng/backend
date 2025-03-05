package com.yizhi.training.application.feign;

import com.yizhi.training.application.vo.manage.StudyCoursePlanProgressVo;
import com.yizhi.training.application.vo.manage.StudyExamPlanProjectVo;
import com.yizhi.training.application.vo.manage.StudyPlanProjectProgressVo;
import com.yizhi.training.application.vo.manage.StudyPlanProjectVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 申万宏源项目接口
 *
 * @author meicunzhi
 */
@FeignClient(name = "trainingProject", contextId = "ShenWanHongYuanProjectClient")
public interface ShenWanHongYuanProjectClient {

    /**
     * 查询所有学习计划信息
     *
     * @param companyId
     * @return
     */
    @GetMapping(value = "swhy/studyplan/all")
    List<StudyPlanProjectVo> swhyStudyplanAll(@RequestParam("companyId") Long companyId);

    /**
     * 查询单个学员学习计划完成情况
     *
     * @param companyId
     * @param accountId
     * @param projectId 可为空
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/account")
    List<StudyPlanProjectVo> swhyRecordAccount(@RequestParam("companyId") Long companyId,
        @RequestParam("accountId") Long accountId, @RequestParam("projectId") Long projectId);

    /**
     * 查询当天指定学习计划的已完成学员学习记录
     *
     * @param companyId
     * @param projectId
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/today")
    List<StudyPlanProjectVo> swhyRecordAccountToday(@RequestParam("companyId") Long companyId,
        @RequestParam("projectId") Long projectId, @RequestParam("queryDays") Integer queryDays);

    /**
     * 查询指定学习计划的考试学习记录
     *
     * @param companyId                     企业ID
     * @param projectId                     项目ID
     * @param planId                        活动ID
     * @param queryDays查询多少天内的记录（含当天）,传空为全部
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/exam")
    List<StudyExamPlanProjectVo> swhyExamRecordAccount(@RequestParam("companyId") Long companyId,
        @RequestParam("projectId") Long projectId, @RequestParam("planId") Long planId,
        @RequestParam("queryDays") Integer queryDays);

    /**
     * 查询指定学习计划的所有课程进度
     *
     * @param siteId
     * @param projectId
     * @param totalCourse
     * @param queryDays查询多少天内的记录（含当天）,传空为全部
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/progress")
    List<StudyPlanProjectProgressVo> swhyRecordAccountProgress(@RequestParam("siteId") Long siteId,
        @RequestParam("projectId") Long projectId, @RequestParam("queryDays") Integer queryDays);

    /**
     * 查询指定学习计划的考试学习记录
     *
     * @param siteId                        站点ID
     * @param projectId                     项目ID
     * @param planId                        活动ID
     * @param queryDays查询多少天内的记录（含当天）,传空为全部
     * @param groupType分组类型1按人员分组，2按人员和活动
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/course/progress")
    List<StudyCoursePlanProgressVo> swhyRecordAccountCourseProgress(@RequestParam("siteId") Long siteId,
        @RequestParam("projectId") Long projectId, @RequestParam("planId") Long planId,
        @RequestParam("queryDays") Integer queryDays, @RequestParam("groupType") Integer groupType);

    /**
     * 获取项目的课程ID
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/project/courseids")
    List<Long> getProjectCourseIds(@RequestParam("projectId") Long projectId);

    /**
     * 获取项目活动下的课程ID
     *
     * @param projectId
     * @param planId
     * @return
     */
    @GetMapping(value = "swhy/studyplan/record/project/planId/courseids")
    List<Long> getProjectPlanActivityCourseIds(@RequestParam("projectId") Long projectId,
        @RequestParam("planId") Long planId);

}
