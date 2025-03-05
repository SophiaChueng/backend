package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.api.StatisticsTrainingProjectParamVO;
import com.yizhi.training.application.vo.domain.TpPlanVo;
import com.yizhi.training.application.vo.domain.TpStudentActivityRecordVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.training.application.vo.manage.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "TrainingProjectReportClient")
public interface TrainingProjectReportClient {

    /**
     * 异步加载报表
     *
     * @param startDate
     * @return
     */
    @GetMapping("/trainingProject/report/asynchronous/load/date")
    public String AsynchronousCourse(@RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate);

    /**
     * 图形用户报表
     *
     * @param startDate
     * @param endDate
     * @param siteId
     * @return
     */
    @GetMapping(value = "/trainingProject/report/chart/group")
    public List<TrainingProjectDataChartsVo> chartGroup(
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId);

    /**
     * 项目主体信息
     *
     * @param projectId
     * @return
     */
    @GetMapping("/trainingProject/report/main/message")
    public TrainingProjectMainMessage mainMessage(@RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "type", required = true) Integer type);

    /**
     * 按照项目统计
     *
     * @param name
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @param siteId
     * @return
     */
    @GetMapping(value = "/trainingProject/report/group")
    public Page<ReportStudyTrainingProjectVo> trainingProjectGroup(
        @RequestParam(name = "kwd", required = false) String kwd,
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId);

    /**
     * 按照培训项目统计查看详情
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "/trainingProject/report/group/view")
    public Page<TrainingProjectDataChartsVo> trainingProjectGroupView(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize);

    /**
     * 按照培训项目-查看-项目学习结果
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping(value = "/trainingProject/report/group/view/export1")
    public Page<TrainingProjectGroupViewExportVO1> trainingProjectGroupViewExport1(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize);

    /**
     * 按照培训项目-查看-学习计划明细
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping(value = "/trainingProject/report/group/view/export2")
    public Page<TrainingProjectGroupViewExportVO2> trainingProjectGroupViewExport2(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize);

    /**
     * 按照培训项目-查看-学员学习明细
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @param startDate
     * @param endDate
     * @return
     */
    @GetMapping(value = "/trainingProject/report/group/view/export3")
    public List<TrainingProjectGroupViewExportVO3> trainingProjectGroupViewExport3(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize);

    /**
     * 按照培训项目-查看-学员学习明细  主体信息（表头）
     *
     * @param projectIdtrainingProjectReportClient
     * @return
     */
    @GetMapping(value = "/trainingProject/report/group/view/export4")
    public TrainingProjectGroupViewExportVO4 trainingProjectGroupViewExport4(
        @RequestParam(name = "projectId", required = true) Long projectId);

    /**
     * 按用户统计
     *
     * @param startDate
     * @param endDate
     * @param page
     * @return
     */
    @GetMapping(value = "/trainingProject/report/account/group")
    public Page<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroup(
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "orgKwd", required = false) String orgKwd,
        @RequestParam(name = "accountKwd", required = false) String accountKwd,
        @RequestParam(name = "pageSize", required = true) Integer pageSize,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId);

    /**
     * 按照用户统计---用户详情
     *
     * @param accountId
     * @param siteId
     * @param orgId
     * @param pageSize
     * @param pageNo
     * @param startDate
     * @param endDate
     * @param accountName
     * @param accountFullName
     * @param departName
     * @return
     */
    @GetMapping(value = "/trainingProject/report/account/group/view")
    public Page<ReportStudyTrainingProjectAccountViewVo> trainingProjectAccountGroupView(
        @RequestParam(name = "accountId", required = true) Long accountId,
        @RequestParam(name = "projectName", required = false) String projectName,
        @RequestParam(name = "pageSize", required = true) Integer pageSize,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "siteId", required = true) Long siteId);

    /**
     * 按部门统计
     *
     * @param startDate
     * @param endDate
     * @param page
     * @return
     */
    @GetMapping(value = "/trainingProject/report/org/group")
    public Page<ReportStudyTrainingProjectOrgVo> trainingProjectOrgGroup(
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "kwd", required = false) String kwd,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId,
        @RequestParam(name = "pageSize", required = false) Integer pageSize,
        @RequestParam(name = "pageNo", required = false) Integer pageNo);

    @GetMapping(value = "/trainingProject/report/select/by/name")
    public Page<TrainingProjectVo> selectTrainingProjectByLikeName(
        @RequestParam(name = "kwd", required = true) String kwd,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId);

    /**
     * 报表服务获取培训项目所有站点id
     *
     * @return
     */
    @GetMapping(value = "/trainingProject/report/getAllSiteIds")
    public List<Long> getAllSiteIds();

    @GetMapping("/trainingProject/statistics/get/all/training")
    public List<TrainingProjectVo> getAllTrainingProject();

    @GetMapping("/trainingProject/statistics/getStatisticsTrainingRecorde/byid")
    public StatisticsTrainingRecorde getStatisticsTrainingRecordeById(@RequestParam("id") Long id,
        @RequestParam(value = "startDate", required = true) String startDate,
        @RequestParam(value = "endDate", required = true) String endDate);

    @GetMapping("/trainingProject/statistics/getRecordeAllWorkId")
    public List<Long> getRecordeAllWorkId();

    @GetMapping("/trainingProject/statistics/getAllRecordeByTimeLimit")
    public List<TpStudentActivityRecordVo> getAllRecordeByTimeLimit(
        @RequestParam(value = "startDate", required = true) String startDate,
        @RequestParam(value = "endDate", required = true) String endDate,
        @RequestParam(value = "relationId", required = true) Long relationId);

    @GetMapping("/trainingProject/statistics/getPlanMessageByTpId")
    public List<TpPlanVo> getPlanMessageByTpId(@RequestParam(value = "tpId", required = true) Long tpId);

    @PostMapping("/trainingProject/statistics/getPlanMessageBySiteIds")
    List<TpPlanVo> getPlanMessageBySiteIds(@RequestBody StatisticsTrainingProjectParamVO requestVO);

    @PostMapping("/trainingProject/statistics/getStatisticsTrainingRecorde/bySiteIds")
    StatisticsTrainingRecorde getStatisticsTrainingRecordeBySiteIds(
        @RequestBody StatisticsTrainingProjectParamVO requestVO);

    @PostMapping("/trainingProject/statistics/getAllRecordeBySiteIds")
    List<TpStudentActivityRecordVo> getAllRecordeBySiteIds(@RequestBody StatisticsTrainingProjectParamVO requestVO);

}
