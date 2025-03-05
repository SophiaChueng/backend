package com.yizhi.training.application.v2.controller.student;

import com.yizhi.training.application.v2.service.biz.TpStudentMyReportBizServices;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "项目学习数据统计/班主任数据")
@RestController
@RequestMapping("/v2/student/total")
public class TotalStudentController {

    @Autowired
    private TpStudentMyReportBizServices tpStudentMyReportBizServices;

    @GetMapping("/my")
    public BizResponse<String> getMyReport() {
        return BizResponse.ok();
    }

}
