package com.yizhi.training.application.v2.service.biz;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.certificate.application.enums.CertificateEnum;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.certificate.application.vo.CertificateStrategyVO;
import com.yizhi.certificate.application.vo.SaveRelationCertificateParamVO;
import com.yizhi.certificate.application.vo.domain.CertificateVo;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.core.application.file.task.TaskVO;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.task.CetificateReissueAsync;
import com.yizhi.training.application.v2.param.CetificateReissueQO;
import com.yizhi.training.application.v2.service.TpPlanService;
import com.yizhi.training.application.v2.service.TrainingProjectService;
import com.yizhi.training.application.v2.vo.TpCertificateStrategyVO;
import com.yizhi.training.application.v2.vo.TpCertificateVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.AddCertificateToPlanRequestVO;
import com.yizhi.training.application.v2.vo.request.SearchCertificateVO;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import com.yizhi.util.application.enums.i18n.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TpCertificateBizService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private CetificateReissueAsync cetificateReissueAsync;

    /**
     * 证书补发
     *
     * @param tpId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String reissueTpCertificate(Long tpId) {
        // 1. 先补发单元证书
        // 1.1 查询学习单元关联的自动发放的证书id及学习单元id
        // 1.2 遍历学习单元，查询每个单元已完成的用户id
        // 1.2.1 TODO 遍历证书，批量发放，利用消息队列，在“证书”项目中消费该消息

        // 2. 补发项目结业证书
        // 2.1 查询项目关联的自动发放的证书id
        // 2.2 查询已完成项目的用户id
        // 2.2.1 TODO 遍历证书，利用消息队列，在“证书”项目中消费该消息
        //
        // 查询已获取证书的用户Id,计算未获得证书的用户。记录任务
        TrainingProject trainingProject = trainingProjectService.getById(tpId);
        if (trainingProject == null) {
            throw new BizException(Constants.MSG_BIZ_FAIL.getCode().toString(), "项目不存在或已被删除");
        }
        List<TpPlan> tpPlansList = tpPlanService.getTpPlansByTpId(tpId);
        if (CollectionUtils.isEmpty(tpPlansList)) {
            throw new BizException(Constants.MSG_BIZ_FAIL.getCode().toString(), "项目中学习目录不存在或已被删除");
        }

        CetificateReissueQO qo = new CetificateReissueQO();
        qo.setProject(trainingProject);
        qo.setTpPLan(tpPlansList);

        String dateStr = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        String taskName =
            new StringBuilder("证书补发_").append(trainingProject.getName()).append("_").append(dateStr).toString();
        String serialNo =
            new StringBuilder("CETIFICATE_REISSUE_").append(dateStr).append("_").append(RandomUtil.randomString(4))
                .toString();
        TaskVO<CetificateReissueQO> param = new TaskVO<CetificateReissueQO>();
        param.setContext(ContextHolder.get());
        param.setQueryVo(qo);
        param.setTaskId(idGenerator.generate());
        param.setSerialNo(serialNo);
        param.setTaskName(taskName);
        cetificateReissueAsync.execute(param, true);
        return taskName;
    }

    /**
     * 学习单元绑定证书
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean addCertificateToTpPlan(AddCertificateToPlanRequestVO request) {

        TpPlan tpPlan = tpPlanService.getById(request.getTpPlanId());
        if (tpPlan == null) {
            return false;
        }
        TrainingProject trainingProject = trainingProjectService.getById(tpPlan.getTrainingProjectId());
        if (trainingProject == null) {
            return false;
        }
        SaveRelationCertificateParamVO paramVO = new SaveRelationCertificateParamVO();
        paramVO.setBizId(tpPlan.getTrainingProjectId());
        paramVO.setBizType(CertificateEnum.BIZ_TYPE_TP_PLAN.getCode());
        paramVO.setBizName(trainingProject.getName());
        paramVO.setTpPlanId(tpPlan.getId());
        paramVO.setTpPlanName(tpPlan.getName());
        paramVO.setIssueStrategy(request.getIssueStrategy());

        if (CollectionUtils.isEmpty(request.getCertificates())) {
            return certificateClient.saveRelationCertificate(paramVO);
        }

        List<Long> certificateIds =
            request.getCertificates().stream().map(TpCertificateVO::getId).collect(Collectors.toList());
        paramVO.setCertificateIds(certificateIds);
        return certificateClient.saveRelationCertificate(paramVO);
    }

    /**
     * 查询项目绑定的证书列表
     *
     * @param bizType
     * @param bizId
     * @return
     */
    public TpCertificateStrategyVO getBindCertificates(Long bizId, Integer bizType) {
        TpCertificateStrategyVO strategyVO = null;
        CertificateStrategyVO certificateStrategyVO = certificateClient.getRelationCertificate(bizId, bizType);
        if (certificateStrategyVO != null && CollectionUtils.isNotEmpty(certificateStrategyVO.getCertificates())) {
            strategyVO = new TpCertificateStrategyVO();
            strategyVO.setIssueStrategy(certificateStrategyVO.getIssueStrategy());
            strategyVO.setCertificates(
                BeanCopyListUtil.copyListProperties(certificateStrategyVO.getCertificates(), TpCertificateVO::new));
        }
        return strategyVO;
    }

    /**
     * 查询项目可绑定的证书
     *
     * @return
     */
    public PageDataVO<TpCertificateVO> getAllTpCertificates(SearchCertificateVO request) {
        PageDataVO<TpCertificateVO> pageDataVO = new PageDataVO<>();
        pageDataVO.setPageNo(request.getPageNo());
        pageDataVO.setPageSize(request.getPageSize());

        // 绑定证书时，要求查询可以绑定的证书列表
        // 由于一个证书在项目中只能绑定一次，所以，需要项目id以及项目下的所有学习单元id，并且排除掉 ”待绑定证书“的id（bizId）
        // 排除原因：自己绑定的证书，在修改时一样可以绑定
        List<Long> tpPlanIds = tpPlanService.getTpPlanIds(Collections.singletonList(request.getTrainingProjectId()));
        Set<Long> excludeBizIds = new HashSet<>(tpPlanIds);
        excludeBizIds.add(request.getTrainingProjectId());
        excludeBizIds.remove(request.getBizId());

        Page<CertificateVo> certificatePage =
            certificateClient.getCertificateExclude(new ArrayList<>(excludeBizIds), request.getSearchTitle(),
                request.getPageNo(), request.getPageSize());
        if (certificatePage == null || certificatePage.getTotal() <= 0 || CollectionUtils.isEmpty(
            certificatePage.getRecords())) {
            pageDataVO.setTotal(0);
            return pageDataVO;
        }
        List<TpCertificateVO> certificateVOS =
            BeanCopyListUtil.copyListProperties(certificatePage.getRecords(), TpCertificateVO::new);
        pageDataVO.setTotal((int)certificatePage.getTotal());
        pageDataVO.setRecords(certificateVOS);
        return pageDataVO;
    }
}
