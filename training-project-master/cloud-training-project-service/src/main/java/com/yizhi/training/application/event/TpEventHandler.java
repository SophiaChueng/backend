package com.yizhi.training.application.event;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.cache.CacheNamespace;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.core.application.publish.CloudEventPublisher;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.point.application.feign.PointRedisFeignClients;
import com.yizhi.point.application.vo.PointParamVO;
import com.yizhi.system.application.constant.MedalMessageConstants;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.*;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.impl.TpContentStudentStatusServiceUsing;
import com.yizhi.training.application.util.ArrayUtil;
import com.yizhi.training.application.util.TrainingEvenSendMessage;
import com.yizhi.training.application.v2.service.TpPlanService;
import com.yizhi.training.application.v2.service.biz.TpStudyBizService;
import com.yizhi.training.application.vo.EvenType;
import com.yizhi.util.application.constant.TpActivityType;
import com.yizhi.util.application.event.TrainingProjectEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 各个微服务业务处理完后向培训过项目微服务发送业务消息 的处理器
 *
 * @Author: shengchenglong
 * @Date: 2018/4/18 11:19
 */
@Slf4j
@Service
@Transactional
public class TpEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TpEventHandler.class);

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpStudentEnrollPassedMapper tpStudentEnrollPassedMapper;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private TpPlanMapper tpPlanMapper;

    @Autowired
    private TpStudentPlanRecordMapper tpStudentPlanRecordMapper;

    @Autowired
    private TpPlanConditionPostMapper tpPlanConditionPostMapper;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private TpStudentProjectRecordMapper tpStudentProjectRecordMapper;

    @Autowired
    private PointRedisFeignClients pointRedisFeignClients;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private TrainingEvenSendMessage trainingEvenSendMessage;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private TpContentStudentStatusServiceUsing tpContentStudentStatusServiceUsing;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private TpStudyBizService tpStudyBizService;

    @Autowired
    private ITpStudentProjectRecordService tpStudentProjectRecordService;

    @Autowired
    private CloudEventPublisher cloudEventPublisher;

    public void handle(EventWrapper<TrainingProjectEvent> ew) {
        TrainingProjectEvent event = ew.getData();
        LOGGER.info("接收到消息：{}", event);

        // 由于以前消息直接发送到rabbitmq默认exchange，导致其他业务无法消费活动完成的消息；所以在这里重新转发一次
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {

                cloudEventPublisher.publish(MedalMessageConstants.activity_status_exchange_name,
                    MedalMessageConstants.activity_finished_medal_routing_key, ew);
            }
        });

        // 如果是报名
        if (event.getType().equals(TpActivityType.TYPE_ENROLL)) {
            TrainingProject tp = trainingProjectMapper.selectById(event.getTrainingProjectId());
            if (null != tp) {
                TpStudentEnrollPassed tep = new TpStudentEnrollPassed();
                tep.setId(idGenerator.generate());
                tep.setTrainingProjectId(tp.getId());
                tep.setEnrollId(event.getId());
                tep.setAccountId(event.getAccountId());
                tep.setStartTime(tp.getStartTime());
                tep.setEndTime(tp.getEndTime());
                tep.setJoinTime(event.getNow());
                tep.setSiteId(event.getSiteId());

                tpStudentEnrollPassedMapper.insert(tep);
            } else {
                LOGGER.error("用户（id：{}）的报名动作发生在 一个没有培训项目的报名（id：{}）上！！！请管理员检查！！！",
                    event.getAccountId(), event.getId());
                LOGGER.error("事件详情：" + event.toString());
            }
        }
        // 如果是其他业务
        else {
            Boolean finished = event.getCourseFinished();
            TpStudentActivityRecord tar = new TpStudentActivityRecord();
            tar.setId(idGenerator.generate());
            tar.setAccountId(event.getAccountId());
            tar.setFinishDate(event.getNow());
            tar.setFinished(finished == null ? 0 : finished ? 1 : 0);
            tar.setSeconds(event.getCourseSeconds());
            tar.setScore(event.getExamScore());
            tar.setRelationId(event.getId());
            tar.setType(event.getType());
            tar.setSiteId(event.getSiteId());
            tar.setIsCustom(event.getIsCustom());
            // 补上companyId,这里应该是往event里添加companyId是最好的,但来不及了
            if (event.getAccountId() != null && event.getAccountId() > 0) {
                AccountVO accountVO = accountClient.findById(event.getAccountId());
                tar.setCompanyId(accountVO.getCompanyId());
            }
            LOGGER.info("入库学习活动完成记录：{}", tar);
            if (tar.getType() == TpActivityType.TYPE_OFFLINE_COURSE) {
                //线下课程会有多次导入情况 需要将之前导入的删除
                QueryWrapper<TpStudentActivityRecord> activityEw = new QueryWrapper<>();
                activityEw.eq("relation_id", event.getId());
                activityEw.eq("account_id", event.getAccountId());
                activityEw.eq("site_id", event.getSiteId());

                tpStudentActivityRecordMapper.delete(activityEw);
            }
            Integer result = tpStudentActivityRecordMapper.insert(tar);
            // 缓存完成记录
            if (result.equals(1)) {
                if (tar.getType() == TpActivityType.TYPE_OFFLINE_COURSE) {
                    //线下课程会有多次导入情况 也需要将之前的redis缓存删除
                    cacheDeleteFinished(tar.getAccountId(), tar.getRelationId());
                }
                if (tar.getFinished().equals(1)) {
                    cacheRecord(CacheNamespace.TP_ACTIVITY_FINISHED, tar.getAccountId(), tar.getRelationId(), 1);
                    cacheDeleteUnfinished(tar.getAccountId(), tar.getRelationId());
                } else {
                    cacheRecord(CacheNamespace.TP_ACTIVITY_UNFINISHED, tar.getAccountId(), tar.getRelationId(), 0);
                }
            }
            // 如果业务 1.没有传输完成状态 2.指明了未完成，则不处理计算内容
            if (finished == null || !finished) {
                return;
            }
            try {
                progressActivityRecord(tar);
            } catch (Exception e) {
                LOGGER.error("处理计划 项目完成情况异常 accountId:{},relationId:{}", event.getAccountId(),
                    event.getId(), e);
            }
        }
    }

    /**
     * 删除已完成中曾经的未完成记录
     *
     * @param accountId
     * @param relationId
     */
    private void cacheDeleteFinished(long accountId, long relationId) {
        redisCache.hdel(CacheNamespace.TP_ACTIVITY_FINISHED.concat(String.valueOf(accountId)),
            new String[] {String.valueOf(relationId)});
    }

    /**
     * 缓存学习记录
     *
     * @param keyPrefix
     * @param accountId
     * @param id        planId or relationId
     */
    private void cacheRecord(String keyPrefix, long accountId, long id, Integer finished) {
        redisCache.hsetIfAbsent(keyPrefix.concat(String.valueOf(accountId)), String.valueOf(id),
            String.valueOf(finished));
    }

    /**
     * 删除已完成中曾经的未完成记录
     *
     * @param accountId
     * @param relationId
     */
    private void cacheDeleteUnfinished(long accountId, long relationId) {
        redisCache.hdel(CacheNamespace.TP_ACTIVITY_UNFINISHED.concat(String.valueOf(accountId)),
            new String[] {String.valueOf(relationId)});
    }

    /**
     * 活动记录后 1. 判别是否发放证书 2. 判别计划是否完成 3. 判别项目是否完成
     *
     * @param activityRecord
     */
    private void progressActivityRecord(TpStudentActivityRecord activityRecord) {
        // 设置上线文
        RequestContext context = ContextHolder.get();
        if (context == null) {
            context = new RequestContext();
        }
        context.setAccountId(activityRecord.getAccountId());
        context.setCompanyId(activityRecord.getCompanyId());
        context.setSiteId(activityRecord.getSiteId());
        ContextHolder.set(context);

        List<Long> allPlanIds =
            tpPlanMapper.getIdsByActivityId(activityRecord.getRelationId(), activityRecord.getSiteId(), null);
        if (CollectionUtils.isEmpty(allPlanIds)) {
            return;
        }
        List<TpPlan> tpPlans = tpPlanMapper.selectBatchIds(allPlanIds);
        List<Long> tpIds = tpPlans.stream().map(TpPlan::getTrainingProjectId).collect(Collectors.toList());
        List<TrainingProject> trainingProjects = trainingProjectMapper.selectBatchIds(tpIds);
        trainingProjects.forEach(tp -> {
            List<TpPlan> tpPlanList = tpPlanService.getTpPlansByTpId(tp.getId());
            // 计算活动的完成及计划的完成
            tpContentStudentStatusServiceUsing.buildPlanActivityV2(tp, activityRecord.getAccountId(), tpPlanList);

            TpStudentProjectRecord tpStudentProjectRecord1 =
                tpStudentProjectRecordService.getTpStudentProjectRecord(activityRecord.getAccountId(), tp.getId());
            if (tpStudentProjectRecord1 == null || tpStudentProjectRecord1.getFinished() != 1) {

                if (tp.getEndTime() != null && tp.getEndTime().getTime() <= new Date().getTime()) {
                    // 项目已过期，不计算完成状态
                    return;
                }

                // 本次查询导致完成项目完成
                Boolean tpFinishedStatus = tpStudyBizService.getTpFinishedStatus(tp, activityRecord.getAccountId());

                // 次字段为后来添加，不想动目前的数据结构（前端已对接。）所以把字段放在第一个对象中
                // 项目未完成 -> 完成
                if (tpFinishedStatus) {
                    tpStudyBizService.tpFirstFinished(tp, activityRecord.getAccountId());
                }
            }
        });
    }

    /**
     * 计算完成计划，并记录
     *
     * @param activityRecord
     * @return 本次完成的计划id集合
     */
    private Set<Long> progressPlanRecord(TpStudentActivityRecord activityRecord) {
        // 1. 查出含有该活动的计划
        List<Long> allPlanIds =
            tpPlanMapper.getIdsByActivityId(activityRecord.getRelationId(), activityRecord.getSiteId(),
                activityRecord.getFinishDate());
        // 待处理的计划id
        List<Long> planIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(allPlanIds)) {
            String planFinishedKey =
                CacheNamespace.TP_PLAN_FINISHED.concat(String.valueOf(activityRecord.getAccountId()));
            // 2. 去除已经完成的计划
            for (Long id : allPlanIds) {
                if (!redisCache.hExisted(planFinishedKey, String.valueOf(id))) {
                    planIds.add(id);
                }
            }
            // 3. 处理等待计算的计划
            if (!CollectionUtils.isEmpty(planIds)) {
                Set<Long> toFinishedPlanIds = new HashSet<>();
                List<TpStudentPlanRecord> records = new ArrayList<>();
                for (Long id : planIds) {
                    TpStudentPlanRecord record = doProgressPlanRecord(id, activityRecord, toFinishedPlanIds);
                    if (null != record) {
                        records.add(record);
                    }
                }
                if (!CollectionUtils.isEmpty(records)) {
                    LOGGER.info("入库学习计划完成记录：{}", records);
                    Integer result = tpStudentPlanRecordMapper.batchInsert(records);
                    // 缓存完成记录
                    if (result.equals(records.size())) {
                        for (TpStudentPlanRecord r : records) {
                            cacheRecord(CacheNamespace.TP_PLAN_FINISHED, r.getAccountId(), r.getTpPlanId(), 1);
                        }
                    }
                }
                return toFinishedPlanIds;
            }
        }
        return null;
    }

    /**
     * 计算计划完成情况
     *
     * @param planId
     * @param activityRecord
     * @param finishedPlanIds 完成的计划id集合
     * @return 完成记录
     */
    private TpStudentPlanRecord doProgressPlanRecord(Long planId, TpStudentActivityRecord activityRecord,
        Set<Long> finishedPlanIds) {
        long accountId = activityRecord.getAccountId();
        Date date = activityRecord.getFinishDate();
        long siteId = activityRecord.getSiteId();

        TpPlanConditionPost conditionPost = new TpPlanConditionPost();
        conditionPost.setDeleted(ProjectConstant.DELETED_NO);
        conditionPost.setTpPlanId(planId);
        List<TpPlanConditionPost> conditionPosts =
            tpPlanConditionPostMapper.selectList(new QueryWrapper<>(conditionPost));

        boolean flag = true;
        String activityFinishedkey = CacheNamespace.TP_ACTIVITY_FINISHED.concat(String.valueOf(accountId));

        // 如果设置了计划完成条件
        if (!CollectionUtils.isEmpty(conditionPosts)) {
            loop:
            for (TpPlanConditionPost post : conditionPosts) {
                // 如果是完成活动数
                if (post.getType().equals(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_NUM)) {
                    // 查询出当前计划的活动  排除证书
                    List<Long> planIdTemp = new ArrayList<>();
                    planIdTemp.add(post.getTpPlanId());
                    List<Long> planActivityIds = tpPlanActivityMapper.getRelationIdsByTpPlanIds(planIdTemp);
                    // 循环活动
                    if (!CollectionUtils.isEmpty(planActivityIds)) {
                        int count = 0;
                        for (Long activityId : planActivityIds) {
                            if (redisCache.hExisted(activityFinishedkey, String.valueOf(activityId))) {
                                count++;
                            }
                        }
                        // 如果完成活动数小于指标
                        if (count < post.getNum()) {
                            flag = false;
                        }
                    }
                }
                // 如果是指定完成活动
                else if (post.getType().equals(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_ID)) {
                    // 如果有任何一条指定活动未完成
                    if (!redisCache.hExisted(activityFinishedkey, String.valueOf(post.getTpPlanActivityRelationId()))) {
                        flag = false;
                        break loop;
                    }
                }
            }
        }
        // 如果没有设置计划完成条件，完成所有活动才算完成该计划
        else {
            // 如果当前活动是证书完成了，那么整个计划算完成
            if (activityRecord.getType().equals(TpActivityType.TYPE_CERTIFICATE)) {
                flag = true;
            }
            // 如果不是证书完成，那么完成所有活动才算完成   排除证书
            else {
                Set<Long> toFinishedRelationIds = tpPlanActivityMapper.getAllRelationIdsByPlanId(siteId, planId);
                if (!CollectionUtils.isEmpty(toFinishedRelationIds)) {
                    // 任何一个未完成，那么就算计划不能完成
                    if (!redisCache.hAllExisted(activityFinishedkey, ArrayUtil.forStringArray(toFinishedRelationIds))) {
                        flag = false;
                    }
                }
            }
        }
        // 如果已经达成完成条件
        if (flag) {
            return buildPlanRecord(date, planId, accountId, finishedPlanIds, siteId);
        }
        return null;
    }

    /**
     * 组装学习计划完成记录
     *
     * @param date            完成时间
     * @param planId          计划id
     * @param accountId       账号id
     * @param finishedPlanIds 已完成计划id集合
     * @return
     */
    private TpStudentPlanRecord buildPlanRecord(Date date, Long planId, Long accountId, Set<Long> finishedPlanIds,
        Long siteId) {
        TpStudentPlanRecord planRecord = new TpStudentPlanRecord();
        planRecord.setFinishDate(date);
        planRecord.setFinished(1);
        planRecord.setId(idGenerator.generate());
        planRecord.setTpPlanId(planId);
        planRecord.setAccountId(accountId);
        planRecord.setSiteId(siteId);
        TpPlan plan = tpPlanMapper.selectById(planId);
        if (null != plan) {
            planRecord.setTrainingProjectId(plan.getTrainingProjectId());
            planRecord.setCompanyId(plan.getCompanyId());
        }
        finishedPlanIds.add(planId);
        return planRecord;
    }

    /**
     * 计算项目完成情况
     *
     * @param planIds   本次完成的计划 id 集合
     * @param accountId
     * @param date
     * @return
     */
    private void progressProjectRecord(Set<Long> planIds, Long accountId, Date date, Long siteId) {
        // 查出培训项目的id
        if (!CollectionUtils.isEmpty(planIds)) {
            List<Long> tpIds = tpPlanMapper.getTpIdsByPlanIds(planIds);
            if (!CollectionUtils.isEmpty(tpIds)) {
                //只为获取培训项目的名字与积分
                Map<Long, TrainingProject> trainingMap = new HashMap<>(tpIds.size());
                //获取用户的信息
                Map<Long, AccountVO> accountMap = new HashMap<>(tpIds.size());
                List<TrainingProject> trainingProjects =
                    trainingProjectMapper.getList(tpIds, new RowBounds(0, Integer.MAX_VALUE));
                if (!CollectionUtils.isEmpty(trainingProjects)) {
                    trainingProjects.forEach(a -> {
                        if (!trainingMap.containsKey(a.getId())) {
                            trainingMap.put(a.getId(), a);
                        }
                    });
                }
                String planFinishedKey = CacheNamespace.TP_PLAN_FINISHED.concat(String.valueOf(accountId));

                List<TpStudentProjectRecord> records = new ArrayList<>();
                // 用户已经完成了该培训项目多少计划
                int tpFinishedPlanCount = 0;
                for (Long tpId : tpIds) {
                    List<Long> tpPlanIds = tpPlanMapper.getIdsByTpId(tpId);
                    // 如果没有完成全部计划，继续下一个项目计算
                    if (!redisCache.hAllExisted(planFinishedKey, ArrayUtil.forStringArray(tpPlanIds))) {
                        continue;
                    } else {
                        TpStudentProjectRecord record = new TpStudentProjectRecord();
                        record.setFinished(1);
                        TrainingProject tp = trainingMap.get(tpId);
                        record.setCompanyId(tp == null ? null : tp.getCompanyId());
                        record.setAccountId(accountId);
                        record.setFinishDate(date);
                        record.setId(idGenerator.generate());
                        record.setTrainingProjectId(tpId);
                        record.setSiteId(siteId);
                        records.add(record);
                    }
                }
                if (!CollectionUtils.isEmpty(records)) {
                    List<Long> accountIds = new ArrayList<>(records.size());
                    List<AccountVO> accounts = null;
                    records.forEach(a -> {
                        accountIds.add(a.getAccountId());
                    });
                    if (!CollectionUtils.isEmpty(accountIds)) {
                        accounts = accountClient.idsGet(accountIds);
                        if (!CollectionUtils.isEmpty(accounts)) {

                            accounts.forEach(a -> {
                                if (!accountMap.containsKey(a.getId())) {
                                    accountMap.put(a.getId(), a);
                                }
                            });

                        }
                    }

                    LOGGER.info("入库学习项目完成记录：{}", records);
                    Integer result = tpStudentProjectRecordMapper.batchInsert(records);
                    // 缓存完成记录
                    if (result.equals(records.size())) {
                        PointParamVO pointParamVO = new PointParamVO();
                        for (TpStudentProjectRecord r : records) {
                            Long tpId = r.getTrainingProjectId();
                            cacheRecord(CacheNamespace.TP_TRAININGPROJECT_FINISHED, r.getAccountId(),
                                r.getTrainingProjectId(), 1);

                            //项目完成发消息
                            try {
                                TrainingProject trainingProject = trainingProjectMapper.selectById(tpId);
                                if (trainingProject != null) {
                                    if (r.getAccountId() != null) {
                                        taskExecutor.asynExecute(new AbstractTaskHandler() {
                                            @Override
                                            public void handle() {
                                                trainingEvenSendMessage.evenSendMessage(trainingProject,
                                                    r.getAccountId(), EvenType.TRAINING_FINISH);
                                            }
                                        });
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            log.info("开始发放培训项目积分！！！{}");
                            //开始发放培训项目积分
                            pointParamVO.setEventName("pointTrainingProject");
                            pointParamVO.setActivityType("学习项目通过，积分发放");
                            pointParamVO.setActivitySource("学习项目");
                            pointParamVO.setReleaseCondition("完成才发放");
                            pointParamVO.setReleaseRules("按照完成取积分");
                            pointParamVO.setCreatePointTime(date);
                            pointParamVO.setSourceId(r.getTrainingProjectId());
                            pointParamVO.setAccountId(accountId);
                            pointParamVO.setSiteId(siteId);
                            if (trainingMap.get(tpId) != null) {
                                pointParamVO.setOperatingPoint(trainingMap.get(tpId).getPoint());
                                pointParamVO.setActivityName(trainingMap.get(tpId).getName());
                            }
                            if (accountMap.get(accountId) != null) {
                                pointParamVO.setCompanyId(accountMap.get(accountId).getCompanyId());
                                pointParamVO.setAccountName(accountMap.get(accountId).getName());
                                pointParamVO.setOrgId(accountMap.get(accountId).getOrgId());
                            }
                            String sitePointId = pointRedisFeignClients.addPointRedis(pointParamVO);
                            // 发送积分
                            if (StringUtils.isNotBlank(sitePointId)) {
                                amqpTemplate.convertAndSend("trainingProject", sitePointId);
                            }

                        }
                    }
                }
            }
        }
    }

}
