package com.yizhi.training.application.v2.service.biz;

import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountRangeVo;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.template.application.enums.RemindRange;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.ITpStudentPlanRecordService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.util.CacheUtil;
import com.yizhi.training.application.v2.enums.TpAuthorizationTypeEnum;
import com.yizhi.training.application.v2.enums.TpVisibleRangeEnum;
import com.yizhi.training.application.v2.model.VisibleRangeModel;
import com.yizhi.training.application.v2.service.TpAuthorizationRangeService;
import com.yizhi.training.application.v2.service.TrainingProjectService;
import com.yizhi.training.application.v2.vo.TpVisibleRangeVO;
import com.yizhi.training.application.v2.vo.request.SaveTpVisibleRangeRequestVO;
import com.yizhi.training.application.v2.vo.request.TpMessageRangeVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TpAuthorizationRangeBizService {

    @Autowired
    private TpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private ITpStudentProjectRecordService projectRecordService;

    @Autowired
    private ITpStudentPlanRecordService planRecordService;

    @Autowired
    private CacheUtil cacheUtil;

    /**
     * 查询可见范围列表
     *
     * @param tpId
     * @return
     */
    public List<TpVisibleRangeVO> getTpAuthorizationRangeList(Long tpId) {
        List<TpAuthorizationRange> ranges = tpAuthorizationRangeService.getAuthorizationRanges(tpId);
        if (CollectionUtils.isEmpty(ranges)) {
            return Collections.emptyList();
        }
        List<TpVisibleRangeVO> result = new ArrayList<>();
        List<TpVisibleRangeVO> userList = new ArrayList<>();
        ranges.forEach(o -> {
            TpVisibleRangeVO vo = new TpVisibleRangeVO();
            BeanUtils.copyProperties(o, vo);
            result.add(vo);

            if (TpAuthorizationTypeEnum.USER.getCode().equals(o.getType())) {
                userList.add(vo);
            }
        });

        if (CollectionUtils.isNotEmpty(userList)) {
            List<Long> accountIds = userList.stream().map(TpVisibleRangeVO::getRelationId).collect(Collectors.toList());
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

        return result;
    }

    /**
     * 指定用户可见范围
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveTpVisibleRange(SaveTpVisibleRangeRequestVO request) {
        return tpAuthorizationRangeService.saveVisibleRange(request.getTrainingProjectId(), request.getVisibleRanges());
    }

    public List<Long> getMessageRange(TpMessageRangeVO vo) {
        if (vo.getTrainingProjectId() == null || vo.getTrainingProjectId() == 0) {
            return Collections.emptyList();
        }
        TrainingProject tp = trainingProjectService.getById(vo.getTrainingProjectId());
        if (tp == null) {
            return Collections.emptyList();
        }
        List<Long> visibleRange = getVisibleAccounts(vo.getTrainingProjectId());
        if (CollectionUtils.isEmpty(visibleRange)) {
            return Collections.emptyList();
        }
        if (RemindRange.basic_visible.getCode().equals(vo.getSendRange())) {
            return visibleRange;
        }
        if (RemindRange.training_not_passed.getCode().equals(vo.getSendRange())) {
            List<Long> finishAccountIds;
            if (vo.getTpPlanId() != null && vo.getTpPlanId() > 0) {
                // 计划未完成
                finishAccountIds =
                    planRecordService.getFinishedAccountIds(vo.getTpPlanId(), tp.getCompanyId(), tp.getSiteId());
            } else {
                // 项目未完成
                finishAccountIds =
                    projectRecordService.getFinishedAccountIds(tp.getId(), tp.getCompanyId(), tp.getSiteId());
            }
            if (CollectionUtils.isNotEmpty(finishAccountIds)) {
                visibleRange.removeAll(finishAccountIds);
            }
            return visibleRange;
        }
        return Collections.emptyList();
    }

    /**
     * 查询可见的账号列表
     *
     * @param trainingProjectId
     * @return
     */
    public List<Long> getVisibleAccounts(Long trainingProjectId) {
        TrainingProject tp = trainingProjectService.getById(trainingProjectId);
        if (tp == null) {
            return Collections.emptyList();
        }
        if (TpVisibleRangeEnum.PLATFORM_USER.getCode().equals(tp.getVisibleRange())) {
            // 平台用户可见，查询站点下用户
            List<AccountVO> accountVOS = accountClient.findBySiteId(tp.getSiteId());
            return accountVOS.stream().map(AccountVO::getId).collect(Collectors.toList());
        } else if (TpVisibleRangeEnum.SPECIFIC_USER.getCode().equals(tp.getVisibleRange())) {
            //1：部门、2：用户
            List<TpAuthorizationRange> ranges = tpAuthorizationRangeService.getAuthorizationRanges(trainingProjectId);
            List<Long> visibleAccountIds =
                ranges.stream().filter(range -> TpAuthorizationTypeEnum.USER.getCode().equals(range.getType()))
                    .map(TpAuthorizationRange::getRelationId).collect(Collectors.toList());
            List<Long> visibleOrgIds =
                ranges.stream().filter(range -> TpAuthorizationTypeEnum.ORGANIZATION.getCode().equals(range.getType()))
                    .map(TpAuthorizationRange::getRelationId).collect(Collectors.toList());
            AccountRangeVo rangeVo = new AccountRangeVo();
            rangeVo.setCompanyId(tp.getCompanyId());
            rangeVo.setAccountIds(visibleAccountIds);
            rangeVo.setOrgIds(visibleOrgIds);
            List<AccountVO> rangeAccountList = accountClient.getRangeAccountList(rangeVo);
            return rangeAccountList.stream().map(AccountVO::getId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 查询项目可见范围的用户ID
     *
     * @param trainingProjectId 项目ID
     * @return
     */
    public boolean checkAccountIdVisible(Long trainingProjectId, Long accountId) {
        VisibleRangeModel authorizationAccountIdList = cacheUtil.getAuthorizationAccountIdList(trainingProjectId);
        if (Objects.nonNull(authorizationAccountIdList)) {
            //缓存中存在
            if (TpVisibleRangeEnum.PLATFORM_USER.getCode().equals(authorizationAccountIdList.getType())) {
                // 平台用户可见，查询站点下用户
                return Boolean.TRUE;
            }
            Set<Long> accountSet = authorizationAccountIdList.getAccountSet();
            if (CollectionUtils.isNotEmpty(accountSet) && accountSet.contains(accountId)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        TrainingProject byId = trainingProjectService.getById(trainingProjectId);
        if (Objects.isNull(byId)) {
            return Boolean.FALSE;
        }
        if (TpVisibleRangeEnum.PLATFORM_USER.getCode().equals(byId.getVisibleRange())) {
            cacheUtil.addAuthorizationAccountIdList(new HashSet<>(), trainingProjectId,
                TpVisibleRangeEnum.PLATFORM_USER.getCode());
            return Boolean.TRUE;
        }
        //用户
        List<TpAuthorizationRange> ranges = tpAuthorizationRangeService.getAuthorizationRanges(trainingProjectId);
        if (CollectionUtils.isEmpty(ranges)) {
            return Boolean.FALSE;
        }
        Set<Long> accountIdSet = new HashSet<>();
        List<Long> visibleAccountIds =
            ranges.stream().filter(range -> TpAuthorizationTypeEnum.USER.getCode().equals(range.getType()))
                .map(TpAuthorizationRange::getRelationId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(visibleAccountIds)) {
            accountIdSet.addAll(visibleAccountIds);
        }
        //部门
        List<Long> visibleOrgIds =
            ranges.stream().filter(range -> TpAuthorizationTypeEnum.ORGANIZATION.getCode().equals(range.getType()))
                .map(TpAuthorizationRange::getRelationId).collect(Collectors.toList());
        AccountRangeVo rangeVo = new AccountRangeVo();
        rangeVo.setCompanyId(byId.getCompanyId());
        rangeVo.setOrgIds(visibleOrgIds);
        Set<Long> rangeAccountIdList = accountClient.getRangeAccountIdList(rangeVo);
        if (CollectionUtils.isNotEmpty(rangeAccountIdList)) {
            accountIdSet.addAll(rangeAccountIdList);
        }
        cacheUtil.addAuthorizationAccountIdList(accountIdSet, trainingProjectId,
            TpVisibleRangeEnum.SPECIFIC_USER.getCode());
        return accountIdSet.contains(accountId);
    }
}
