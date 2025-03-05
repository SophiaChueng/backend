package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.StatisticsTrainingProjectLearn;
import com.yizhi.training.application.vo.manage.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学员学习记录 Mapper 接口
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
public interface StatisticsTrainingProjectLearnMapper extends BaseMapper<StatisticsTrainingProjectLearn> {

    /**
     * 图形报表
     *
     * @param startDate
     * @param endDate
     * @param companyId
     * @param orgIds
     * @param siteId
     * @return
     */
    List<TrainingProjectDataChartsVo> getCharGroup(@Param("startDate") String startDate,
        @Param("endDate") String endDate, @Param("companyId") Long companyId, @Param("orgIds") List<Long> orgIds,
        @Param("siteId") Long siteId);

    Integer getCharGroupProjectNum(@Param("value") String value, @Param("companyId") Long companyId,
        @Param("orgIds") List<Long> orgIds, @Param("siteId") Long siteId);

    /**
     * 按照培训项目统计
     *
     * @param kwd
     * @param startDate
     * @param endDate
     * @param companyId
     * @param orgIds
     * @param siteId
     * @param page
     * @return
     */
    List<Long> listTrainingProjectIds(@Param("kwd") String kwd, @Param("startDate") String startDate,
        @Param("endDate") String endDate, @Param("companyId") Long companyId, @Param("orgIds") List<Long> orgIds,
        @Param("siteId") Long siteId, Page<Long> pageTrainingProjectIds);

    List<ReportStudyTrainingProjectVo> trainingProjectGroup(
        @Param("listTrainingProjectIds") List<Long> listTrainingProjectIds);

    /**
     * 按照培训项目查看详情
     *
     * @param projectId
     * @return
     */
    List<TrainingProjectDataChartsVo> trainingProjectGroupView(@Param("projectId") Long projectId,
        Page<TrainingProjectDataChartsVo> page);

    /**
     * 第一个下载
     *
     * @param projectId
     * @return
     */
    List<TrainingProjectGroupViewExportVO1> trainingProjectGroupViewExport1(@Param("projectId") Long projectId);

    Integer trainingProjectGroupViewExportNum1(@Param("projectId") Long projectId);

    /**
     * 第二个下载
     *
     * @param projectId
     * @return
     */
    List<TrainingProjectGroupViewExportVO2> trainingProjectGroupViewExport2(@Param("projectId") Long projectId);

    Integer trainingProjectGroupViewExportNum2(@Param("projectId") Long projectId);

    List<Long> listAccountIds(@Param("projectId") Long projectId);

    List<TrainingProjectGroupViewExportVO3> trainingProjectGroupViewExport3(@Param("projectId") Long projectId,
        @Param("accountIds") List<Long> accountIds);

    //Integer trainingProjectGroupViewExportNum3(@Param("projectId")Long projectId,@Param("startDate") String
    // startDate,@Param("endDate") String endDate);

    TrainingProjectGroupViewExportVO4 trainingProjectGroupViewExportVO4(@Param("projectId") Long projectId);

    /**
     * 按照用户统计，先查出来十个人
     *
     * @param paramMap
     * @param page
     * @return
     */
    List<Long> selectLearnOnAccounts(@Param("paramMap") Map<String, Object> map,
        @Param("rowBounds") Page<Long> rowBounds);

    Integer selectLearnOnAccountsTotal(@Param("paramMap") Map<String, Object> paramMap);

    //查出来符合这个时间段的培训项目Id
    List<Long> selectProjectIds(@Param("paramMap") Map<String, Object> map);

    List<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroup(@Param("accountIds") List<Long> accountIds,
        @Param("projectIds") List<Long> projectIds, @Param("startDate") String startDate,
        @Param("endDate") String endDate);

    /**
     * 按照用户统计详情
     *
     * @param startDate
     * @param endDate
     * @param accountId
     * @return
     */
    List<ReportStudyTrainingProjectAccountViewVo> trainingProjectAccountGroupView(@Param("startDate") String startDate,
        @Param("endDate") String endDate, @Param("projectName") String projectName, @Param("accountId") Long accountId,
        @Param("companyId") Long companyId, @Param("siteId") Long siteId,
        Page<ReportStudyTrainingProjectAccountViewVo> page);

    /**
     * 按照部门统计
     *
     * @param paramMap
     * @param rowBounds
     * @return
     */
    List<ReportStudyTrainingProjectOrgVo> trainingProjectOrgGroup(@Param("paramMap") Map<String, Object> paramMap,
        @Param("rowBounds") RowBounds rowBounds);

}
