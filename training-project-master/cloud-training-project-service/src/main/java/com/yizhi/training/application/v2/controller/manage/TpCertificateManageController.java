package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.certificate.application.enums.CertificateEnum;
import com.yizhi.training.application.v2.service.biz.TpCertificateBizService;
import com.yizhi.training.application.v2.vo.TpCertificateStrategyVO;
import com.yizhi.training.application.v2.vo.TpCertificateVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.AddCertificateToPlanRequestVO;
import com.yizhi.training.application.v2.vo.request.SearchCertificateVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目:证书")
@RestController
@RequestMapping("/v2/manage/certificate")
public class TpCertificateManageController {

    @Autowired
    private TpCertificateBizService tpCertificateBizService;

    @ApiOperation("重发证书")
    @GetMapping("/reissue")
    public BizResponse<String> reissueTpCertificate(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpCertificateBizService.reissueTpCertificate(trainingProjectId));
    }

    @ApiOperation("学习单元绑定证书")
    @PostMapping("/add")
    public BizResponse<Boolean> addCertificateToTpPlan(@RequestBody AddCertificateToPlanRequestVO request) {
        return BizResponse.ok(tpCertificateBizService.addCertificateToTpPlan(request));
    }

    @ApiOperation("查询项目绑定的证书列表")
    @GetMapping("/bind/tp/list/get")
    public BizResponse<TpCertificateStrategyVO> getTpBindCertificates(
        @RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpCertificateBizService.getBindCertificates(trainingProjectId,
            CertificateEnum.BIZ_TYPE_TRAINING.getCode()));
    }

    @ApiOperation("查询学习单元绑定的证书列表")
    @GetMapping("/bind/tpPlan/list/get")
    public BizResponse<TpCertificateStrategyVO> getTpPlanBindCertificates(
        @RequestParam("trainingProjectId") Long trainingProjectId, @RequestParam("tpPlanId") Long tpPlanId) {
        return BizResponse.ok(
            tpCertificateBizService.getBindCertificates(tpPlanId, CertificateEnum.BIZ_TYPE_TP_PLAN.getCode()));
    }

    @ApiOperation("查询可绑定的证书列表")
    @PostMapping("/canBind/list/get")
    public BizResponse<PageDataVO<TpCertificateVO>> getTpCanBindCertificates(@RequestBody SearchCertificateVO request) {
        return BizResponse.ok(tpCertificateBizService.getAllTpCertificates(request));
    }

}
