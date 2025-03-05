package com.yizhi.training.application.v2.service.biz;

import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.model.SiteOrgIdModel;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.system.remote.SiteClient;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.TpViewRecordService;
import com.yizhi.training.application.v2.model.total.AccountNumVO;
import com.yizhi.training.application.v2.service.TpAuthorizationRangeService;
import com.yizhi.training.application.v2.service.TrainingProjectService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TpStudentTeacherReportBizServices {

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private SiteClient siteClient;

    @Autowired
    private TpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private ITpStudentProjectRecordService tpRecordService;

    @Autowired
    private TpViewRecordService tpViewRecordService;

    public AccountNumVO getAccountNumTotal(Long tpId) {
        // 更具可见范围查询用户总人数
        TrainingProject trainingProject = trainingProjectService.getById(tpId);
        RequestContext context = ContextHolder.get();
        AccountNumVO vo = new AccountNumVO();

        // 用户总数
        vo.setTotalAccountCount(getTpCount(trainingProject));

        // 完成的用户
        Integer finishedCount =
            tpRecordService.getFinishedAccountCount(tpId, context.getCompanyId(), context.getSiteId());
        vo.setFinishedAccountCount(finishedCount);
        //进行中的用户
        Integer learningCount = tpViewRecordService.getLearningCount(tpId, context.getCompanyId(), context.getSiteId());
        vo.setLearningAccountCount(learningCount);
        return vo;
    }

    /**
     * 获取项目的总人数
     *
     * @param trainingProject
     * @return
     */
    private Integer getTpCount(TrainingProject trainingProject) {
        if (trainingProject.getVisibleRange().equals(1)) {
            // 平台可见
            return siteClient.getSitAccountCount(trainingProject.getSiteId(), trainingProject.getCompanyId());
        }
        List<TpAuthorizationRange> rangeList =
            tpAuthorizationRangeService.getAuthorizationRanges(trainingProject.getId());
        if (CollectionUtils.isEmpty(rangeList)) {
            return 0;
        }

        Map<Integer, List<TpAuthorizationRange>> mapVisiableType =
            rangeList.stream().collect(Collectors.groupingBy(TpAuthorizationRange::getType));
        List<TpAuthorizationRange> accountType = mapVisiableType.get(2);
        Integer accountCount = 0;
        if (CollectionUtils.isNotEmpty(accountType)) {
            accountCount = accountType.size();
        }

        List<TpAuthorizationRange> orgType = mapVisiableType.get(1);
        if (CollectionUtils.isEmpty(orgType)) {
            return accountCount;
        }

        SiteOrgIdModel model = new SiteOrgIdModel();
        model.setSiteId(trainingProject.getSiteId());
        model.setOrgIds(orgType.stream().map(TpAuthorizationRange::getRelationId).collect(Collectors.toList()));
        Integer orgAccountCount = accountClient.getSiteOrgWithChildAccountNum(model);
        if (orgAccountCount == null) {
            return accountCount;
        }
        return orgAccountCount + accountCount;
    }
}
