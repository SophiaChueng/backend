package com.yizhi.training.application.v2.service.biz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.lecturer.application.vo.LecturerListVO;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.TpAuthorizationRangeMapper;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.service.TpViewRecordService;
import com.yizhi.training.application.v2.TpProEnum;
import com.yizhi.training.application.v2.enums.TpDirectoryItemTypeEnum;
import com.yizhi.training.application.v2.enums.TpExceptionCodeEnum;
import com.yizhi.training.application.v2.service.*;
import com.yizhi.training.application.v2.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TpIntroduceBizServices {

    @Autowired
    private TpProMappingService tpProMappingService;

    @Autowired
    private TpAuthorizationRangeMapper tpAuthorizationRangeMapper;

    @Autowired
    private TpViewRecordService tpViewRecordService;

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private ITpPlanActivityService activityService;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private TpEnrollService tpEnrollService;

    @Autowired
    private TrainingProjectProService trainingProjectProService;

    @Autowired
    private TpIntroduceDirectoryService tpIntroduceDirectoryService;

    @Autowired
    private TpRichTextService tpRichTextService;

    @Autowired
    private TpConsultEntranceService tpConsultEntranceService;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private TpPlanStudyTimeConditionService tpPlanStudyTimeConditionService;

    @Autowired
    private TpPlanConditionPostService tpPlanConditionPostService;

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private TrainingProjectBizService projectBizService;

    @Autowired
    private TpStudyBizService tpStudyBizService;

    public TpStatusDetailsVO getStatusDetails(Long tpId, Integer tpType) {
        TpStatusDetailsVO vo = new TpStatusDetailsVO();
        RequestContext requestContext = ContextHolder.get();
        Long accountId = requestContext.getAccountId();
        Long siteId = requestContext.getSiteId();
        Long companyId = requestContext.getCompanyId();
        if (TpProEnum.TP_PRO.getCode().equals(tpType)) {
            // 查询可参加的项目
            List<OnlineTpVO> tpListByProId = getTpListByProId(tpId);
            if (CollectionUtils.isEmpty(tpListByProId)) {
                log.error("无法查询到可以参见的项目 tpId:{},accountId:{},siteId:{}", tpId, accountId, siteId);
                throw new BizException(TpExceptionCodeEnum.TP_PRO_NOT_EXISTR_VISIABLE_TP.getCode(),
                    TpExceptionCodeEnum.TP_PRO_NOT_EXISTR_VISIABLE_TP.getDescription());
            }
            TrainingProjectPro projectPro = trainingProjectProService.getById(tpId);
            vo.setTpProName(projectPro.getTpProName());
            // 最后一次点击学习的项目
            List<Long> tpIdList = tpListByProId.stream().map(OnlineTpVO::getTpId).collect(Collectors.toList());
            Long lastStudyTpId = tpViewRecordService.getLastStudyTpId(tpIdList, accountId, companyId, siteId);
            if (null == lastStudyTpId) {
                tpId = tpListByProId.get(0).getTpId();
            } else {
                tpId = lastStudyTpId;
            }
        }
        TrainingProject trainingProject = trainingProjectService.getById(tpId);

        vo.setTpId(tpId);
        vo.setTpName(trainingProject.getName());
        vo.setLogo(trainingProject.getLogoImg());
        vo.setTpStartAt(trainingProject.getStartTime());
        vo.setTpEndAt(trainingProject.getEndTime());
        vo.setPointCount(trainingProject.getPoint());
        List<Long> tpPlanIds = tpPlanService.getTpPlanIds(CollUtil.toList(tpId));
        ArrayList<Long> tpIds = CollUtil.toList(tpId);
        tpIds.addAll(tpPlanIds);
        Integer certificateCount = certificateClient.getCertificateCount(tpIds);

        vo.setCertificateCount(certificateCount);

        Integer activityCount = activityService.getExcCertificateActivityNumByTpId(tpId);
        vo.setActivityCount(activityCount);

        String studyHour = tpStudyBizService.getStudyHour(tpId);
        vo.setStudyHourCount(studyHour);

        vo.setEnableEnroll(trainingProject.getEnableEnroll());
        // 需要报名
        setEnrollDetails(vo, accountId, trainingProject);
        // 查询详情及状态
        //设置系统时间时间戳
        vo.setSystemTime(System.currentTimeMillis());
        return vo;
    }

    public List<OnlineTpVO> getTpListByProId(Long tpProId) {
        List<TrainingProject> tpListOfPro = tpProMappingService.getTpListOfPro(tpProId, null, 1, 1000, 1);

        RequestContext requestContext = ContextHolder.get();
        // 可见的项目
        List<Long> authTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(requestContext.getRelationIds(),
            requestContext.getSiteId());
        if (CollectionUtils.isEmpty(authTpIds)) {
            authTpIds = new ArrayList<>();
        }
        List<Long> finalAuthTpIds = authTpIds;
        List<TrainingProject> collect =
            tpListOfPro.stream().filter(it -> it.getVisibleRange().equals(1) || finalAuthTpIds.contains(it.getId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return null;
        }
        List<OnlineTpVO> list = new ArrayList<>();
        collect.stream().forEach(it -> {
            OnlineTpVO vo = new OnlineTpVO();
            vo.setName(it.getName());
            vo.setTpId(it.getId());
            vo.setStartAt(it.getStartTime());
            vo.setEndAt(it.getEndTime());
            list.add(vo);
        });
        return list;
    }

    public List<TpIntroduceVO> getIntroduceItemDetails(Long tpId) {
        if(Objects.isNull(tpId)){
            return null;
        }
        List<TpIntroduceDirectory> directoryList = tpIntroduceDirectoryService.getIntroduceDirectory(tpId);
        if (CollectionUtils.isEmpty(directoryList)) {
            return null;
        }
        TrainingProject trainingProject = trainingProjectService.getById(tpId);

        List<TpIntroduceVO> vos = new ArrayList<>();
        List<TpIntroduceLecturerVO> lecturer = getLecturer(tpId);
        ;
        directoryList.stream().forEach(item -> {
            TpIntroduceVO vo = new TpIntroduceVO();
            Integer itemType = item.getItemType();
            vo.setItemType(itemType);
            vo.setSort(directoryList.indexOf(item));
            vo.setTitle(item.getItemName());
            if (itemType == TpDirectoryItemTypeEnum.BRIEF_INTRODUCE.getCode()) {
                // 简介
                vo.setContent(trainingProject.getDescription());
                vo.setLecturer(lecturer);
                vos.add(vo);
            } else if (itemType == TpDirectoryItemTypeEnum.INTRODUCE_RICH_TEXT.getCode()) {
                // 富文本
                TpRichText richText = tpRichTextService.getRichText(tpId, 1, item.getId());
                if (richText != null) {
                    vo.setContent(richText.getContent());
                    vos.add(vo);
                }
            } else if (itemType == TpDirectoryItemTypeEnum.RICH_TEXT.getCode()) {
                // 学习页关联的富文本
                TpRichText richText = tpRichTextService.getRichText(tpId, 0, item.getItemId());
                if (richText != null) {
                    vo.setContent(richText.getContent());
                    vos.add(vo);
                }
            } else if (itemType == TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode()) {
                // 咨询
                TpConsultEntrance one = tpConsultEntranceService.getOne(tpId, item.getId());
                if (one != null) {
                    vo.setShowService(true);
                    vo.setServiceLogo(one.getEntranceImg());
                    vo.setServiceName(one.getEntranceName());
                    vos.add(vo);
                }

            } else if (itemType == TpDirectoryItemTypeEnum.COMMENT.getCode()) {
                // 评论
                vo.setShowComment(true);
                vos.add(vo);
            } else {
                // 学习单元
                List<TpIntroduceContentVO> items = getStudyItems(tpId, item);
                if (items == null)
                    return;
                vo.setStudyItmes(items);
                vos.add(vo);
            }
        });
        return vos;
    }

    public List<TpIntroduceLecturerVO> getLecturer(Long tpId) {
        List<LecturerListVO> tpLecturers = projectBizService.getTpLecturers(tpId);
        if (CollectionUtils.isEmpty(tpLecturers)) {
            return null;
        }
        List<TpIntroduceLecturerVO> lecturer = new ArrayList<>();
        tpLecturers.stream().forEach(it -> {
            TpIntroduceLecturerVO vo = new TpIntroduceLecturerVO();
            vo.setLecturerName(it.getLecturerName());
            vo.setLecturerAvatar(it.getAvatar());
            vo.setLecturerTitle(it.getTitle());
            vo.setLecturerId(it.getId());
            lecturer.add(vo);
        });
        return lecturer;
    }

    private void setEnrollDetails(TpStatusDetailsVO vo, Long accountId, TrainingProject trainingProject) {
        if (trainingProject.getEnableEnroll() == null || trainingProject.getEnableEnroll() != 1) {
            return;
        }
        TpEnroll tpEnroll = tpEnrollService.selectByTpId(trainingProject.getId());
        if (tpEnroll == null) {
            return;
        }

        // 0：不需要审核；1：需要审核
        vo.setPayType(tpEnroll.getPayType());
        vo.setEnrollEndAt(tpEnroll.getEndTime());
        vo.setEnrollStartAt(tpEnroll.getStartTime());
        //        if (tpEnroll.getPayType().equals(TpEnrollPayTypeEnum.VIRTUAL_COIN.getCode())
        //                || tpEnroll.getPayType().equals(TpEnrollPayTypeEnum.VIRTUAL_COIN_OR_EXCHANGE_CODE.getCode()
        //                )) {
        //        }
        vo.setTpEnrollUserLimit(tpEnroll.getPersonLimitNum());

        vo.setEnrollActualPrice(tpEnroll.getActualPrice());
        vo.setEnrollOriginalPrice(tpEnroll.getOriginalPrice());

        Integer auditStatus = tpEnrollService.getEnrollStatus(trainingProject.getId(), accountId);
        if (auditStatus == null) {
            vo.setEnrollStatus(0);
        } else {
            vo.setEnrollStatus(1);
            vo.setEnrollAuditStatus(auditStatus);
        }
        Integer count = tpEnrollService.getEnrollUserCount(trainingProject.getId(), tpEnroll.getNeedAudit());
        vo.setTpEnrollUserCount(count);
        // vo.getTpEnrollUserLimit() 不等于0时，才限制人数
        if (vo.getTpEnrollUserCount() != null && vo.getTpEnrollUserLimit() != null && vo.getTpEnrollUserLimit() > 0 && vo.getTpEnrollUserCount() >= vo.getTpEnrollUserLimit()) {
            vo.setEnrollAuditStatus(4);
        }
    }

    /**
     * 获取学习单元
     *
     * @param tpId
     * @param item
     * @return
     */
    private List<TpIntroduceContentVO> getStudyItems(Long tpId, TpIntroduceDirectory item) {
        List<TpPlan> tpPlans = tpPlanService.getTpPlans(tpId, item.getItemId());
        if (CollectionUtils.isEmpty(tpPlans)) {
            return null;
        }
        List<TpIntroduceContentVO> items = new ArrayList<>();
        tpPlans.stream().forEach(plan -> {
            TpIntroduceContentVO vo = new TpIntroduceContentVO();
            // 单元名
            vo.setName(plan.getName());
            String studyTime = tpStudyBizService.getStudyTimeStr(plan, null);
            // 学习时间
            vo.setStudyDateStr(studyTime);
            // 单元中的活动
            List<TpPlanActivity> byTpPlanList = activityService.getByTpPlanId(plan.getId());
            if (CollectionUtils.isEmpty(byTpPlanList)) {
                return;
            }
            List<TpIntroduceContentItemVO> activitys = new ArrayList<>();

            // 查询必学的活动Id
            List<TpPlanConditionPost> needActivity = tpPlanConditionPostService.getConditionPosts(plan.getId());
            List<Long> needActivityId = null;
            if (!CollectionUtils.isEmpty(needActivity)) {
                needActivityId =
                    needActivity.stream().map(TpPlanConditionPost::getTpPlanActivityId).collect(Collectors.toList());
            }
            List<Long> finalNeedActivityId = needActivityId;
            byTpPlanList.stream().forEach(activity -> {
                TpIntroduceContentItemVO studyItem = new TpIntroduceContentItemVO();
                studyItem.setName(
                    StrUtil.isEmpty(activity.getCustomizeName()) ? activity.getName() : activity.getCustomizeName());
                studyItem.setType(activity.getType());
                // 设置是否必学
                if (!CollectionUtils.isEmpty(finalNeedActivityId) && finalNeedActivityId.contains(activity.getId())) {
                    studyItem.setMustStudy(1);
                }
                activitys.add(studyItem);
            });
            vo.setItemVOList(activitys);
            items.add(vo);
        });
        return items;
    }

}
