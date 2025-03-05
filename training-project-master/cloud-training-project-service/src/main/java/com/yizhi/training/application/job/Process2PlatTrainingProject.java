package com.yizhi.training.application.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpStudentPlanRecord;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.TrainingProjectMapper;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpPlanService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Process2PlatTrainingProject
 * @Description 处理2.0平台的培训计划、培训项目完成记录（同时也是3.0平台的完成记录数据校验补全，一般情况下，是不需要校验补全的） 并且，校验补全完成记录缓存状态
 * @Author chengchenglong
 * @DATE 2019-04-15 10:34
 * @Version 1.0
 */
@Service
public class Process2PlatTrainingProject {

    /**
     * 站点id：用户
     */
    public static final Map<Long, List<AccountVO>> map = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(Process2PlatTrainingProject.class);

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ProcessFinishedRecordCacheJob processFinishedRecordCacheJob;

    /**
     * 开始处理
     */
    public void startProcess(Long siteId) {
        // 这里处理旧的时候，写死一个时间，方便排查数据
        LOGGER.info("-----------------------处理2.0平台的培训计划、培训项目完成记录 开始------------------------------");
        Date date = new Date(1555264922000L);
        // 查询出所有培训项目
        TrainingProject project = new TrainingProject();
        project.setDeleted(ProjectConstant.DELETED_NO);
        project.setStatus(ProjectConstant.PROJECT_STATUS_ENABLE);
        project.setVisibleRange(ProjectConstant.PROJECT_VISIBLE_RANGE_SITE);
        project.setSiteId(siteId);

        QueryWrapper<TrainingProject> ew = new QueryWrapper<>(project);
        ew.orderByAsc("site_id");

        List<TrainingProject> list = trainingProjectMapper.selectList(ew);
        LOGGER.info("----------------总共有 {} 个培训项目-------------------", list.size());
        if (CollectionUtils.isNotEmpty(list)) {
            for (TrainingProject tp : list) {
                dealTrainingProject(tp, date);
            }
            processFinishedRecordCacheJob.processClicked(date);
            processFinishedRecordCacheJob.processActivityUnfinished(date);
            processFinishedRecordCacheJob.processActivityFinished(date, null);
            processFinishedRecordCacheJob.processPlan(date);
            processFinishedRecordCacheJob.processTrainingProject(date);
        }
        map.clear();
        LOGGER.info("-----------------------处理2.0平台的培训计划、培训项目完成记录 完成------------------------------");
    }

    /**
     * 处理培训项目
     *
     * @param trainingProject
     */
    public void dealTrainingProject(TrainingProject trainingProject, Date now) {
        List<AccountVO> accountVOS = getAccountBySiteId(trainingProject.getSiteId());
        if (CollectionUtils.isNotEmpty(accountVOS)) {
            for (AccountVO account : accountVOS) {
                LOGGER.info("----------------当前处理培训项目：{}, 用户：{}------------------", trainingProject.getName(),
                    account.getName());
                boolean finisheAllPlan = dealTpPlan(trainingProject, account, now);
                // 1. 是否完成所有计划
                if (finisheAllPlan) {
                    TpStudentProjectRecord record = new TpStudentProjectRecord();
                    record.setAccountId(account.getId());
                    record.setFinished(1);
                    record.setTrainingProjectId(trainingProject.getId());
                    record.setSiteId(trainingProject.getSiteId());
                    int existed = (int)record.selectCount(new QueryWrapper(record));
                    // 没有项目完成记录，插入一条
                    if (existed < 1) {
                        record.setFinishDate(now);
                        record.setId(idGenerator.generate());
                        record.insert();
                        LOGGER.info("----------入库培训项目：accountId: {}, trainingProjectId: {}-------------",
                            account.getId(), trainingProject.getId());
                    }
                }
            }
        }
    }

    /**
     * 处理培训计划是否完成
     *
     * @param trainingProject
     * @param account
     * @return 是否完成该项目下所有培训计划
     */
    public boolean dealTpPlan(TrainingProject trainingProject, AccountVO account, Date now) {
        List<TpPlan> plans = tpPlanService.listAll(trainingProject.getId());
        if (CollectionUtils.isNotEmpty(plans)) {
            boolean finisheAllPlan = true;
            for (TpPlan plan : plans) {
                // 1. 先查看是否完成过
                TpStudentPlanRecord record = new TpStudentPlanRecord();
                record.setAccountId(account.getId());
                record.setFinished(1);
                record.setSiteId(trainingProject.getSiteId());
                record.setTpPlanId(plan.getId());
                int existed = (int)record.selectCount(new QueryWrapper(record));
                // 2. 如果没有完成过，处理活动是否全部完成
                if (existed < 1) {
                    boolean finishedAllActivity = dealTpPlanActivity(plan, account);
                    // 3. 如果活动全部完成，入库完成记录
                    if (finishedAllActivity) {
                        record.setTrainingProjectId(trainingProject.getId());
                        record.setId(idGenerator.generate());
                        record.setFinishDate(now);
                        record.insert();
                        LOGGER.info("----------入库培训计划：accountId: {}, planId: {}-------------", account.getId(),
                            plan.getId());
                    } else {
                        finisheAllPlan = finisheAllPlan && finishedAllActivity;
                    }
                }
            }
            return finisheAllPlan;
        }
        return false;
    }

    /**
     * 处理培训活动是否完成
     *
     * @param tpPlan
     * @param account
     * @return 是否完成该培训计划下所有活动
     */
    public boolean dealTpPlanActivity(TpPlan tpPlan, AccountVO account) {
        return tpPlanActivityService.hasFinisheAllActivity(tpPlan.getId(), account.getId());
    }

    /**
     * 获取站点下人员
     *
     * @param siteId
     * @return
     */
    private List<AccountVO> getAccountBySiteId(Long siteId) {
        if (map.containsKey(siteId)) {
            return map.get(siteId);
        }
        List<AccountVO> accounts = accountClient.findBySiteId(siteId);
        map.put(siteId, accounts);
        LOGGER.info("---------------------查询站点：{}，用户总共 {} 人-----------------------", siteId, accounts.size());
        return accounts;
    }

}
