package com.yizhi.training.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.assignment.application.feign.AssignmentClient;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.core.application.publish.CloudEventPublisher;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.library.application.feign.CaseLibraryClient;
import com.yizhi.training.application.constant.CertificateGrantStatus;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.*;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.util.DeleteActivityAdvice;
import com.yizhi.training.application.vo.domain.CourseRelateProjectVO;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.manage.ConditionDeleteVo;
import com.yizhi.training.application.vo.manage.TpPlanActivityConditionUpdateVo;
import com.yizhi.training.application.vo.manage.TpPlanActivitySingleVo;
import com.yizhi.util.application.constant.TpActivityType;
import com.yizhi.util.application.enums.i18n.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p>
 * 学习计划中的活动 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Service
@Transactional
public class TpPlanActivityServiceImpl extends ServiceImpl<TpPlanActivityMapper, TpPlanActivity>
    implements ITpPlanActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TpPlanActivityServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private TpPlanActivityConditionPreMapper tpPlanActivityConditionPreMapper;

    @Autowired
    private TpPlanActivityConditionPostMapper tpPlanActivityConditionPostMapper;

    @Autowired
    private TpPlanMapper tpPlanMapper;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private CloudEventPublisher cloudEventPublisher;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    @Autowired
    private AssignmentClient assignmentClient;

    @Autowired
    private DeleteActivityAdvice deleteActivityAdvice;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private CaseLibraryClient caseLibraryClient;

    @Override
    public Integer updateSort(List<TpPlanActivity> list) {
        int result = 0;
        for (TpPlanActivity activity : list) {
            result += tpPlanActivityMapper.updateById(activity);
        }
        return result;
    }

    @Override
    public List<TpPlanActivity> allList(Long tpPlanId, String name, Integer type) {
        // 先查询培训活动
        TpPlanActivity example = new TpPlanActivity();
        example.setTpPlanId(tpPlanId);
        example.setType(type);
        example.setDeleted(ProjectConstant.DELETED_NO);

        QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>(example);
        if (!StrUtil.isBlank(name)) {
            ew.like("name", name);
        }
        ew.orderByDesc("sort");

        List<TpPlanActivity> activities = tpPlanActivityMapper.selectList(ew);

        // 再查询条件
        if (!CollectionUtils.isEmpty(activities)) {
            List<Long> activityIds = new ArrayList<>();
            Map<Long, TpPlanActivity> map = new HashMap<>();
            for (TpPlanActivity activity : activities) {
                activityIds.add(activity.getId());
                map.put(activity.getId(), activity);
            }

            // 开启条件
            List<TpPlanActivityConditionPre> conditionPres =
                tpPlanActivityConditionPreMapper.selectListByActivityIds(activityIds);
            if (!CollectionUtils.isEmpty(conditionPres)) {
                for (TpPlanActivityConditionPre conditionPre : conditionPres) {
                    if (conditionPre.getType().equals(ProjectConstant.TP_PLAN_ACTIVITY_CONDITION_PRE_ID)) {
                        if (CollectionUtils.isEmpty(
                            map.get(conditionPre.getTpPlanActivityId()).getCondition().getPreActivityRelationIds())) {
                            List<Long> relationIds = new ArrayList<>();
                            relationIds.add(conditionPre.getPreTpPlanActivityRelationId());
                            map.get(conditionPre.getTpPlanActivityId()).getCondition()
                                .setPreActivityRelationIds(relationIds);
                        } else {
                            map.get(conditionPre.getTpPlanActivityId()).getCondition().getPreActivityRelationIds()
                                .add(conditionPre.getPreTpPlanActivityRelationId());
                        }
                    } else {
                        map.get(conditionPre.getTpPlanActivityId()).getCondition().setPreNum(conditionPre.getNum());
                    }
                }
            }
            // 完成条件
            List<TpPlanActivityConditionPost> conditionPosts =
                tpPlanActivityConditionPostMapper.selectListByActivityIds(activityIds);
            if (!CollectionUtils.isEmpty(conditionPosts)) {
                for (TpPlanActivityConditionPost conditionPost : conditionPosts) {
                    map.get(conditionPost.getTpPlanActivityId()).getCondition().setExamId(conditionPost.getExamId());
                    map.get(conditionPost.getTpPlanActivityId()).getCondition()
                        .setPostExamScore(conditionPost.getExamScore());
                }
            }

        }
        return activities;
    }

    @Override
    public List<TpPlanActivity> allListByTpPlanId(Long tpPlanId, Integer... type) {
        TpPlanActivity example = new TpPlanActivity();
        example.setDeleted(ProjectConstant.DELETED_NO);

        QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>(example);

        if (type != null) {
            if (type.length == 1) {
                ew.eq("type", type[0]);
            } else if (type.length > 1) {
                ew.in("type", type);
            }
        }
        return this.baseMapper.selectList(ew);
    }

    @Override
    public List<TpPlanActivity> allListByTp(Long tpId) {
        TpPlanActivity example = new TpPlanActivity();
        example.setDeleted(ProjectConstant.DELETED_NO);
        example.setTrainingProjectId(tpId);
        QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>(example);
        ew.orderByAsc("sort");

        return tpPlanActivityMapper.selectList(ew);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Integer deleteByIds(BaseModel<List<Long>> model) {
        List<Long> ids = model.getObj();

        //删除活动 触发通知需要的业务删除关联关系
        int num = deleteActivityAdvice.deletedActivityAdvice(ids, model.getContext(), model.getDate());
        // 删除活动条件
        tpPlanActivityConditionPreMapper.deleteByActivityIds(ids);
        tpPlanActivityConditionPostMapper.deleteByActivityIds(ids);
        // 更新排序
        // 查出TplanId

        TpPlanActivity tpPlanActivityEW = new TpPlanActivity();
        tpPlanActivityEW.setCompanyId(model.getContext().getCompanyId());
        tpPlanActivityEW.setSiteId(model.getContext().getSiteId());
        tpPlanActivityEW.setDeleted(1);
        QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>(tpPlanActivityEW);
        ew.in("id", ids);
        TpPlanActivity tpPlanActivity = tpPlanActivityEW.selectOne(ew);
        if (null != tpPlanActivityEW) {
            // 查出TPplanId 下的 活动
            tpPlanActivityEW.setDeleted(ProjectConstant.DELETED_NO);
            tpPlanActivityEW.setTpPlanId(tpPlanActivity.getTpPlanId());
            ew = new QueryWrapper<>(tpPlanActivityEW);
            ew.orderByAsc("sort");
            List<TpPlanActivity> activities = tpPlanActivityMapper.selectList(ew);
            if (!CollectionUtils.isEmpty(activities)) {
                // 更新活动排序
                TpPlanActivity activity = new TpPlanActivity();
                int index = 0;
                for (TpPlanActivity a : activities) {
                    index++;
                    activity.setId(a.getId());
                    activity.setSort(index);
                    activity.updateById();
                }
            }
        }
        return num;
    }

    @Override
    public Integer conditionEdit(BaseModel<TpPlanActivityConditionUpdateVo> model) throws Exception {
        Date now = model.getDate();
        RequestContext context = model.getContext();
        TpPlanActivityConditionUpdateVo vo = model.getObj();

        Long activityId = vo.getActivityId();

        TpPlanActivity tpPlanActivity = tpPlanActivityMapper.selectById(activityId);
        if (null == tpPlanActivity) {
            throw new Exception("学习活动不存在 -- 活动id：" + activityId);
        }

        // 删除以前的
        List<Long> activityIds = new ArrayList<>();
        activityIds.add(activityId);
        tpPlanActivityConditionPreMapper.deleteByActivityIds(activityIds);
        tpPlanActivityConditionPostMapper.deleteByActivityIds(activityIds);

        List<TpPlanActivityConditionPre> activityConditionPres = new ArrayList<>();
        List<TpPlanActivityConditionPost> activityConditionPosts = new ArrayList<>();

        // 如果是活动开启条件 -- 指定完成活动集合
        if (!CollectionUtils.isEmpty(vo.getPreActivityIds())) {

            TpPlanActivity planActivityPreEx = new TpPlanActivity();
            planActivityPreEx.setDeleted(ProjectConstant.DELETED_NO);
            planActivityPreEx.setSiteId(context.getSiteId());
            planActivityPreEx.setTpPlanId(tpPlanActivity.getTpPlanId());
            QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>(planActivityPreEx);
            ew.in("relation_id", vo.getPreActivityIds());
            List<TpPlanActivity> planActivitiyPres = tpPlanActivityMapper.selectList(ew);

            Map<Long, TpPlanActivity> map = new HashMap<>();
            for (TpPlanActivity activity : planActivitiyPres) {
                map.put(activity.getRelationId(), activity);
            }

            for (Long preActivityId : vo.getPreActivityIds()) {
                TpPlanActivityConditionPre conditionPre = new TpPlanActivityConditionPre();
                conditionPre.setId(idGenerator.generate());
                conditionPre.setDeleted(ProjectConstant.DELETED_NO);
                conditionPre.setType(ProjectConstant.TP_PLAN_ACTIVITY_CONDITION_PRE_ID);
                conditionPre.setTpPlanActivityId(tpPlanActivity.getId());
                conditionPre.setPreTpPlanActivityId(map.get(preActivityId).getId());
                conditionPre.setPreTpPlanActivityRelationId(preActivityId);
                activityConditionPres.add(conditionPre);
            }
        }
        // 如果是活动开启条件 -- 设置了完成数
        if (null != vo.getPreNum() && vo.getPreNum() > 0) {
            TpPlanActivityConditionPre conditionPre = new TpPlanActivityConditionPre();
            conditionPre.setId(idGenerator.generate());
            conditionPre.setDeleted(ProjectConstant.DELETED_NO);
            conditionPre.setNum(vo.getPreNum());
            conditionPre.setTpPlanActivityId(tpPlanActivity.getId());
            conditionPre.setType(ProjectConstant.TP_PLAN_ACTIVITY_CONDITION_PRE_NUM);
            activityConditionPres.add(conditionPre);
        }
        // 如果是活动完成条件（只有考试和证书有完成条件）
        if (null != vo.getExamId() && !vo.getExamId().equals(0L)) {
            // 如果活动是考试类型
            if (tpPlanActivity.getType().equals(TpActivityType.TYPE_EXAM)) {
                TpPlanActivityConditionPost conditionPost = new TpPlanActivityConditionPost();
                conditionPost.setId(idGenerator.generate());
                conditionPost.setDeleted(ProjectConstant.DELETED_NO);
                conditionPost.setTpPlanActivityId(tpPlanActivity.getId());
                conditionPost.setExamId(tpPlanActivity.getRelationId());
                conditionPost.setExamScore(vo.getPostExamScore());
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
                conditionPost.setType(2);
                conditionPost.setExamId(vo.getExamId());
                // 查出指定考试活动的完成条件的得分
                if (null == vo.getPostExamScore()) {
                    TpPlanActivityConditionPost examConPost = new TpPlanActivityConditionPost();
                    examConPost.setExamId(vo.getExamId());
                    examConPost.setDeleted(ProjectConstant.DELETED_NO);
                    examConPost.setType(1);
                    examConPost = examConPost.selectOne(new QueryWrapper(examConPost));
                    if (examConPost != null) {
                        conditionPost.setExamScore(examConPost.getExamScore());
                    }
                } else {
                    conditionPost.setExamScore(vo.getPostExamScore());
                }
                activityConditionPosts.add(conditionPost);
            }
        }

        int result = 0;
        if (!CollectionUtils.isEmpty(activityConditionPres)) {
            result = tpPlanActivityConditionPreMapper.batchInsert(activityConditionPres);
        }
        if (!CollectionUtils.isEmpty(activityConditionPosts)) {
            result += tpPlanActivityConditionPostMapper.batchInsert(activityConditionPosts);
        }
        return result;
    }

    @Override
    public Integer addActivity(BaseModel<List<TpPlanActivitySingleVo>> model) {
        Date now = model.getDate();
        RequestContext context = model.getContext();
        List<TpPlanActivitySingleVo> activityVos = model.getObj();

        int result = 0;

        if (!CollectionUtils.isEmpty(activityVos)) {
            Long planId = activityVos.get(0).getPlanId();

            // 查出之前的活动
            TpPlanActivity oldActivity = new TpPlanActivity();
            oldActivity.setDeleted(ProjectConstant.DELETED_NO);
            oldActivity.setTpPlanId(planId);
            oldActivity.setSiteId(context.getSiteId());
            List<TpPlanActivity> oldList = tpPlanActivityMapper.selectList(new QueryWrapper<>(oldActivity));

            // 之前活动的 relationIds 集合
            List<Long> oldRelationIds = null;
            // 本次修改传过来的 relationId 集合
            List<Long> nowRelationIds = new ArrayList<>();
            // 需要修改的（只有外部链接）活动
            Map<Long, TpPlanActivity> mapToUpdate = new HashMap<>();

            // 待入库的集合
            List<TpPlanActivity> activitiesToInsert = new ArrayList<>();
            // 待删除的
            List<Long> activitiesToDelete = new ArrayList<>();
            // 需要修改的（只有外部链接）
            List<TpPlanActivity> activitiesToUpdate = new ArrayList<>();

            // 组装以前就有的活动的 relationId 集合
            if (!CollectionUtils.isEmpty(oldList)) {
                oldRelationIds = new ArrayList<>();
                List<Long> caselibraryIds = new ArrayList<>();
                for (TpPlanActivity t : oldList) {
                    // 若是外部链接，可能需要修改
                    if (t.getType().equals(TpActivityType.TYPE_LINK)) {
                        mapToUpdate.put(t.getRelationId(), t);
                    } else if (t.getType().equals(TpActivityType.TYPE_CASE_ACTIVITY)) {
                        //原创活动需要收集id 调用接口来取消与项目的关联
                        caselibraryIds.add(t.getRelationId());
                    }
                    oldRelationIds.add(t.getRelationId());
                }
                if (!CollectionUtils.isEmpty(caselibraryIds)) {
                    //调删除接口来取消与项目之间的关联
                    for (Long caselibraryId : caselibraryIds) {
                        caseLibraryClient.cancelRelateProject(caselibraryId);
                    }
                }
            }
            // 组装活动
            for (TpPlanActivitySingleVo activityVo : activityVos) {
                // 收集本次传过来的 relationId
                if (!Objects.isNull(activityVo.getRelationId())) {
                    nowRelationIds.add(activityVo.getRelationId());
                }
                // 如果以前为空，则全部是新增的
                if (CollectionUtils.isEmpty(oldRelationIds)) {
                    activitiesToInsert.add(buildTpPlanActivity(context, now, activityVo));
                }
                // 否则可能新增
                else {
                    // 如果是外部链接类型 并且 relationId为空，则是新增的
                    if (activityVo.getType().equals(7) && Objects.isNull(activityVo.getRelationId())) {
                        activitiesToInsert.add(buildTpPlanActivity(context, now, activityVo));
                    }
                    // 如果以前没有该 relationId，那就是新增的
                    else if (!oldRelationIds.contains(activityVo.getRelationId())) {
                        activitiesToInsert.add(buildTpPlanActivity(context, now, activityVo));
                    }
                    // 如果以前有，并且是活动链接类型，修改
                    else if (oldRelationIds.contains(activityVo.getRelationId()) && activityVo.getType().equals(7)) {
                        TpPlanActivity activity = mapToUpdate.get(activityVo.getRelationId());
                        if (null != activity) {
                            activity.setUpdateById(context.getAccountId());
                            activity.setUpdateByName(context.getAccountName());
                            activity.setUpdateTime(now);

                            activity.setAddress(activityVo.getAddress());
                            activity.setSort(activityVo.getSort());
                            activity.setName(activityVo.getName());
                            activitiesToUpdate.add(activity);
                        }
                    }
                }
            }

            // 组装是要删除的活动 relationId 集合
            if (!CollectionUtils.isEmpty(oldRelationIds)) {
                for (Long oldRelationId : oldRelationIds) {
                    if (!nowRelationIds.contains(oldRelationId)) {
                        activitiesToDelete.add(oldRelationId);
                    }
                }
            }

            // 删除
            if (!CollectionUtils.isEmpty(activitiesToDelete)) {
                // 查出这些活动id
                List<Long> activityIds = tpPlanActivityMapper.getIdsByRelationIds(activitiesToDelete, planId);
                if (!CollectionUtils.isEmpty(activityIds)) {
                    tpPlanActivityConditionPreMapper.deleteByActivityIds(activityIds);
                    tpPlanActivityConditionPostMapper.deleteByActivityIds(activityIds);

                    QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>();
                    ew.in("id", activityIds);
                    result += tpPlanActivityMapper.delete(ew);
                }
            }
            // 新增
            if (!CollectionUtils.isEmpty(activitiesToInsert)) {
                result += tpPlanActivityMapper.batchInsert(activitiesToInsert);
            }
            // 修改
            if (!CollectionUtils.isEmpty(activitiesToUpdate)) {
                for (TpPlanActivity activity : activitiesToUpdate) {
                    result += tpPlanActivityMapper.updateById(activity);
                }
            }
        }
        return result;
    }

    @Override
    public Integer deleteConditions(BaseModel<ConditionDeleteVo> model) {
        Date now = model.getDate();
        RequestContext context = model.getContext();
        ConditionDeleteVo vo = model.getObj();

        int result = 0;

        // 前置条件
        List<Long> preIds = vo.getPreConditionIds();
        if (!CollectionUtils.isEmpty(preIds)) {
            TpPlanActivityConditionPre example = new TpPlanActivityConditionPre();
            for (Long id : preIds) {
                example.setId(id);
                result += tpPlanActivityConditionPreMapper.deleteById(example);
            }
        }
        // 完成条件
        List<Long> postIds = vo.getPostConditionIds();
        if (!CollectionUtils.isEmpty(postIds)) {
            TpPlanActivityConditionPost example = new TpPlanActivityConditionPost();
            for (Long id : postIds) {
                example.setId(id);
                result += tpPlanActivityConditionPostMapper.deleteById(example);
            }
        }
        return result;
    }

    @Override
    public List<Long> checkBizIsExistInTp(List<Long> relationIds) {
        if (!CollectionUtils.isEmpty(relationIds)) {
            return tpPlanActivityMapper.checkBizIsExistInTp(relationIds);
        }
        return Collections.emptyList();
    }

    @Override
    public Set<String> checkBizIsExistInTpNames(List<Long> relationIds) {
        if (!CollectionUtils.isEmpty(relationIds)) {
            return tpPlanActivityMapper.checkBizIsExistInTpNames(relationIds);
        }
        return null;
    }

    @Override
    public CertificateGrantStatus certificateGrant(Map<String, Long> param) {
        Long companyId = param.get("companyId");
        Long siteId = param.get("siteId");
        Long certificateId = param.get("certificateId");
        Long accountId = param.get("accountId");
        Long planId = param.get("planId");

        TpPlan plan = tpPlanMapper.selectById(planId);
        if (plan == null) {
            LOGGER.error("点击发放证书错误：没有查询到该计划：{}", plan);
            return null;
        }

        // 1. 先看有没有获得过该证书，获得过，直接返回
        TpStudentActivityRecord record = new TpStudentActivityRecord();
        record.setAccountId(accountId);
        record.setFinished(1);
        record.setRelationId(certificateId);
        record.setType(TpActivityType.TYPE_CERTIFICATE);
        record.setSiteId(siteId);
        record.setCompanyId(companyId);
        List<TpStudentActivityRecord> records = record.selectList(new QueryWrapper(record));
        if (!CollectionUtils.isEmpty(records)) {
            return CertificateGrantStatus.HAS_GOT;
        }

        // 2. 如果没有获取过该证书，查看计划是否已经完成
        TpStudentPlanRecord planRecord = new TpStudentPlanRecord();
        planRecord.setTpPlanId(planId);
        planRecord.setFinished(1);
        planRecord.setSiteId(siteId);
        planRecord.setAccountId(accountId);
        List<TpStudentPlanRecord> planRecords = planRecord.selectList(new QueryWrapper(planRecord));
        // 2.1 如果计划已经完成，发放证书
        if (!CollectionUtils.isEmpty(planRecords)) {
            doGrantCertificate(plan.getTrainingProjectId(), siteId, certificateId, accountId, planId);
            return CertificateGrantStatus.SUCCESS_GET;
        }
        // 2.2 如果计划没有完成，查找出该计划是否有前置计划，并且是否完成该前置计划
        else {
            TpPlanConditionPre conditionPre = new TpPlanConditionPre();
            conditionPre.setPlanId(planId);
            conditionPre.setDeleted(ProjectConstant.DELETED_NO);
            List<TpPlanConditionPre> conditionPres = conditionPre.selectList(new QueryWrapper(conditionPre));
            // 2.2.1 如果有前置计划
            if (!CollectionUtils.isEmpty(conditionPres)) {
                Set<Long> toFinishedPlanIds = new HashSet<>(conditionPres.size());
                conditionPres.forEach((pre) -> toFinishedPlanIds.add(pre.getPrePlanId()));

                TpStudentPlanRecord finishedPlanRecord = new TpStudentPlanRecord();
                finishedPlanRecord.setFinished(1);
                finishedPlanRecord.setSiteId(siteId);
                finishedPlanRecord.setAccountId(accountId);
                QueryWrapper<TpStudentPlanRecord> ew = new QueryWrapper<>(finishedPlanRecord);
                ew.in("tp_plan_id", toFinishedPlanIds);
                int finishedPlanRecordSize = (int)finishedPlanRecord.selectCount(ew);
                // 2.2.1.1 前置计划没有完成
                if (toFinishedPlanIds.size() > finishedPlanRecordSize) {
                    return CertificateGrantStatus.UN_FINISHED;
                }
                // 2.2.1.2 前置计划完成，判断计划的完成条件，发放证书
                else {
                    if (finishAllAtivityInPlanAndGrant(plan, siteId, certificateId, accountId)) {
                        return CertificateGrantStatus.SUCCESS_GET;
                    } else {
                        return CertificateGrantStatus.UN_FINISHED;
                    }
                }
            }
            // 2.2.2 如果没有前置计划，完成所有计划才算完成
            else {
                // 查询当前计划培训项目的所有计划
                List<Long> allPlanIds = tpPlanMapper.getIdsByTpId(plan.getTrainingProjectId());
                if (!CollectionUtils.isEmpty(allPlanIds)) {
                    allPlanIds.remove(planId);
                    //只有一个计划且只有证书类型的活动，需求允许直接获得证书
                    if (CollectionUtils.isEmpty(allPlanIds) && !allActivityIsOnlyCertificate(planId)) {
                        //上面已对当前计划完成情况进行了判断，若只有一个计划，则执行到此处肯定计划未完成
                        return CertificateGrantStatus.UN_FINISHED;
                    }
                    // 查询这些计划的完成记录
                    TpStudentPlanRecord toFinishedPlanRecord = new TpStudentPlanRecord();
                    toFinishedPlanRecord.setAccountId(accountId);
                    toFinishedPlanRecord.setFinished(1);
                    toFinishedPlanRecord.setSiteId(siteId);
                    toFinishedPlanRecord.setTrainingProjectId(plan.getTrainingProjectId());
                    QueryWrapper<TpStudentPlanRecord> ew = new QueryWrapper<>(toFinishedPlanRecord);
                    ew.in("tp_plan_id", allPlanIds);
                    int count = (int)toFinishedPlanRecord.selectCount(ew);
                    // 如果出了当前计划，其他计划都已经完成
                    if (count == allPlanIds.size()) {
                        if (finishAllAtivityInPlanAndGrant(plan, siteId, certificateId, accountId)) {
                            return CertificateGrantStatus.SUCCESS_GET;
                        } else {
                            return CertificateGrantStatus.UN_FINISHED;
                        }
                    } else {
                        return CertificateGrantStatus.UN_FINISHED;
                    }
                }
            }
        }
        return CertificateGrantStatus.UN_FINISHED;
    }

    /************************************************************************  PC端 获取证书
     *  ****************************************************/

    @Override
    public Constants certificatePcGrant(Map<String, Long> param) {
        CertificateGrantStatus status = this.certificateGrant(param);
        if (status.equals(CertificateGrantStatus.HAS_GOT)) {
            return Constants.TRAINING_MSG_CERTIFICATE_HAS_OBTAINED;
        } else if (status.equals(CertificateGrantStatus.SUCCESS_GET)) {
            return Constants.TRAINING_MSG_CERTIFICATE_SUCCESS_GET;
        } else {
            return Constants.TRAINING_MSG_CERTIFICATE_UNABLE_OBTAIN;
        }
    }

    @Override
    public List<Long> getcourseIdsByTrainingProjectId(Long id) {
        // TODO Auto-generated method stub
        TpPlanActivity tpa = new TpPlanActivity();
        tpa.setTrainingProjectId(id);
        //tpa.setType(0);//课程,现在又不要是课程的了，要全部活动
        tpa.setDeleted(0);//未被删除
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<TpPlanActivity>(tpa);
        List<TpPlanActivity> listTpPlanActivity = this.list(wrapper);
        List<Long> ids = null;
        if (!CollectionUtils.isEmpty(listTpPlanActivity)) {
            ids = new ArrayList<Long>();
            for (int i = 0; i < listTpPlanActivity.size(); i++) {
                ids.add(listTpPlanActivity.get(i).getRelationId());
            }
        }
        return ids;
    }

    @Override
    public List<Long> getExcCertifercourseIdsByTrainingProjectId(Long id) {
        TpPlanActivity tpa = new TpPlanActivity();
        tpa.setTrainingProjectId(id);
        tpa.setDeleted(0);//未被删除
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<TpPlanActivity>(tpa);
        wrapper.ne("type", 6);
        List<TpPlanActivity> listTpPlanActivity = this.list(wrapper);
        List<Long> ids = null;
        if (!CollectionUtils.isEmpty(listTpPlanActivity)) {
            ids = new ArrayList<Long>();
            for (int i = 0; i < listTpPlanActivity.size(); i++) {
                ids.add(listTpPlanActivity.get(i).getRelationId());
            }
        }
        return ids;
    }

    @Override
    public List<Long> getAllCourseIdByTrainingProjectId(Long id) {
        TpPlanActivity tpa = new TpPlanActivity();
        tpa.setTrainingProjectId(id);
        tpa.setType(0);//课程,现在又不要是课程的了，要全部活动
        tpa.setDeleted(0);//未被删除
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<TpPlanActivity>(tpa);
        List<TpPlanActivity> listTpPlanActivity = this.list(wrapper);
        List<Long> ids = null;
        if (!CollectionUtils.isEmpty(listTpPlanActivity)) {
            ids = new ArrayList<Long>();
            for (int i = 0; i < listTpPlanActivity.size(); i++) {
                ids.add(listTpPlanActivity.get(i).getRelationId());
            }
        }
        return ids;
    }

    @Override
    public Integer getactivityNumByTrainingProjectId(Long id) {
        TpPlanActivity tpa = new TpPlanActivity();
        tpa.setTrainingProjectId(id);
        //        tpa.setType(0);//课程,现在又不要是课程的了，要全部活动
        tpa.setDeleted(0);//未被删除
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<TpPlanActivity>(tpa);
        return (int)this.count(wrapper);
    }

    @Override
    public Integer getExcCertificateActivityNumByTpId(Long id) {
        TpPlanActivity tpa = new TpPlanActivity();
        tpa.setTrainingProjectId(id);
        tpa.setDeleted(0);//未被删除
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<TpPlanActivity>(tpa);
        wrapper.ne("type", 6);
        return (int)this.count(wrapper);
    }

    @Override
    public boolean hasFinisheAllActivity(Long tpPlanId, Long accountId) {
        List<TpPlanActivity> activities =
            this.allListByTpPlanId(tpPlanId, TpActivityType.TYPE_COURSE, TpActivityType.TYPE_ASSIGNMENT,
                TpActivityType.TYPE_RESEARCH, TpActivityType.TYPE_LINK, TpActivityType.TYPE_EXAM,
                TpActivityType.TYPE_OFFLINE_COURSE, TpActivityType.TYPE_LIVE);
        if (!CollectionUtils.isEmpty(activities)) {
            TpPlanActivity tpPlanActivity = activities.get(0);

            List<Long> relationIds = new ArrayList<>();
            activities.forEach(item -> relationIds.add(item.getRelationId()));

            List<Long> finishedRelationIds =
                tpStudentActivityRecordMapper.selectFinished(accountId, relationIds, 1, tpPlanActivity.getCompanyId(),
                    tpPlanActivity.getSiteId());

            if (!CollectionUtils.isEmpty(finishedRelationIds)) {
                Set<Long> relationSet = new HashSet<>();
                relationSet.addAll(relationIds);

                Set<Long> finishedRelationSet = new HashSet<>();
                finishedRelationSet.addAll(finishedRelationIds);
                return finishedRelationIds.containsAll(relationSet);
            }
        }
        return false;
    }

    @Override
    public List<TpPlanActivity> listTpPlanActivityByTpId(Long id) {
        // TODO Auto-generated method stub
        return this.baseMapper.listTpPlanActivityByTpId(id);
    }

    @Override
    public List<TpPlanActivityVo> getTpPlanActivitiesBySiteIds(List<Long> siteIds) {
        return tpPlanActivityMapper.selectBySiteIds(siteIds);
    }

    @Override
    public List<TpPlanActivity> getByTpPlanId(Long tpPlanId) {
        TpPlanActivity activity = new TpPlanActivity();
        activity.setTpPlanId(tpPlanId);
        activity.setDeleted(0);
        QueryWrapper wrapper = new QueryWrapper(activity);
        wrapper.orderByDesc("sort");
        wrapper.orderByAsc("create_time");
        return tpPlanActivityMapper.selectList(wrapper);
    }

    @Override
    public Boolean checkBizCanDown(Integer bizType, Long relationId) {
        Integer num = tpPlanActivityMapper.checkBizCanDown(bizType, relationId);
        if (num == null || num == 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Long> allListByAssignmentIds(List<TpPlanActivityVo> tpPlanActivityVos) {
        if (CollectionUtils.isEmpty(tpPlanActivityVos)) {
            return new ArrayList<>();
        }
        List<Long> assIds = new ArrayList<>();
        QueryWrapper<TpPlanActivity> entity = null;
        //找出 关联了作业并没有被删除的计划 并取出相关的作业id
        for (int i = 0; i < tpPlanActivityVos.size(); i++) {
            TpPlanActivity tpPlanActivity = new TpPlanActivity();
            BeanUtils.copyProperties(tpPlanActivityVos.get(i), tpPlanActivity);
            tpPlanActivity.setDeleted(0);
            entity = new QueryWrapper<>(tpPlanActivity);
            tpPlanActivity = getOne(entity);
            if (tpPlanActivity != null) {
                assIds.add(tpPlanActivity.getRelationId());
            }
        }
        return assIds;
    }

    /**
     * 课程关联项目列表
     *
     * @param courseId
     * @param page
     * @return
     */
    @Override
    public List<CourseRelateProjectVO> courseRelateProjectList(Long courseId, Page<CourseRelateProjectVO> page) {
        List<CourseRelateProjectVO> courseRelateProjectVOS =
            tpPlanActivityMapper.courseRelateProjectList(courseId, page);
        return courseRelateProjectVOS;
    }

    @Override
    public List<BaseViewRecordVO> getFinishedActivityList(BaseParamVO paramVO) {

        List<BaseViewRecordVO> voList = new ArrayList<>();
        try {

            Long companyId = paramVO.getCompanyId();
            Long siteId = paramVO.getSiteId();
            Integer relationType = paramVO.getRelationType();
            Long relationId = paramVO.getRelationId();

            return tpStudentActivityRecordMapper.selectStuActivityFinishedList(companyId, siteId, relationId,
                relationType);

        } catch (Exception e) {

            LOGGER.error("查询学员完成活动异常", e);
        }

        return voList;
    }

    @Override
    public Set<String> checkExistRelatedProject(Long id, Integer type) {
        return tpPlanActivityMapper.checkExistRelatedProject(id, type);
    }

    /**
     * 发放证书
     *
     * @param siteId
     * @param certificateId
     * @param accountId
     */
    private void doGrantCertificate(Long tpId, Long siteId, Long certificateId, Long accountId, Long tpPlanId) {
        TrainingProject project = new TrainingProject();
        project.setId(tpId);
        project = project.selectById();

        Map<String, Object> map = new HashMap<>();
        map.put("siteId", siteId);
        map.put("certificateId", certificateId);
        map.put("accountId", accountId);
        map.put("projectId", project.getId());
        map.put("projectName", project.getName());

        //证书需要以下新增字段进行逻辑判断
        map.put("trPlanId", tpPlanId);
        TpPlan tpPlan = tpPlanService.getById(tpPlanId);
        map.put("trPlanName", null == tpPlan ? "" : tpPlan.getName());
        cloudEventPublisher.publish("myCertificate", new EventWrapper<Map>(certificateId, map));
    }

    /**
     * 完成当当前计划内所有活动并发放证书
     *
     * @param plan
     * @param siteId
     * @param certificateId
     * @param accountId
     * @return 是否发放
     */
    private boolean finishAllAtivityInPlanAndGrant(TpPlan plan, Long siteId, Long certificateId, Long accountId) {
        TpPlanConditionPost planConditionPost = new TpPlanConditionPost();
        planConditionPost.setTpPlanId(plan.getId());
        planConditionPost.setDeleted(ProjectConstant.DELETED_NO);
        List<TpPlanConditionPost> posts = planConditionPost.selectList(new QueryWrapper(planConditionPost));
        // 1. 如果当前计划指定了完成条件
        if (!CollectionUtils.isEmpty(posts)) {
            int type = posts.get(0).getType();
            // 如果是指定完成活动
            if (type == ProjectConstant.TP_PLAN_CONDITION_POST_ACTIVITY_ID) {
                List<Long> toFinisheActivityRelationIds = new ArrayList<>(posts.size());
                posts.forEach((post) -> toFinisheActivityRelationIds.add(post.getTpPlanActivityRelationId()));

                TpStudentActivityRecord record = new TpStudentActivityRecord();
                record.setSiteId(siteId);
                record.setAccountId(accountId);
                record.setFinished(1);
                QueryWrapper<TpStudentActivityRecord> ew = new QueryWrapper<>(record);
                ew.in("relation_id", toFinisheActivityRelationIds);

                int finishedSize = (int)record.selectCount(ew);
                // 如果完成所有指定的活动
                if (finishedSize == posts.size()) {
                    doGrantCertificate(plan.getTrainingProjectId(), siteId, certificateId, accountId, plan.getId());
                    return true;
                }
            }
            // 否则是指定完成数
            else {
                int toFinishedSize = posts.get(0).getNum();
                // 查出所有活动id 排除证书
                List<Long> planActivityIds = tpPlanActivityMapper.getRelationIdsByTpPlanId(plan.getId());
                TpStudentActivityRecord record = new TpStudentActivityRecord();
                record.setSiteId(siteId);
                record.setAccountId(accountId);
                record.setFinished(1);
                QueryWrapper<TpStudentActivityRecord> ew = new QueryWrapper<>(record);
                ew.in("relation_id", planActivityIds);

                int finishedSize = (int)record.selectCount(ew);
                if (finishedSize >= toFinishedSize) {
                    doGrantCertificate(plan.getTrainingProjectId(), siteId, certificateId, accountId, plan.getId());
                    return true;
                }
            }
        } else {
            doGrantCertificate(plan.getTrainingProjectId(), siteId, certificateId, accountId, plan.getId());
            return true;
        }
        return false;
    }

    /**
     * 判断当前计划是否所有的活动均为证书
     *
     * @param planId
     * @return
     */
    private boolean allActivityIsOnlyCertificate(Long planId) {
        TpPlanActivity activity = new TpPlanActivity();
        activity.setTpPlanId(planId);
        activity.setDeleted(0);
        QueryWrapper wrapper = new QueryWrapper(activity);
        //不为证书的活动
        wrapper.ne("type", 6);
        Integer result = tpPlanActivityMapper.selectCount(wrapper).intValue();
        if (result > 0) {
            return false;
        }
        return true;
    }

    /**
     * 修改培训计划时，单个添加学习活动用 -- 组装学习活动
     *
     * @param context
     * @param now
     * @param activityVo
     * @return
     */
    private TpPlanActivity buildTpPlanActivity(RequestContext context, Date now, TpPlanActivitySingleVo activityVo) {
        TpPlanActivity activity = new TpPlanActivity();

        activity.setCompanyId(context.getCompanyId());
        activity.setOrgId(context.getOrgId() == null ? new Long(0) : context.getOrgId());
        activity.setSiteId(context.getSiteId());

        activity.setCreateById(context.getAccountId());
        activity.setCreateByName(context.getAccountName());
        activity.setCreateTime(now);

        activity.setId(idGenerator.generate());
        activity.setDeleted(ProjectConstant.DELETED_NO);
        activity.setName(activityVo.getName());
        activity.setAddress(activityVo.getAddress());
        if (activityVo.getType().equals(7)) {
            activity.setRelationId(activity.getId());
        } else {
            activity.setRelationId(activityVo.getRelationId());
        }
        activity.setSort(activityVo.getSort());
        activity.setTpPlanId(activityVo.getPlanId());
        activity.setTrainingProjectId(activityVo.getProjectId());
        activity.setType(activityVo.getType());
        return activity;
    }
}
