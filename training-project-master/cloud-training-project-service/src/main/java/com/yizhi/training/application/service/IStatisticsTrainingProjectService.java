package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.StatisticsTrainingProject;
import com.yizhi.training.application.domain.StatisticsTrainingProjectLearn;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.vo.manage.*;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
public interface IStatisticsTrainingProjectService extends IService<StatisticsTrainingProject> {

    @Override
    default StatisticsTrainingProject getOne(Wrapper<StatisticsTrainingProject> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 批量插入学习记录
     *
     * @param courseId 课程ID
     * @param curDate  学习日期
     * @return 返回学习的学生ID集合
     */
    List<StatisticsTrainingProjectLearn> insertAccountLearn(Long courseId, String curDate);

    TrainingProjectMainMessage mainMessage(Long projectId, Integer type);

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
    List<TrainingProjectDataChartsVo> getCharGroup(String startDate, String endDate, Long companyId, List<Long> orgIds,
        Long siteId);

    /**
     * 按照项目统计
     *
     * @param kwd
     * @param startDate
     * @param endDate
     * @param companyId
     * @param orgIds
     * @param siteId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<ReportStudyTrainingProjectVo> trainingProjectGroup(String kwd, String startDate, String endDate,
        Long companyId, List<Long> orgIds, Long siteId, Integer pageNo, Integer pageSize);

    /**
     * 按照培训项目详情统计
     *
     * @param projectId
     * @return
     */
    Page<TrainingProjectDataChartsVo> trainingProjectGroupView(Long projectId, Integer pageNo, Integer pageSize);

    /**
     * 按项目统计-查看-下载项目学习结果-导出记录
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<TrainingProjectGroupViewExportVO1> trainingProjectGroupViewExport1(Long projectId, Integer pageNo,
        Integer pageSize);

    /**
     * 按项目统计-查看-学习计划明细-导出记录
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<TrainingProjectGroupViewExportVO2> trainingProjectGroupViewExport2(Long projectId, Integer pageNo,
        Integer pageSize);

    /**
     * 按项目统计-查看-学员学习明细-导出记录
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<TrainingProjectGroupViewExportVO3> trainingProjectGroupViewExport3(Long projectId, Integer pageNo,
        Integer pageSize);

    /**
     * 下载记录 得到主体信息
     *
     * @param projectId
     * @return
     */
    TrainingProjectGroupViewExportVO4 trainingProjectGroupViewExportVO4(Long projectId);

    /**
     * 按照用户统计
     *
     * @param startDate
     * @param endDate
     * @param orgKwd
     * @param accountKwd
     * @param companyId
     * @param orgIds
     * @param siteId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroup(String startDate, String endDate,
        String orgKwd, String accountKwd, Long companyId, List<Long> orgIds, Long siteId, Integer pageNo,
        Integer pageSize);

    /**
     * 按照用户统计---详情
     *
     * @param accountId
     * @param pageNo
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     */
    Page<ReportStudyTrainingProjectAccountViewVo> trainingProjectAccountGroupView(Long accountId, String projectName,
        Integer pageNo, Integer pageSize, String startDate, String endDate, Long companyId, Long siteId);

    /**
     * 按照部门统计
     *
     * @param startDate
     * @param endDate
     * @param kwd
     * @param companyId
     * @param orgIds
     * @param siteId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<ReportStudyTrainingProjectOrgVo> trainingProjectOrgGroup(String startDate, String endDate, String kwd,
        Long companyId, List<Long> orgIds, Long siteId, Integer pageNo, Integer pageSize);

    /**
     * 根据名字模糊查询
     *
     * @param name
     * @param pageNo
     * @param pageSize
     * @param companyId
     * @param orgIds
     * @param siteId
     * @return
     */
    Page<TrainingProject> selectTrainingProjectByLikeName(String name, Integer pageNo, Integer pageSize, Long companyId,
        List<Long> orgIds, Long siteId);

    List<Long> getAllSiteIds();
}
