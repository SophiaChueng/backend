package com.yizhi.training.application.v2.controller.student;

import com.yizhi.training.application.v2.model.total.AccountNumVO;
import com.yizhi.training.application.v2.service.biz.TpStudentTeacherReportBizServices;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "项目学习数据统计/班主任数据")
@RestController
@RequestMapping("/v2/student/teacher/total")
public class TotalTeacherController {

    @Autowired
    private TpStudentTeacherReportBizServices tpStudentTeacherReportBizServices;

    @GetMapping("/account/num")
    @ApiOperation("学习情况人数统计")
    public BizResponse<AccountNumVO> getAccountNumTotal(@RequestParam Long tpId) {
        AccountNumVO total = tpStudentTeacherReportBizServices.getAccountNumTotal(tpId);
        return BizResponse.ok(total);
    }

    @GetMapping("/certificate/num")
    @ApiOperation("证书统计")
    public BizResponse<String> getCertificateTotal(@RequestParam Long tpId) {
        return BizResponse.ok();
    }

    @GetMapping("/content/num")
    @ApiOperation("学习目录统计")
    public BizResponse<String> getContentTotal(@RequestParam Long tpId) {
        return BizResponse.ok();
    }

}
