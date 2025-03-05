package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.cache.CacheNamespace;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.enums.InternationalEnums;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.course.application.vo.domain.CourseEntityVo;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.exception.TrainingProjectException;
import com.yizhi.training.application.mapper.*;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.util.DeleteActivityAdvice;
import com.yizhi.training.application.util.TrainingEvenSendMessage;
import com.yizhi.training.application.vo.api.TrainingProjectContentPlanVo;
import com.yizhi.training.application.vo.api.TrainingProjectContentVo;
import com.yizhi.training.application.vo.manage.*;
import com.yizhi.util.application.constant.TpActivityType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 培训项目 - 学习计划 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Service
@Slf4j
@Transactional
public class TpPlanServiceImpl extends ServiceImpl<TpPlanMapper, TpPlan> implements ITpPlanService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TpPlanServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpPlanMapper tpPlanMapper;

    @Autowired
    private TpPlanConditionPreMapper tpPlanConditionPreMapper;

    @Autowired
    private TpPlanConditionPostMapper tpPlanConditionPostMapper;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private TpPlanActivityConditionPreMapper tpPlanActivityConditionPreMapper;

    @Autowired
    private TpPlanActivityConditionPostMapper tpPlanActivityConditionPostMapper;

    @Autowired
    private TpPlanRemindMapper tpPlanRemindMapper;

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    @Autowired
    private TpStudentPlanRecordMapper tpStudentPlanRecordMapper;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private TrainingEvenSendMessage trainingEvenSendMessage;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private DeleteActivityAdvice deleteActivityAdvice;

    @Autowired
    private TpContentStudentPcStatusServiceUsing tpContentStudentPcStatusServiceUsing;

    @Autowired
    private RedisCache redisCache;

    @Override
    public List<TpPlan> listAll(Long trainingProjectId) {
        TpPlan example = new TpPlan();
        example.setDeleted(ProjectConstant.DELETED_NO);
        example.setTrainingProjectId(trainingProjectId);

        QueryWrapper<TpPlan> ew = new QueryWrapper<TpPlan>(example);
        ew.orderByAsc("sort");
        return tpPlanMapper.selectList(ew);
    }

    @Override
    public Integer updateSort(List<TpPlan> list) {
        int result = 0;
        for (TpPlan tpPlan : list) {
            result += tpPlanMapper.updateById(tpPlan);
        }
        return result;
    }

    @Override
    public com.yizhi.training.application.vo.domain.TpPlanVo save(BaseModel<TpPlanVo> model) {
        RequestContext context = model.getContext();
        Date date = model.getDate();
        Map<String, Object> mapNode = new HashMap<>();
        // 学习计划vo

        TpPlanVo tpPlanVo = model.getObj();
        com.yizhi.training.application.vo.domain.TpPlanVo tp = new com.yizhi.training.application.vo.domain.TpPlanVo();
        // 判断时间段
        TrainingProject trainingProject = trainingProjectMapper.selectById(tpPlanVo.getTrainingProjectId());
        if (DateUtils.ceiling(trainingProject.getStartTime(), Calendar.MINUTE).getTime() > DateUtils.ceiling(
            tpPlanVo.getStartTime(), Calendar.MINUTE).getTime() || DateUtils.ceiling(trainingProject.getEndTime(),
            Calendar.MINUTE).getTime() < DateUtils.ceiling(tpPlanVo.getEndTime(), Calendar.MINUTE).getTime()) {
            tp.setSubMsg(InternationalEnums.TP_PLAN_TIME_BEYOND.getCode());
            return tp;
        }
        // 组装学习计划
        TpPlan tpPlan = buildTpPlan(tpPlanVo, context, date);

        if (null == tpPlan) {
            tp.setSubMsg(InternationalEnums.TP_PLAN_FAIL1.getCode());
            return tp;
        }
        // 组装学习计划条件前置条件（后置条件需要在活动组装完成后组装）
        List<TpPlanConditionPre> planConditionPres = new ArrayList<>();
        List<TpPlanConditionPost> planConditionPosts = new ArrayList<>();
        try {
            buildTpPlanConditionPre(tpPlanVo, tpPlan, planConditionPres);
        } catch (TrainingProjectException e) {
            log.error("组装学习条件发生异常:{}", e);
            tp.setSubMsg(InternationalEnums.TP_PLAN_FAIL2.getCode());
            return tp;
        }

        //        // 组装计划提醒
        //        List<TpPlanRemindVo> planReminds = buildTpPlanRemind(tpPlanVo.getRemindVo(), tpPlan, context, date);

        // 学习活动vo
        List<TpPlanActivityVo> activityVos = tpPlanVo.getActivities();
        // 组装学习活动 和 组装学习活动条件
        List<TpPlanActivityConditionPre> activityConditionPres = new ArrayList<>();
        List<TpPlanActivityConditionPost> activityConditionPosts = new ArrayList<>();
        List<TpPlanActivity> planActivities = null;
        try {
            planActivities =
                buildTpPlanActivity(activityVos, context, date, tpPlan, tpPlanVo.getCondition(), planConditionPosts,
                    activityConditionPres, activityConditionPosts);
        } catch (TrainingProjectException e) {
            log.error("组装学习活动 和 组装学习活动条件:{}", e);
            tp.setSubMsg(InternationalEnums.TP_PLAN_FAIL3.getCode());
            return tp;
        }

        // 插入学习计划
        LOGGER.info("准备插入学习计划--{}", tpPlan.getName());
        tpPlanMapper.insert(tpPlan);
        // 插入学习计划条件
        if (!CollectionUtils.isEmpty(planConditionPres)) {
            LOGGER.info("准备插入学习计划前置条件");
            tpPlanConditionPreMapper.batchInsert(planConditionPres);
        }
        if (!CollectionUtils.isEmpty(planConditionPosts)) {
            LOGGER.info("准备插入学习计划完成条件");
            tpPlanConditionPostMapper.batchInsert(planConditionPosts);
        }
        //发消息保存计划提醒
        try {
            if (trainingProject != null && tpPlanVo.getMessageRemindVo() != null) {
                taskExecutor.asynExecute(new AbstractTaskHandler() {
                    @Override
                    public void handle() {
                        MessageRemindVo mrv = tpPlanVo.getMessageRemindVo();
                        MessageRemindVo mrvs = new MessageRemindVo();
                        BeanUtils.copyProperties(mrv, mrvs);
                        trainingEvenSendMessage.systemSendMessage(trainingProject, tpPlan.getId(), mrvs, context);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        if (!CollectionUtils.isEmpty(planReminds)) {
        //            LOGGER.info("准备插入学习计划提醒完成条件");
        //            tpPlanRemindMapper.batchInsert(planReminds);
        //        }
        // 插入学习活动
        if (!CollectionUtils.isEmpty(planActivities)) {
            LOGGER.info("准备插入学习活动");
            tpPlanActivityMapper.batchInsert(planActivities);
        }
        if (!CollectionUtils.isEmpty(activityConditionPres)) {
            LOGGER.info("准备插入学习活动开启条件");
            tpPlanActivityConditionPreMapper.batchInsert(activityConditionPres);
        }
        if (!CollectionUtils.isEmpty(activityConditionPosts)) {
            LOGGER.info("准备插入学习活动完成条件");
            tpPlanActivityConditionPostMapper.batchInsert(activityConditionPosts);
        }
        BeanUtils.copyProperties(tpPlan, tp);
        return tp;
    }

    @Override
    public com.yizhi.training.application.vo.domain.TpPlanVo update(BaseModel<TpPlanUpdateVo> model)
        throws TrainingProjectException {
        RequestContext context = model.getContext();
        Date date = model.getDate();
        TpPlanUpdateVo vo = model.getObj();

        TrainingProject trainingProject = null;
        com.yizhi.training.application.vo.domain.TpPlanVo tp = new com.yizhi.training.application.vo.domain.TpPlanVo();
        // 判断时间段
        TpPlan oldPlan = tpPlanMapper.selectById(vo.getId());
        if (null != oldPlan) {
            trainingProject = trainingProjectMapper.selectById(oldPlan.getTrainingProjectId());
            if (DateUtils.ceiling(trainingProject.getStartTime(), Calendar.MINUTE).getTime() > DateUtils.ceiling(
                vo.getStartTime(), Calendar.MINUTE).getTime() || DateUtils.ceiling(trainingProject.getEndTime(),
                Calendar.MINUTE).getTime() < DateUtils.ceiling(vo.getEndTime(), Calendar.MINUTE).getTime()) {
                tp.setSubMsg(InternationalEnums.TP_PLAN_TIME_BEYOND.getCode());
                return tp;
            }
        }

        TpPlan tpPlan = new TpPlan();
        tpPlan.setId(vo.getId());
        tpPlan.setName(vo.getName());
        tpPlan.setStartTime(vo.getStartTime());
        tpPlan.setEndTime(vo.getEndTime());
        if (vo.getRemindVo() != null) {
            tpPlan.setEnableRemindApp(vo.getRemindVo().getEnableApp());
        } else {
            LOGGER.info("remindVo为空！！！");
        }

        tpPlan.setUpdateTime(date);
        tpPlan.setUpdateByName(context.getAccountName());
        tpPlan.setUpdateById(context.getAccountId());

        List<TpPlanConditionPre> planConditionPres = new ArrayList<>();
        List<TpPlanConditionPost> planConditionPosts = new ArrayList<>();

        // 如果前置计划
        if (!CollectionUtils.isEmpty(vo.getConditionPreIds())) {
            // 组装前置条件
            for (Long prePlanId : vo.getConditionPreIds()) {
                TpPlanConditionPre conditionPre = new TpPlanConditionPre();
                conditionPre.setId(idGenerator.generate());
                conditionPre.setDeleted(ProjectConstant.DELETED_NO);
                conditionPre.setPlanId(tpPlan.getId());
                conditionPre.setPrePlanId(prePlanId);
                conditionPre.setTrainingProjectId(oldPlan.getTrainingProjectId());
                planConditionPres.add(conditionPre);
            }
        }
        // 组装完成条件
        if (null != vo.getConditionPostFinishNum()) {
            TpPlanConditionPost conditionPost = new TpPlanConditionPost();
            conditionPost.setId(idGenerator.generate());
            conditionPost.setDeleted(ProjectConstant.DELETED_NO);
            conditionPost.setTpPlanId(tpPlan.getId());
            conditionPost.setTrainingProjectId(oldPlan.getTrainingProjectId());
            conditionPost.setType(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_NUM);
            conditionPost.setNum(vo.getConditionPostFinishNum());
            planConditionPosts.add(conditionPost);
        }
        if (!CollectionUtils.isEmpty(vo.getConditionPostActivityIds())) {
            // 查出对应的所有的活动
            List<TpPlanActivity> activities = tpPlanActivityMapper.selectListByIds(vo.getConditionPostActivityIds());
            if (CollectionUtils.isEmpty(activities) || activities.size() != vo.getConditionPostActivityIds().size()) {
                tp.setSubMsg(InternationalEnums.TP_PLAN_FAIL4.getCode());
                return tp;
            }
            // 活动id -->  活动
            Map<Long, TpPlanActivity> map = new HashMap<>();
            for (TpPlanActivity a : activities) {
                map.put(a.getId(), a);
            }
            for (Long activityId : vo.getConditionPostActivityIds()) {
                TpPlanConditionPost conditionPost = new TpPlanConditionPost();

                conditionPost.setId(idGenerator.generate());
                conditionPost.setDeleted(ProjectConstant.DELETED_NO);
                conditionPost.setTpPlanId(tpPlan.getId());
                conditionPost.setTrainingProjectId(oldPlan.getTrainingProjectId());
                conditionPost.setType(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_ID);
                conditionPost.setTpPlanActivityId(activityId);
                conditionPost.setTpPlanActivityRelationId(map.get(activityId).getRelationId());
                planConditionPosts.add(conditionPost);
            }
        }

        //        List<TpPlanRemindVo> planReminds = null;
        //        if (StrUtil.isBlank(vo.getRemindVo())) {
        //            tpPlan.setEnableRemindApp(0);
        //            tpPlan.setEnableRemindMail(0);
        //        } else {
        //            // 组装计划提醒
        //            planReminds = buildTpPlanRemind(vo.getRemindVo(), tpPlan, context, date);
        //        }

        // 更新培训计划主体
        tpPlanMapper.updateById(tpPlan);
        tpPlan = tpPlanMapper.selectById(tpPlan.getId());
        // 删除以前的条件
        List<Long> planIds = new ArrayList<Long>();
        planIds.add(tpPlan.getId());
        tpPlanConditionPreMapper.deleteByPlanIds(planIds);
        tpPlanConditionPostMapper.deleteByPlanIds(planIds);
        // 插入新增的
        if (!CollectionUtils.isEmpty(planConditionPres)) {
            tpPlanConditionPreMapper.batchInsert(planConditionPres);
        }
        if (!CollectionUtils.isEmpty(planConditionPosts)) {
            tpPlanConditionPostMapper.batchInsert(planConditionPosts);
        }
        //        // 删除以前的提醒
        //        TpPlanRemindVo remind = new TpPlanRemindVo();
        //        remind.setTpPlanId(vo.getId());
        //        QueryWrapper<TpPlanRemindVo> ew = new QueryWrapper<>(remind);
        //        tpPlanRemindMapper.delete(ew);
        //        // 插入计划提醒
        //        if (!CollectionUtils.isEmpty(planReminds)) {
        //            tpPlanRemindMapper.batchInsert(planReminds);
        //        }

        try { //发消息告知提醒有变化
            if (trainingProject != null) {
                TrainingProject finalTrainingProject = trainingProject;
                TpPlan finalTpPlan = tpPlan;
                taskExecutor.asynExecute(new AbstractTaskHandler() {
                    @Override
                    public void handle() {
                        MessageRemindVo mrv = vo.getMessageRemindVo();
                        MessageRemindVo mrvs = new MessageRemindVo();
                        BeanUtils.copyProperties(mrv, mrvs);
                        trainingEvenSendMessage.systemSendMessage(finalTrainingProject, finalTpPlan.getId(), mrvs,
                            context);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        com.yizhi.training.application.vo.domain.TpPlanVo p = new com.yizhi.training.application.vo.domain.TpPlanVo();
        BeanUtils.copyProperties(tpPlan, tp);
        return tp;
    }

    @Override
    public Integer deleteConditions(BaseModel<ConditionDeleteVo> model) {
        RequestContext context = model.getContext();
        Date date = model.getDate();
        ConditionDeleteVo vo = model.getObj();

        int result = 0;
        // 处理前置条件
        List<Long> preIds = vo.getPreConditionIds();
        if (!CollectionUtils.isEmpty(preIds)) {
            TpPlanConditionPre example = new TpPlanConditionPre();
            for (Long id : preIds) {
                example.setId(id);
                result += tpPlanConditionPreMapper.deleteById(example);
            }
        }
        // 处理后置条件
        List<Long> postIds = vo.getPostConditionIds();
        if (!CollectionUtils.isEmpty(postIds)) {
            TpPlanConditionPost example = new TpPlanConditionPost();
            for (Long id : postIds) {
                example.setId(id);
                result += tpPlanConditionPostMapper.deleteById(example);
            }

        }

        return result;
    }

    @Override
    public Integer deleteByIds(BaseModel<List<Long>> model) {
        List<Long> tpPlanIds = model.getObj();
        if (!CollectionUtils.isEmpty(tpPlanIds)) {
            //add by hutao
            for (Long tpPlanId : tpPlanIds) {
                TpPlanConditionPre pre = new TpPlanConditionPre();
                pre.setPrePlanId(tpPlanId);
                pre.setDeleted(0);
                QueryWrapper<TpPlanConditionPre> QueryWrapper = new QueryWrapper<>(pre);
                int i = tpPlanConditionPreMapper.selectCount(QueryWrapper).intValue();
                if (i > 0) {
                    return -1;//该计划已被其他计划作为前置计划所依赖，不能删除！！！
                }
            }
            //
            int num = tpPlanMapper.deleteByIds(tpPlanIds, model.getContext().getAccountId(),
                model.getContext().getAccountName(), model.getDate());
            // 再删除计划条件
            tpPlanConditionPreMapper.deleteByPlanIds(tpPlanIds);
            tpPlanConditionPostMapper.deleteByPlanIds(tpPlanIds);
            // 再删除计划提醒
            //            tpPlanRemindMapper.batchDeleteByTpPlanIds(tpPlanIds);
            // 再删除活动 和 活动条件
            List<Long> activityIds = tpPlanActivityMapper.getIdsByTpPlanIds(tpPlanIds);
            if (!CollectionUtils.isEmpty(activityIds)) {
                //删除活动 触发通知需要的业务删除关联关系
                deleteActivityAdvice.deletedActivityAdvice(activityIds, model.getContext(), model.getDate());
                tpPlanActivityConditionPreMapper.deleteByActivityIds(activityIds);
                tpPlanActivityConditionPostMapper.deleteByActivityIds(activityIds);
            }
            if (num > 0) {
                RequestContext context = ContextHolder.get();
                TpPlan tpPlan = this.getById(tpPlanIds.get(0));
                if (tpPlan == null) {
                    return num;
                }
                TrainingProject trainingProject = trainingProjectMapper.selectById(tpPlan.getTrainingProjectId());
                for (Long tpPlanId : tpPlanIds) {
                    TpPlan tpPlan1 = this.getById(tpPlanId);
                    if (tpPlan1.getEnableRemindApp() == 1) {
                        //若开启了提醒功能，需要更新消息那边的业务状态
                        try {
                            //发消息告知提醒有变化
                            MessageRemindVo remindVo = new MessageRemindVo();
                            remindVo.setHasDeleted(true);
                            taskExecutor.asynExecute(new AbstractTaskHandler() {
                                @Override
                                public void handle() {
                                    trainingEvenSendMessage.systemSendMessage(trainingProject, tpPlanId, remindVo,
                                        context);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return num;
        }
        return null;
    }

    @Override
    public TpPlan viewDetail(Long planId) {
        // 查出计划主体
        TpPlan plan = tpPlanMapper.selectById(planId);
        if (null != plan) {
            TpPlanVo planVo = new TpPlanVo();
            planVo.setEndTime(plan.getEndTime());
            planVo.setName(plan.getName());
            planVo.setStartTime(plan.getStartTime());

            TpPlanConditionVo tpPlanConditionVo = new TpPlanConditionVo();
            // 查找开启条件
            TpPlanConditionPre conditionPre = new TpPlanConditionPre();
            conditionPre.setDeleted(ProjectConstant.DELETED_NO);
            conditionPre.setPlanId(planId);
            //            plan.setConditionPres();
            List<TpPlanConditionPre> conditionPres =
                tpPlanConditionPreMapper.selectList(new QueryWrapper<>(conditionPre));
            if (!CollectionUtils.isEmpty(conditionPres)) {
                for (TpPlanConditionPre tpPlanConditionPre : conditionPres) {
                    if (CollectionUtils.isEmpty(tpPlanConditionVo.getPrePlanIds())) {
                        List<Long> preIds = new ArrayList<>();
                        preIds.add(tpPlanConditionPre.getPrePlanId());
                        tpPlanConditionVo.setPrePlanIds(preIds);
                    } else {
                        tpPlanConditionVo.getPrePlanIds().add(tpPlanConditionPre.getPrePlanId());
                    }
                }
            }
            // 查找完成条件
            TpPlanConditionPost conditionPost = new TpPlanConditionPost();
            conditionPost.setDeleted(ProjectConstant.DELETED_NO);
            conditionPost.setTpPlanId(planId);
            List<TpPlanConditionPost> conditionPosts =
                tpPlanConditionPostMapper.selectList(new QueryWrapper<>(conditionPost));
            if (!CollectionUtils.isEmpty(conditionPosts)) {
                for (TpPlanConditionPost tpPlanConditionPost : conditionPosts) {
                    if (tpPlanConditionPost.getType().equals(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_ID)) {
                        if (CollectionUtils.isEmpty(tpPlanConditionVo.getPostActivityRelationIds())) {
                            List<Long> postIds = new ArrayList<>();
                            //                            postIds.add(tpPlanConditionPost.getTpPlanActivityRelationId
                            //                            ());
                            postIds.add(tpPlanConditionPost.getTpPlanActivityId());
                            tpPlanConditionVo.setPostActivityRelationIds(postIds);
                        } else {
                            tpPlanConditionVo.getPostActivityRelationIds()
                                .add(tpPlanConditionPost.getTpPlanActivityId());
                        }
                    } else {
                        tpPlanConditionVo.setPostActivityNum(tpPlanConditionPost.getNum());
                    }
                }
            }

            if (CollectionUtils.isEmpty(tpPlanConditionVo.getPostActivityRelationIds())) {
                tpPlanConditionVo.setPostActivityRelationIds(null);
            }
            if (CollectionUtils.isEmpty(tpPlanConditionVo.getPrePlanIds())) {
                tpPlanConditionVo.setPrePlanIds(null);
            }
            plan.setCondition(tpPlanConditionVo);

            // 查找提醒内容
            TpRemindVo tpRemindVo = markRemindDetail(plan);
            plan.setRemindVo(tpRemindVo);
        }
        return plan;
    }

    @Override
    public Integer truncateActivity(BaseModel<Long> model) {
        Long planId = model.getObj();
        List<Long> tpPlanIds = new ArrayList<>();
        tpPlanIds.add(planId);
        int num = 0;
        // 再删除计划条件
        tpPlanConditionPreMapper.deleteByPlanIds(tpPlanIds);
        tpPlanConditionPostMapper.deleteByPlanIds(tpPlanIds);
        // 再删除计划提醒
        tpPlanRemindMapper.batchDeleteByTpPlanIds(tpPlanIds);
        // 再删除活动 和 活动条件
        List<Long> activityIds = tpPlanActivityMapper.getIdsByTpPlanIds(tpPlanIds);
        if (!CollectionUtils.isEmpty(activityIds)) {
            //删除活动 触发通知需要的业务删除关联关系
            num = deleteActivityAdvice.deletedActivityAdvice(activityIds, model.getContext(), model.getDate());
            tpPlanActivityConditionPreMapper.deleteByActivityIds(activityIds);
            tpPlanActivityConditionPostMapper.deleteByActivityIds(activityIds);
        }
        return num;
    }

    /**
     * 任务完成情况API获取计划列表
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<TpPlanFinishedListVo> finishedList(Long projectId, Integer pageNo, Integer pageSize) {
        RequestContext res = ContextHolder.get();
        Long companyId = res.getCompanyId();
        Long siteId = res.getSiteId();
        Long accountId = res.getAccountId();
        // 和公有云查询项目状态保持一致；此处只取最后需要的字段
        Page<TpPlanFinishedListVo> page = new Page<>(pageNo, pageSize);
        TrainingProject project = trainingProjectMapper.selectById(projectId);
        if (null == project) {
            log.error("获取计划完成列表异常！根据项目id查询项目为空!projectId = {};", projectId);
            return page;
        }
        TrainingProjectContentVo contentVo = tpContentStudentPcStatusServiceUsing.getTpContent(project, accountId);

        List<TrainingProjectContentPlanVo> plans = contentVo.getPlans();
        if (CollectionUtils.isEmpty(plans)) {
            log.error("获取计划完成列表异常！根据项目id查询计划列表为空!projectId = {};", projectId);
            return page;
        }
        Map<Long, TrainingProjectContentPlanVo> planMap =
            plans.stream().collect(Collectors.toMap(TrainingProjectContentPlanVo::getId, Function.identity()));
        List<TpPlanFinishedListVo> list = null;
        list = tpPlanMapper.finishedList(projectId, siteId, companyId, accountId, page);
        if (!CollectionUtils.isEmpty(list)) {
            for (TpPlanFinishedListVo tpPlanFinishedListVo : list) {

                Long tpPlanId = tpPlanFinishedListVo.getTpPlanId();
                TrainingProjectContentPlanVo planVo = planMap.get(tpPlanId);
                if (null == planVo) {
                    if (tpPlanFinishedListVo.getStartTime().getTime() > System.currentTimeMillis()) {
                        tpPlanFinishedListVo.setState(0);
                    }
                    continue;
                }
                Boolean finished = planVo.getFinished();

                if (finished) {
                    tpPlanFinishedListVo.setPass(1);
                    tpPlanFinishedListVo.setState(1);
                } else {
                    tpPlanFinishedListVo.setPass(0);
                    tpPlanFinishedListVo.setState(2);
                }
                if (tpPlanFinishedListVo.getStartTime().getTime() > System.currentTimeMillis()) {
                    tpPlanFinishedListVo.setState(0);
                }

            }
            page.setRecords(list);
        }
        return page;
    }

    /**
     * 任务完成情况计划关联活动信息
     *
     * @param projectId
     * @param tpPlanId
     * @return
     */
    @Override
    public TpPlanFinishedVo getTpPlanActivity(Long projectId, Long tpPlanId) {
        RequestContext res = ContextHolder.get();
        Long accountId = res.getAccountId();
        //获取计划基本信息
        TpPlanFinishedVo tpPlanFinishedVo = new TpPlanFinishedVo();
        TpPlan tpPlan = tpPlanMapper.selectById(tpPlanId);
        if (null != tpPlan && System.currentTimeMillis() >= tpPlan.getStartTime()
            .getTime() && System.currentTimeMillis() <= tpPlan.getEndTime().getTime()) {
            tpPlanFinishedVo.setTpPlanINTime(true);
        } else {
            tpPlanFinishedVo.setTpPlanINTime(false);
        }
        //查询培训项目是否过期并装入实体中
        TrainingProject trainingProject = new TrainingProject();
        trainingProject = trainingProjectMapper.selectById(projectId);
        String logoUrl = trainingProject.getLogoImg();
        if (null != trainingProject && (System.currentTimeMillis() >= trainingProject.getStartTime()
            .getTime()) && (System.currentTimeMillis() <= trainingProject.getEndTime().getTime())) {
            tpPlanFinishedVo.setTpInTime(true);
        } else {
            tpPlanFinishedVo.setTpInTime(false);
        }
        //查询关联活动列表
        List<TpPlanFinishedActivityVo> list = new ArrayList<>();
        list = tpPlanActivityMapper.getActivities(tpPlanId);
        Map<Long, String> courseLogoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> courseIds = new ArrayList<>();
            for (TpPlanFinishedActivityVo vo : list) {
                courseIds.add(vo.getRelationId());
            }
            if (!CollectionUtils.isEmpty(courseIds)) {
                courseLogoMap = courseClient.getCourseLogos(courseIds);
            }
        }
        String keyForActivityFinished = CacheNamespace.TP_ACTIVITY_FINISHED.concat(String.valueOf(accountId));
        if (!CollectionUtils.isEmpty(list)) {
            tpPlanFinishedVo.setNum(list.size());
            for (TpPlanFinishedActivityVo tpPlanFinishedActivityVo : list) {
                if (null != courseLogoMap && null != courseLogoMap.get(tpPlanFinishedActivityVo.getRelationId())) {
                    tpPlanFinishedActivityVo.setLogoUrl(courseLogoMap.get(tpPlanFinishedActivityVo.getRelationId()));
                }
                //改成和公有云一致的查询逻辑；从缓存查。。。
                boolean finish = redisCache.hExisted(keyForActivityFinished,
                    String.valueOf(tpPlanFinishedActivityVo.getRelationId()));
                if (finish) {
                    tpPlanFinishedActivityVo.setIsFinished(1);
                } else {
                    tpPlanFinishedActivityVo.setIsFinished(0);
                }
                //                TpStudentActivityRecord tpStudentActivityRecord = new TpStudentActivityRecord();
                //                tpStudentActivityRecord.setRelationId(tpPlanFinishedActivityVo.getRelationId());
                //                tpStudentActivityRecord.setAccountId(accountId);
                //                tpStudentActivityRecord.setFinished(1);
                //                QueryWrapper<TpStudentActivityRecord> QueryWrapper = new QueryWrapper<>
                //                (tpStudentActivityRecord);
                //                List<TpStudentActivityRecord> tpStudentActivityRecords =
                //                tpStudentActivityRecordMapper.selectList(QueryWrapper);
                //                if (!CollectionUtils.isEmpty(tpStudentActivityRecords)) {
                //                    tpPlanFinishedActivityVo.setIsFinished(1);
                //                } else {
                //                    tpPlanFinishedActivityVo.setIsFinished(0);
                //                }
            }
        }
        if (!CollectionUtils.isEmpty(list)) {
            tpPlanFinishedVo.setActivityList(list);
        }

        return tpPlanFinishedVo;
    }

    @Override
    public TpPlanFinishedVo getTpPlanActivityAia(Long projectId, Long tpPlanId) {
        TpPlanFinishedVo tpPlanFinishedVo = getTpPlanActivity(projectId, tpPlanId);
        List<TpPlanFinishedActivityVo> listData = tpPlanFinishedVo.getActivityList();
        List<CourseEntityVo> courseEntityVos = courseClient.getCourseByIds(
            listData.stream().map(coureIds -> coureIds.getRelationId()).collect(Collectors.toList()));
        List<TpPlanFinishedActivityVo> listRet = new ArrayList<>();
        String remark = "";
        if (!CollectionUtils.isEmpty(courseEntityVos)) {
            Map<Long, CourseEntityVo> dataMap =
                courseEntityVos.stream().collect(Collectors.toMap(key -> key.getId(), val -> val));
            for (TpPlanFinishedActivityVo vo : listData) {
                if (dataMap.containsKey(vo.getRelationId())) {
                    CourseEntityVo courseEntityVo = dataMap.get(vo.getRelationId());
                    vo.setRemark(courseEntityVo.getRemark());
                    vo.setAuthorUnit(courseEntityVo.getAuthorUnit());
                    String loopDesc = StringUtils.trimToEmpty(courseEntityVo.getRemark());
                    log.info("remark={}" + remark + "   ;  loopDesc=" + loopDesc + "");
                    if (StringUtils.isNotBlank(loopDesc) && !remark.equals(loopDesc)) {
                        TpPlanFinishedActivityVo activityVo = new TpPlanFinishedActivityVo();
                        activityVo.setType(-1);
                        activityVo.setTitle(courseEntityVo.getRemark());
                        listRet.add(activityVo);
                    }
                    remark = StringUtils.trimToEmpty(courseEntityVo.getRemark());
                }
                listRet.add(vo);
            }
            tpPlanFinishedVo.setActivityList(listRet);
        }
        return tpPlanFinishedVo;
    }

    /**
     * 任务完成情况点击活动
     *
     * @param activityId
     * @param tpPlanId
     * @return
     */
    @Override
    public Map<String, Object> ClickActivity(Long activityId, Long tpPlanId) {
        RequestContext res = ContextHolder.get();
        Long accountId = res.getAccountId();
        List<Long> prePlanIds = null;
        //返回对象
        Map<String, Object> map = new HashMap<>();
        //查出所有前置计划id
        prePlanIds = tpPlanConditionPreMapper.getPrePlanIdsByPlanId(tpPlanId);
        List<Long> unFinishedPrePlanIds = new ArrayList<>();
        List<TpStudentPlanRecord> list = null;
        //遍历前置计划id,查看是否完成,没完成将id加进未完成列表
        if (!CollectionUtils.isEmpty(prePlanIds)) {
            for (Long prePlanId : prePlanIds) {
                TpStudentPlanRecord tpStudentPlanRecord = new TpStudentPlanRecord();
                tpStudentPlanRecord.setAccountId(accountId);
                tpStudentPlanRecord.setTpPlanId(prePlanId);
                tpStudentPlanRecord.setFinished(1);
                QueryWrapper<TpStudentPlanRecord> QueryWrapper = new QueryWrapper<>(tpStudentPlanRecord);
                list = tpStudentPlanRecordMapper.selectList(QueryWrapper);
                if (CollectionUtils.isEmpty(list)) {
                    unFinishedPrePlanIds.add(prePlanId);
                }
            }
        }
        Integer isOpen = 0;
        if (CollectionUtils.isEmpty(unFinishedPrePlanIds)) {
            isOpen = 1;
        }
        map.put("isOpen", isOpen);
        map.put("unFinishedPrePlanIds", unFinishedPrePlanIds);
        return map;
    }

    /**
     * 报表需要所有的计划包括删掉的计划  王飞达
     */
    @Override
    public List<TpPlan> getListByStatistics(Long tpId) {
        // TODO Auto-generated method stub
        TpPlan example = new TpPlan();
        example.setTrainingProjectId(tpId);
        QueryWrapper<TpPlan> ew = new QueryWrapper<TpPlan>(example);
        return tpPlanMapper.selectList(ew);
    }

    @Override
    public List<TpPlan> getListBySiteIds(List<Long> siteIds) {
        QueryWrapper<TpPlan> wrapper = new QueryWrapper<>();
        wrapper.in("site_id", siteIds);
        return tpPlanMapper.selectList(wrapper);
    }

    @Override
    public Map<Long, TrainingProject> getCaseLibraryProject(List<Long> idList) {
        List<TrainingProject> trainingProjectList = trainingProjectMapper.getCaseLibraryProject(idList);
        Map<Long, TrainingProject> map = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(trainingProjectList)) {
            for (TrainingProject trainingProject : trainingProjectList) {
                map.put(trainingProject.getId(), trainingProject);
            }
            return map;
        }
        return null;
    }

    @Override
    public Integer move(Integer type, Long id) {
        RequestContext context = ContextHolder.get();

        TpPlan sourceTpPlan = this.getById(id);
        Integer sourceSort = sourceTpPlan.getSort();
        TpPlan targetPlan = this.baseMapper.getSort(id, sourceTpPlan.getTrainingProjectId(), context.getSiteId(), type);
        if (targetPlan != null) {
            Integer targetSort = targetPlan.getSort();
            if (null != targetSort) {
                sourceTpPlan.setSort(targetSort);
                this.updateById(sourceTpPlan);
                targetPlan.setSort(sourceSort);
                this.updateById(targetPlan);
            }
            return 1;
        } else {
            return type == 1 ? -1 : -2;//-1到顶了   -2到底了
        }
    }

    /**
     * 组装学习计划消息提醒
     *
     * @param plan 学习对象
     * @return
     */
    private TpRemindVo markRemindDetail(TpPlan plan) {
        Long planId = plan.getId();
        if (null == planId) {
            return null;
        }

        // 组装提醒
        TpRemindVo tpRemindVo = new TpRemindVo();
        tpRemindVo.setAppTemplateId(plan.getAppRemindTemplateId());
        tpRemindVo.setContent(plan.getRemindContent());
        tpRemindVo.setEnableApp(plan.getEnableRemindApp());
        tpRemindVo.setEnableMail(plan.getEnableRemindMail());

        //查询学习计划消息提醒
        TpPlanRemind planRemind = new TpPlanRemind();
        planRemind.setTpPlanId(planId);
        List<TpPlanRemind> reminds = tpPlanRemindMapper.selectList(new QueryWrapper<>(planRemind));
        if (CollectionUtils.isEmpty(reminds)) {
            return null;
        }
        TpRemindTimeVo timeVo = null;
        List<TpRemindTimeVo> remindTimeVos = new ArrayList<TpRemindTimeVo>(reminds.size());
        for (TpPlanRemind tpRemind : reminds) {
            timeVo = new TpRemindTimeVo();
            timeVo.setId(tpRemind.getId());
            //区分提醒时间类型
            if (tpRemind.getType() != null && tpRemind.getType() == 3) {
                //自定义
                timeVo.setTime(tpRemind.getTime());
            } else {
                //开始结束前后
                timeVo.setSeconds(tpRemind.getSeconds());
            }
            timeVo.setType(tpRemind.getType());

            remindTimeVos.add(timeVo);
        }
        tpRemindVo.setReminds(remindTimeVos);

        return tpRemindVo;
    }

    /**
     * 组装学习计划
     *
     * @param planVo  学习计划vo
     * @param context 上下文
     * @param date    操作时间
     * @return
     */
    private TpPlan buildTpPlan(TpPlanVo planVo, RequestContext context, Date date) {
        TpPlan tpPlan = new TpPlan();
        tpPlan.setId(idGenerator.generate());
        tpPlan.setSort(planVo.getSort());
        tpPlan.setTrainingProjectId(planVo.getTrainingProjectId());
        TpRemindVo remindVo = planVo.getRemindVo();
        if (remindVo != null) {
            tpPlan.setAppRemindTemplateId(remindVo.getAppTemplateId());
            tpPlan.setMailRemindTemplateId(remindVo.getMailTemplateId());
            //此处不可删除
            tpPlan.setEnableRemindApp(remindVo.getEnableApp());
            tpPlan.setEnableRemindMail(remindVo.getEnableMail());
            tpPlan.setRemindContent(remindVo.getContent());
        }

        tpPlan.setDeleted(ProjectConstant.DELETED_NO);
        tpPlan.setName(planVo.getName());
        tpPlan.setStartTime(planVo.getStartTime());
        tpPlan.setEndTime(planVo.getEndTime());
        // 权限范围
        tpPlan.setCompanyId(context.getCompanyId());
        tpPlan.setOrgId(context.getOrgId() == null ? new Long(0) : context.getOrgId());
        tpPlan.setSiteId(context.getSiteId());
        // 审计字段
        tpPlan.setCreateById(context.getAccountId());
        tpPlan.setCreateByName(context.getAccountName());
        tpPlan.setCreateTime(date);
        return tpPlan;
    }

    /**
     * 组装学习计划 前置计划（前置条件）
     *
     * @param planVo            学习计划vo
     * @param tpPlan            组装好的学习计划
     * @param planConditionPres 要入库的学习计划前置条件集合
     * @throws Exception
     */
    private void buildTpPlanConditionPre(TpPlanVo planVo, TpPlan tpPlan, List<TpPlanConditionPre> planConditionPres)
        throws TrainingProjectException {
        // 学习计划条件vo
        TpPlanConditionVo conditionVo = planVo.getCondition();

        if (null != conditionVo) {
            // 指定前置计划id集合不为空
            if (!CollectionUtils.isEmpty(conditionVo.getPrePlanIds())) {
                for (Long prePlanId : conditionVo.getPrePlanIds()) {
                    TpPlanConditionPre conditionPre = new TpPlanConditionPre();
                    conditionPre.setId(idGenerator.generate());
                    conditionPre.setDeleted(ProjectConstant.DELETED_NO);
                    conditionPre.setPlanId(tpPlan.getId());
                    conditionPre.setPrePlanId(prePlanId);
                    conditionPre.setTrainingProjectId(tpPlan.getTrainingProjectId());
                    planConditionPres.add(conditionPre);
                }
            }
        }

    }

    /**
     * 组装学习活动
     *
     * @param activityVos            学习活动vo集合
     * @param context                上下文
     * @param date                   操作时间
     * @param tpPlan                 组装好的学习计划
     * @param planConditionVo        学习计划条件vo
     * @param planConditionPosts     要入库的学习计划完成条件集合
     * @param activityConditionPres  要入库的学习活动开启条件集合
     * @param activityConditionPosts 要入库的学习活动完成条件集合
     * @return
     * @throws Exception
     */
    private List<TpPlanActivity> buildTpPlanActivity(List<TpPlanActivityVo> activityVos, RequestContext context,
        Date date, TpPlan tpPlan, TpPlanConditionVo planConditionVo, List<TpPlanConditionPost> planConditionPosts,
        List<TpPlanActivityConditionPre> activityConditionPres,
        List<TpPlanActivityConditionPost> activityConditionPosts) throws TrainingProjectException {
        List<TpPlanActivity> list = null;

        if (!CollectionUtils.isEmpty(activityVos)) {
            list = new ArrayList<>();
            // 活动关联id ：活动 map
            Map<Long, TpPlanActivity> relationActivityMap = new HashMap<>();

            int sort = 0;

            for (TpPlanActivityVo activityVo : activityVos) {
                TpPlanActivity activity = new TpPlanActivity();

                activity.setCompanyId(context.getCompanyId());
                activity.setOrgId(context.getOrgId() == null ? new Long(0) : context.getOrgId());
                activity.setSiteId(context.getSiteId());

                activity.setCreateById(context.getAccountId());
                activity.setCreateByName(context.getAccountName());
                activity.setCreateTime(date);

                activity.setId(idGenerator.generate());
                activity.setDeleted(ProjectConstant.DELETED_NO);
                activity.setName(activityVo.getName());
                activity.setAddress(activityVo.getAddress());
                // 如果是外部链接，使用本身的id
                if (activityVo.getType().equals(TpActivityType.TYPE_LINK)) {
                    activity.setRelationId(activity.getId());
                } else {
                    activity.setRelationId(activityVo.getRelationId());
                }
                activity.setTpPlanId(tpPlan.getId());
                activity.setTrainingProjectId(tpPlan.getTrainingProjectId());
                activity.setType(activityVo.getType());
                activity.setSort(++sort);

                list.add(activity);
                relationActivityMap.put(activity.getRelationId(), activity);
            }

            // 组装活动条件
            for (TpPlanActivityVo activityVo : activityVos) {
                TpPlanActivityConditionVo activityConditionVo = activityVo.getCondition();
                buildTpPlanActivityCondition(relationActivityMap, relationActivityMap.get(activityVo.getRelationId()),
                    activityConditionVo, activityConditionPres, activityConditionPosts);
            }
            // 组装学习计划完成条件之一：指定的完成活动集合不为空
            if (null != planConditionVo) {
                if (!CollectionUtils.isEmpty(planConditionVo.getPostActivityRelationIds())) {
                    for (Long relationId : planConditionVo.getPostActivityRelationIds()) {
                        TpPlanConditionPost conditionPost = new TpPlanConditionPost();

                        conditionPost.setId(idGenerator.generate());
                        conditionPost.setDeleted(ProjectConstant.DELETED_NO);
                        conditionPost.setTpPlanId(tpPlan.getId());
                        conditionPost.setType(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_ID);
                        conditionPost.setTpPlanActivityId(relationActivityMap.get(relationId).getId());
                        conditionPost.setTpPlanActivityRelationId(relationId);
                        conditionPost.setTrainingProjectId(tpPlan.getTrainingProjectId());
                        planConditionPosts.add(conditionPost);
                    }
                }
                // 组装学习计划完成条件之：设置的完成学习活动数不为空
                if (null != planConditionVo.getPostActivityNum() && planConditionVo.getPostActivityNum() > 0) {
                    TpPlanConditionPost conditionPost = new TpPlanConditionPost();
                    conditionPost.setId(idGenerator.generate());
                    conditionPost.setDeleted(ProjectConstant.DELETED_NO);
                    conditionPost.setTpPlanId(tpPlan.getId());
                    conditionPost.setType(ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_NUM);
                    conditionPost.setNum(planConditionVo.getPostActivityNum());
                    conditionPost.setTrainingProjectId(tpPlan.getTrainingProjectId());
                    planConditionPosts.add(conditionPost);
                }
            }
        }
        return list;
    }

    /**
     * 组装学习活动的条件
     *
     * @param relationActivityMap    活动关联id ：活动 map
     * @param tpPlanActivity         组装好的学习活动
     * @param activityConditionVo    学习活动条件vo
     * @param activityConditionPres  要入库的学习活动开启条件集合
     * @param activityConditionPosts 要入库的学习活动完成条件集合
     * @throws Exception
     */
    private void buildTpPlanActivityCondition(Map<Long, TpPlanActivity> relationActivityMap,
        TpPlanActivity tpPlanActivity, TpPlanActivityConditionVo activityConditionVo,
        List<TpPlanActivityConditionPre> activityConditionPres,
        List<TpPlanActivityConditionPost> activityConditionPosts) throws TrainingProjectException {
        if (null != activityConditionVo) {

            // 如果是活动开启条件 -- 指定完成活动
            if (!CollectionUtils.isEmpty(activityConditionVo.getPreActivityRelationIds())) {
                for (Long preActivityRelationId : activityConditionVo.getPreActivityRelationIds()) {
                    TpPlanActivityConditionPre conditionPre = new TpPlanActivityConditionPre();
                    conditionPre.setId(idGenerator.generate());
                    conditionPre.setDeleted(ProjectConstant.DELETED_NO);
                    conditionPre.setType(ProjectConstant.TP_PLAN_ACTIVITY_CONDITION_PRE_ID);
                    conditionPre.setTpPlanActivityId(tpPlanActivity.getId());
                    conditionPre.setPreTpPlanActivityId(relationActivityMap.get(preActivityRelationId).getId());
                    conditionPre.setPreTpPlanActivityRelationId(preActivityRelationId);
                    activityConditionPres.add(conditionPre);
                }
            }
            // 如果是活动开启条件 -- 设置了完成数
            if (null != activityConditionVo.getPreNum() && activityConditionVo.getPreNum() > 0) {
                TpPlanActivityConditionPre conditionPre = new TpPlanActivityConditionPre();
                conditionPre.setId(idGenerator.generate());
                conditionPre.setDeleted(ProjectConstant.DELETED_NO);
                conditionPre.setNum(activityConditionVo.getPreNum());
                conditionPre.setTpPlanActivityId(tpPlanActivity.getId());
                conditionPre.setType(ProjectConstant.TP_PLAN_ACTIVITY_CONDITION_PRE_NUM);
                activityConditionPres.add(conditionPre);
            }
            // 如果是活动完成条件
            if (null != activityConditionVo.getPostExamScore() && activityConditionVo.getPostExamScore() > 0) {
                // 如果活动是考试类型
                if (tpPlanActivity.getType().equals(TpActivityType.TYPE_EXAM)) {
                    TpPlanActivityConditionPost conditionPost = new TpPlanActivityConditionPost();
                    conditionPost.setId(idGenerator.generate());
                    conditionPost.setDeleted(ProjectConstant.DELETED_NO);
                    conditionPost.setTpPlanActivityId(tpPlanActivity.getId());
                    conditionPost.setExamId(tpPlanActivity.getRelationId());
                    conditionPost.setExamScore(activityConditionVo.getPostExamScore());
                    conditionPost.setType(1);
                    activityConditionPosts.add(conditionPost);
                }
                // 如果活动是证书类型
                if (tpPlanActivity.getType().equals(TpActivityType.TYPE_CERTIFICATE)) {
                    TpPlanActivityConditionPost conditionPost = new TpPlanActivityConditionPost();
                    conditionPost.setId(idGenerator.generate());
                    conditionPost.setDeleted(ProjectConstant.DELETED_NO);
                    conditionPost.setTpPlanActivityId(tpPlanActivity.getId());
                    conditionPost.setCertificateId(tpPlanActivity.getRelationId());
                    conditionPost.setExamId(activityConditionVo.getExamId());
                    conditionPost.setExamScore(activityConditionVo.getPostExamScore());
                    conditionPost.setType(2);
                    activityConditionPosts.add(conditionPost);
                }
            }
        }
    }

    /**
     * 组装培训计划提醒
     *
     * @param tpRemindVo 学习计划提醒vo
     * @param plan       组装好的学习计划
     * @param context    上下文
     * @param now        当前时间
     * @return
     * @throws Exception
     */
    private List<TpPlanRemind> buildTpPlanRemind(TpRemindVo tpRemindVo, TpPlan plan, RequestContext context, Date now)
        throws TrainingProjectException {
        // 提醒vo
        if (null != tpRemindVo) {
            if (!CollectionUtils.isEmpty(tpRemindVo.getReminds())) {
                List<TpPlanRemind> list = new ArrayList<>();
                for (TpRemindTimeVo timeVo : tpRemindVo.getReminds()) {
                    TpPlanRemind remind = new TpPlanRemind();
                    remind.setId(idGenerator.generate());
                    remind.setTpPlanId(plan.getId());
                    remind.setCreateById(context.getAccountId());
                    remind.setCreateByName(context.getAccountName());
                    remind.setCreateTime(now);

                    remind.setType(timeVo.getType());
                    // 1：开始时间之前
                    if (timeVo.getType() == 1) {
                        remind.setTime(new Date(plan.getStartTime().getTime() + (timeVo.getSeconds() * 1000)));
                        remind.setSeconds(Long.valueOf(timeVo.getSeconds()));
                    }
                    // 2：结束时间之前
                    else if (timeVo.getType() == 2) {
                        remind.setTime(new Date(plan.getEndTime().getTime() + (timeVo.getSeconds() * 1000)));
                        remind.setSeconds(Long.valueOf(timeVo.getSeconds()));
                    }
                    // 3：自定义时间
                    else if (timeVo.getType() == 3) {
                        remind.setTime(timeVo.getTime());
                    } else {
                        throw new TrainingProjectException(
                            "未知的提醒时间类型，请检查：1：开始时间之前，2：结束时间事前，3：自定义时间");
                    }
                    list.add(remind);
                }
                return list;
            }
        }
        return null;
    }
}
