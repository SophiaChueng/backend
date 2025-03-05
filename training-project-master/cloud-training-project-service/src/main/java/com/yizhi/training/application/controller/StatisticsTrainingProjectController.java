package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.system.application.system.remote.ReportClient;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.IStatisticsTrainingProjectService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.manage.*;
import com.yizhi.util.application.domain.Response;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trainingProject/report")
public class StatisticsTrainingProjectController {

    @Autowired
    private IStatisticsTrainingProjectService statisticsTrainingProjectService;

    @Autowired
    private StatisticsTrainingProjectRun statisticsTrainingProjectRun;

    @Autowired
    private ReportClient reportClient;

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @GetMapping("/test")
    public String testTrainingProject(@RequestParam(value = "x", required = false) int x) {

        String startDate = "2018-12-01";
        String endDate = "2018-12-31";
        Long projectId111 = 1019600151789109248L;
        Long companyId = 1314L;
        Long accountId = 1314L;
        List<Long> orgIds = new ArrayList<Long>();

        Long siteId = 1314L;
        Long orgId = 1314L;
        Integer pageNo = 1;
        Integer pageSize = 10;
        String departMessage = null;
        String userName = null;
        String projectName = null;

        switch (x) {
            case 1:
                //图形报表
                List<TrainingProjectDataChartsVo> list = chartGroup(startDate, endDate, companyId, orgIds, siteId);
                for (int i = 0; i < list.size(); i++) {
                    
                }

                break;

            //按项目统计
            case 2:
                String kwd = null;
                Page<ReportStudyTrainingProjectVo> page =
                    trainingProjectGroup(kwd, startDate, endDate, pageNo, pageSize, companyId, orgIds, siteId);
                List<ReportStudyTrainingProjectVo> list2 = page.getRecords();
                
                for (int i = 0; i < list2.size(); i++) {
                    
                }
                break;

            // 按项目详情统计
            case 3:
                Long projectId = 1016655741672902656L;
                Page<TrainingProjectDataChartsVo> trainingProjectGroupView =
                    trainingProjectGroupView(projectId, pageNo, pageSize);
                
                break;

            //按用户统计
            case 4:

                Page<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroupPage =
                    trainingProjectAccountGroup(startDate, endDate, departMessage, userName, companyId, orgIds, siteId,
                        pageSize, pageNo);
                List<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroupList =
                    trainingProjectAccountGroupPage.getRecords();
                for (int i = 0; i < trainingProjectAccountGroupList.size(); i++) {
                    
                }
                break;

            //按用户统计详情
            case 5:

                Page<ReportStudyTrainingProjectAccountViewVo> trainingProjectAccountGroupViewList =
                    trainingProjectAccountGroupView(accountId, projectName, pageSize, pageNo, startDate, endDate,
                        companyId, siteId);
                break;

            //按部门统计
            case 6:
                Page<ReportStudyTrainingProjectOrgVo> trainingProjectOrgGroupList =
                    trainingProjectOrgGroup(startDate, endDate, departMessage, companyId, orgIds, siteId, pageSize,
                        pageNo);
                break;

            case 7:
                Page<TrainingProject> page7 =
                    selectTrainingProjectByLikeName("签", pageSize, pageNo, companyId, orgIds, siteId);
                List<TrainingProject> list7 = page7.getRecords();
                
                break;

            case 8:
                Page<TrainingProjectGroupViewExportVO2> trainingProjectGroupViewExport1 =
                    trainingProjectGroupViewExport2(projectId111, pageNo, pageSize);
                
                break;

            default:
                return "";

        }
        return "测试数据";
    }

    /**
     * 图形报表    -- 验证没问题
     *
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     * @throws ParseException
     */
    @GetMapping(value = "/chart/group")
    public List<TrainingProjectDataChartsVo> chartGroup(
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId) {
        return statisticsTrainingProjectService.getCharGroup(startDate, endDate, companyId, orgIds, siteId);

    }

    /**
     * 按照项目统计  数据按照项目走         -----sql 优化查询完毕
     *
     * @param name
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @param siteId
     * @return
     */
    @GetMapping(value = "/group")
    public Page<ReportStudyTrainingProjectVo> trainingProjectGroup(
        @RequestParam(name = "kwd", required = false) String kwd,
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId) {

        String kwdTrim = null;
        if (kwd != null && kwd.length() > 0) {
            kwdTrim = kwd.trim();
            if (kwdTrim.length() == 0) {
                kwdTrim = null;
            }
        }

        return statisticsTrainingProjectService.trainingProjectGroup(kwdTrim, startDate, endDate, companyId, orgIds,
            siteId, pageNo, pageSize);

    }

    /**
     * 按照培训项目统计查看详情
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "/group/view")
    public Page<TrainingProjectDataChartsVo> trainingProjectGroupView(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize) {
        return statisticsTrainingProjectService.trainingProjectGroupView(projectId, pageNo, pageSize);
    }

    /**
     * 按照用户统计
     *
     * @param startDate
     * @param endDate
     * @param page
     * @return
     */
    @GetMapping("/account/group")
    public Page<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroup(
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "orgKwd", required = false) String orgKwd,
        @RequestParam(name = "accountKwd", required = false) String accountKwd,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId,
        @RequestParam(name = "pageSize", required = false) Integer pageSize,
        @RequestParam(name = "pageNo", required = false) Integer pageNo) {
        /**
         * 关键字去掉前后空格
         */
        String accountKwdTrim = null;
        String orgKwdTrim = null;
        if (accountKwd != null && accountKwd.length() > 0) {
            accountKwdTrim = accountKwd.trim();
            if (accountKwdTrim.length() == 0) {
                accountKwdTrim = null;
            }
        }

        if (orgKwd != null && orgKwd.length() > 0) {
            orgKwdTrim = orgKwd.trim();
            if (orgKwdTrim.length() == 0) {
                orgKwdTrim = null;
            }
        }

        return statisticsTrainingProjectService.trainingProjectAccountGroup(startDate, endDate, orgKwdTrim,
            accountKwdTrim, companyId, orgIds, siteId, pageNo, pageSize);
    }

    /***
     * 按照用户统计---详情
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
    @GetMapping("/account/group/view")
    public Page<ReportStudyTrainingProjectAccountViewVo> trainingProjectAccountGroupView(
        @RequestParam(name = "accountId", required = true) Long accountId,
        @RequestParam(name = "projectName", required = false) String projectName,
        @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
        @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "siteId", required = true) Long siteId) {
        String projectNameTrim = null;
        if (projectName != null && projectName.length() > 0) {
            projectNameTrim = projectName.trim();
            if (projectNameTrim.length() == 0) {
                projectNameTrim = null;
            }

        }

        return statisticsTrainingProjectService.trainingProjectAccountGroupView(accountId, projectNameTrim, pageNo,
            pageSize, startDate, endDate, companyId, siteId);
    }

    /**
     * 按照部门统计
     *
     * @param startDate
     * @param endDate
     * @param page
     * @return
     */
    @GetMapping("/org/group")
    public Page<ReportStudyTrainingProjectOrgVo> trainingProjectOrgGroup(
        @RequestParam(name = "startDate", required = true) String startDate,
        @RequestParam(name = "endDate", required = true) String endDate,
        @RequestParam(name = "kwd", required = false) String kwd,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId,
        @RequestParam(name = "pageSize", required = false) Integer pageSize,
        @RequestParam(name = "pageNo", required = false) Integer pageNo) {

        String kwdTrim = null;
        if (kwd != null && kwd.length() > 0) {
            kwdTrim = kwd.trim();
            if (kwdTrim.length() == 0) {
                kwdTrim = null;
            }
        }

        return statisticsTrainingProjectService.trainingProjectOrgGroup(startDate, endDate, kwdTrim, companyId, orgIds,
            siteId, pageNo, pageSize);
    }

    @GetMapping("/select/by/name")
    public Page<TrainingProject> selectTrainingProjectByLikeName(
        @RequestParam(name = "kwd", required = true) String name,
        @RequestParam(name = "pageSize", required = true) Integer pageSize,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "companyId", required = true) Long companyId,
        @RequestParam(name = "orgIds", required = false) List<Long> orgIds,
        @RequestParam(name = "siteId", required = true) Long siteId) {
        return statisticsTrainingProjectService.selectTrainingProjectByLikeName(name, pageNo, pageSize, companyId,
            orgIds, siteId);
    }

    @GetMapping(value = "/group/view/export2")
    public Page<TrainingProjectGroupViewExportVO2> trainingProjectGroupViewExport2(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize) {
        return statisticsTrainingProjectService.trainingProjectGroupViewExport2(projectId, pageNo, pageSize);
    }

    @ApiOperation(value = "培训项目异步加载报表", notes = "培训项目异步加载报表")
    @GetMapping("/asynchronous/load/date")
    public Response<String> AsynchronousCourse(@RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startDate", startDate);
        map.put("endDate", startDate);
        //statisticsTrainingProjectRun.execute(map,true);
        return Response.ok();
    }

    @ApiOperation(value = "项目主体信息", notes = "项目主体信息")
    @GetMapping("/main/message")
    public TrainingProjectMainMessage mainMessage(@RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "type", required = true) Integer type) {
        return statisticsTrainingProjectService.mainMessage(projectId, type);
    }

    /**
     * @param projectId
     * @param startDate
     * @param endDate
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/group/view/export1")
    public Page<TrainingProjectGroupViewExportVO1> trainingProjectGroupViewExport1(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize) {
        return statisticsTrainingProjectService.trainingProjectGroupViewExport1(projectId, pageNo, pageSize);
    }

    @GetMapping(value = "/group/view/export3")
    public List<TrainingProjectGroupViewExportVO3> trainingProjectGroupViewExport3(
        @RequestParam(name = "projectId", required = true) Long projectId,
        @RequestParam(name = "pageNo", required = true) Integer pageNo,
        @RequestParam(name = "pageSize", required = true) Integer pageSize) {
        return statisticsTrainingProjectService.trainingProjectGroupViewExport3(projectId, pageNo, pageSize);
    }

    /**
     * 第三个导出的基础信息
     *
     * @param projectId
     * @return
     */
    @GetMapping(value = "/group/view/export4")
    public TrainingProjectGroupViewExportVO4 trainingProjectGroupViewExportVO4(
        @RequestParam(name = "projectId", required = true) Long projectId) {
        return statisticsTrainingProjectService.trainingProjectGroupViewExportVO4(projectId);
    }

    @GetMapping(value = "/getAllSiteIds")
    public List<Long> getAllSiteIds() {
        return statisticsTrainingProjectService.getAllSiteIds();
    }

}
