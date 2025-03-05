package com.yizhi.training.application.v2.service.biz;

import cn.hutool.core.util.StrUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.assignment.application.feign.AssignmentClient;
import com.yizhi.assignment.application.vo.entity.Assignment;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.course.application.feign.OfflineCourseClient;
import com.yizhi.course.application.vo.CourseVo;
import com.yizhi.course.application.vo.OfflineCourseVo;
import com.yizhi.exam.application.feign.ExamClient;
import com.yizhi.exam.application.vo.domain.Exam;
import com.yizhi.game.application.feign.PkActivityClient;
import com.yizhi.game.application.vo.domain.PkActivityVO;
import com.yizhi.library.application.feign.CaseLibraryClient;
import com.yizhi.library.application.feign.StudentCaseClient;
import com.yizhi.library.application.vo.CaseLibraryVO;
import com.yizhi.library.application.vo.FavoriteVO;
import com.yizhi.live.application.feign.LiveActivityClient;
import com.yizhi.live.application.vo.LiveActivityVO;
import com.yizhi.practice.application.feign.PracticeConfigStudentClient;
import com.yizhi.practice.application.pojo.vo.PracticeVo;
import com.yizhi.research.application.feign.ResearchClient;
import com.yizhi.research.application.vo.domain.ResearchVo;
import com.yizhi.system.application.feign.CommitmentLettersClient;
import com.yizhi.system.application.vo.CommitmentLetterVo;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.v2.constant.TrainingProjectConstant;
import com.yizhi.training.application.v2.enums.TpActivityTypeEnum;
import com.yizhi.training.application.v2.enums.TpExceptionCodeEnum;
import com.yizhi.training.application.v2.service.TpPlanActivityService;
import com.yizhi.training.application.v2.service.TpPlanService;
import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.training.application.v2.vo.request.UpdateActivitiesRequestVO;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import com.yizhi.util.application.domain.BizResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TpPlanActivityBizService {

    @Autowired
    private TpPlanActivityService tpPlanActivityService;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private OfflineCourseClient offlineCourseClient;

    @Autowired
    private ExamClient examClient;

    @Autowired
    private ResearchClient researchClient;

    @Autowired
    private LiveActivityClient liveActivityClient;

    @Autowired
    private AssignmentClient assignmentClient;

    @Autowired
    private CaseLibraryClient caseLibraryClient;

    @Autowired
    private StudentCaseClient studentCaseClient;

    @Autowired
    private PracticeConfigStudentClient practiceClient;

    @Autowired
    private PkActivityClient pkActivityClient;

    @Autowired
    private CommitmentLettersClient commitmentLettersClient;

    @Transactional(rollbackFor = Exception.class)
    public Integer updateTpPlanActivities(UpdateActivitiesRequestVO request) {
        if (CollectionUtils.isEmpty(request.getActivities())) {
            throw new BizException(TpExceptionCodeEnum.ACTIVITY_CAN_BE_EMPTY.getCode(),
                TpExceptionCodeEnum.ACTIVITY_CAN_BE_EMPTY.getDescription());
        }

        RequestContext context = ContextHolder.get();

        List<TpPlanActivity> activities = tpPlanActivityService.getActivities(request.getTpPlanId());
        Map<Long, TpPlanActivity> activityMap =
            activities.stream().collect(Collectors.toMap(TpPlanActivity::getRelationId, o -> o, (o1, o2) -> o1));
        Set<Long> deleteActivityIdSet = activities.stream().map(TpPlanActivity::getId).collect(Collectors.toSet());
        List<TpPlanActivity> addList = new ArrayList<>();
        List<TpPlanActivity> updateList = new ArrayList<>();

        Date now = new Date();
        for (TpPlanActivityVO vo : request.getActivities()) {
            if (vo.getRelationId() != null && activityMap.containsKey(vo.getRelationId())) {
                TpPlanActivity oldActivity = activityMap.get(vo.getRelationId());
                // 已有的，可能是更新
                TpPlanActivity updateActivity = new TpPlanActivity();
                BeanUtils.copyProperties(vo, updateActivity);
                updateActivity.setId(oldActivity.getId());

                updateActivity.setUpdateById(context.getAccountId());
                updateActivity.setUpdateByName(context.getAccountName());
                updateActivity.setUpdateTime(now);

                updateList.add(updateActivity);

                // 从待删除列表移除
                deleteActivityIdSet.remove(oldActivity.getId());
                continue;
            }

            TpPlanActivity newActivity = new TpPlanActivity();
            BeanUtils.copyProperties(vo, newActivity);

            newActivity.setId(idGenerator.generate());
            newActivity.setCompanyId(context.getCompanyId());
            newActivity.setSiteId(context.getSiteId());
            newActivity.setOrgId(context.getOrgId() == null ? 0 : context.getOrgId());
            newActivity.setTrainingProjectId(request.getTrainingProjectId());
            newActivity.setTpPlanId(request.getTpPlanId());
            newActivity.setType(vo.getType());

            if (TpActivityTypeEnum.LINK.getCode().equals(vo.getType())) {
                newActivity.setRelationId(newActivity.getId());
            }
            newActivity.setCreateById(context.getAccountId());
            newActivity.setCreateByName(context.getAccountName());
            newActivity.setCreateTime(now);

            newActivity.setSort(vo.getSort() == null ? TrainingProjectConstant.DEFAULT_ACTIVITY_SORT : vo.getSort());

            addList.add(newActivity);
        }
        // 删除活动
        if (CollectionUtils.isNotEmpty(deleteActivityIdSet)) {
            tpPlanActivityService.deleteBatch(new ArrayList<>(deleteActivityIdSet));
        }
        if (CollectionUtils.isNotEmpty(addList)) {
            tpPlanActivityService.saveBatch(addList);
            for (TpPlanActivity tpPlanActivity : addList) {
                if (tpPlanActivity.getType() != null && tpPlanActivity.getType() == 20) {
                    //承诺书类型活动添加与承诺书的关联
                    commitmentLettersClient.saveCommitmentLetterAssociation(1, tpPlanActivity.getRelationId(),
                        tpPlanActivity.getTrainingProjectId(), tpPlanActivity.getTpPlanId(), 2);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            tpPlanActivityService.updateBatchById(updateList);
        }
        return request.getActivities().size();
    }

    /**
     * 查询考试和作业
     *
     * @param tpId
     * @return
     */
    public List<TpPlanVO> getExamAndAssignment(Long tpId) {
        List<TpPlanActivity> examAnAssignments = tpPlanActivityService.getExamAndAssignment(tpId);
        if (CollectionUtils.isEmpty(examAnAssignments)) {
            return Collections.emptyList();
        }
        List<Long> tpPlanIds =
            examAnAssignments.stream().map(TpPlanActivity::getTpPlanId).distinct().collect(Collectors.toList());
        Map<Long, List<TpPlanActivity>> activityMap =
            examAnAssignments.stream().collect(Collectors.groupingBy(TpPlanActivity::getTpPlanId));
        List<TpPlan> tpPlans = tpPlanService.getTpPlansOrderByDir(tpPlanIds);
        List<TpPlanVO> response = new ArrayList<>();
        for (TpPlan plan : tpPlans) {
            List<TpPlanActivity> activities = activityMap.get(plan.getId());
            List<TpPlanActivityVO> activityVos = new ArrayList<>();
            activities.forEach(o -> {
                TpPlanActivityVO vo = new TpPlanActivityVO();
                BeanUtils.copyProperties(o, vo);
                if (StrUtil.isNotEmpty(vo.getCustomizeName())) {
                    vo.setName(vo.getCustomizeName());
                }
                activityVos.add(vo);
            });

            TpPlanVO planVO = new TpPlanVO();
            BeanUtils.copyProperties(plan, planVO);
            planVO.setActivities(activityVos);

            response.add(planVO);
        }
        return response;
    }

    /**
     * 查询作为学习活动的帖子列表
     *
     * @param trainingProjectId
     * @return
     */
    public List<TpPlanActivityVO> getPostsActivity(Long trainingProjectId) {
        List<TpPlanActivity> postsActivities =
            tpPlanActivityService.getActivitiesBy(trainingProjectId, null, TpActivityTypeEnum.FORUM_POST.getCode());
        if (CollectionUtils.isEmpty(postsActivities)) {
            return Collections.emptyList();
        }
        Map<Long, List<TpPlanActivity>> activityMap =
            postsActivities.stream().collect(Collectors.groupingBy(TpPlanActivity::getTpPlanId));
        List<TpPlan> tpPlans = tpPlanService.getTpPlansOrderByDir(new ArrayList<>(activityMap.keySet()));

        List<TpPlanActivityVO> activityVOS = new ArrayList<>();
        for (TpPlan tpPlan : tpPlans) {
            List<TpPlanActivity> activities = activityMap.get(tpPlan.getId());
            for (TpPlanActivity activity : activities) {
                TpPlanActivityVO vo = new TpPlanActivityVO();
                BeanUtils.copyProperties(activity, vo);
                vo.setTpPlanName(tpPlan.getName());

                activityVOS.add(vo);
            }
        }

        return activityVOS;
    }

    /**
     * 查询学习单元的学习活动
     *
     * @param tpPlanId
     * @return
     */
    public List<TpPlanActivityVO> getTpPlanActivities(Long tpPlanId) {
        List<TpPlanActivity> activities = tpPlanActivityService.getActivities(tpPlanId);
        if (CollectionUtils.isEmpty(activities)) {
            return Collections.emptyList();
        }
        return BeanCopyListUtil.copyListProperties(activities, TpPlanActivityVO::new);
    }

    /**
     * 更新学习活动
     *
     * @param request
     * @return
     */
    public Boolean updateTpPlanActivityInfo(TpPlanActivityVO request) {
        if (request.getId() == null || request.getId() < 1) {
            return false;
        }
        TpPlanActivity tpPlanActivity = new TpPlanActivity();
        BeanUtils.copyProperties(request, tpPlanActivity);
        return tpPlanActivityService.updateById(tpPlanActivity);
    }

    /**
     * 根据学习活动id列表删除学习活动
     *
     * @param activityIds
     * @return
     */
    public Boolean deleteBatchByIds(List<Long> activityIds) {
        if (CollectionUtils.isEmpty(activityIds)) {
            return false;
        }
        return tpPlanActivityService.deleteBatch(activityIds);
    }

    /**
     * 通用的查询活动的方法
     *
     * @param trainingProjectId 必传
     * @param tpPlanId          非必传
     * @param type              非必传
     * @return
     */
    public List<TpPlanActivityVO> getActivities(Long trainingProjectId, Long tpPlanId, Integer type) {
        List<TpPlanActivity> activities = tpPlanActivityService.getActivitiesBy(trainingProjectId, tpPlanId, type);
        if (CollectionUtils.isEmpty(activities)) {
            return Collections.emptyList();
        }
        return BeanCopyListUtil.copyListProperties(activities, TpPlanActivityVO::new);
    }

    public Boolean reverseActivitySort(Long companyId, Long siteId) {
        int pageNo = 1;
        int pageSize = 1000;
        while (true) {
            List<TpPlan> tpPlanList = tpPlanService.getAllTpPlan(companyId, siteId, pageNo++, pageSize);
            if (CollectionUtils.isEmpty(tpPlanList)) {
                break;
            }
            for (TpPlan tpPlan : tpPlanList) {
                reverseActivitySortOfPlan(tpPlan);
            }
        }
        return true;
    }

    public Boolean reverseActivitySortOfPlan(TpPlan tpPlan) {
        List<TpPlanActivity> activities = tpPlanActivityService.getActivitiesBySortAsc(tpPlan.getId());
        if (CollectionUtils.isEmpty(activities)) {
            return true;
        }
        List<TpPlanActivity> updateList = new ArrayList<>();
        int sort = 999;
        for (TpPlanActivity activity : activities) {
            TpPlanActivity updateEntity = new TpPlanActivity();
            updateEntity.setId(activity.getId());
            updateEntity.setSort(sort--);

            updateList.add(updateEntity);
        }
        return tpPlanActivityService.updateBatchById(updateList);
    }

    /**
     * 更新项目下所有活动的logoUrl
     *
     * @param trainingProjectId
     * @return
     */
    public Boolean refreshLogoUrl(Long trainingProjectId) {
        List<TpPlanActivity> activities = tpPlanActivityService.getActivitiesBy(trainingProjectId, null, null);
        if (CollectionUtils.isEmpty(activities)) {
            return true;
        }
        List<TpPlanActivity> updateList = new ArrayList<>();
        for (TpPlanActivity activity : activities) {
            String newLogoUrl = null;
            try {
                newLogoUrl = queryLogoUrl(activity.getRelationId(), activity.getType());
            } catch (Exception e) {
                log.error("更新活动图片，远程调用异常，activityId:{}, relationId:{}, activityType{}", activity.getId(),
                    activity.getRelationId(), activity.getType(), e);
            }
            if (StringUtils.isBlank(newLogoUrl)) {
                continue;
            }
            TpPlanActivity updateEntity = new TpPlanActivity();
            updateEntity.setId(activity.getId());
            updateEntity.setLogoUrl(newLogoUrl);

            updateList.add(updateEntity);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            return tpPlanActivityService.updateBatchById(updateList);
        }
        return true;
    }

    private String queryLogoUrl(Long relationId, Integer activityType) {
        TpActivityTypeEnum typeEnum = TpActivityTypeEnum.getTypeEnum(activityType);
        if (typeEnum == null) {
            return null;
        }
        switch (typeEnum) {
            case COURSE:
                CourseVo course = courseClient.getOne(relationId);
                return course == null ? null : course.getImage();
            case EXAM:
                Exam exam = examClient.selectExamById(relationId);
                return exam == null ? null : exam.getImage();
            case RESEARCH:
                ResearchVo research = researchClient.getOne(relationId);
                return research == null ? null : research.getImage();
            case LIVE:
                LiveActivityVO live = liveActivityClient.getLive(relationId);
                return live == null ? null : live.getLogoImage();
            case ASSIGNMENT:
                Assignment assignment = assignmentClient.get(relationId);
                return assignment == null ? null : assignment.getImage();
            case OFFLINE_COURSE:
                OfflineCourseVo offlineCourse = offlineCourseClient.get(relationId);
                return offlineCourse == null ? null : offlineCourse.getImage();
            case CASE_ACTIVITY:
                CaseLibraryVO caseLibrary = caseLibraryClient.getCaseLibrary(relationId);
                return caseLibrary == null ? null : caseLibrary.getLogoUrl();
            case SELECT_CASE:
                FavoriteVO favorite = studentCaseClient.getStudentCaseDetail(relationId);
                return favorite == null ? null : favorite.getStudentCaseLogoUrl();
            case PRACTICE:
                BizResponse<PracticeVo> response = practiceClient.getPractice(relationId);
                return response == null || response.getData() == null ? null : response.getData().getLogoUrl();
            case ANSWER_ACTIVITY:
                PkActivityVO pkActivity = pkActivityClient.getActivityDetails(relationId);
                return pkActivity == null ? null : pkActivity.getLogo();
            case COMMITMENT_LETTER:
                CommitmentLetterVo commitmentLetterVo = commitmentLettersClient.getCommitmentLetter(relationId, null);
                return commitmentLetterVo == null ? null : commitmentLetterVo.getLogo();
            default:
                return null;
        }
    }
}
