package com.yizhi.training.application.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.excel.EasyExcel;
import com.yizhi.certificate.application.enums.CertificateEnum;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.certificate.application.vo.CertificateStrategyVO;
import com.yizhi.certificate.application.vo.domain.CertificateVo;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.context.TaskContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.core.application.file.task.AbstractDefaultTask;
import com.yizhi.core.application.file.task.TaskVO;
import com.yizhi.core.application.file.util.OssUpload;
import com.yizhi.core.application.publish.CloudEventPublisher;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.ITpStudentPlanRecordService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.util.ExportUtil;
import com.yizhi.training.application.v2.param.CertificateReissueVO;
import com.yizhi.training.application.v2.param.CetificateReissueFileVO;
import com.yizhi.training.application.v2.param.CetificateReissueQO;
import com.yizhi.util.application.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CetificateReissueAsync extends AbstractDefaultTask<String, TaskVO<CetificateReissueQO>> {

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private ITpStudentPlanRecordService planRecordService;

    @Autowired
    private ITpStudentProjectRecordService projectRecordService;

    @Autowired
    private CloudEventPublisher cloudEventPublisher;

    @Autowired
    private AccountClient accountClient;

    private List<CertificateReissueVO> reissueCertificate(Long tpId, String tpName, Long bizId, String bizName,
        CertificateEnum type, Long companyId, Long siteId) {
        // 查询每个业务关联的证书，筛选出完成后直接发放的证书，然后查询已完成人员，再查询已获证人员，计算出需要补发的人员。
        CertificateStrategyVO config = certificateClient.getRelationCertificate(bizId, type.getCode());
        if (config == null || config.getIssueStrategy() == 1 || CollUtil.isEmpty(config.getCertificates())) {
            log.info("证书补发 bizId:{},没有证书或证书需要申请", bizId);
            return null;
        }
        List<Long> finishedAccountIds = null;
        if (type.getCode().equals(CertificateEnum.BIZ_TYPE_TRAINING.getCode())) {
            finishedAccountIds = projectRecordService.getFinishedAccountIds(bizId, companyId, siteId);
        } else {
            finishedAccountIds = planRecordService.getFinishedAccountIds(bizId, companyId, siteId);
        }
        if (CollUtil.isEmpty(finishedAccountIds)) {
            log.info("证书补发 bizId:{},没有完成的用户", bizId);
            return null;
        }

        List<CertificateVo> certificateList = config.getCertificates();
        final List<Long> finalFinishedAccountFinal = finishedAccountIds;

        List<CertificateReissueVO> list = new ArrayList<>();

        certificateList.stream().forEach(certificateVo -> {
            List<Long> finalFinishedAccountCopy = new ArrayList<>(finalFinishedAccountFinal);
            // 查询已发放证书的用户
            List<Long> certificateUserIds =
                certificateClient.getAccountIdsByCertificateId(certificateVo.getId(), siteId);
            if (certificateUserIds == null) {
                certificateUserIds = new ArrayList<>();
            }
            //从已完成的用户中 移除已获得证书的用户，剩余的为需要部分证书的用户
            finalFinishedAccountCopy.removeAll(certificateUserIds);

            if (CollUtil.isEmpty(finalFinishedAccountCopy)) {
                return;
            }
            CertificateReissueVO vo = new CertificateReissueVO();
            vo.setCertificateId(certificateVo.getId());
            vo.setCetificateName(certificateVo.getTitle());
            vo.setAccountIds(finalFinishedAccountCopy);
            vo.setSiteId(siteId);
            vo.setTpId(tpId);
            vo.setTpName(tpName);
            vo.setPlanId(bizId);
            vo.setPlanName(bizName);
            vo.setBizType(type.getCode());
            list.add(vo);
        });

        return list;

    }

    private void giveCertificate(Long siteId, List<CertificateReissueVO> reIssusList) {
        reIssusList.forEach(it -> {
            List<Long> accountIds = it.getAccountIds();
            accountIds.forEach(accountId -> {
                Map<String, Object> map = new HashMap<>();
                map.put("siteId", siteId);
                map.put("certificateId", it.getCertificateId());
                map.put("accountId", accountId);
                map.put("projectId", it.getTpId());
                map.put("projectName", it.getTpName());
                //证书需要以下新增字段进行逻辑判断
                map.put("trPlanId", it.getPlanId());
                map.put("trPlanName", it.getPlanName());
                map.put("bizdType", it.getBizType());
                cloudEventPublisher.publish("myCertificate", new EventWrapper<Map>(it.getCertificateId(), map));
            });
        });
    }

    private List<CetificateReissueFileVO> createFileData(List<CertificateReissueVO> reIssusList) {
        List<Long> accountIds = new ArrayList<>();
        reIssusList.forEach(it -> {
            accountIds.addAll(it.getAccountIds());
        });

        List<AccountVO> accountVOS = accountClient.findByIds(accountIds);
        Map<Long, AccountVO> accountMap =
            accountVOS.stream().collect(Collectors.toMap(AccountVO::getId, Function.identity()));

        List<CetificateReissueFileVO> fileDataList = new ArrayList<>();
        reIssusList.forEach(it -> {
            it.getAccountIds().forEach(accountId -> {
                CetificateReissueFileVO data = new CetificateReissueFileVO();
                data.setCetificateName(it.getCetificateName());
                AccountVO accountVO = accountMap.get(accountId);
                if (accountVO != null) {
                    data.setAccountName(accountVO.getName());
                    data.setFullName(accountVO.getFullName());
                }
                fileDataList.add(data);
            });

        });
        return fileDataList;
    }

    @Override
    protected String execute(TaskVO<CetificateReissueQO> param) {

        TaskContext taskContext = new TaskContext(param.getTaskId(), param.getSerialNo(), param.getTaskName(),
            param.getContext().getAccountId(), new Date(), param.getContext().getSiteId(),
            param.getContext().getCompanyId());
        RequestContext context = param.getContext();
        working(taskContext);
        CetificateReissueQO queryVo = param.getQueryVo();
        TrainingProject project = queryVo.getProject();
        try {
            List<CertificateReissueVO> reIssusList = new ArrayList<>();

            List<CertificateReissueVO> tpCetificate =
                reissueCertificate(project.getId(), project.getName(), project.getId(), project.getName(),
                    CertificateEnum.BIZ_TYPE_TRAINING, context.getCompanyId(), context.getSiteId());
            if (CollUtil.isNotEmpty(tpCetificate)) {
                reIssusList.addAll(tpCetificate);
            }

            List<TpPlan> tpPLan = queryVo.getTpPLan();
            tpPLan.forEach(plan -> {
                List<CertificateReissueVO> planCetificate =
                    reissueCertificate(project.getId(), project.getName(), plan.getId(), plan.getName(),
                        CertificateEnum.BIZ_TYPE_TP_PLAN, context.getCompanyId(), context.getSiteId());
                if (CollUtil.isNotEmpty(planCetificate)) {
                    reIssusList.addAll(planCetificate);
                }
            });
            if (CollUtil.isEmpty(reIssusList)) {
                fail(taskContext, "没有需要补发证书的人员");
                return null;
            }

            // 发证书
            giveCertificate(context.getSiteId(), reIssusList);

            // 生成文件
            List<CetificateReissueFileVO> data = createFileData(reIssusList);

            File tmpFile = null;
            tmpFile = FileUtils.createLocalTmpFile(FileUtils.EXCEL_SUFFIX_07);

            String ossExcelDate = DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN);
            String ossExcelName = new StringBuffer("证书补发结果清单").append("_")
                .append(ExportUtil.replaceUnsupportedSheetName(project.getName())).append("_").append(ossExcelDate)
                .append(FileUtils.EXCEL_SUFFIX_07).toString();

            EasyExcel.write(tmpFile, CetificateReissueFileVO.class).sheet(ossExcelDate).doWrite(data);

            String ossAddress = OssUpload.upload(tmpFile.toPath().toString(), ossExcelName);
            success(taskContext, "成功", ossAddress);
            return null;
        } catch (Exception e) {
            log.error("证书补发异常：{}", project.getName(), e);
            fail(taskContext, ExceptionUtil.stacktraceToString(e, 500));
        }
        return null;
    }
}
