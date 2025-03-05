package com.yizhi.training.application.v2.service.biz;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.hierarchicalauthorization.HQueryUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.certificate.application.enums.CertificateEnum;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.certificate.application.vo.CertificateStrategyVO;
import com.yizhi.certificate.application.vo.SaveRelationCertificateParamVO;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.enums.LearnPayTypeEnum;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.documents.application.feign.DocumentRelationClient;
import com.yizhi.forum.application.feign.PostsRelationClient;
import com.yizhi.lecturer.application.enums.LecturerRelationTypeEnum;
import com.yizhi.lecturer.application.feign.LecturerClient;
import com.yizhi.lecturer.application.vo.LecturerListVO;
import com.yizhi.system.application.enums.MemberResourceEnum;
import com.yizhi.system.application.request.member.SaveMemberResourceRequest;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.system.remote.MemberClient;
import com.yizhi.system.application.vo.AccountRangeVo;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.util.CacheUtil;
import com.yizhi.training.application.v2.constant.TrainingProjectConstant;
import com.yizhi.training.application.v2.enums.*;
import com.yizhi.training.application.v2.model.SearchTpConditionBO;
import com.yizhi.training.application.v2.service.*;
import com.yizhi.training.application.v2.vo.*;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.SearchProjectVO;
import com.yizhi.training.application.v2.vo.request.UpdateTpBriefIntroduceRequestVO;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import com.yizhi.util.application.domain.BizResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainingProjectBizService {

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private TpPlanActivityService tpPlanActivityService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpConditionPostService tpConditionPostService;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private TpPlanConditionPostService tpPlanConditionPostService;

    @Autowired
    private TpPlanConditionPreService tpPlanConditionPreService;

    @Autowired
    private TpPlanStudyTimeConditionService tpPlanStudyTimeConditionService;

    @Autowired
    private TpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private TpSignService tpSignService;

    @Autowired
    private TpSignTimeService tpSignTimeService;

    @Autowired
    private TpEnrollService tpEnrollService;

    @Autowired
    private TpStudyDirectoryService tpStudyDirectoryService;

    @Autowired
    private TpIntroduceDirectoryService tpIntroduceDirectoryService;

    @Autowired
    private TpRichTextService tpRichTextService;

    @Autowired
    private TpConsultEntranceService tpConsultEntranceService;

    @Autowired
    private PostsRelationClient postsRelationClient;

    @Autowired
    private DocumentRelationClient documentRelationClient;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private TpHeadTeacherService tpHeadTeacherService;

    @Autowired
    private LecturerClient lecturerClient;

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private TpAnnouncementService tpAnnouncementService;

    @Autowired
    private TpProMappingService tpProMappingService;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private MemberClient memberClient;

    /**
     * 查询项目列表（筛选）
     *
     * @param request
     * @return
     */
    public PageDataVO<TrainingProjectVO> getProjectList(SearchProjectVO request) {
        // companyId的获取
        RequestContext context = ContextHolder.get();

        PageDataVO<TrainingProjectVO> pageData = new PageDataVO<>();
        pageData.setPageNo(request.getPageNo());
        pageData.setPageSize(request.getPageSize());

        SearchTpConditionBO condition = new SearchTpConditionBO();
        BeanUtils.copyProperties(request, condition);
        condition.setCompanyId(context.getCompanyId());
        condition.setSiteId(context.getSiteId());
        condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
        if (TpEnrollStatusEnum.NO_ENROLL.getCode().equals(request.getEnrollStatus())) {
            condition.setEnableEnroll(0);
        } else if (TpEnrollStatusEnum.FREE_ENROLL.getCode().equals(request.getEnrollStatus())) {
            condition.setEnableEnroll(1);
            condition.setEnablePay(0);
        } else if (TpEnrollStatusEnum.PAY_ENROLL.getCode().equals(request.getEnrollStatus())) {
            condition.setEnableEnroll(1);
            condition.setEnablePay(1);
        }
        if (condition.getProcessStatus() != null) {
            condition.setCurrent(new Date());
        }
        HQueryUtil.startHQ(TrainingProject.class);
        pageData.setTotal(trainingProjectService.getTrainingProjectCount(condition));
        if (pageData.getTotal() > 0) {
            HQueryUtil.startHQ(TrainingProject.class);
            List<TrainingProject> list = trainingProjectService.getTrainingProjectList(condition);
            List<TrainingProjectVO> records =
                BeanCopyListUtil.copyListProperties(list, TrainingProjectVO::new, (s, t) -> {
                    if (t.getEnablePay() == null) {
                        t.setEnablePay(0);
                    }
                    if (t.getPayType() == null) {
                        t.setPayType(TpEnrollPayTypeEnum.FREE.getCode());
                    }
                });
            pageData.setRecords(records);
        }
        return pageData;
    }

    /**
     * 更新项目排序
     *
     * @param tpId
     * @param sort
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSort(Long tpId, Integer sort) {
        return trainingProjectService.updateSort(tpId, sort);
    }

    /**
     * 上架
     *
     * @param tpId
     * @return
     */
    public Boolean putOnShelf(Long tpId) {
        // 上架前校验
        if (!validatePutOnShelf(tpId)) {
            return false;
        }
        Boolean aBoolean = trainingProjectService.putOnShelf(tpId);
        if (aBoolean) {
            //根据支付类型处理 会员内容
            int learnPay = 0;
            TrainingProject byId = trainingProjectService.getById(tpId);
            if (byId.getEnableEnroll() == 1) {
                TpEnroll tpEnroll = tpEnrollService.selectByTpId(tpId);
                learnPay = tpEnroll.getPayType();
            }
            this.updateMemberResource(tpId, MemberResourceEnum.TRAINING.getType(), learnPay, 1);
        }
        return aBoolean;
    }

    /**
     * 下架
     *
     * @param tpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean putOffShelf(Long tpId) {
        TrainingProject project = trainingProjectService.getById(tpId);
        if (project == null || !TpStatusEnum.IN_USE.getCode().equals(project.getStatus())) {
            throw new BizException(TpExceptionCodeEnum.PROJECT_STATUS_IS_NOT_ON.getCode(),
                TpExceptionCodeEnum.PROJECT_STATUS_IS_NOT_ON.getDescription());
        }
        Boolean aBoolean = trainingProjectService.putOffShelf(tpId);
        if (aBoolean) {
            //根据支付类型处理 会员内容
            int learnPay = 0;
            TrainingProject byId = trainingProjectService.getById(tpId);
            if (byId.getEnableEnroll() == 1) {
                TpEnroll tpEnroll = tpEnrollService.selectByTpId(tpId);
                learnPay = tpEnroll.getPayType();
            }
            this.updateMemberResource(tpId, MemberResourceEnum.TRAINING.getType(), learnPay, 0);
        }
        return aBoolean;
    }

    /**
     * 复制项目
     *
     * @param tpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean copyProject(Long tpId) {
        RequestContext requestContext = ContextHolder.get();
        // 复制基础信息
        TrainingProject oldProject = trainingProjectService.getById(tpId);
        if (oldProject == null) {
            return false;
        }
        TrainingProject newProject = new TrainingProject();
        BeanUtils.copyProperties(oldProject, newProject);
        newProject.setId(idGenerator.generate());
        newProject.setName(oldProject.getName() + "[复制]");
        // 报名信息和签到信息不复制
        newProject.setEnableEnroll(0);
        newProject.setEnableSign(0);
        newProject.setStatus(TpStatusEnum.DRAFT.getCode());
        newProject.setCreateById(requestContext.getAccountId());
        newProject.setCreateByName(requestContext.getAccountName());
        newProject.setUpdateByName(requestContext.getAccountName());
        newProject.setUpdateById(requestContext.getAccountId());
        newProject.setCreateTime(new Date());
        newProject.setUpdateTime(new Date());
        trainingProjectService.save(newProject);

        // 复制学习页目录，包括学习单元，以及学习活动
        Map<Long, Long> studyDirIdOldToNewMap = tpStudyDirectoryService.copyByTp(tpId, newProject.getId());

        // 复制公告
        tpAnnouncementService.copyByTp(tpId, newProject.getId(), studyDirIdOldToNewMap);

        // 复制介绍页
        Map<Long, Long> introDirIdOldToNewMap =
            tpIntroduceDirectoryService.copyByTp(tpId, newProject.getId(), studyDirIdOldToNewMap);

        // 复制咨询
        tpConsultEntranceService.copyByTp(tpId, newProject.getId(), introDirIdOldToNewMap);

        // 复制富文本
        tpRichTextService.copyByTp(tpId, newProject.getId(), studyDirIdOldToNewMap, introDirIdOldToNewMap);

        // 复制学习单元/计划和学习活动
        Map<Long, Long> planIdOldToNewMap = tpPlanService.copyByTp(tpId, newProject.getId(), studyDirIdOldToNewMap);

        // 复制学习活动
        Map<Long, Long> actIdOldToNewMap = tpPlanActivityService.copyByTp(tpId, newProject.getId(), planIdOldToNewMap);

        // 复制学习计划前置条件
        tpPlanConditionPreService.copyByTp(tpId, newProject.getId(), planIdOldToNewMap);

        // 复制学习计划完成条件
        tpPlanConditionPostService.copyByTp(tpId, newProject.getId(), planIdOldToNewMap, actIdOldToNewMap);

        // 复制学习计划完成时间条件
        tpPlanStudyTimeConditionService.copyByTp(tpId, newProject.getId(), planIdOldToNewMap);

        // 复制资料关联关系 远程调用
        // 复制和项目直接关联的
        Map<Long, Long> oldToNewBizIdMap = new HashMap<>(planIdOldToNewMap);
        oldToNewBizIdMap.put(oldProject.getId(), newProject.getId());
        documentRelationClient.copyRelation(oldToNewBizIdMap);

        // 复制帖子关联关系 远程调用
        postsRelationClient.copyPostsRelation(tpId, newProject.getId(), newProject.getName(), null);

        // 复制证书关联关系
        certificateClient.copyCertificateRelation(oldToNewBizIdMap);

        // 复制项目完成条件
        tpConditionPostService.copyByTp(tpId, newProject.getId(), planIdOldToNewMap);

        // 复制讲师
        lecturerClient.copyRelation(oldProject.getId(), newProject.getId(), newProject.getName());
        return true;
    }

    /**
     * 批量删除项目
     *
     * @param tpIds
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer batchDeleteProject(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return 0;
        }

        // 校验有没有上架
        List<TrainingProject> onShelfTps = trainingProjectService.getOnShelves(tpIds);
        if (CollectionUtils.isNotEmpty(onShelfTps)) {
            throw new BizException(TpExceptionCodeEnum.CAN_NOT_DELETE_ON_SHELF.getCode(),
                TpExceptionCodeEnum.CAN_NOT_DELETE_ON_SHELF.getDescription() + ":" + onShelfTps.get(0).getName());
        }

        // 校验有没有在项目pro中
        List<Long> inTpProIds = tpProMappingService.getInTpProIds(tpIds);
        if (CollectionUtils.isNotEmpty(inTpProIds)) {
            TrainingProject canNotDelTp = trainingProjectService.getById(inTpProIds.get(0));
            throw new BizException(TpExceptionCodeEnum.CAN_NOT_DELETE_IN_PRO.getCode(),
                TpExceptionCodeEnum.CAN_NOT_DELETE_IN_PRO.getDescription() + ":" + canNotDelTp.getName());
        }

        // 删除项目
        trainingProjectService.removeBatchByIds(tpIds);
        // 删除项目完成条件
        tpConditionPostService.deleteBatchByTpId(tpIds);
        // 删除学习页目录
        tpStudyDirectoryService.deleteBatchByTpIds(tpIds);
        // 删除介绍页目录
        tpIntroduceDirectoryService.deleteBatchByTpIds(tpIds);
        // 删除学习计划
        List<Long> tpPlanIds = tpPlanService.getTpPlanIds(tpIds);
        tpPlanService.deleteBatchById(tpPlanIds);
        // 删除学习计划条件（学习时间，解锁条件，完成条件）
        tpPlanConditionPostService.deleteBatchByTpPlanId(tpPlanIds);
        tpPlanConditionPreService.deleteBatchByTpPlanId(tpPlanIds);
        tpPlanStudyTimeConditionService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除活动
        List<Long> tpPlanActivityIds = tpPlanActivityService.getTpPlanActivityIds(tpPlanIds);
        tpPlanActivityService.deleteBatch(tpPlanActivityIds);
        // 删除资料关联关系 远程调用
        List<Long> relationIds = new ArrayList<>(tpIds);
        relationIds.addAll(tpPlanIds);
        documentRelationClient.deleteByRelationId(relationIds);
        // 删除帖子关联关系 远程调用
        postsRelationClient.deleteBatchRelation(tpIds);
        // 删除证书关联关系
        List<Long> bizIds = new ArrayList<>(tpIds);
        bizIds.addAll(tpPlanIds);
        certificateClient.deleteBatchRelation(bizIds);
        // 删除富文本
        tpRichTextService.deleteBatchByTpIds(tpIds);
        // 删除咨询
        tpConsultEntranceService.deleteBatchByTpIds(tpIds);
        //删除会员内容
        memberClient.deletedResourceByRelationIds(tpIds);
        return tpIds.size();
    }

    /**
     * 保存基本信息
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TpBaseInfoVO saveTrainingProject(TpBaseInfoVO request) {
        TrainingProject project = new TrainingProject();
        BeanUtils.copyProperties(request, project);

        RequestContext context = ContextHolder.get();

        project.setId(idGenerator.generate());
        project.setCompanyId(context.getCompanyId());
        project.setSiteId(context.getSiteId());
        project.setOrgId(context.getOrgId());

        project.setStatus(TpStatusEnum.DRAFT.getCode());

        if (request.getSort() == null) {
            project.setSort(TrainingProjectConstant.DEFAULT_SORT);
        }

        if (!trainingProjectService.save(project)) {
            return null;
        }
        // 报名
        if (Objects.equals(request.getEnableEnroll(), 1)) {
            TpEnrollVO enrollVO = request.getEnrollInfo();
            TpEnroll enroll = new TpEnroll();
            BeanUtils.copyProperties(enrollVO, enroll);
            enroll.setId(idGenerator.generate());
            enroll.setCompanyId(context.getCompanyId());
            enroll.setSiteId(context.getSiteId());
            enroll.setOrgId(context.getOrgId());
            enroll.setTrainingProjectId(project.getId());

            tpEnrollService.save(enroll);
            //如果开启了报名且是会员项目则绑定会员内容
            if (LearnPayTypeEnum.isMember(enroll.getPayType())) {
                this.updateMemberResource(project.getId(), MemberResourceEnum.TRAINING.getType(), enrollVO.getPayType(),
                    0);
            }
        }

        return getProjectBaseInfo(project.getId());
    }

    /**
     * 查询项目基础信息
     *
     * @param tpId
     * @return
     */
    public TpBaseInfoVO getProjectBaseInfo(Long tpId) {
        TpBaseInfoVO trainingProjectVO = new TpBaseInfoVO();
        if (Objects.isNull(tpId)) {
            log.error("tpId为空结束查询");
            return trainingProjectVO;
        }
        TrainingProject project = trainingProjectService.getById(tpId);
        if (Objects.isNull(project)) {
            log.error("未查询到数据tpId={}", tpId);
            return trainingProjectVO;
        }
        BeanUtils.copyProperties(project, trainingProjectVO);

        if (Objects.equals(project.getEnableEnroll(), 1)) {
            // 若开启了报名，则查询报名信息
            TpEnroll enroll = tpEnrollService.selectByTpId(tpId);
            if (enroll == null) {
                // 脏数据处理，没有报名设置
                trainingProjectVO.setEnableEnroll(0);
            } else {
                TpEnrollVO enrollVO = new TpEnrollVO();
                BeanUtils.copyProperties(enroll, enrollVO);
                trainingProjectVO.setEnrollInfo(enrollVO);
            }
        }

        return trainingProjectVO;
    }

    /**
     * 更新基本信息
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTrainingProject(TpBaseInfoVO request) {
        RequestContext context = ContextHolder.get();

        // 1.校验，只有草稿和下架可以修改
        TrainingProject oldProject = trainingProjectService.getById(request.getId());
        if (oldProject == null || TpStatusEnum.IN_USE.getCode().equals(oldProject.getStatus())) {
            throw new BizException(TpExceptionCodeEnum.JUST_NOT_ON_SHELF_CAN_BE_EDIT.getCode(),
                TpExceptionCodeEnum.JUST_NOT_ON_SHELF_CAN_BE_EDIT.getDescription());
        }
        // 2.更新项目基本信息
        TrainingProject updateProject = new TrainingProject();
        BeanUtils.copyProperties(request, updateProject);
        if (!trainingProjectService.updateTpBaseInfo(updateProject)) {
            return false;
        }
        // 3.如果更新了报名信息，需要进行修改
        int payType = 0;
        if (Objects.equals(request.getEnableEnroll(), 1)) {
            TpEnrollVO enrollVO = request.getEnrollInfo();
            TpEnroll oldEnroll = tpEnrollService.selectByTpId(request.getId());
            TpEnroll enroll = new TpEnroll();
            BeanUtils.copyProperties(enrollVO, enroll);
            if (oldEnroll == null) {
                enroll.setId(idGenerator.generate());
                enroll.setCompanyId(context.getCompanyId());
                enroll.setSiteId(context.getSiteId());
                enroll.setOrgId(context.getOrgId());
                enroll.setTrainingProjectId(request.getId());
                tpEnrollService.save(enroll);
            } else {
                enroll.setId(oldEnroll.getId());
                tpEnrollService.updateById(enroll);
            }
            if (LearnPayTypeEnum.isMember(enroll.getPayType())) {
                payType = enroll.getPayType();
            }
        }
        if (Objects.equals(request.getEnableEnroll(), 0)) {
            payType = LearnPayTypeEnum.FREE.getCode();
        }
        this.updateMemberResource(request.getId(), MemberResourceEnum.TRAINING.getType(), payType, 0);
        return true;
    }

    /**
     * 查询项目高级设置信息
     *
     * @param trainingProjectId
     * @return
     */
    public TpDetailInfoVO getTpDetailInfo(Long trainingProjectId) {
        TpDetailInfoVO detailInfoVO = new TpDetailInfoVO();
        TrainingProject project = trainingProjectService.getById(trainingProjectId);
        if (project == null || Objects.equals(project.getDeleted(), 1)) {
            return null;
        }
        BeanUtils.copyProperties(project, detailInfoVO);
        detailInfoVO.setTrainingProjectId(project.getId());
        // 可见范围
        if (TpVisibleRangeEnum.SPECIFIC_USER.getCode().equals(project.getVisibleRange())) {
            List<TpAuthorizationRange> ranges = tpAuthorizationRangeService.getAuthorizationRanges(trainingProjectId);
            if (CollectionUtils.isNotEmpty(ranges)) {
                List<TpVisibleRangeVO> userList = new ArrayList<>();
                List<TpVisibleRangeVO> visibleRanges =
                    BeanCopyListUtil.copyListProperties(ranges, TpVisibleRangeVO::new, (s, t) -> {
                        if (TpAuthorizationTypeEnum.USER.getCode().equals(t.getType())) {
                            userList.add(t);
                        }
                    });

                if (CollectionUtils.isNotEmpty(userList)) {
                    List<Long> accountIds =
                        userList.stream().map(TpVisibleRangeVO::getRelationId).collect(Collectors.toList());
                    List<AccountVO> accountVOS = accountClient.findByIds(accountIds);
                    if (CollectionUtils.isNotEmpty(accountVOS)) {
                        Map<Long, AccountVO> accountVOMap =
                            accountVOS.stream().collect(Collectors.toMap(AccountVO::getId, o -> o));
                        userList.forEach(o -> {
                            AccountVO accountVO = accountVOMap.get(o.getRelationId());
                            if (accountVO != null) {
                                o.setFullName(accountVO.getFullName());
                                o.setWorkNum(accountVO.getWorkNum());
                            }
                        });
                    }
                }

                detailInfoVO.setVisibleRanges(visibleRanges);
            }

        }
        // 项目完成条件
        List<TpConditionPost> conditions = tpConditionPostService.getCompleteConditions(trainingProjectId);
        TpCompleteConditionVO conditionVO = new TpCompleteConditionVO();
        conditionVO.setConditionPostType(TpCompleteConditionTypeEnum.ALL_PLAN.getCode());
        if (CollectionUtils.isNotEmpty(conditions)) {
            List<Long> tpPlanIds = new ArrayList<>();
            for (TpConditionPost condition : conditions) {
                if (TpCompleteConditionTypeEnum.SPECIFIC_COUNT.getCode().equals(condition.getConditionType())) {
                    conditionVO.setCompleteCount(condition.getCompleteCount());
                } else if (TpCompleteConditionTypeEnum.SPECIFIC_PLANS.getCode().equals(condition.getConditionType())) {
                    tpPlanIds.add(condition.getTpPlanId());
                }
            }
            if (CollectionUtils.isNotEmpty(tpPlanIds)) {
                List<TpPlan> tpPlans = tpPlanService.listByIds(tpPlanIds);
                List<TpPlanVO> tpPlanVOS = BeanCopyListUtil.copyListProperties(tpPlans, TpPlanVO::new);
                conditionVO.setTpPlans(tpPlanVOS);
            }

            boolean numFlag = conditionVO.getCompleteCount() != null && conditionVO.getCompleteCount() > 0;
            boolean specificFlag = CollectionUtils.isNotEmpty(tpPlanIds);
            conditionVO.setConditionPostType(
                numFlag ? (specificFlag ? TpCompleteConditionTypeEnum.COUNT_AND_SPECIFIC.getCode()
                    : TpCompleteConditionTypeEnum.SPECIFIC_COUNT.getCode())
                    : (specificFlag ? TpCompleteConditionTypeEnum.SPECIFIC_PLANS.getCode()
                        : TpCompleteConditionTypeEnum.ALL_PLAN.getCode()));
        }
        detailInfoVO.setCompleteCondition(conditionVO);

        // 证书
        TpCertificateStrategyVO strategyVO = null;
        CertificateStrategyVO certificateStrategyVO =
            certificateClient.getRelationCertificate(trainingProjectId, CertificateEnum.BIZ_TYPE_TRAINING.getCode());
        if (certificateStrategyVO != null && CollectionUtils.isNotEmpty(certificateStrategyVO.getCertificates())) {
            strategyVO = new TpCertificateStrategyVO();
            strategyVO.setIssueStrategy(certificateStrategyVO.getIssueStrategy());
            strategyVO.setCertificates(
                BeanCopyListUtil.copyListProperties(certificateStrategyVO.getCertificates(), TpCertificateVO::new));
        }
        detailInfoVO.setCertificateStrategy(strategyVO);

        // 签到
        TpSignVO tpSignVO = null;
        if (Objects.equals(project.getEnableSign(), 1)) {
            tpSignVO = new TpSignVO();
            TpSign sign = tpSignService.selectByTpId(trainingProjectId);
            tpSignVO.setEnablePosition(sign.getEnablePosition());
            tpSignVO.setEnableRetroactive(sign.getEnableRetroactive());

            List<TpSignTime> signTimes = tpSignTimeService.selectBySignId(trainingProjectId, sign.getId());
            List<TpSignTimeVO> signTimeVOS = BeanCopyListUtil.copyListProperties(signTimes, TpSignTimeVO::new);
            tpSignVO.setTpSignTimes(signTimeVOS);
        }
        detailInfoVO.setSignInfo(tpSignVO);

        // 班主任
        if (Objects.equals(project.getEnableHeadTeacher(), 1)) {
            List<Long> accIds = tpHeadTeacherService.getTeacherIds(trainingProjectId);
            List<AccountVO> accountVOS = accountClient.findByIds(accIds);
            if (CollectionUtils.isNotEmpty(accountVOS)) {
                List<TpHeadTeacherVO> headTeacherVOS =
                    BeanCopyListUtil.copyListProperties(accountVOS, TpHeadTeacherVO::new, (s, t) -> {
                        t.setAccountId(s.getId());
                        t.setTrainingProjectId(trainingProjectId);
                    });
                detailInfoVO.setHeadTeachers(headTeacherVOS);
            }
        }

        return detailInfoVO;
    }

    /**
     * 更新高级设置
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTpDetailInfo(TpDetailInfoVO request) {
        TrainingProject updateProject = new TrainingProject();
        TrainingProject oldProject = trainingProjectService.getById(request.getTrainingProjectId());
        if (oldProject == null) {
            return false;
        }

        RequestContext context = ContextHolder.get();

        // 2.积分，签到，task，enableQueue,提醒，数据统计，班主任
        BeanUtils.copyProperties(request, updateProject);
        updateProject.setId(request.getTrainingProjectId());
        updateProject.setVisibleRange(
            CollectionUtils.isEmpty(request.getVisibleRanges()) ? TpVisibleRangeEnum.PLATFORM_USER.getCode()
                : TpVisibleRangeEnum.SPECIFIC_USER.getCode());
        updateProject.setEnableHeadTeacher(CollectionUtils.isEmpty(request.getHeadTeachers()) ? 0 : 1);
        if (!trainingProjectService.updateById(updateProject)) {
            return false;
        }
        // 4.可见范围详细设置
        if (CollectionUtils.isNotEmpty(request.getVisibleRanges())) {
            tpAuthorizationRangeService.saveVisibleRange(request.getTrainingProjectId(), request.getVisibleRanges());
        }
        // 5.项目完成规则
        tpConditionPostService.updateCompleteCondition(request.getTrainingProjectId(), request.getCompleteCondition());

        // 6.证书

        TpCertificateStrategyVO strategyVO = request.getCertificateStrategy();
        if (strategyVO != null) {
            SaveRelationCertificateParamVO paramVO = new SaveRelationCertificateParamVO();
            paramVO.setBizId(request.getTrainingProjectId());
            paramVO.setBizType(CertificateEnum.BIZ_TYPE_TRAINING.getCode());
            paramVO.setBizName(oldProject.getName());
            paramVO.setTpPlanId(request.getTrainingProjectId());
            paramVO.setTpPlanName(oldProject.getName());
            paramVO.setIssueStrategy(strategyVO.getIssueStrategy());

            if (CollectionUtils.isNotEmpty(strategyVO.getCertificates())) {
                List<Long> certificateIds =
                    strategyVO.getCertificates().stream().map(TpCertificateVO::getId).collect(Collectors.toList());
                paramVO.setCertificateIds(certificateIds);
            }
            certificateClient.saveRelationCertificate(paramVO);
        }

        // 签到
        if (Objects.equals(request.getEnableSign(), 1)) {
            TpSignVO signVO = request.getSignInfo();
            if (signVO == null || CollectionUtils.isEmpty(signVO.getTpSignTimes())) {
                throw new BizException(TpExceptionCodeEnum.PARAM_ERROR.getCode(),
                    TpExceptionCodeEnum.PARAM_ERROR.getDescription());
            }
            TpSign oldSign = tpSignService.selectByTpId(request.getTrainingProjectId());
            TpSign sign = new TpSign();
            BeanUtils.copyProperties(signVO, sign);
            if (oldSign == null) {
                sign.setId(idGenerator.generate());
                sign.setCompanyId(context.getCompanyId());
                sign.setSiteId(context.getSiteId());
                sign.setOrgId(context.getOrgId());
                sign.setTrainingProjectId(request.getTrainingProjectId());
                tpSignService.save(sign);
            } else {
                sign.setId(oldSign.getId());
                tpSignService.updateById(sign);
            }

            tpSignTimeService.updateSignTime(context.getCompanyId(), context.getSiteId(),
                request.getTrainingProjectId(), sign, signVO.getTpSignTimes());
        }

        // 班主任
        if (CollectionUtils.isNotEmpty(request.getHeadTeachers())) {
            List<Long> accountIds = request.getHeadTeachers().stream().map(TpHeadTeacherVO::getAccountId).distinct()
                .collect(Collectors.toList());
            tpHeadTeacherService.saveHeadTeachers(request.getTrainingProjectId(), accountIds);
        }
        //生成可见范围缓存
        generateVisibleAccountIdsCache(request.getTrainingProjectId(), context.getCompanyId(),
            request.getVisibleRanges());
        return true;
    }

    public void generateVisibleAccountIdsCache(Long tpId, Long companyId, List<TpVisibleRangeVO> tpVisibleRanges) {
        Set<Long> accountIdSet = new HashSet<>();
        if (CollectionUtils.isEmpty(tpVisibleRanges)) {
            cacheUtil.addAuthorizationAccountIdList(accountIdSet, tpId, TpVisibleRangeEnum.PLATFORM_USER.getCode());
            return;
        }
        //tpVisibleRanges 按照type分组生成map
        Map<Integer, List<TpVisibleRangeVO>> map =
            tpVisibleRanges.stream().collect(Collectors.groupingBy(TpVisibleRangeVO::getType));
        //用户OR部门
        if (map.containsKey(TpAuthorizationTypeEnum.USER.getCode())) {
            List<TpVisibleRangeVO> tpVisibleRangeVOS = map.get(TpAuthorizationTypeEnum.USER.getCode());
            //tpVisibleRangeVOS 提取relationId 的set集合
            Set<Long> relationIds =
                tpVisibleRangeVOS.stream().map(TpVisibleRangeVO::getRelationId).collect(Collectors.toSet());
            accountIdSet.addAll(relationIds);
        }
        if (map.containsKey(TpAuthorizationTypeEnum.ORGANIZATION.getCode())) {
            List<TpVisibleRangeVO> tpVisibleRangeVOS = map.get(TpAuthorizationTypeEnum.ORGANIZATION.getCode());
            List<Long> orgIds =
                tpVisibleRangeVOS.stream().map(TpVisibleRangeVO::getRelationId).collect(Collectors.toList());
            AccountRangeVo rangeVo = new AccountRangeVo();
            rangeVo.setCompanyId(companyId);
            rangeVo.setOrgIds(orgIds);
            Set<Long> rangeAccountIdList = accountClient.getRangeAccountIdList(rangeVo);
            accountIdSet.addAll(rangeAccountIdList);
        }
        cacheUtil.addAuthorizationAccountIdList(accountIdSet, tpId, TpVisibleRangeEnum.SPECIFIC_USER.getCode());
    }

    /**
     * 查询项目简介
     *
     * @param tpId
     * @return
     */
    public String getTpBriefIntroduce(Long tpId) {
        return trainingProjectService.getDescription(tpId);
    }

    /**
     * 更新项目简介
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTpBriefIntroduce(UpdateTpBriefIntroduceRequestVO request) {
        TrainingProject project = new TrainingProject();
        project.setId(request.getTrainingProjectId());
        project.setDescription(request.getDescription());
        return trainingProjectService.updateById(project);
    }

    /**
     * 查询项目关联的讲师
     *
     * @param trainingProjectId
     * @return
     */
    public List<LecturerListVO> getTpLecturers(Long trainingProjectId) {
        return lecturerClient.getLecturerByRelation(trainingProjectId,
            LecturerRelationTypeEnum.TRAINING_PROJECT.getCode());
    }

    /**
     * 更新项目关联的讲师
     *
     * @param trainingProjectId
     * @param lecturerIds
     * @return
     */
    public Boolean updateTpLecturer(Long trainingProjectId, List<Long> lecturerIds) {
        TrainingProject project = trainingProjectService.getById(trainingProjectId);
        if (project == null) {
            return false;
        }
        return lecturerClient.updateRelationByBiz(trainingProjectId,
            LecturerRelationTypeEnum.TRAINING_PROJECT.getCode(), project.getName(), lecturerIds);
    }

    public Page<RecentStudyTrainingVO> getRecentStudyList(Integer pageNo, Integer pageSize) {
        RequestContext context = ContextHolder.get();
        return trainingProjectService.getRecentStudyList(context.getCompanyId(), context.getSiteId(),
            context.getAccountId(), new Page<RecentStudyTrainingVO>(pageNo, pageSize));
    }

    /**
     * 上架校验
     *
     * @param tpId
     * @return
     */
    private Boolean validatePutOnShelf(Long tpId) {
        // 校验项目是否存在,以及项目状态
        TrainingProject project = trainingProjectService.getById(tpId);
        if (project == null || TpStatusEnum.IN_USE.getCode().equals(project.getStatus())) {
            throw new BizException(TpExceptionCodeEnum.PROJECT_STATUS_IS_ON.getCode(),
                TpExceptionCodeEnum.PROJECT_STATUS_IS_ON.getDescription());
        }
        // 校验项目是否有学习计划
        Integer planCount = tpPlanService.getTpPlanCount(tpId);
        if (planCount == 0) {
            throw new BizException(TpExceptionCodeEnum.PROJECT_NEED_PLAN.getCode(),
                TpExceptionCodeEnum.PROJECT_NEED_PLAN.getDescription());
        }
        // 校验是否有没有学习活动的学习计划
        Integer activityPlanCount = tpPlanActivityService.getTpPlanCount(tpId);
        if (!planCount.equals(activityPlanCount)) {
            throw new BizException(TpExceptionCodeEnum.PROJECT_NEED_ACTIVITY.getCode(),
                TpExceptionCodeEnum.PROJECT_NEED_ACTIVITY.getDescription());
        }
        return true;
    }

    public boolean updateMemberResource(Long tpId, Integer type, Integer learnType, Integer tpStatus) {
        SaveMemberResourceRequest saveMemberResourceRequest = new SaveMemberResourceRequest();
        saveMemberResourceRequest.setRelationId(tpId);
        saveMemberResourceRequest.setType(type);
        saveMemberResourceRequest.setLearnType(learnType);
        saveMemberResourceRequest.setRelationStatus(tpStatus);
        BizResponse<Boolean> booleanBizResponse = memberClient.addOrUpdate(saveMemberResourceRequest);
        log.info("课程更新修改会员内容返回结果={}", JSON.toJSONString(booleanBizResponse));
        return true;
    }
}
