package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.cache.CacheNamespace;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.documents.application.enums.RelationType;
import com.yizhi.documents.application.feign.DocumentClient;
import com.yizhi.documents.application.vo.documents.DocumentRelationVo;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.*;
import com.yizhi.training.application.util.ArrayUtil;
import com.yizhi.training.application.vo.api.TrainingProjectContentActivityVo;
import com.yizhi.training.application.vo.api.TrainingProjectContentPlanVo;
import com.yizhi.training.application.vo.api.TrainingProjectContentVo;
import com.yizhi.util.application.constant.TpActivityType;
import com.yizhi.util.application.enums.i18n.Constants;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 学员查看培训项目内容的组装
 * @ClassName TpContentStudentStatusServiceUsing
 * @Author shengchenglong
 * @Date 2019-04-01 13:49
 * @Version 1.0
 **/
@Service
public class TpContentStudentPcStatusServiceUsing {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpPlanMapper tpPlanMapper;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private TpPlanConditionPostMapper tpPlanConditionPostMapper;

    @Autowired
    private TpPlanConditionPreMapper tpPlanConditionPreMapper;

    @Autowired
    private TpStudentPlanRecordMapper tpStudentPlanRecordMapper;

    @Autowired
    private TpStudentProjectRecordMapper tpStudentProjectRecordMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    @Autowired
    private DocumentClient documentClient;

    //不参与活动的计算  类型有 6-证书
    private List<Integer> NOT_CALCULATE = Arrays.asList(6);

    /**
     * 查询培训项目内容
     *
     * @param project
     * @param accountId
     * @return
     */
    public TrainingProjectContentVo getTpContent(TrainingProject project, Long accountId) {
        Date date = new Date();
        TrainingProjectContentVo trainingProjectVo;
        // 要返回的vo
        trainingProjectVo = new TrainingProjectContentVo();
        List<TrainingProjectContentPlanVo> planVos = new ArrayList<>();

        // 2. 查出培训计划
        List<TpPlan> plans = null;
        TpPlan planExample = new TpPlan();
        planExample.setTrainingProjectId(project.getId());
        planExample.setDeleted(ProjectConstant.DELETED_NO);
        QueryWrapper<TpPlan> planEW = new QueryWrapper<>(planExample);
        planEW.orderByAsc("sort");
        plans = tpPlanMapper.selectList(planEW);

        // 3. 查出培训活动（首先处理计划状态）
        if (!CollectionUtils.isEmpty(plans)) {
            buildPlanActivity(project, accountId, date, plans, planVos);
        }

        trainingProjectVo.setPlans(planVos);
        return trainingProjectVo;
    }

    /**
     * 组装计划和活动的入口方法
     *
     * @param project
     * @param accountId
     * @param now
     * @param plans
     * @param planVos
     */
    private void buildPlanActivity(TrainingProject project, Long accountId, Date now, List<TpPlan> plans,
        List<TrainingProjectContentPlanVo> planVos) {
        TpPlanConditionPost conditionPost = new TpPlanConditionPost();
        conditionPost.setDeleted(ProjectConstant.DELETED_NO);
        conditionPost.setTrainingProjectId(project.getId());
        List<TpPlanConditionPost> conditionPosts =
            tpPlanConditionPostMapper.selectList(new QueryWrapper<>(conditionPost));
        List<TpPlanConditionPost> conditionPostsTemp;

        TpPlanConditionPre conditionPre = new TpPlanConditionPre();
        conditionPre.setDeleted(ProjectConstant.DELETED_NO);
        conditionPre.setTrainingProjectId(project.getId());
        List<TpPlanConditionPre> conditionPres = tpPlanConditionPreMapper.selectList(new QueryWrapper<>(conditionPre));
        List<TpPlanConditionPre> conditionPresTemp;

        String accountIdStr = String.valueOf(accountId);
        String keyForActivityFinished = CacheNamespace.TP_ACTIVITY_FINISHED.concat(accountIdStr);
        String keyForActivityUnFinished = CacheNamespace.TP_ACTIVITY_UNFINISHED.concat(accountIdStr);
        String keyForActivityClicked = CacheNamespace.TP_ACTIVITY_CLICKED.concat(accountIdStr);

        String keyForPlanFinished = CacheNamespace.TP_PLAN_FINISHED.concat(accountIdStr);
        //项目完成
        String keyForProjectFinished = CacheNamespace.TP_TRAININGPROJECT_FINISHED.concat(accountIdStr);

        // 当前时间在计划时间范围内
        boolean inplanTime;
        // 当前时间在计划时间开始时间后
        boolean inPlanStartTime;
        // 当前时间在计划结束时间之前
        boolean inPlanEndTime;

        // 计划 资料 对应map
        Map<Long, List<DocumentRelationVo>> documentMap = new HashMap<>();
        // 判断计划是否关联了资料
        List<Long> allPlanIds = plans.stream().map(TpPlan::getId).collect(Collectors.toList());
        // 查询培训计划是否有关联资料
        List<DocumentRelationVo> documentRelationVos =
            documentClient.queryRelationId(RelationType.PROJECT_PLAN.getType(), allPlanIds);
        documentMap = documentRelationVos.stream().collect(Collectors.groupingBy(t -> t.getRelationId()));

        // 计划 id ：计划 vo map
        Map<Long, TrainingProjectContentPlanVo> map = new TreeMap<>();
        for (TpPlan plan : plans) {
            conditionPost.setTpPlanId(plan.getId());
            conditionPre.setPlanId(plan.getId());

            inPlanStartTime = DateUtils.truncatedCompareTo(now, plan.getStartTime(), Calendar.SECOND) >= 0;
            inPlanEndTime = DateUtils.truncatedCompareTo(plan.getEndTime(), now, Calendar.SECOND) >= 0;
            inplanTime = inPlanStartTime && inPlanEndTime;

            TrainingProjectContentPlanVo planVo = new TrainingProjectContentPlanVo();
            planVo.setId(plan.getId());
            planVo.setName(plan.getName());
            planVo.setStartTime(plan.getStartTime());
            planVo.setEndTime(plan.getEndTime());
            planVo.setInPlanStartTime(inPlanStartTime);
            planVo.setInPlanEndTime(inPlanEndTime);
            planVo.setInTime(inplanTime);

            // 组装计划完成条件
            conditionPostsTemp = conditionPosts.stream().filter(item -> item.getTpPlanId().equals(plan.getId()))
                .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(conditionPostsTemp)) {
                for (TpPlanConditionPost post : conditionPostsTemp) {
                    // 指定完成数
                    if (post.getType().equals(0)) {
                        planVo.setToFinishedActivityNum(post.getNum());
                        break;
                    }
                    // 指定完成活动
                    else {
                        planVo.getToFinishedActivityIds().add(post.getTpPlanActivityId());
                    }
                }
            }
            // 组装计划前置计划
            conditionPresTemp = conditionPres.stream().filter(item -> item.getPlanId().equals(plan.getId()))
                .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(conditionPresTemp)) {
                for (TpPlanConditionPre pre : conditionPresTemp) {
                    planVo.getConditionPrePlanIds().add(pre.getPrePlanId());
                }
            }
            if (documentMap.containsKey(planVo.getId())) {
                planVo.setThereAnyData(true);
            }
            planVos.add(planVo);
            map.put(plan.getId(), planVo);
        }

        // 2. 查出所有活动，组装到相应的计划 vo 中
        TpPlanActivity activityExample = new TpPlanActivity();
        activityExample.setCompanyId(project.getCompanyId());
        activityExample.setSiteId(project.getSiteId());
        activityExample.setDeleted(ProjectConstant.DELETED_NO);
        activityExample.setTrainingProjectId(project.getId());
        QueryWrapper<TpPlanActivity> activityEW = new QueryWrapper<>(activityExample);
        activityEW.orderByAsc("sort");
        List<TpPlanActivity> allActivities = tpPlanActivityMapper.selectList(activityEW);
        // 查询学员的一个项目的所有活动完成情况；
        Map<Long, Integer> activityFinishMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(allActivities)) {
            List<Long> relationIds =
                allActivities.stream().map(TpPlanActivity::getRelationId).collect(Collectors.toList());
            List<TpStudentActivityRecord> planActivityRecords =
                tpStudentActivityRecordMapper.getPlanActivityRecord(accountId, relationIds,project.getCompanyId(),project.getSiteId());
            if (!CollectionUtils.isEmpty(planActivityRecords)) {
                activityFinishMap = planActivityRecords.stream().collect(
                    Collectors.toMap(TpStudentActivityRecord::getRelationId, TpStudentActivityRecord::getFinished));
            }
            for (TpPlanActivity activity : allActivities) {
                TrainingProjectContentPlanVo planVo = map.get(activity.getTpPlanId());
                TrainingProjectContentActivityVo activityVo = new TrainingProjectContentActivityVo();
                activityVo.setType(activity.getType());
                activityVo.setRelationId(activity.getRelationId());
                Integer activityType = activity.getType();
                if (null != activityType && TpActivityType.TYPE_COURSE.equals(activityType)) {
                    Integer finished = activityFinishMap.get(activity.getRelationId());
                    activityVo.setFinished((null != finished && finished.equals(1)));
                } else {
                    activityVo.setFinished(
                        redisCache.hExisted(keyForActivityFinished, String.valueOf(activity.getRelationId())));
                }
                // 如果已经完成
                if (activityVo.getFinished() && !NOT_CALCULATE.contains(activityVo.getType())) {
                    // 证书不作为计算条件
                    map.get(activity.getTpPlanId()).getFinishedActivityIds().add(activity.getId());
                }
                // 否则，未完成：未通过、未导入
                else {
                    // 考试、线下课程 有未通过状态
                    if (activityVo.getType().equals(TpActivityType.TYPE_EXAM) || activityVo.getType()
                        .equals(TpActivityType.TYPE_OFFLINE_COURSE)) {
                        activityVo.setUnPassed(
                            redisCache.hExisted(keyForActivityUnFinished, String.valueOf(activity.getRelationId())));
                    }
                    // 线下课程有 未导入状态
                    if (activityVo.getType().equals(TpActivityType.TYPE_OFFLINE_COURSE)) {
                        // 既没有 已完成，也没有 未完成
                        if (!activityVo.getFinished() && !activityVo.getUnPassed()) {
                            activityVo.setUnImported(true);
                        }
                    }
                }

                activityVo.setClicked(redisCache.hExisted(keyForActivityClicked,
                    String.valueOf(activity.getId()).concat("_")
                        .concat(String.valueOf(activity.getTrainingProjectId()))));
                activityVo.setId(activity.getId());
                activityVo.setName(activity.getName());
                activityVo.setUrl(activity.getAddress());
                activityVo.setSort(activity.getSort());
                map.get(activity.getTpPlanId()).getActivities().add(activityVo);
                map.get(activity.getTpPlanId()).getAllActivityIds().add(activityVo.getId());
                if (NOT_CALCULATE.contains(activityVo.getType())) {
                    //有些活动类型不参与计算，比如证书
                    continue;
                } else {
                    planVo.setIsOnly(false);
                }
                map.get(activity.getTpPlanId()).getNeedCalculateActivityIds().add(activityVo.getId());
            }
        }

        // 查询计划完成记录
        Set<Long> finishPlanIds = new HashSet<>();
        for (Map.Entry<Long, TrainingProjectContentPlanVo> entry : map.entrySet()) {
            buildPlanStatuss(project, accountId, now, entry.getValue(), keyForPlanFinished);
            if (entry.getValue().getFinished()) {
                finishPlanIds.add(entry.getValue().getId());
            }
        }
        // 查询项目的完成情况
        Integer projectFinishNum =
            tpStudentProjectRecordMapper.projectIsFinish(accountId, project.getId(), project.getSiteId());
        //判断当前项目没完成
        if (!(null != projectFinishNum && projectFinishNum.compareTo(0) > 0)) {
            //所有计划全部完成
            if (finishPlanIds.containsAll(allPlanIds)) {
                doInsertProjectRecord(accountId, new Date(), project, keyForProjectFinished);
            }
        }
    }

    /**
     * 组建计划状态
     *
     * @param project
     * @param accountId
     * @param now
     * @param planVo
     * @param keyForPlanFinished
     */
    private void buildPlanStatuss(TrainingProject project, Long accountId, Date now,
        TrainingProjectContentPlanVo planVo, String keyForPlanFinished) {
        String status = null;
        String percent = null;
        boolean planFinished = false;
        boolean planStarted = false;

        boolean processPlanFinished = false;
        List<Long> conditionPrePlanIds;

        // 如果当前时间在计划开始时间之前
        if (!planVo.getInPlanStartTime()) {
            status = Constants.TRAINING_MSG_CURRENT_STUDY_PLAN_NOT_AVAILABLE.getName();
            percent = "";
            planFinished = false;
            planStarted = false;
        }
        // 当前时间在 计划时间之内
        else if (planVo.getInTime()) {
            // 如果已经完成
            if (redisCache.hExisted(keyForPlanFinished, String.valueOf(planVo.getId()))) {
                status = "已完成";
                percent = "100%";
                planFinished = true;
                planStarted = true;
            }
            // 如果没有完成
            else {
                // 查看是否有前置计划
                conditionPrePlanIds = planVo.getConditionPrePlanIds();
                // 没有前置计划 或者 前置计划已完成
                if (CollectionUtils.isEmpty(conditionPrePlanIds) || (redisCache.hAllExisted(keyForPlanFinished,
                    ArrayUtil.forStringArray(conditionPrePlanIds)))) {
                    // 查看活动是否全部完成
                    // 如果是指定完成活动都已经完成
                    if (!CollectionUtils.isEmpty(planVo.getToFinishedActivityIds())) {
                        // 有已完成的并且完成所有的应完成
                        if (!CollectionUtils.isEmpty(planVo.getFinishedActivityIds()) && planVo.getFinishedActivityIds()
                            .containsAll(planVo.getToFinishedActivityIds())) {
                            processPlanFinished = true;
                        }
                    }
                    // 如果是指定完成活动数达标
                    else if (planVo.getToFinishedActivityNum() > 0) {
                        if (planVo.getToFinishedActivityNum() <= planVo.getFinishedActivityIds().size()) {
                            processPlanFinished = true;
                        }
                    }
                    // 未指定完成条件，如果总活动数 == 已经完成活动数  证书不作为完成条件
                    else {
                        if (planVo.getNeedCalculateActivityIds().size() <= planVo.getFinishedActivityIds().size()) {
                            processPlanFinished = true;
                        }
                    }

                    if (processPlanFinished) {
                        status = "已完成";
                        percent = "100%";
                        planStarted = true;
                        planFinished = true;
                        //只有证书的计划不应该保存计划完成记录,否则会bug15001
                        if (!planVo.getIsOnly()) {
                            doInsertPlanRecord(accountId, now, project, planVo, keyForPlanFinished);
                        }
                    } else {
                        status = "进行中";
                        percent = buildPercent(planVo);
                        planStarted = true;
                        planFinished = false;
                    }
                }
                // 前置计划未完成
                else {
                    status = Constants.TRAINING_MSG_STUDY_PLAN_NOT_FINISHED.getName();
                    percent = "";
                    planStarted = false;
                    planFinished = false;
                    Set<Object> finishedPlanIds = redisCache.hKeys(keyForPlanFinished);
                    List<Long> finishedPlanIdList = new ArrayList<>();
                    if (finishedPlanIds != null) {
                        finishedPlanIds.forEach(item -> finishedPlanIdList.add(Long.valueOf((String)item)));
                    }
                    planVo.setPrePlanNames(tpPlanMapper.getNames(planVo.getConditionPrePlanIds(), finishedPlanIdList));
                }
            }
        }
        // 当前时间在结束时间之后
        else if (!planVo.getInPlanEndTime()) {
            // 如果已经完成
            if (redisCache.hExisted(keyForPlanFinished, String.valueOf(planVo.getId()))) {
                status = "已完成";
                percent = "100%";
                planStarted = true;
                planFinished = true;
            }
            // 如果没有完成
            else {
                status = Constants.TRAINING_MSG_CURRENT_PLAN_EXPIRED_NOT_LEARNED.getName();
                percent = buildPercent(planVo);
                planStarted = false;
                planFinished = false;
            }
        }
        planVo.setStatus(status);
        planVo.setFinished(planFinished);
        planVo.setStarted(planStarted);
        planVo.setPercentageOfCompletion(percent);

        buildPlanActivityStatuss(planVo);
    }

    /**
     * 插入项目完成记录
     *
     * @param accountId
     * @param date
     * @param project
     */
    private void doInsertProjectRecord(long accountId, Date date, TrainingProject project,
        String keyForProjectFinished) {
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {
                TpStudentProjectRecord projectRecord = new TpStudentProjectRecord();
                projectRecord.setId(idGenerator.generate());
                projectRecord.setCompanyId(project.getCompanyId());
                projectRecord.setAccountId(accountId);
                projectRecord.setFinishDate(date);
                projectRecord.setSiteId(project.getSiteId());
                projectRecord.setFinished(1);
                projectRecord.setTrainingProjectId(project.getId());
                Integer result = tpStudentProjectRecordMapper.insert(projectRecord);
                // 缓存完成记录
                if (result.equals(1)) {
                    redisCache.hset(keyForProjectFinished, String.valueOf(projectRecord.getTrainingProjectId()),
                        String.valueOf(1));
                }
            }
        });
    }

    /**
     * 插入计划记录
     *
     * @param accountId
     * @param date
     * @param project
     * @param planVo
     */
    private void doInsertPlanRecord(long accountId, Date date, TrainingProject project,
        TrainingProjectContentPlanVo planVo, String keyForPlanFinished) {
        TpStudentPlanRecord planRecord = new TpStudentPlanRecord();
        planRecord.setId(idGenerator.generate());
        planRecord.setCompanyId(project.getCompanyId());
        planRecord.setAccountId(accountId);
        planRecord.setFinishDate(date);
        planRecord.setSiteId(project.getSiteId());
        planRecord.setFinished(1);
        planRecord.setTpPlanId(planVo.getId());
        planRecord.setTrainingProjectId(project.getId());
        Integer result = tpStudentPlanRecordMapper.insert(planRecord);
        // 缓存完成记录
        if (result.equals(1)) {
            redisCache.hset(keyForPlanFinished, String.valueOf(planRecord.getTpPlanId()), String.valueOf(1));
        }
    }

    private String buildPercent(TrainingProjectContentPlanVo planVo) {
        String percent = null;
        int finishedIdNum = 0;
        // 如果是指定完成活动数
        if (planVo.getToFinishedActivityNum() > 0) {
            //这里不需要对活动类型做判断，因为在保存时。已由前端做了筛选，只要是保存了的数据，默认为有效数据
            percent = getPercent(planVo.getFinishedActivityIds().size(), planVo.getToFinishedActivityNum());
        }
        // 如果是指定完成活动
        else if (!CollectionUtils.isEmpty(planVo.getToFinishedActivityIds())) {
            List<Long> finishedIds = planVo.getFinishedActivityIds();
            if (!CollectionUtils.isEmpty(finishedIds)) {
                for (Long id : finishedIds) {
                    if (planVo.getToFinishedActivityIds().contains(id)) {
                        finishedIdNum++;
                    }
                }
            }
            percent = getPercent(finishedIdNum, planVo.getToFinishedActivityIds().size());
        }
        // 如果没有完成条件
        else {
            percent = getPercent(planVo.getFinishedActivityIds().size(), planVo.getNeedCalculateActivityIds().size());
        }
        return percent;
    }

    /**
     * 组建活动状态
     *
     * @param planVo
     */
    private void buildPlanActivityStatuss(TrainingProjectContentPlanVo planVo) {
        List<TrainingProjectContentActivityVo> activityVos = planVo.getActivities();
        if (!CollectionUtils.isEmpty(activityVos)) {
            String status = null;
            boolean started = false;
            boolean expired = false;

            for (TrainingProjectContentActivityVo activityVo : activityVos) {
                // 计划可以开始
                if (planVo.getStarted()) {
                    // 活动已完成
                    if (activityVo.getFinished()) {
                        status = getFinishedStatus(activityVo.getType());
                        started = true;
                        expired = false;
                    }
                    // 活动未完成
                    else {
                        status = getUnderwayStatus(activityVo);
                        started = true;
                        expired = false;
                    }
                }
                // 计划未到开始时间 || 计划在有效时间范围内(前置计划未完成)
                else if (!planVo.getInPlanStartTime() || planVo.getInTime()) {
                    status = "";
                    started = false;
                    expired = false;
                }
                // 计划已过期
                else {
                    // 活动已完成
                    if (activityVo.getFinished()) {
                        status = getFinishedStatus(activityVo.getType());
                        started = true;
                        expired = false;
                    }
                    // 活动未完成
                    else {
                        status = getExpiredStatus(activityVo.getType());
                        started = false;
                        expired = true;
                    }
                }

                activityVo.setStarted(started);
                activityVo.setStatus(status);
                activityVo.setExpired(expired);
            }
        }
    }

    private String getPercent(int x, int total) {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);
        String result = numberFormat.format((float)x / (float)total * 100);
        return result + "%";
    }

    /**
     * 获取已完成状态
     *
     * @param type
     * @return
     */
    private String getFinishedStatus(Integer type) {
        if (TpActivityType.TYPE_CERTIFICATE.equals(type)) {
            return Constants.TRAINING_MSG_CERTIFICATE_OBTAINED.getName();//已获得
        } else {
            if (TpActivityType.TYPE_OFFLINE_COURSE.equals(type)) {
                return Constants.TRAINING_MSG_FINISHED.getName();//线下课程已完成
            }
            if (TpActivityType.TYPE_RESEARCH.equals(type)) {
                return Constants.TRAINING_MSG_THE_RESEARCH_COMPLETED.getName();//当前调研已完成,谢谢您的参与 (调研已完成)
            }
            return "已完成";
        }
    }

    /**
     * 获取进行中状态
     *
     * @param activityVo
     * @return
     */
    private String getUnderwayStatus(TrainingProjectContentActivityVo activityVo) {

        if (activityVo.getUnPassed()) {
            if (activityVo.getType().equals(TpActivityType.TYPE_OFFLINE_COURSE)) {
                return Constants.TRAINING_MSG_UNFINISHED.getName();//未完成;
            }
            if (activityVo.getType().equals(TpActivityType.TYPE_EXAM)) {
                return "进行中";
            }
        }

        Integer type = activityVo.getType();
        // 如果没有点击过，显示空白状态
        if (!activityVo.getClicked()) {
            if (TpActivityType.TYPE_OFFLINE_COURSE.equals(type)) {
                return Constants.TRAINING_MSG_DATA_NOT_IMPORTED.getName();//数据未导入(状态)
            } else if (TpActivityType.TYPE_CERTIFICATE.equals(type)) {
                return Constants.TRAINING_MSG_TO_GET_THE_CERTIFICATE.getName();//未获得 (前端显示灰色的 "获取证书")
            } else {
                return "";
            }

        }

        if (TpActivityType.TYPE_CERTIFICATE.equals(type)) {
            return Constants.TRAINING_MSG_TO_GET_THE_CERTIFICATE.getName();//未获得 (前端显示灰色的 "获取证书")
        }
        if (TpActivityType.TYPE_LINK.equals(type) || TpActivityType.TYPE_LIVE.equals(type)) {
            return "";
        }
        if (TpActivityType.TYPE_OFFLINE_COURSE.equals(type)) {
            return Constants.TRAINING_MSG_DATA_NOT_IMPORTED.getName();//数据未导入(状态)
        }
        return "进行中";
    }

    /**
     * 获取过期状态
     *
     * @param type
     * @return
     */
    private String getExpiredStatus(Integer type) {
        if (TpActivityType.TYPE_CERTIFICATE.equals(type)) {
            return Constants.TRAINING_MSG_TO_GET_THE_CERTIFICATE.getName();//未获得
        }
        if (TpActivityType.TYPE_OFFLINE_COURSE.equals(type)) {
            return Constants.TRAINING_MSG_UNFINISHED.getName();//未完成
        }
        return "未完成";
    }
}
