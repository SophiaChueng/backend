package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.vo.manage.StudyCoursePlanProgressVo;
import com.yizhi.training.application.vo.manage.StudyExamPlanProjectVo;
import com.yizhi.training.application.vo.manage.StudyPlanProjectProgressVo;
import com.yizhi.training.application.vo.manage.StudyPlanProjectVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 申万宏源学习计划
 *
 * @author meicunzhi
 */
public interface ShenWanHongYuanProjectMapper extends BaseMapper<TrainingProject> {

    /**
     * 查询所有学习计划信息
     *
     * @param companyId
     * @return
     */
    List<StudyPlanProjectVo> swhyStudyplanAll(@Param("companyId") Long companyId);

    /**
     * 查询单个学员学习计划完成情况
     *
     * @param companyId
     * @param accountId
     * @param projectId 可为空
     * @return
     */
    List<StudyPlanProjectVo> swhyRecordAccount(@Param("companyId") Long companyId, @Param("accountId") Long accountId,
        @Param("projectId") Long projectId);

    /**
     * 查询当天指定学习计划的已完成学员学习记录
     *
     * @param companyId
     * @param projectId
     * @return
     */
    List<StudyPlanProjectVo> swhyRecordAccountToday(@Param("companyId") Long companyId,
        @Param("projectId") Long projectId, @Param("queryDays") Integer queryDays);

    /**
     * 查询指定学习计划的考试学习记录
     *
     * @param companyId                     企业ID
     * @param projectId                     项目ID
     * @param planId                        活动ID
     * @param queryDays查询多少天内的记录（含当天）,传空为全部
     * @return
     */
    List<StudyExamPlanProjectVo> swhyExamRecordAccount(@Param("companyId") Long companyId,
        @Param("projectId") Long projectId, @Param("planId") Long planId, @Param("queryDays") Integer queryDays);

    /**
     * 查询指定学习计划的所有课程进度
     *
     * @param siteId
     * @param projectId
     * @param totalCourse
     * @param queryDays查询多少天内的记录（含当天）,传空为全部
     * @return
     */
    List<StudyPlanProjectProgressVo> swhyRecordAccountProgress(@Param("siteId") Long siteId,
        @Param("projectId") Long projectId, @Param("totalCourse") Integer totalCourse,
        @Param("queryDays") Integer queryDays);

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
    List<StudyCoursePlanProgressVo> swhyRecordAccountCourseProgress(@Param("siteId") Long siteId,
        @Param("projectId") Long projectId, @Param("planId") Long planId, @Param("queryDays") Integer queryDays,
        @Param("groupType") Integer groupType);

    /**
     * 获取项目的课程数量
     *
     * @param projectId
     * @return
     */
    Integer getProjectCourseCount(@Param("projectId") Long projectId);

    /**
     * 获取计划名称
     *
     * @param planId
     * @return
     */
    String getPlanName(@Param("planId") Long planId);

    /**
     * 获取项目的课程ID
     *
     * @param projectId
     * @return
     */
    List<Long> getProjectCourseIds(@Param("projectId") Long projectId);

    /**
     * 获取项目活动下的课程ID
     *
     * @param projectId
     * @param planId
     * @return
     */
    List<Long> getProjectPlanActivityCourseIds(@Param("projectId") Long projectId, @Param("planId") Long planId);
}
