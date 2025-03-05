package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.StatisticsTrainingProject;
import com.yizhi.training.application.domain.StatisticsTrainingProjectLearn;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.StatisticsTrainingProjectLearnMapper;
import com.yizhi.training.application.mapper.StatisticsTrainingProjectMapper;
import com.yizhi.training.application.mapper.TrainingProjectMapper;
import com.yizhi.training.application.service.IStatisticsTrainingProjectService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.manage.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
@Slf4j
@Service
public class StatisticsTrainingProjectAccountServiceImpl
    extends ServiceImpl<StatisticsTrainingProjectMapper, StatisticsTrainingProject>
    implements IStatisticsTrainingProjectService {

    @Autowired
    private StatisticsTrainingProjectMapper statisticsTrainingProjectMapper;

    @Autowired
    private StatisticsTrainingProjectLearnMapper statisticsTrainingProjectLearnMapper;

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Override
    public List<StatisticsTrainingProjectLearn> insertAccountLearn(Long courseId, String curDate) {
        // TODO Auto-generated method stub
        int count = statisticsTrainingProjectMapper.insertAccountLearn(courseId, curDate);
        if (count == 0) {
            return null;
        }

        return statisticsTrainingProjectMapper.selectAccountLearn(courseId, curDate);
    }

    @Override
    public TrainingProjectMainMessage mainMessage(Long projectId, Integer type) {
        // TODO Auto-generated method stub
        /**
         * 查到主体信息
         */
        TrainingProject trainingProject = trainingProjectService.getById(projectId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        /**
         * 放在vo中
         */
        TrainingProjectMainMessage trainingProjectMainMessage = new TrainingProjectMainMessage();
        if (trainingProject != null) {
            trainingProjectMainMessage.setValue(trainingProject.getName());
            if (trainingProject.getStartTime() != null) {
                trainingProjectMainMessage.setStartTime(df.format(trainingProject.getStartTime()));
            }
            if (trainingProject.getEndTime() != null) {
                trainingProjectMainMessage.setEndTime(df.format(trainingProject.getEndTime()));
            }
        }
        /**
         * type=1 total 下载一的总数据量
         */
        if (type == 1) {
            Integer total = statisticsTrainingProjectLearnMapper.trainingProjectGroupViewExportNum1(projectId);
            trainingProjectMainMessage.setTotal(total);
        }
        if (type == 2) {
            Integer total = statisticsTrainingProjectLearnMapper.trainingProjectGroupViewExportNum2(projectId);
            trainingProjectMainMessage.setTotal(total);
        }
        return trainingProjectMainMessage;
    }

    /**
     * 图形报表
     */
    @Override
    public List<TrainingProjectDataChartsVo> getCharGroup(String startDate, String endDate, Long companyId,
        List<Long> orgIds, Long siteId) {
        // TODO Auto-generated method stub
        Date date = new Date();
        List<TrainingProjectDataChartsVo> list =
            statisticsTrainingProjectLearnMapper.getCharGroup(startDate, endDate, companyId, orgIds, siteId);
        Date date2 = new Date();
        log.info("培训项目--后台--图形报表--sql花费时间(ms)：{}" + (date2.getTime() - date.getTime()));
        log.info("培训项目--后台--图形报表--整合前的数据：{}" + (list));

        List<TrainingProjectDataChartsVo> listAllRecord = new ArrayList<TrainingProjectDataChartsVo>();
        Date startDate1, endDate1;
        TrainingProjectDataChartsVo rsccvUpdate = null;
        int num = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            startDate1 = dateFormat.parse(startDate);
            endDate1 = dateFormat.parse(endDate);
            num = (int)((endDate1.getTime() - startDate1.getTime()) / (1000 * 3600 * 24));
            Date updateDate = startDate1;
            String updateDateValue = dateFormat.format(updateDate);
            Boolean isTrue = false;
            int j = 0;
            for (int i = 0; i <= num; i++) {
                if (list != null && list.size() > 0) {
                    if (j < list.size()) {
                        TrainingProjectDataChartsVo rsccv = list.get(j);
                        if (rsccv.getValue().equals(updateDateValue)) {
                            j = j + 1;
                            listAllRecord.add(rsccv);
                            isTrue = true;
                        } else {
                            isTrue = false;
                        }
                    }
                }
                if (!isTrue) {
                    rsccvUpdate = new TrainingProjectDataChartsVo();
                    rsccvUpdate.setValue(updateDateValue);
                    rsccvUpdate.setTotalFinish(0);
                    rsccvUpdate.setTotalJoin(0);
                    ;
                    rsccvUpdate.setTotalProject(0);
                    ;
                    listAllRecord.add(rsccvUpdate);
                }
                Calendar c = Calendar.getInstance();
                c.setTime(updateDate);
                c.add(Calendar.DAY_OF_MONTH, 1); // 利用Calendar 实现 Date日期+1天
                updateDate = c.getTime();
                updateDateValue = dateFormat.format(updateDate);
                isTrue = false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date date3 = new Date();
        log.info("培训项目--后台--图形报表--补数据--花费时间(ms)：{}" + (date3.getTime() - date2.getTime()));
        if (!CollectionUtils.isEmpty(listAllRecord)) {
            for (int i = 0; i < listAllRecord.size(); i++) {
                String value = listAllRecord.get(i).getValue();
                Integer numProject =
                    statisticsTrainingProjectLearnMapper.getCharGroupProjectNum(value, companyId, orgIds, siteId);
                listAllRecord.get(i).setTotalProject(numProject);
            }

        }
        return listAllRecord;
    }

    @Override
    public Page<ReportStudyTrainingProjectVo> trainingProjectGroup(String kwd, String startDate, String endDate,
        Long companyId, List<Long> orgIds, Long siteId, Integer pageNo, Integer pageSize) {
        // TODO Auto-generated method stub

        Date date1 = new Date();

        //先拿出来十个培训项目Id，根据这十个id去查找不在记录里边通过page去找，效率特别慢
        Page<Long> pageTrainingProjectIds = new Page<Long>(pageNo, pageSize);
        List<Long> listTrainingProjectIds =
            statisticsTrainingProjectLearnMapper.listTrainingProjectIds(kwd, startDate, endDate, companyId, orgIds,
                siteId, pageTrainingProjectIds);

        //带着十个培训项目的IDs以及总条数去对比
        Page<ReportStudyTrainingProjectVo> page = new Page<ReportStudyTrainingProjectVo>(pageNo, pageSize);
        List<ReportStudyTrainingProjectVo> ListReportStudyTrainingProjectVo = null;
        if (!CollectionUtils.isEmpty(listTrainingProjectIds)) {
            ListReportStudyTrainingProjectVo =
                statisticsTrainingProjectLearnMapper.trainingProjectGroup(listTrainingProjectIds);
            page.setRecords(ListReportStudyTrainingProjectVo);
            page.setTotal(pageTrainingProjectIds.getTotal());
        }
        Date date2 = new Date();
        log.info("培训项目--后台--按照项目统计--花费时间(ms)：{}" + (date2.getTime() - date1.getTime()));
        return page;
    }

    @Override
    public Page<TrainingProjectDataChartsVo> trainingProjectGroupView(Long projectId, Integer pageNo,
        Integer pageSize) {
        // TODO Auto-generated method stub
        Date date1 = new Date();
        Page<TrainingProjectDataChartsVo> page = new Page<TrainingProjectDataChartsVo>(pageNo, pageSize);
        List<TrainingProjectDataChartsVo> list =
            statisticsTrainingProjectLearnMapper.trainingProjectGroupView(projectId, page);
        if (page != null) {
            page.setRecords(list);
        }
        Date date2 = new Date();
        log.info("培训项目--后台--按照项目统计--查看详情--花费时间(ms)：{}" + (date2.getTime() - date1.getTime()));
        return page;
    }

    @Override
    public Page<TrainingProjectGroupViewExportVO1> trainingProjectGroupViewExport1(Long projectId, Integer pageNo,
        Integer pageSize) {
        // TODO Auto-generated method stub
        Page<TrainingProjectGroupViewExportVO1> page = new Page<TrainingProjectGroupViewExportVO1>(pageNo, pageSize);
        List<TrainingProjectGroupViewExportVO1> list =
            statisticsTrainingProjectLearnMapper.trainingProjectGroupViewExport1(projectId);
        page.setRecords(list);
        return page;
    }

    @Override
    public Page<TrainingProjectGroupViewExportVO2> trainingProjectGroupViewExport2(Long projectId, Integer pageNo,
        Integer pageSize) {
        // TODO Auto-generated method stub
        Page<TrainingProjectGroupViewExportVO2> page = new Page<TrainingProjectGroupViewExportVO2>(pageNo, pageSize);
        List<TrainingProjectGroupViewExportVO2> list =
            statisticsTrainingProjectLearnMapper.trainingProjectGroupViewExport2(projectId);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<TrainingProjectGroupViewExportVO3> trainingProjectGroupViewExport3(Long projectId, Integer pageNo,
        Integer pageSize) {
        // TODO Auto-generated method stub
        /**
         * 先查询到30个应该参加的人
         */
        /**
         * 这个项目应该参加的人数的账号
         */
        List<Long> accountIds = statisticsTrainingProjectLearnMapper.listAccountIds(projectId);
        List<Long> accountIdsLimit = new ArrayList<Long>();

        if (!CollectionUtils.isEmpty(accountIds)) {
            for (int i = (pageNo - 1) * pageSize; i < pageNo * pageSize; i++) {

                if (i >= accountIds.size()) {
                    break;
                }
                accountIdsLimit.add(accountIds.get(i));
            }
        }
        log.info("页数:{}" + pageNo + "。大小:" + pageSize);

        Date date1 = new Date();
        List<TrainingProjectGroupViewExportVO3> list =
            statisticsTrainingProjectLearnMapper.trainingProjectGroupViewExport3(projectId, accountIdsLimit);
        Date date2 = new Date();
        log.info("培训项目--按照培训项目-查看-下载3:查询记录消耗时间(ms):{}" + (date2.getTime() - date1.getTime()));
        return list;
    }

    @Override
    public TrainingProjectGroupViewExportVO4 trainingProjectGroupViewExportVO4(Long projectId) {
        // TODO Auto-generated method stub
        TrainingProjectGroupViewExportVO4 trainingProjectGroupViewExportVO4 =
            statisticsTrainingProjectLearnMapper.trainingProjectGroupViewExportVO4(projectId);
        if (trainingProjectGroupViewExportVO4 != null) {
            List<Long> accountIds = statisticsTrainingProjectLearnMapper.listAccountIds(projectId);
            trainingProjectGroupViewExportVO4.setAccountIds(accountIds);
        }
        return trainingProjectGroupViewExportVO4;
    }

    @Override
    public Page<ReportStudyTrainingProjectAccountVo> trainingProjectAccountGroup(String startDate, String endDate,
        String orgKwd, String accountKwd, Long companyId, List<Long> orgIds, Long siteId, Integer pageNo,
        Integer pageSize) {
        // TODO Auto-generated method stub
        Date d1 = new Date();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", endDate);
        paramMap.put("orgKwd", orgKwd);
        paramMap.put("accountKwd", accountKwd);
        paramMap.put("companyId", companyId);
        paramMap.put("orgIds", orgIds);
        paramMap.put("siteId", siteId);

        /**
         * 查出来符合这个日期的项目ids
         */
        List<Long> projectIds = statisticsTrainingProjectLearnMapper.selectProjectIds(paramMap);
        Date d2 = new Date();
        log.info(
            "培训项目--后台--按照个人统计--第一步：查询符合日期的项目--花费时间(ms)：" + (d2.getTime() - d1.getTime()));

        /**
         * 先查出来10个人的id   有多少人
         */

        Page<Long> accountGroup = new Page<Long>(pageNo, pageSize);
        //RowBounds row = new RowBounds(accountGroup.getOffset(), accountGroup.getLimit());
        /**
         * 这个站点下有这么多人
         */
        statisticsTrainingProjectLearnMapper.selectLearnOnAccounts(paramMap, accountGroup);
        Date d3 = new Date();
        log.info(
            "培训项目--后台--按照个人统计--第二步：查询十个人--花费时间(ms)：" + (d3.getTime() - d2.getTime()));

        if (accountGroup == null || accountGroup.getRecords() == null || accountGroup.getRecords().size() == 0) {
            return new Page<ReportStudyTrainingProjectAccountVo>(pageNo, pageSize);
        }

        //拿着这十个人员ID进行统计
        Page<ReportStudyTrainingProjectAccountVo> page = new Page<ReportStudyTrainingProjectAccountVo>();
        List<ReportStudyTrainingProjectAccountVo> list =
            statisticsTrainingProjectLearnMapper.trainingProjectAccountGroup(accountGroup.getRecords(), projectIds,
                startDate, endDate);
        page.setRecords(list);
        page.setTotal(accountGroup.getTotal());

        Date d4 = new Date();
        log.info(
            "培训项目--后台--按照个人统计--第三步：进行数据整合--花费时间(ms)：" + (d4.getTime() - d3.getTime()));
        log.info(
            "培训项目--后台--按照个人统计--------------------总花费时间(ms)：" + (d4.getTime() - d1.getTime()));
        return page;
    }

    @Override
    public Page<ReportStudyTrainingProjectAccountViewVo> trainingProjectAccountGroupView(Long accountId,
        String projectName, Integer pageNo, Integer pageSize, String startDate, String endDate, Long companyId,
        Long siteId) {
        // TODO Auto-generated method stub

        Date d1 = new Date();
        Page<ReportStudyTrainingProjectAccountViewVo> page =
            new Page<ReportStudyTrainingProjectAccountViewVo>(pageNo, pageSize);
        List<ReportStudyTrainingProjectAccountViewVo> list =
            statisticsTrainingProjectLearnMapper.trainingProjectAccountGroupView(startDate, endDate, projectName,
                accountId, companyId, siteId, page);
        page.setRecords(list);
        Date d2 = new Date();
        log.info("培训项目--后台--按照个人统计-查看详情--花费时间(ms){}" + (d2.getTime() - d1.getTime()));
        return page;
    }

    @Override
    public Page<ReportStudyTrainingProjectOrgVo> trainingProjectOrgGroup(String startDate, String endDate,
        String orgKwd, Long companyId, List<Long> orgIds, Long siteId, Integer pageNo, Integer pageSize) {
        // TODO Auto-generated method stub

		/*Page<ReportStudyTrainingProjectOrgVo> page=new Page<ReportStudyTrainingProjectOrgVo>(pageNo,pageSize);
		Map<String, Object> paramMap=new HashMap<String,Object>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("orgKwd", orgKwd);
		paramMap.put("companyId", companyId);
		paramMap.put("orgIds", orgIds);
		paramMap.put("siteId", siteId);

		Page<Long> pageOrg = new Page<Long>(pageNo, pageSize);
		RowBounds row = new RowBounds(pageNo, pageSize);
		Integer count = statisticsTrainingProjectLearnMapper.selectLearnOnCoursesTotal(paramMap);
		List<Long> ids = statisticsTrainingProjectLearnMapper.selectLearnOnCourses(paramMap, row);
		pageOrg.setRecords(ids);
		pageOrg.setTotal(count);




		List<ReportStudyTrainingProjectOrgVo> list=statisticsTrainingProjectLearnMapper.trainingProjectOrgGroup
		(paramMap);
	    page.setRecords(list);*/
        return null;
    }

    @Override
    public Page<TrainingProject> selectTrainingProjectByLikeName(String name, Integer pageNo, Integer pageSize,
        Long companyId, List<Long> orgIds, Long siteId) {
        Page<TrainingProject> page = new Page<TrainingProject>(pageNo, pageSize);
        TrainingProject tp = new TrainingProject();
        tp.setSiteId(siteId);
        tp.setCompanyId(companyId);

        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<TrainingProject>(tp);
        wrapper.like("name", name);
        if (orgIds != null) {
            wrapper.in("org_id", orgIds);
        }
        return trainingProjectService.page(page, wrapper);

    }

    @Override
    public List<Long> getAllSiteIds() {
        return trainingProjectMapper.getAllSiteIds();
    }

}
