package com.yizhi.training.application.v2.service.biz;

import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.certificate.application.vo.CertificateCountVO;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.documents.application.feign.DocumentRelationClient;
import com.yizhi.documents.application.vo.documents.DocumentCountVo;
import com.yizhi.system.application.feign.CommitmentLettersClient;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.v2.constant.TrainingProjectConstant;
import com.yizhi.training.application.v2.enums.TpActivityTypeEnum;
import com.yizhi.training.application.v2.enums.TpPlanConditionPostTypeEnum;
import com.yizhi.training.application.v2.enums.TpPlanTimeConditionTypeEnum;
import com.yizhi.training.application.v2.service.*;
import com.yizhi.training.application.v2.vo.*;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import com.yizhi.util.application.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TpPlanBizService {

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private TpPlanActivityService tpPlanActivityService;

    @Autowired
    private TpPlanConditionPreService tpPlanConditionPreService;

    @Autowired
    private TpPlanConditionPostService tpPlanConditionPostService;

    @Autowired
    private TpPlanStudyTimeConditionService tpPlanStudyTimeConditionService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private DocumentRelationClient documentRelationClient;

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private CommitmentLettersClient commitmentLettersClient;

    @Transactional(rollbackFor = Exception.class)
    public TpPlanDetailVO addTpPlan(TpPlanDetailVO request) {
        // 校验
        if (!validate(request)) {
            return null;
        }
        // 构建计划基础信息
        RequestContext context = ContextHolder.get();
        TpPlan tpPlan = buildTpPlan(request, context);
        // 组装学习活动
        List<TpPlanActivity> activities = buildActivities(request, context, tpPlan);
        // 构建学习时间
        TpPlanStudyTimeCondition timeCondition = buildTpPlanStudyTimeCondition(request, context, tpPlan);
        // 构建前置条件
        List<TpPlanConditionPre> conditionPres = buildTpPlanConditionPre(request, context, tpPlan);
        // 构建完成条件
        Map<Long, TpPlanActivity> activityMap =
            activities.stream().collect(Collectors.toMap(TpPlanActivity::getRelationId, o -> o, (o1, o2) -> o1));
        List<TpPlanConditionPost> conditionPosts = buildTpPlanConditionPost(request, context, tpPlan, activityMap);
        // 入库
        boolean planRes = tpPlanService.save(tpPlan);
        if (CollectionUtils.isNotEmpty(activities)) {
            tpPlanActivityService.saveBatch(activities);
            //承诺书类型的活动构建关联关系
            for (TpPlanActivity activity : activities) {
                Integer type = activity.getType();
                if (type == null || type != 20) {
                    continue;
                }
                commitmentLettersClient.saveCommitmentLetterAssociation(1, activity.getRelationId(),
                    request.getTrainingProjectId(), tpPlan.getId(), 2);
            }
        }
        tpPlanStudyTimeConditionService.save(timeCondition);
        if (CollectionUtils.isNotEmpty(conditionPres)) {
            tpPlanConditionPreService.saveBatch(conditionPres);
        }
        if (CollectionUtils.isNotEmpty(conditionPosts)) {
            tpPlanConditionPostService.saveBatch(conditionPosts);
        }
        return getTpPlan(tpPlan.getId());
    }

    /**
     * 查询学习计划信息
     *
     * @param tpPlanId
     * @return
     */
    public TpPlanDetailVO getTpPlan(Long tpPlanId) {
        TpPlan tpPlan = tpPlanService.getById(tpPlanId);
        return buildTpPlanDetail(tpPlan);
    }

    public TpPlanDetailVO buildTpPlanDetail(TpPlan tpPlan) {
        if (tpPlan == null) {
            return null;
        }
        TpPlanDetailVO tpPlanVO = new TpPlanDetailVO();
        // 基本信息
        BeanUtils.copyProperties(tpPlan, tpPlanVO);
        // 学习活动
        List<TpPlanActivity> activities = tpPlanActivityService.getActivities(tpPlan.getId());
        List<TpPlanActivityVO> activityVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(activities)) {
            activityVOS = BeanCopyListUtil.copyListProperties(activities, TpPlanActivityVO::new);
        }
        tpPlanVO.setActivities(activityVOS);

        // 学习时间条件
        TpPlanStudyTimeCondition timeCondition = tpPlanStudyTimeConditionService.getTimeCondition(tpPlan.getId());
        TpPlanStudyTimeConditionVO timeConditionVO = new TpPlanStudyTimeConditionVO();
        if (timeCondition != null) {
            BeanUtils.copyProperties(timeCondition, timeConditionVO);
        } else if (tpPlan.getStartTime() != null && tpPlan.getEndTime() != null) {
            timeConditionVO.setConditionType(TpPlanTimeConditionTypeEnum.SPECIFIC_TIME.getCode());
            timeConditionVO.setStartTime(tpPlan.getStartTime());
            timeConditionVO.setEndTime(tpPlan.getEndTime());
            timeConditionVO.setTrainingProjectId(tpPlan.getTrainingProjectId());
            timeConditionVO.setTpPlanId(tpPlan.getId());
        } else {
            timeConditionVO.setConditionType(TpPlanTimeConditionTypeEnum.NO_CONDITION.getCode());
            timeConditionVO.setTrainingProjectId(tpPlan.getTrainingProjectId());
            timeConditionVO.setTpPlanId(tpPlan.getId());
        }
        tpPlanVO.setStudyTimeCondition(timeConditionVO);

        // 前置条件
        List<TpPlan> prePlans = tpPlanConditionPreService.getPrePlans(tpPlan.getId());
        TpPlanConditionPreVO preVO = null;
        if (CollectionUtils.isNotEmpty(prePlans)) {
            preVO = new TpPlanConditionPreVO();
            List<TpPlanVO> list = BeanCopyListUtil.copyListProperties(prePlans, TpPlanVO::new);
            preVO.setPrePlans(list);
            preVO.setFinishCount(tpPlanConditionPreService.getFinishCount(tpPlan.getId()));
        }
        tpPlanVO.setConditionPre(preVO);

        // 完成条件
        List<TpPlanConditionPost> conditionPosts = tpPlanConditionPostService.getConditionPosts(tpPlan.getId());
        // 如果没有，则默认表示：完成全部活动
        TpPlanConditionPostVO postVO = new TpPlanConditionPostVO();
        postVO.setConditionPostType(TpPlanConditionPostTypeEnum.ALL_ACTIVITY.getCode());
        if (CollectionUtils.isNotEmpty(conditionPosts)) {
            postVO = new TpPlanConditionPostVO();
            List<Long> activityIds = new ArrayList<>();
            for (TpPlanConditionPost post : conditionPosts) {
                if (TpPlanConditionPostTypeEnum.SPECIFIC_COUNT.getCode().equals(post.getType())) {
                    // 指定数量
                    postVO.setCompleteCount(post.getNum());
                } else if (TpPlanConditionPostTypeEnum.SPECIFIC_ACTIVITIES.getCode().equals(post.getType())) {
                    activityIds.add(post.getTpPlanActivityId());
                }
            }
            if (CollectionUtils.isNotEmpty(activityIds)) {
                List<TpPlanActivity> postActivities = tpPlanActivityService.listByIds(activityIds);
                List<TpPlanActivityVO> postActivityVOs =
                    BeanCopyListUtil.copyListProperties(postActivities, TpPlanActivityVO::new);
                postVO.setActivities(postActivityVOs);
            }

            boolean numFlag = postVO.getCompleteCount() != null && postVO.getCompleteCount() > 0;
            boolean specificFlag = CollectionUtils.isNotEmpty(activityIds);
            postVO.setConditionPostType(
                numFlag ? (specificFlag ? TpPlanConditionPostTypeEnum.COUNT_AND_SPECIFIC.getCode()
                    : TpPlanConditionPostTypeEnum.SPECIFIC_COUNT.getCode())
                    : (specificFlag ? TpPlanConditionPostTypeEnum.SPECIFIC_ACTIVITIES.getCode()
                        : TpPlanConditionPostTypeEnum.ALL_ACTIVITY.getCode()));
        }
        tpPlanVO.setConditionPost(postVO);

        return tpPlanVO;
    }

    /**
     * 更新学习计划
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTpPlan(TpPlanDetailVO request) {
        // 校验
        if (!validate(request)) {
            return false;
        }
        // 构建计划基础信息
        TpPlan tpPlan = new TpPlan();
        BeanUtils.copyProperties(request, tpPlan);

        RequestContext context = ContextHolder.get();
        // 构建学习时间
        TpPlanStudyTimeCondition timeCondition = buildTpPlanStudyTimeCondition(request, context, tpPlan);
        // 构建前置条件
        List<TpPlanConditionPre> conditionPres = buildTpPlanConditionPre(request, context, tpPlan);
        // 构建完成条件
        // PS:应要求，学习计划中学习活动的编辑不整页保存，故在修改学习计划信息时，不处理学习活动
        List<TpPlanActivity> activities = tpPlanActivityService.getActivities(tpPlan.getId());
        Map<Long, TpPlanActivity> activityMap =
            activities.stream().collect(Collectors.toMap(TpPlanActivity::getRelationId, o -> o, (o1, o2) -> o1));
        List<TpPlanConditionPost> conditionPosts = buildTpPlanConditionPost(request, context, tpPlan, activityMap);
        // 更新学习计划基本信息
        boolean planRes = tpPlanService.updateById(tpPlan);
        // 更新时间条件
        tpPlanStudyTimeConditionService.updateTimeCondition(timeCondition);
        // 先删除前置条件，再新增
        tpPlanConditionPreService.removeByPlanId(tpPlan.getId());
        if (CollectionUtils.isNotEmpty(conditionPres)) {
            tpPlanConditionPreService.saveBatch(conditionPres);
        }
        // 先删除完成条件，再新增
        tpPlanConditionPostService.removeByPlanId(tpPlan.getId());
        if (CollectionUtils.isNotEmpty(conditionPosts)) {
            tpPlanConditionPostService.saveBatch(conditionPosts);
        }
        return planRes;
    }

    /**
     * 删除学习计划
     *
     * @param tpPlanIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBatchTpPlan(List<Long> tpPlanIds) {
        // 删除学习计划
        boolean res = tpPlanService.deleteBatchById(tpPlanIds);
        // 删除时间条件
        tpPlanStudyTimeConditionService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除学习计划前置条件
        tpPlanConditionPreService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除学习计划完成条件
        tpPlanConditionPostService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除证书绑定
        certificateClient.deleteBatchRelation(tpPlanIds);
        // 删除活动
        tpPlanActivityService.deleteBatchByTpPlan(tpPlanIds);
        return res;
    }

    /**
     * 查询学习计划列表（详细）
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @return
     */
    public List<TpPlanDetailVO> getTpPlanDetailList(Long trainingProjectId, Long directoryItemId) {
        List<TpPlan> plans = tpPlanService.getTpPlans(trainingProjectId, directoryItemId);
        if (CollectionUtils.isEmpty(plans)) {
            return Collections.emptyList();
        }

        List<Long> tpPlanIds = plans.stream().map(TpPlan::getId).collect(Collectors.toList());
        Map<Long, Integer> docCountMap = new HashMap<>();
        Map<Long, Integer> certificateCountMap = new HashMap<>();
        try {
            List<DocumentCountVo> docCountList = documentRelationClient.getRelationCount(tpPlanIds);
            docCountMap = docCountList.stream()
                .collect(Collectors.toMap(DocumentCountVo::getRelationId, DocumentCountVo::getCount, (o1, o2) -> o2));

            List<CertificateCountVO> certificateCountList = certificateClient.getCertificateCountBatch(tpPlanIds);
            certificateCountMap = certificateCountList.stream()
                .collect(Collectors.toMap(CertificateCountVO::getBizId, CertificateCountVO::getCount, (o1, o2) -> o2));
        } catch (Exception e) {
            log.error("远程调用查询资料数和证书数异常", e);
        }

        List<TpPlanDetailVO> planDetailVOS = new ArrayList<>();
        for (TpPlan plan : plans) {
            TpPlanDetailVO tpPlanDetail = buildTpPlanDetail(plan);
            tpPlanDetail.setDocumentCount(docCountMap.getOrDefault(plan.getId(), 0));
            tpPlanDetail.setCertificateCount(certificateCountMap.getOrDefault(plan.getId(), 0));
            planDetailVOS.add(tpPlanDetail);
        }

        return planDetailVOS;
    }

    /**
     * 查询学习计划列表（基本信息）
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @return
     */
    public List<TpPlanVO> getTpPlanSimpleList(Long trainingProjectId, Long directoryItemId) {
        List<TpPlan> plans = tpPlanService.getTpPlans(trainingProjectId, directoryItemId);
        return BeanCopyListUtil.copyListProperties(plans, TpPlanVO::new);
    }

    public TpPlanVO getTpPlanSimple(Long tpPlanId) {
        TpPlan tpPlan = tpPlanService.getById(tpPlanId);
        if (tpPlan == null) {
            return null;
        }
        TpPlanVO tpPlanVO = new TpPlanVO();
        BeanUtils.copyProperties(tpPlan, tpPlanVO);
        return tpPlanVO;
    }

    /**
     * 更新学习单元排序
     *
     * @param trainingProjectId
     * @param moveId
     * @param preId
     * @return
     */
    public Boolean updateTpPlanSort(Long trainingProjectId, Long directoryItemId, Long moveId, Long preId) {
        Integer preSort = 0;
        if (preId != null && preId > 0) {
            TpPlan prePlan = tpPlanService.getById(preId);
            preSort = prePlan == null ? 0 : prePlan.getSort();
        }
        tpPlanService.addTpSort(trainingProjectId, directoryItemId, preSort + 1);

        TpPlan updatePlan = new TpPlan();
        updatePlan.setId(moveId);
        updatePlan.setSort(preSort + 1);
        return tpPlanService.updateById(updatePlan);
    }

    public String getTimeConditionStr(Long tpPlanId) {
        TpPlanStudyTimeCondition condition = tpPlanStudyTimeConditionService.getTimeCondition(tpPlanId);
        if (condition == null) {
            TpPlan tpPlan = tpPlanService.getById(tpPlanId);
            if (tpPlan == null || tpPlan.getStartTime() == null || tpPlan.getEndTime() == null) {
                return "无时间限制";
            }
            return DateUtil.toSeconds(tpPlan.getStartTime()) + "~" + DateUtil.toSeconds(tpPlan.getEndTime());
        }
        if (TpPlanTimeConditionTypeEnum.NO_CONDITION.getCode().equals(condition.getConditionType())) {
            return "无时间限制";
        }
        if (TpPlanTimeConditionTypeEnum.SPECIFIC_TIME.getCode().equals(condition.getConditionType())) {
            return DateUtil.toSeconds(condition.getStartTime()) + "~" + DateUtil.toSeconds(condition.getEndTime());
        }
        if (TpPlanTimeConditionTypeEnum.AFTER_PRE_PLAN.getCode().equals(condition.getConditionType())) {
            return "完成前置单元后" + condition.getAfterPrePlanDate() + "天";
        }
        if (TpPlanTimeConditionTypeEnum.AFTER_START.getCode().equals(condition.getConditionType())) {
            return "开始学习后" + condition.getAfterStartDate() + "天";
        }
        return "无时间限制";
    }

    /**
     * 入参校验 项目时间和学习计划时间校验
     *
     * @param request
     * @return
     */
    private Boolean validate(TpPlanDetailVO request) {
        // 项目时间校验
        if (request.getStudyTimeCondition() == null) {
            return false;
        }
        // 校验项目是否存在
        TrainingProject tp = trainingProjectService.getById(request.getTrainingProjectId());
        if (tp == null) {
            return false;
        }
        TpPlanStudyTimeConditionVO studyTimeCondition = request.getStudyTimeCondition();
        if (request.getStudyTimeCondition() != null && TpPlanTimeConditionTypeEnum.SPECIFIC_TIME.getCode()
            .equals(studyTimeCondition.getConditionType())) {
            // 如果是指定时间，需要校验是否超出了项目时间
            if (studyTimeCondition.getStartTime() == null || studyTimeCondition.getEndTime() == null) {
                return false;
            }
            if (tp.getStartTime() != null && tp.getStartTime().getTime() > studyTimeCondition.getStartTime()
                .getTime()) {
                return false;
            }
            if (tp.getEndTime() != null && tp.getEndTime().getTime() < studyTimeCondition.getEndTime().getTime()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 组装学习计划基本信息
     *
     * @param request
     * @param context
     * @return
     */
    private TpPlan buildTpPlan(TpPlanDetailVO request, RequestContext context) {
        TpPlan plan = new TpPlan();
        // 基本数据
        plan.setId(idGenerator.generate());
        plan.setCompanyId(context.getCompanyId());
        plan.setSiteId(context.getSiteId());
        plan.setOrgId(context.getOrgId());

        plan.setTrainingProjectId(request.getTrainingProjectId());
        plan.setDirectoryItemId(request.getDirectoryItemId());
        plan.setName(request.getName());

        plan.setEnableStudyInSequence(request.getEnableStudyInSequence());
        plan.setEnableContinueStudy(request.getEnableContinueStudy());

        // 排序
        Integer maxSort = tpPlanService.getMaxSort(request.getTrainingProjectId(), request.getDirectoryItemId());
        plan.setSort(maxSort == null ? 1 : maxSort + 1);
        return plan;
    }

    /**
     * 组装学习活动列表
     *
     * @param request
     * @param context
     * @param tpPlan
     * @return
     */
    private List<TpPlanActivity> buildActivities(TpPlanDetailVO request, RequestContext context, TpPlan tpPlan) {
        List<TpPlanActivityVO> activityVOs = request.getActivities();
        List<TpPlanActivity> activities = new ArrayList<>();
        for (TpPlanActivityVO vo : activityVOs) {
            TpPlanActivity activity = new TpPlanActivity();
            activity.setId(idGenerator.generate());
            activity.setCompanyId(context.getCompanyId());
            activity.setSiteId(context.getSiteId());
            activity.setOrgId(context.getOrgId() == null ? 0 : context.getOrgId());
            activity.setTrainingProjectId(request.getTrainingProjectId());
            activity.setTpPlanId(tpPlan.getId());

            activity.setCreateById(context.getAccountId());
            activity.setCreateByName(context.getAccountName());

            activity.setType(vo.getType());
            activity.setName(vo.getName());
            activity.setCustomizeName(vo.getCustomizeName());
            activity.setRelationId(vo.getRelationId());
            activity.setLogoUrl(vo.getLogoUrl());
            if (TpActivityTypeEnum.LINK.getCode().equals(activity.getType())) {
                // 外链
                activity.setAddress(vo.getAddress());
                activity.setRelationId(activity.getId());
            }
            activity.setSort(vo.getSort() == null ? TrainingProjectConstant.DEFAULT_SORT : vo.getSort());

            activities.add(activity);
        }
        return activities;
    }

    /**
     * 构建学习计划学习时间条件
     *
     * @param tpPlanVO
     * @param context
     * @param tpPlan
     * @return
     */
    private TpPlanStudyTimeCondition buildTpPlanStudyTimeCondition(TpPlanDetailVO tpPlanVO, RequestContext context,
        TpPlan tpPlan) {
        TpPlanStudyTimeCondition condition = new TpPlanStudyTimeCondition();
        condition.setId(idGenerator.generate());
        condition.setCompanyId(context.getCompanyId());
        condition.setSiteId(context.getSiteId());
        condition.setTrainingProjectId(tpPlanVO.getTrainingProjectId());
        condition.setTpPlanId(tpPlan.getId());

        TpPlanStudyTimeConditionVO vo = tpPlanVO.getStudyTimeCondition();
        if (vo == null) {
            condition.setConditionType(TpPlanTimeConditionTypeEnum.NO_CONDITION.getCode());
            return condition;
        }
        condition.setConditionType(vo.getConditionType());
        condition.setStartTime(vo.getStartTime());
        condition.setEndTime(vo.getEndTime());
        condition.setAfterStartDate(vo.getAfterStartDate());
        condition.setAfterPrePlanDate(vo.getAfterPrePlanDate());

        // 兼容旧逻辑
        tpPlan.setStartTime(vo.getStartTime());
        tpPlan.setEndTime(vo.getEndTime());
        return condition;
    }

    /**
     * 组装学习计划前置条件
     *
     * @param tpPlanVO
     * @param tpPlan
     * @return
     */
    private List<TpPlanConditionPre> buildTpPlanConditionPre(TpPlanDetailVO tpPlanVO, RequestContext context,
        TpPlan tpPlan) {
        TpPlanConditionPreVO preCondition = tpPlanVO.getConditionPre();
        if (preCondition == null || CollectionUtils.isEmpty(preCondition.getPrePlans())) {
            return Collections.emptyList();
        }
        List<TpPlanConditionPre> list = new ArrayList<>();
        for (TpPlanVO prePlan : preCondition.getPrePlans()) {
            TpPlanConditionPre conditionPre = new TpPlanConditionPre();
            conditionPre.setId(idGenerator.generate());
            conditionPre.setTrainingProjectId(tpPlanVO.getTrainingProjectId());
            conditionPre.setPlanId(tpPlan.getId());
            conditionPre.setPrePlanId(prePlan.getId());
            conditionPre.setFinishCount(preCondition.getFinishCount());
            conditionPre.setCompanyId(context.getCompanyId());
            conditionPre.setSiteId(context.getSiteId());
            list.add(conditionPre);
        }
        return list;
    }

    /**
     * 组装学习计划完成条件
     *
     * @param tpPlanVO
     * @param tpPlan
     * @param activityMap
     * @return
     */
    private List<TpPlanConditionPost> buildTpPlanConditionPost(TpPlanDetailVO tpPlanVO, RequestContext context,
        TpPlan tpPlan, Map<Long, TpPlanActivity> activityMap) {
        List<TpPlanConditionPost> list = new ArrayList<>();
        TpPlanConditionPostVO postCondition = tpPlanVO.getConditionPost();
        if (postCondition == null) {
            // 默认完成全部活动
            return Collections.emptyList();
        }
        if (postCondition.getCompleteCount() != null && postCondition.getCompleteCount() > 0) {
            TpPlanConditionPost conditionPost = new TpPlanConditionPost();
            conditionPost.setId(idGenerator.generate());
            conditionPost.setTrainingProjectId(tpPlanVO.getTrainingProjectId());
            conditionPost.setTpPlanId(tpPlan.getId());
            conditionPost.setType(TpPlanConditionPostTypeEnum.SPECIFIC_COUNT.getCode());
            conditionPost.setNum(postCondition.getCompleteCount());
            conditionPost.setCompanyId(context.getCompanyId());
            conditionPost.setSiteId(context.getSiteId());
            list.add(conditionPost);
        }

        if (CollectionUtils.isNotEmpty(postCondition.getActivities())) {
            for (TpPlanActivityVO vo : postCondition.getActivities()) {
                if (!activityMap.containsKey(vo.getRelationId())) {
                    continue;
                }
                TpPlanConditionPost conditionPost = new TpPlanConditionPost();
                conditionPost.setId(idGenerator.generate());
                conditionPost.setTrainingProjectId(tpPlanVO.getTrainingProjectId());
                conditionPost.setTpPlanId(tpPlan.getId());
                conditionPost.setType(TpPlanConditionPostTypeEnum.SPECIFIC_ACTIVITIES.getCode());
                conditionPost.setTpPlanActivityRelationId(vo.getRelationId());
                conditionPost.setTpPlanActivityId(activityMap.get(vo.getRelationId()).getId());
                conditionPost.setCompanyId(context.getCompanyId());
                conditionPost.setSiteId(context.getSiteId());
                list.add(conditionPost);
            }
        }
        return list;
    }
}
