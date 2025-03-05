package com.yizhi.training.application.v2.service.biz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.assignment.application.feign.TpAssignmentClient;
import com.yizhi.assignment.application.vo.apivo.ApiAssignmentDetailsVo;
import com.yizhi.certificate.application.enums.CertificateEnum;
import com.yizhi.certificate.application.feign.CertificateApplyClient;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.certificate.application.feign.CertificateUserFeignClients;
import com.yizhi.certificate.application.feign.TpCertificateClient;
import com.yizhi.certificate.application.vo.CertificateStrategyVO;
import com.yizhi.certificate.application.vo.IssueCertificateParamVO;
import com.yizhi.certificate.application.vo.domain.CertificateApplyVO;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.core.application.publish.CloudEventPublisher;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.documents.application.enums.RelationType;
import com.yizhi.documents.application.feign.DocumentClient;
import com.yizhi.documents.application.vo.documents.DocumentRelationVo;
import com.yizhi.exam.application.feign.ExamClient;
import com.yizhi.exam.application.vo.MyExamVO;
import com.yizhi.forum.application.feign.student.StudentPostsClient;
import com.yizhi.forum.application.vo.forum.PostsManageVo;
import com.yizhi.forum.application.vo.forum.PostsStudentVo;
import com.yizhi.point.application.feign.PointRedisFeignClients;
import com.yizhi.point.application.vo.PointParamVO;
import com.yizhi.site.application.enums.FunctionTypeCode;
import com.yizhi.site.application.feign.api.FunctionDisplayConfigApiClients;
import com.yizhi.site.application.vo.domain.FunctionDisplayConfigVo;
import com.yizhi.system.application.constant.MedalMessageConstants;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.TpStudentActivityRecordMapper;
import com.yizhi.training.application.mapper.TpViewRecordMapper;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpStudentPlanRecordService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.TpViewRecordService;
import com.yizhi.training.application.service.impl.TpContentStudentStatusServiceUsing;
import com.yizhi.training.application.v2.enums.TpDirectoryTypeEnum;
import com.yizhi.training.application.v2.enums.TpStatusEnum;
import com.yizhi.training.application.v2.service.*;
import com.yizhi.training.application.v2.vo.TpIntroduceBaseVO;
import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.training.application.v2.vo.TpStudyDetailsVO;
import com.yizhi.training.application.v2.vo.request.ProjectJudgeAO;
import com.yizhi.training.application.v2.vo.study.*;
import com.yizhi.util.application.constant.TpActivityType;
import com.yizhi.util.application.event.TrainingProjectEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TpStudyBizService implements ITpStudyBizService {

    @Autowired
    TpStudyClickLogService tpStudyClickLogService;

    @Autowired
    IdGenerator idGenerator;

    @Autowired
    TpStudyDirectoryService tpStudyDirectoryService;

    @Autowired
    CloudEventPublisher cloudEventPublisher;

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private ITpPlanActivityService activityService;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private ITpStudentProjectRecordService tpStudentProjectRecordService;

    @Autowired
    private ITpStudentPlanRecordService tpStudentPlanRecordService;

    @Autowired
    private TpCertificateClient tpCertificateClient;

    @Autowired
    private CertificateApplyClient certificateApplyClient;

    @Autowired
    private TpHeadTeacherService tpHeadTeacherService;

    @Autowired
    private TpIntroduceBizServices tpIntroduceBizServices;

    @Autowired
    private TpRichTextService tpRichTextService;

    @Autowired
    private TpAnnouncementBizService tpAnnouncementBizService;

    @Autowired
    private TpPlanActivityBizService tpPlanActivityBizService;

    @Autowired
    private TpForumBizService tpForumBizService;

    @Autowired
    private StudentPostsClient studentPostsClient;

    @Autowired
    private TpAssignmentClient tpAssignmentClient;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private TpPlanConditionPreService tpPlanConditionPreService;

    @Autowired
    private ITpStudentPlanRecordService planRecordService;

    @Autowired
    private TpStudentActivityRecordMapper activityRecordMapper;

    @Autowired
    private TpContentStudentStatusServiceUsing tpContentStudentStatusServiceUsing;

    @Autowired
    private DocumentClient documentClient;

    @Autowired
    private TpPlanStudyTimeConditionService conditionService;

    @Autowired
    private TpPlanConditionPostService tpPlanConditionPostService;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Autowired
    private TpViewRecordService tpViewRecordService;

    @Autowired
    private TpConditionPostService tpConditionPostService;

    @Autowired
    private CertificateUserFeignClients certificateUserFeignClients;

    @Autowired
    private FunctionDisplayConfigApiClients configApiClients;

    @Autowired
    private PointRedisFeignClients pointRedisFeignClients;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private TpCommentService tpCommentService;

    @Autowired
    private TaskExecutor taskExecutor;

    /**
     * 设置计划结束时间、学习时间描述、锁定状态
     *
     * @param plan
     * @param accountId
     * @param siteId
     */
    public void setPlanEndTimeAndLockAndMsg(TpStudyPlanVO plan, Long accountId, Long siteId) {

    }

    public void startStudySaveLog(Long tpId) {
        RequestContext context = ContextHolder.get();
        TpViewRecord record = new TpViewRecord();
        record.setAccountId(context.getAccountId());
        record.setId(idGenerator.generate());
        record.setTime(new Date());
        record.setTrainingProjectId(tpId);
        record.setCompanyId(context.getCompanyId());
        record.setOrgId(context.getOrgId());
        record.setSiteId(context.getSiteId());
        tpViewRecordMapper.insert(record);
    }

    public List<TpIntroduceBaseVO> getStudyDirect(Long tpId) {
        List<TpStudyDirectory> studyDirectory = tpStudyDirectoryService.getStudyDirectory(tpId);
        if (CollectionUtils.isEmpty(studyDirectory)) {
            return null;
        }
        List<TpIntroduceBaseVO> vos = new ArrayList<>();
        studyDirectory.stream().forEach(it -> {
            //            if (it.getItemType().equals(TpDirectoryItemTypeEnum.COMMENT.getCode())) {
            //                // 判断下当前项目中是否有关联评论，如果没有关联评论，则不返回该tab栏
            //                Integer tpCommentCount = tpCommentService.getTpCommentCount(tpId, 0);
            //                if (tpCommentCount == null || tpCommentCount < 1) {
            //                    return;
            //                }
            //            }
            TpIntroduceBaseVO vo = new TpIntroduceBaseVO();
            vo.setSort(it.getSort());
            vo.setTitle(it.getItemName());
            vo.setItemType(it.getItemType());
            vo.setItemId(it.getId());
            vos.add(vo);
        });
        return vos;
    }

    @Override
    public TpStudyDetailsVO getTpDetails(Long tpId) {
        RequestContext requestContext = ContextHolder.get();
        TpStudyDetailsVO vo = new TpStudyDetailsVO();
        TrainingProject trainingProject = trainingProjectService.getById(tpId);

        vo.setShowPersonTotal(trainingProject.getEnableStatistics() == 1);
        if (trainingProject.getEnableHeadTeacher() == 1 && tpHeadTeacherService.checkTeacher(tpId,
            ContextHolder.get().getAccountId())) {
            vo.setShowTotal(true);
        }

        vo.setTpId(tpId);
        vo.setTpName(trainingProject.getName());
        vo.setLogo(trainingProject.getLogoImg());
        vo.setTpStartAt(trainingProject.getStartTime());
        vo.setTpEndAt(trainingProject.getEndTime());
        vo.setPointCount(trainingProject.getPoint());

        Integer certificateCount = getAllCertificateCount(tpId);
        vo.setCertificateCount(certificateCount);

        Integer activityCount = activityService.getExcCertificateActivityNumByTpId(tpId);
        vo.setActivityCount(activityCount);

        String studyHour = getStudyHour(tpId);
        vo.setStudyHourCount(studyHour);

        vo.setShowSign(trainingProject.getEnableSign() == 1);
        vo.setTpAiUrl(trainingProject.getTpAiUrl());
        vo.setTpAiOpen(trainingProject.getTpAiOpen());

        CertificateStrategyVO relationCertificate =
            certificateClient.getRelationCertificate(tpId, CertificateEnum.BIZ_TYPE_TRAINING.getCode());
        if (relationCertificate != null && relationCertificate.getIssueStrategy() != null) {
            vo.setShowTpCertificate(true);
            vo.setCertificateApplyStatus(relationCertificate.getIssueStrategy() == 1);
            vo.setCertificateAuditStatus(
                getTpcertificateStatus(tpId, requestContext.getAccountId(), CertificateEnum.BIZ_TYPE_TRAINING));
        }

        // 项目状态
        TpStudentProjectRecord tpStudentProjectRecord =
            tpStudentProjectRecordService.getTpStudentProjectRecord(requestContext.getAccountId(), tpId);
        vo.setFinishedTp(tpStudentProjectRecord != null && tpStudentProjectRecord.getFinished() == 1);
        //设置系统时间时间戳
        vo.setSystemTime(System.currentTimeMillis());
        return vo;
    }

    @Override
    public TpStudyIntroduceVO getIntroduceDetails(Long tpId) {
        TpStudyIntroduceVO vo = new TpStudyIntroduceVO();
        TrainingProject trainingProject = trainingProjectService.getById(tpId);
        vo.setContent(trainingProject.getDescription());

        // TODO 数据统计先关闭
        //        vo.setShowPersonTotal(trainingProject.getEnableStatistics() == 1);
        //        if (trainingProject.getEnableHeadTeacher() == 1 && tpHeadTeacherService.checkTeacher(tpId,
        //        ContextHolder.get().getAccountId())) {
        //            vo.setShowTotal(true);
        //        }
        vo.setLecturers(tpIntroduceBizServices.getLecturer(tpId));
        return vo;
    }

    @Override
    public String getHtmlDetails(Long tpId, Long itemId) {
        TpRichText richText = tpRichTextService.getRichText(tpId, TpDirectoryTypeEnum.STUDY_PAGE.getCode(), itemId);
        return richText.getContent();
    }

    @Override
    public List<TpStudyPlanVO<TpStudyExamVO>> getExamAndAssignmentDetails(Long tpId) {
        // 作业和考试
        List<TpPlanVO> examAndAssignment = tpPlanActivityBizService.getExamAndAssignment(tpId);
        if (CollectionUtils.isEmpty(examAndAssignment)) {
            return null;
        }
        Map<Long, List<TpPlanActivityVO>> examMap = new HashMap<>();
        Map<Long, List<TpPlanActivityVO>> assignmentMap = new HashMap<>();

        RequestContext requestContext = ContextHolder.get();
        Long accountId = requestContext.getAccountId();
        Long siteId = requestContext.getSiteId();

        List<TpStudyPlanVO<TpStudyExamVO>> allPlan = new ArrayList<>();

        examAndAssignment.stream().filter(it -> !CollectionUtils.isEmpty(it.getActivities())).forEach(it -> {
            TpStudyPlanVO<TpStudyExamVO> studyPlan = new TpStudyPlanVO<>();
            studyPlan.setName(it.getName());
            studyPlan.setId(it.getId());
            // 设置是否可学
            TpPlan tpPlan = tpPlanService.getById(it.getId());
            // 到期是否可继续续
            studyPlan.setContinueStudy(tpPlan.getEnableContinueStudy() == 1);
            studyPlan.setSequenceStudy(tpPlan.getEnableStudyInSequence() == 1);
            //            studyPlan.setStartTime(tpPlan.getStartTime());
            //            studyPlan.setEndTime(tpPlan.getEndTime());

            // 设置解锁状态
            Boolean lockStatus = getPlanLockStatus(accountId, siteId, it.getId());
            studyPlan.setLockStatus(lockStatus);
            studyPlan.setStudyTime(getStudyTimeStr(tpPlan, studyPlan));
            allPlan.add(studyPlan);
            // 获取每个计划中的考试和作业
            setExamIdAndAssignment(examMap, assignmentMap, it);

        });

        // 设置计划中的活动  作业和考试详情
        allPlan.stream().forEach(plan -> {
            List<TpPlanActivityVO> examIds = examMap.get(plan.getId());
            List<TpPlanActivityVO> assignentIds = assignmentMap.get(plan.getId());
            List<TpStudyExamVO> activityList = new ArrayList<>();

            List<TpStudyExamVO> examByPlan = getExamByPlan(examIds);
            List<TpStudyExamVO> assignmentByPlan = getAssignmentByPlan(assignentIds);

            if (!CollectionUtils.isEmpty(examByPlan)) {
                activityList.addAll(examByPlan);
            }
            if (!CollectionUtils.isEmpty(assignmentByPlan)) {
                activityList.addAll(assignmentByPlan);
            }
            plan.setActivityList(activityList);
        });

        allPlan.stream().filter(it -> it.getSequenceStudy()).forEach(it -> {
            List<TpStudyExamVO> examAndAss = it.getActivityList();
            examAndAss.forEach(exam -> {
                // 当计划设为连续学习时。设置上一个活动的完成情况
                List<TpPlanActivityVO> tpPlanActivities = tpPlanActivityBizService.getTpPlanActivities(it.getId());
                TpPlanActivityVO currActivity =
                    tpPlanActivities.stream().filter(activity -> activity.getId().equals(exam.getActivityId()))
                        .findFirst().get();
                Integer index = tpPlanActivities.indexOf(currActivity);
                if (index <= 0) {
                    exam.setPreActivityFinished(true);
                } else {
                    TpPlanActivityVO tpPlanActivityVO = tpPlanActivities.get(index - 1);
                    Integer finishedCount = activityRecordMapper.getFinishedCountByRelationIds(
                        CollUtil.toList(tpPlanActivityVO.getRelationId()), ContextHolder.get().getAccountId(),
                        ContextHolder.get().getSiteId());
                    exam.setPreActivityFinished(finishedCount > 0);
                }
            });
        });

        // 设置为和查询到的顺序一样。
        allPlan.stream().forEach(it -> {
            List<TpStudyExamVO> planExamAndAssig = it.getActivityList();
            List<TpStudyExamVO> sortExam = new ArrayList<>();
            TpPlanVO tpPlanVO =
                examAndAssignment.stream().filter(plan -> plan.getId().equals(it.getId())).findFirst().get();
            List<TpPlanActivityVO> activitiePlan = tpPlanVO.getActivities();
            activitiePlan.stream().forEach(activity -> {
                TpStudyExamVO tpStudyExamVO =
                    planExamAndAssig.stream().filter(exam -> exam.getActivityId().equals(activity.getId())).findFirst()
                        .get();
                sortExam.add(tpStudyExamVO);
            });
            it.setActivityList(sortExam);
        });

        return allPlan;
    }

    @Override
    public List<TpStudyForumVO> getTpForumDetails(Long tpId, Integer forumType) {
        List<Long> forumIds = getAllForumIds(tpId, forumType);
        if (CollectionUtils.isEmpty(forumIds)) {
            return null;
        }
        //
        Map<Long, PostsStudentVo> postMap = studentPostsClient.getMapByIds(forumIds);
        Collection<PostsStudentVo> values = postMap.values();
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        List<TpStudyForumVO> vos = new ArrayList<>();

        // 帖子Id，计划名
        Map<Long, String> forumAndPlanName = getForumAndPlanName(tpId);
        values.stream().forEach(it -> {
            TpStudyForumVO vo = new TpStudyForumVO();
            vo.setForumId(it.getId());
            vo.setTitle(it.getTitle());
            vo.setContent(it.getContent());
            vo.setReadNum(it.getReadNum());
            vo.setThumbsNum(it.getThumbsNum());
            vo.setCommentTime(it.getCommentTime());
            if (CollUtil.isNotEmpty(forumAndPlanName) && forumAndPlanName.get(it.getId()) != null) {
                vo.setPlanName(forumAndPlanName.get(it.getId()));
            }
            vos.add(vo);
        });
        return vos;
    }

    /**
     * @param tpId
     * @param itemId
     * @return
     */
    @Override
    public List<TpStudyPlanVO<TpStudyActivityVO>> getContentDetails(Long tpId, Long itemId) {
        List<TpPlan> tpPlans = tpPlanService.getTpPlans(tpId, itemId);
        if (CollectionUtils.isEmpty(tpPlans)) {
            return null;
        }
        TrainingProject trainingProject = trainingProjectService.getById(tpId);

        // 设置活动的完成及计划的完成
        List<TpStudyPlanVO<TpStudyActivityVO>> tpStudyPlanVOS =
            tpContentStudentStatusServiceUsing.buildPlanActivityV2(trainingProject, ContextHolder.get().getAccountId(),
                tpPlans);

        // 查询每个计划的最后学习时间
        RequestContext context = ContextHolder.get();
        Long lastStudyPlanId = tpPlanService.getMaxStudyTimePlanId(context.getAccountId(), context.getSiteId(), tpId);

        // 设置计划的基础信息
        tpStudyPlanVOS = buildPlanDetails(tpPlans, tpStudyPlanVOS, lastStudyPlanId);

        if (trainingProject.getEndTime() != null && trainingProject.getEndTime().getTime() <= new Date().getTime()) {
            // 项目已过期，不计算完成状态
            return tpStudyPlanVOS;
        }

        if (TpStatusEnum.DRAFT.getCode().equals(trainingProject.getStatus())) {
            // 草稿不完成
            return tpStudyPlanVOS;
        }

        // 判断项目的完成状态
        TpStudentProjectRecord tpStudentProjectRecord =
            tpStudentProjectRecordService.getTpStudentProjectRecord(context.getAccountId(), tpId);
        if (tpStudentProjectRecord != null && tpStudentProjectRecord.getFinished() == 1) {
            // 已完成
            return tpStudyPlanVOS;
        } else {
            // 本次查询导致完成项目完成
            Boolean tpFinishedStatus = getTpFinishedStatus(trainingProject, context.getAccountId());

            // 次字段为后来添加，不想动目前的数据结构（前端已对接。）所以把字段放在第一个对象中
            tpStudyPlanVOS.get(0).setTpFinished(tpFinishedStatus);
            // 项目未完成 -> 完成
            if (tpFinishedStatus) {
                tpFirstFinished(trainingProject, context.getAccountId());
            }
        }
        return tpStudyPlanVOS;
    }

    @Override
    public void applyCeitificate(Long relationId, Integer relationType) {
        RequestContext context = ContextHolder.get();

        Integer bizType = 0;
        CertificateEnum retionType = CertificateEnum.BIZ_TYPE_TRAINING;
        if (relationType == 1) {
            bizType = CertificateEnum.BIZ_TYPE_TP_PLAN.getCode();
            retionType = CertificateEnum.BIZ_TYPE_TP_PLAN;
        }

        // 0:待审批，1：通过（自动发放，已获得证书），2：不通过,null：待申请；
        Integer certificateStatus = getTpcertificateStatus(relationId, context.getAccountId(), retionType);
        if (certificateStatus != null && (certificateStatus == 1 || certificateStatus == 0)) {
            throw new BizException("4000", "已申请，无需重复申请");
        }
        tpCertificateClient.applyCertificate(bizType, relationId);
    }

    /**
     * 计算学习单元的结束时间
     *
     * @param plan
     * @param planVO 为null时，处理介绍页学习时间描述，不实时计算
     * @return
     */
    @Override
    public String getStudyTimeStr(TpPlan plan, TpStudyPlanVO planVO) {
        TpPlanStudyTimeCondition timeCondition = conditionService.getTimeCondition(plan.getId());
        String dateFormat = "yyyy.MM.dd";

        if (timeCondition == null) {
            if (planVO != null) {
                planVO.setStartTime(plan.getStartTime());
                planVO.setEndTime(plan.getEndTime());
            }
            return DateUtil.format(plan.getStartTime(), dateFormat) + "~" + DateUtil.format(plan.getEndTime(),
                dateFormat);
        }
        Integer conditionType = timeCondition.getConditionType();
        if (planVO == null) {
            planVO = new TpStudyPlanVO();
        }

        if (conditionType == 0) {
            planVO.setStudyTime("随到随学");
            return planVO.getStudyTime();
        }

        if (conditionType == 1) {
            planVO.setStudyTime(DateUtil.format(timeCondition.getStartTime(), dateFormat) + "~" + DateUtil.format(
                timeCondition.getEndTime(), dateFormat));
            planVO.setStartTime(plan.getStartTime());
            planVO.setEndTime(plan.getEndTime());
            return planVO.getStudyTime();
        }

        // planVO
        if (conditionType == 2 && planVO.getId() == null) {
            planVO.setStudyTime(
                new StringBuffer("开始学习后").append(timeCondition.getAfterStartDate()).append("天内可学").toString());
            return planVO.getStudyTime();
        }

        if (conditionType == 2 && planVO.getId() != null) {
            // 查询项目开始时间
            Date date =
                tpViewRecordService.getstartTimeByTpId(plan.getTrainingProjectId(), ContextHolder.get().getAccountId(),
                    plan.getCompanyId(), plan.getSiteId());
            planVO.setStudyTime(getStudyByStartAt(date, timeCondition.getAfterStartDate()));
            planVO.setEndTime(DateUtil.offsetDay(date, timeCondition.getAfterStartDate()));
            return planVO.getStudyTime();
        }

        // 锁定状态，不用就仨u呢
        if (conditionType == 3 && (planVO.getId() == null || planVO.getLockStatus())) {
            planVO.setStudyTime(
                new StringBuffer("前置单元完成后").append(timeCondition.getAfterPrePlanDate()).append("天内可学")
                    .toString());
            return planVO.getStudyTime();
        }

        // 查询前置单元完成时间最早时间
        List<TpPlanConditionPre> preConditionByPlan = tpPlanConditionPreService.getPreConditionByPlanId(plan.getId());
        Date date = new Date();
        if (!CollectionUtils.isEmpty(preConditionByPlan)) {
            List<Long> prePlanIds =
                preConditionByPlan.stream().map(TpPlanConditionPre::getPrePlanId).collect(Collectors.toList());
            Integer finishCount = preConditionByPlan.get(0).getFinishCount();
            if (finishCount == null || finishCount == 0) {
                finishCount = prePlanIds.size();
            }
            Date datePlan =
                tpStudentPlanRecordService.getPlanMinFinishedTime(ContextHolder.get().getAccountId(), plan.getSiteId(),
                    prePlanIds, finishCount - 1);
            if (datePlan != null) {
                date = datePlan;
            }
        }
        planVO.setStudyTime(getStudyByStartAt(date, timeCondition.getAfterPrePlanDate()));
        planVO.setEndTime(DateUtil.offsetDay(date, timeCondition.getAfterPrePlanDate()));
        return planVO.getStudyTime();

    }

    /**
     * 判断是否需要展示项目介绍页
     *
     * @param ao 项目id,区分哪个端
     * @return true
     */
    public String judgeProjectDesc(ProjectJudgeAO ao) {
        RequestContext requestContext = ContextHolder.get();
        ao.setSiteId(requestContext.getSiteId());
        ao.setCompanyId(requestContext.getCompanyId());
        ao.setAccountId(requestContext.getAccountId());
        return trainingProjectService.judgeProjectDesc(ao);
    }

    /**
     * @param trainingProject
     * @param accountId
     * @return 当项目已完成时：true；未完成：false
     */
    public Boolean getTpFinishedStatus(TrainingProject trainingProject, Long accountId) {

        List<TpPlan> tpPlans = tpPlanService.getTpPlansByTpId(trainingProject.getId());
        List<Long> planIds = tpPlans.stream().map(TpPlan::getId).collect(Collectors.toList());

        List<TpConditionPost> completeConditions =
            tpConditionPostService.getCompleteConditions(trainingProject.getId());
        List<Long> finishedPlanIdList =
            planRecordService.getFinishedIdByTpId(accountId, trainingProject.getSiteId(), planIds);

        // 没有计划完成。则项目绝对没有完成
        if (CollectionUtils.isEmpty(finishedPlanIdList)) {
            return false;
        }

        if (CollectionUtils.isEmpty(completeConditions)) {
            // 完成所有学习单元
            return !CollectionUtils.isEmpty(finishedPlanIdList) && finishedPlanIdList.size() >= planIds.size();
        }

        Map<Integer, List<TpConditionPost>> conditionMap =
            completeConditions.stream().collect(Collectors.groupingBy(TpConditionPost::getConditionType));
        List<TpConditionPost> finishedCountList = conditionMap.get(0);
        List<TpConditionPost> finishedPlan = conditionMap.get(1);
        if (CollectionUtils.isEmpty(finishedPlan) && !CollectionUtils.isEmpty(finishedCountList)) {
            // 完成计划数
            return !CollectionUtils.isEmpty(finishedPlanIdList) && finishedPlanIdList.size() >= finishedCountList.get(0)
                .getCompleteCount();
        }

        List<Long> prePlanIds = finishedPlan.stream().map(TpConditionPost::getTpPlanId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(finishedCountList) && !CollectionUtils.isEmpty(finishedPlan)) {
            // 完成指定计划
            return finishedPlanIdList.containsAll(prePlanIds);
        }

        // 完成指定计划+其余计划数
        Boolean prePlanIdFinished = finishedPlanIdList.containsAll(prePlanIds);

        // 指定的活动没有完成
        if (!prePlanIdFinished) {
            return false;
        }

        finishedPlanIdList.removeAll(prePlanIds);

        // 排除指定的计划，剩余完成的数量大于指定的数量
        return finishedPlanIdList.size() >= finishedCountList.get(0).getCompleteCount();
    }

    public void tpFirstFinished(TrainingProject trainingProject, Long accountId) {
        // 添加项目完成记录/training/v2/student/introduce/details
        addTpStudentRecord(trainingProject, accountId);
        // 发积分
        sendPoint(trainingProject);
        // 发证书
        givenCertificate(trainingProject, accountId);
        // 发勋章
        sendMedal(trainingProject, accountId);
    }

    /**
     * 查看计划的锁定状态，true :待解锁；false:已解锁/无续解锁
     *
     * @param accountId
     * @param siteId
     * @param tpPlanId  计划Id
     * @return
     */
    public Boolean getPlanLockStatus(Long accountId, Long siteId, Long tpPlanId) {
        List<TpPlanConditionPre> prePlans = tpPlanConditionPreService.getPreConditionByPlanId(tpPlanId);
        // 无前置单元
        if (CollectionUtils.isEmpty(prePlans)) {
            return false;
        }
        List<Long> planIds =
            prePlans.stream().map(TpPlanConditionPre::getPrePlanId).distinct().collect(Collectors.toList());

        // 完成前置单元中的指定数量
        Integer needCount = prePlans.get(0).getFinishCount();
        if (needCount == null || needCount == 0) {
            needCount = planIds.size();
        }

        Integer count = planRecordService.getFinishedCountAllByPlanId(accountId, siteId, planIds);
        // 有前置单元 ，但是的数量小于 设置的数量 未解锁
        return count == null || needCount > count;
    }

    public String getStudyByStartAt(Date startAt, Integer num) {

        startAt = DateUtil.offsetDay(startAt, num);
        Long day = DateUtil.between(new Date(), startAt, DateUnit.DAY, false);
        if (day < 0) {
            return "已过期";
        } else if (day + 1 > num) {
            return num + "天内可学";
        } else if (day >= 1) {
            return day + 1 + "天内可学";
        } else {
            if (new Date().getTime() > startAt.getTime()) {
                return "已过期";
            }
            Long hour = DateUtil.between(new Date(), startAt, DateUnit.HOUR, false);
            return hour + 1 + "小时内可学";
        }
    }

    public String getStudyHour(Long tpId) {

        if (!getShowTpStudyHour()) {
            return null;
        }

        List<Long> courseIds = activityService.getcourseIdsByTrainingProjectId(tpId);
        if (CollectionUtils.isEmpty(courseIds)) {
            return null;
        }
        Float studyDuration = courseClient.getStudyDuration(courseIds);
        return studyDuration != null ? studyDuration.toString() : null;
    }

    /**
     * 查询是否展示项目学时
     *
     * @return
     */
    public Boolean getShowTpStudyHour() {
        List<FunctionDisplayConfigVo> dataFunction = configApiClients.getAllBySiteId();
        if (CollectionUtils.isEmpty(dataFunction)) {
            return false;
        }
        Optional<FunctionDisplayConfigVo> first =
            dataFunction.stream().filter(it -> it.getFunctionType().equals(FunctionTypeCode.TRAINING.getCode()))
                .findFirst();
        if (!first.isPresent()) {
            return false;
        }
        Boolean showDisplay = first.get().getShowDisplay();
        if (showDisplay == null) {
            return false;
        }
        return showDisplay;
    }

    private List<Long> getAllForumIds(Long tpId, Integer forumType) {
        List<Long> forumIds = new ArrayList<>();
        if (forumType == 1) {
            List<TpPlanActivityVO> postsActivity = tpPlanActivityBizService.getPostsActivity(tpId);
            if (!CollectionUtils.isEmpty(postsActivity)) {
                forumIds = postsActivity.stream().map(TpPlanActivityVO::getRelationId).collect(Collectors.toList());
            }
        } else {
            List<PostsManageVo> tpPosts = tpForumBizService.getTpPosts(tpId);
            if (!CollectionUtils.isEmpty(tpPosts)) {
                forumIds = tpPosts.stream().map(PostsManageVo::getId).collect(Collectors.toList());
            }
        }
        return forumIds;
    }

    private Map<Long, String> getForumAndPlanName(Long tpId) {
        List<TpPlanActivityVO> postsActivity = tpPlanActivityBizService.getPostsActivity(tpId);
        if (CollectionUtils.isEmpty(postsActivity)) {
            return null;
        }
        List<TpPlan> tpPlansByTpId = tpPlanService.getTpPlansByTpId(tpId);
        if (CollectionUtils.isEmpty(tpPlansByTpId)) {
            return null;
        }
        Map<Long, Long> relationIdAndPalnId = postsActivity.stream().collect(
            Collectors.toMap(TpPlanActivityVO::getRelationId, TpPlanActivityVO::getTpPlanId, (key1, key2) -> key1));
        Map<Long, String> planIdAndName =
            tpPlansByTpId.stream().collect(Collectors.toMap(TpPlan::getId, TpPlan::getName, (key1, key2) -> key1));

        Map<Long, String> map = new HashMap<>();

        relationIdAndPalnId.keySet().forEach(key -> {
            if (StrUtil.isEmpty(planIdAndName.get(key))) {
                return;
            }
            map.put(key, planIdAndName.get(key));
        });
        return map;
    }

    private List<TpStudyPlanVO<TpStudyActivityVO>> buildPlanDetails(List<TpPlan> tpPlans,
        List<TpStudyPlanVO<TpStudyActivityVO>> tpStudyPlanVOS, Long lastStudyPlanId) {

        RequestContext requestContext = ContextHolder.get();
        Long accountId = requestContext.getAccountId();
        Long siteId = requestContext.getSiteId();
        if (CollectionUtils.isEmpty(tpPlans)) {
            return tpStudyPlanVOS;
        }

        List<Long> planIds = tpPlans.stream().map(TpPlan::getId).collect(Collectors.toList());

        Map<Long, List<TpPlan>> planMap = tpPlans.stream().collect(Collectors.groupingBy(TpPlan::getId));

        // 查询培训计划是否有关联资料
        List<DocumentRelationVo> documentRelationVos =
            documentClient.queryRelationId(RelationType.PROJECT_PLAN.getType(), planIds);
        Map<Long, List<DocumentRelationVo>> docmentCountMap;
        if (CollectionUtils.isEmpty(documentRelationVos)) {
            docmentCountMap = new HashMap<>();
        } else {
            docmentCountMap =
                documentRelationVos.stream().collect(Collectors.groupingBy(DocumentRelationVo::getRelationId));

        }
        tpStudyPlanVOS.forEach(studyPlan -> {

            int index = tpStudyPlanVOS.indexOf(studyPlan);
            if (lastStudyPlanId == null && index == 0) {
                studyPlan.setShowActivity(true);
            } else {
                studyPlan.setShowActivity(lastStudyPlanId != null && lastStudyPlanId.equals(studyPlan.getId()));
            }

            TpPlan plan = planMap.get(studyPlan.getId()).get(0);
            studyPlan.setId(plan.getId());
            studyPlan.setName(plan.getName());
            //            studyPlan.setStartTime(plan.getStartTime());
            //            studyPlan.setEndTime(plan.getEndTime());
            studyPlan.setSequenceStudy(plan.getEnableStudyInSequence() == 1);
            studyPlan.setContinueStudy(plan.getEnableContinueStudy() == 1);

            studyPlan.setCertificateAuditStatus(
                getTpcertificateStatus(plan.getId(), accountId, CertificateEnum.BIZ_TYPE_TP_PLAN));
            studyPlan.setFinishedProgressMsg(getProgeressMsg(studyPlan));
            List<DocumentRelationVo> documentCountVOS = docmentCountMap.get(plan.getId());
            studyPlan.setShowDocument(!CollUtil.isEmpty(documentCountVOS));
            //

            studyPlan.setLockStatus(getPlanLockStatus(accountId, siteId, plan.getId()));
            if (studyPlan.getLockStatus()) {
                studyPlan.setLockMsg(getPLanLockMsg(planMap, plan.getId()));
            }
            //            studyPlan.setStudyTime(getStudyTimeStr(plan,studyPlan));

        });
        return tpStudyPlanVOS;

    }

    /**
     * n个活动未完成（对应条件为 完成所有活动）
     *
     * n个必修活动未完成（对应条件为 完成指定学习活动）
     *
     * n个选修活动未完成（对应条件为 完成X个学习活动）
     *
     * n个必修、m个选修未完成（对应条件为 完成指定学习活动+完成X个学习活动）
     *
     * 其中n、m根据完成条件的要求数量与实际已完成的数量计算得到
     */
    private String getProgeressMsg(TpStudyPlanVO<TpStudyActivityVO> studyPlan) {

        List<TpPlanConditionPost> conditionPosts = tpPlanConditionPostService.getConditionPosts(studyPlan.getId());
        List<TpStudyActivityVO> activityList = studyPlan.getActivityList();
        if (CollectionUtils.isEmpty(activityList)) {
            return "";
        }
        Map<Boolean, List<TpStudyActivityVO>> collect =
            activityList.stream().collect(Collectors.groupingBy(t -> t.getFinished()));

        List<TpStudyActivityVO> finishedList = collect.get(true);
        List<TpStudyActivityVO> unFinishedList = collect.get(false);
        Integer unFinishedCount = 0;
        Integer finishedCount = 0;
        // 未完成数量为0，则计划已完成（任务情况下都使用）
        if (CollectionUtils.isEmpty(unFinishedList)) {
            studyPlan.setFinished(true);
            return "";
        } else {
            unFinishedCount = unFinishedList.size();
        }

        if (!CollectionUtils.isEmpty(finishedList)) {
            finishedCount = finishedList.size();
        }

        // 对应条件为 完成所有活动
        if (CollectionUtils.isEmpty(conditionPosts) && unFinishedCount > 0) {
            return unFinishedCount + "个活动未完成";
        }

        Map<Integer, List<TpPlanConditionPost>> postMap =
            conditionPosts.stream().collect(Collectors.groupingBy(it -> it.getType()));
        List<TpPlanConditionPost> mustPost = postMap.get(1);
        List<TpPlanConditionPost> selectPost = postMap.get(0);

        // 对应条件为 完成X个学习活动
        if (CollectionUtils.isEmpty(mustPost) && !CollectionUtils.isEmpty(selectPost)) {
            Integer num = selectPost.get(0).getNum() - finishedCount;
            if (num <= 0) {
                studyPlan.setFinished(true);
                return "";
            }
            return num + "个选修活动未完成";
        }

        // (对应条件为 完成指定学习活动）
        if (!CollectionUtils.isEmpty(mustPost) && CollectionUtils.isEmpty(selectPost)) {
            if (finishedCount <= 0) {
                return mustPost.size() + "个必修活动未完成";
            }
            List<Long> finishedIds =
                finishedList.stream().map(TpStudyActivityVO::getActivityId).collect(Collectors.toList());
            Long count = mustPost.stream().filter(it -> !finishedIds.contains(it.getTpPlanActivityId())).count();
            if (count <= 0) {
                studyPlan.setFinished(true);
                return "";
            }
            return count + "个必修活动未完成";
        }

        // 对应条件为 完成指定学习活动+完成X个学习活动）
        if (!CollectionUtils.isEmpty(mustPost) && !CollectionUtils.isEmpty(selectPost)) {
            Integer mustNum = 0;
            // 已经已完成的数量（不包括必修的）
            Integer selectFinisehedNum = 0;
            if (finishedCount <= 0) {
                mustNum = mustPost.size();
            } else {
                List<Long> finishedIds =
                    finishedList.stream().map(TpStudyActivityVO::getActivityId).collect(Collectors.toList());
                // 必须完成，但是还未完成的。
                Long count = mustPost.stream().filter(it -> !finishedIds.contains(it.getTpPlanActivityId())).count();
                mustNum = Integer.valueOf(count.toString());
                // 必须完成的Id
                List<Long> mustFinishedIds =
                    mustPost.stream().map(TpPlanConditionPost::getTpPlanActivityId).collect(Collectors.toList());
                // 已经已完成的数量（不包括必修的）
                Long selectFinisehedNumLong = finishedIds.stream().filter(it -> !mustFinishedIds.contains(it)).count();
                selectFinisehedNum = Integer.valueOf(selectFinisehedNumLong.toString());

            }
            // 选修需要完成的数量-已经完成的（不包括必修的）
            Integer selectNum = selectPost.get(0).getNum() - selectFinisehedNum;
            if (mustNum <= 0 && selectNum <= 0) {
                studyPlan.setFinished(true);
                return "";
            }
            if (selectNum <= 0) {
                return new StringBuffer(mustNum.toString()).append("个必修未完成").toString();
            }

            if (mustNum <= 0) {
                return new StringBuffer(selectNum.toString()).append("个选修未完成").toString();
            }
            return new StringBuffer(mustNum.toString()).append("个必修、").append(selectNum).append("个选修未完成")
                .toString();
        }
        return "";
    }

    /**
     * 补充计划锁定的原因
     *
     * @param planMap
     * @param tpPlanId
     * @return
     */
    private String getPLanLockMsg(Map<Long, List<TpPlan>> planMap, Long tpPlanId) {
        List<TpPlanConditionPre> prePlans = tpPlanConditionPreService.getPreConditionByPlanId(tpPlanId);
        // 无前置单元
        if (CollectionUtils.isEmpty(prePlans)) {
            return "";
        }
        List<Long> planIds =
            prePlans.stream().map(TpPlanConditionPre::getPrePlanId).distinct().collect(Collectors.toList());

        // 完成前置单元中的指定数量
        Integer needCount = prePlans.get(0).getFinishCount();
        StringBuilder sb = new StringBuilder();
        sb.append("前置单元尚未完成，无法开始新单元。").append("请先完成");
        for (Long planId : planIds) {
            List<TpPlan> plans = planMap.get(planId);
            if (CollectionUtils.isEmpty(plans)) {
                continue;
            }
            sb.append("【").append(plans.get(0).getName()).append("】");
        }
        if (needCount != null && needCount > 0) {
            sb.append("中的").append(needCount).append("个");
        }
        return sb.toString();
    }

    private void addTpStudentRecord(TrainingProject trainingProject, Long accountId) {
        TpStudentProjectRecord tpStudentProjectRecord =
            tpStudentProjectRecordService.getTpStudentProjectRecord(accountId, trainingProject.getId());
        if (tpStudentProjectRecord == null) {
            TpStudentProjectRecord record = new TpStudentProjectRecord();
            record.setFinished(1);
            record.setCompanyId(trainingProject.getCompanyId());
            record.setAccountId(accountId);
            record.setSiteId(trainingProject.getSiteId());
            record.setId(idGenerator.generate());
            record.setFinishDate(new Date());
            record.setTrainingProjectId(trainingProject.getId());
            tpStudentProjectRecordService.save(record);
        } else if (tpStudentProjectRecord.getFinished() == 0) {
            tpStudentProjectRecord.setFinished(1);
            tpStudentProjectRecordService.updateById(tpStudentProjectRecord);
        }
    }

    private void sendPoint(TrainingProject trainingProject) {
        if (trainingProject.getPoint() == null || trainingProject.getPoint() <= 0) {
            return;
        }

        RequestContext context = ContextHolder.get();

        PointParamVO pointParamVO = new PointParamVO();
        pointParamVO.setEventName("pointTrainingProject");
        pointParamVO.setActivityType("学习项目通过，积分发放");
        pointParamVO.setActivitySource("学习项目");
        pointParamVO.setReleaseCondition("完成才发放");
        pointParamVO.setReleaseRules("按照完成取积分");
        pointParamVO.setCreatePointTime(new Date());
        pointParamVO.setSourceId(trainingProject.getId());
        pointParamVO.setAccountId(context.getAccountId());
        pointParamVO.setSiteId(context.getSiteId());
        pointParamVO.setOperatingPoint(trainingProject.getPoint());
        pointParamVO.setActivityName(trainingProject.getName());
        pointParamVO.setCompanyId(context.getCompanyId());
        pointParamVO.setAccountName(context.getAccountName());
        pointParamVO.setOrgId(context.getOrgId());
        String sitePointId = pointRedisFeignClients.addPointRedis(pointParamVO);
        // 发送积分
        if (StringUtils.isNotBlank(sitePointId)) {
            amqpTemplate.convertAndSend("trainingProject", sitePointId);
        }
    }

    private void givenCertificate(TrainingProject trainingProject, Long accountId) {
        CertificateStrategyVO relationCertificate = certificateClient.getRelationCertificate(trainingProject.getId(),
            CertificateEnum.BIZ_TYPE_TRAINING.getCode());
        if (relationCertificate == null || relationCertificate.getIssueStrategy() == null || relationCertificate.getIssueStrategy() == 1) {
            return;
        }
        List<IssueCertificateParamVO> list = new ArrayList<>();
        relationCertificate.getCertificates().stream().forEach(it -> {
            IssueCertificateParamVO vo = new IssueCertificateParamVO();
            vo.setAccountId(accountId);
            vo.setBizId(trainingProject.getId());
            vo.setBizName(trainingProject.getName());
            vo.setCertificateId(it.getId());
            vo.setBizType(CertificateEnum.BIZ_TYPE_TRAINING.getCode());
            list.add(vo);
        });
        certificateUserFeignClients.issueCertificate(trainingProject.getCompanyId(), trainingProject.getSiteId(), list);
    }

    private void sendMedal(TrainingProject trainingProject, Long accountId) {

        //2023/4/16 异步发送消息 发勋章
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {

                TrainingProjectEvent event =
                    new TrainingProjectEvent(trainingProject.getId(), TpActivityType.TRAIN_PROJECT, accountId,
                        new Date(), null, null, null, true, trainingProject.getSiteId(), null);
                EventWrapper<TrainingProjectEvent> ew = new EventWrapper<>(trainingProject.getId(), event);

                cloudEventPublisher.publish(MedalMessageConstants.activity_status_exchange_name,
                    MedalMessageConstants.activity_finished_medal_routing_key, ew);
            }
        });
    }

    private void setExamIdAndAssignment(Map<Long, List<TpPlanActivityVO>> examIdAll,
        Map<Long, List<TpPlanActivityVO>> assignmentIdsAll, TpPlanVO it) {
        List<TpPlanActivityVO> exam =
            it.getActivities().stream().filter(activity -> activity.getType().equals(1)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(exam)) {
            List<TpPlanActivityVO> mapExamIds = examIdAll.get(it.getId());
            if (CollectionUtils.isEmpty(mapExamIds)) {
                mapExamIds = new ArrayList<>();
            }
            mapExamIds.addAll(exam);
            examIdAll.put(it.getId(), mapExamIds);
        }

        List<TpPlanActivityVO> assignment =
            it.getActivities().stream().filter(activity -> activity.getType().equals(5)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(assignment)) {
            List<TpPlanActivityVO> mapassignmentIds = assignmentIdsAll.get(it.getId());
            if (CollectionUtils.isEmpty(mapassignmentIds)) {
                mapassignmentIds = new ArrayList<>();
            }
            mapassignmentIds.addAll(assignment);
            assignmentIdsAll.put(it.getId(), mapassignmentIds);
        }
    }

    private List<TpStudyExamVO> getExamByPlan(List<TpPlanActivityVO> examList) {

        if (CollectionUtils.isEmpty(examList)) {
            return null;
        }

        List<Long> examIds = examList.stream().map(TpPlanActivityVO::getRelationId).collect(Collectors.toList());
        // 业务ID，活动ID
        Map<Long, TpPlanActivityVO> examIdAndRelationId = examList.stream()
            .collect(Collectors.toMap(TpPlanActivityVO::getRelationId, Function.identity(), (key1, key2) -> key2));

        List<MyExamVO> tpExamTotal = examClient.getTpExamTotal(examIds);
        if (CollectionUtils.isEmpty(tpExamTotal)) {
            return null;
        }
        List<TpStudyExamVO> list = new ArrayList<>();
        tpExamTotal.stream().forEach(exam -> {
            TpPlanActivityVO activityVO = examIdAndRelationId.get(exam.getId());
            TpStudyExamVO vo = new TpStudyExamVO();
            vo.setActivityId(activityVO.getId());
            vo.setRelationName(
                StrUtil.isEmpty(activityVO.getCustomizeName()) ? activityVO.getName() : activityVO.getCustomizeName());
            vo.setRelationType(1);
            vo.setRelationId(exam.getId());
            vo.setStartAt(exam.getStartTime());
            vo.setEndAt(exam.getEndTime());
            vo.setSubmitCount(exam.getExamNum());
            vo.setPassLimitScore(exam.getQualifiedScore());
            vo.setUnLimitCount(exam.getMaxNum() == null || exam.getMaxNum() == 0);
            if (exam.getState() == null || exam.getState().equals(0)) {
                vo.setStatus(0);
            } else if (exam.getState().equals(1)) {
                vo.setStatus(1);
            } else if (exam.getState().equals(2) && exam.getScore() != null && exam.getQualifiedScore() != null) {
                vo.setStatus(exam.getScore().compareTo(exam.getQualifiedScore()) >= 0 ? 2 : 9);
            } else {
                vo.setStatus(2);
            }
            list.add(vo);
        });
        return list;
    }

    private List<TpStudyExamVO> getAssignmentByPlan(List<TpPlanActivityVO> assignmentList) {
        if (CollectionUtils.isEmpty(assignmentList)) {
            return null;
        }

        List<Long> assignmentIds =
            assignmentList.stream().map(TpPlanActivityVO::getRelationId).collect(Collectors.toList());
        // 业务ID，活动ID
        Map<Long, TpPlanActivityVO> assignmentIdAndRelation = assignmentList.stream()
            .collect(Collectors.toMap(TpPlanActivityVO::getRelationId, Function.identity(), (key1, key2) -> key2));

        List<ApiAssignmentDetailsVo> assigmentTotal = tpAssignmentClient.getAssigmentTotal(assignmentIds);
        if (CollectionUtils.isEmpty(assigmentTotal)) {
            return null;
        }
        List<TpStudyExamVO> list = new ArrayList<>();
        assigmentTotal.forEach(it -> {
            TpPlanActivityVO activityVO = assignmentIdAndRelation.get(it.getId());
            TpStudyExamVO assignment = new TpStudyExamVO();
            assignment.setActivityId(activityVO.getId());
            assignment.setRelationName(
                StrUtil.isEmpty(activityVO.getCustomizeName()) ? activityVO.getName() : activityVO.getCustomizeName());
            assignment.setRelationType(5);
            assignment.setRelationId(it.getId());
            assignment.setRelationType(5);
            assignment.setPassLimitScore(BigDecimal.valueOf(it.getPassScore()));
            assignment.setEndAt(it.getFinishTime());
            assignment.setUnLimitDate(
                it.getFinishTime() != null && it.getFinishTime().getTime() >= DateUtil.parseDate("2038-01-01")
                    .getTime());
            assignment.setSurplusCount(Math.max(it.getCount() - it.getSubmitCount(), 0));
            if (it.getStatus() == null) {
                assignment.setStatus(0);
            } else if (it.getStatus() == 1) {
                assignment.setStatus(1);
            } else if (it.getStatus() == 2 && it.getScore() != null && it.getPassScore() != null) {
                assignment.setStatus(it.getScore() >= it.getPassScore() ? 2 : 9);
            } else {
                assignment.setStatus(2);
            }
            list.add(assignment);
        });
        return list;
    }

    private Integer getAllCertificateCount(Long tpId) {
        List<Long> tpPlanIds = tpPlanService.getTpPlanIds(CollUtil.toList(tpId));
        ArrayList<Long> tpIds = CollUtil.toList(tpId);
        tpIds.addAll(tpPlanIds);
        Integer certificateCount = certificateClient.getCertificateCount(tpIds);
        return certificateCount;
    }

    /**
     * 0:待审批，1：通过（自动发放，已获得证书），2：不通过,null：待申请；
     *
     * @param accountId
     * @param
     * @return
     */
    private Integer getTpcertificateStatus(Long relationId, Long accountId, CertificateEnum retionType) {
        // TODO 设置证书状态
        List<CertificateApplyVO> allApply =
            certificateApplyClient.getAllApply(accountId, relationId, retionType.getCode());
        if (CollectionUtils.isEmpty(allApply)) {
            return null;
        }
        Long success = allApply.stream().filter(it -> it.getAuditState().equals(1)).count();
        if (success > 0) {
            return 1;
        }
        Long wait = allApply.stream().filter(it -> it.getAuditState().equals(0)).count();
        if (wait == 0) {
            return 2;
        }
        return 0;
    }
}
