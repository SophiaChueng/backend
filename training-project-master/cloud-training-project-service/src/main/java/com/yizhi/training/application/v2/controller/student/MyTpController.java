package com.yizhi.training.application.v2.controller.student;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.v2.MyTpFinishedVO;
import com.yizhi.training.application.v2.MyTpUnFinishedVO;
import com.yizhi.training.application.v2.service.biz.MyTpService;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "项目学习页")
@RestController
@RequestMapping("/v2/student/my/tp")
public class MyTpController {

    @Autowired
    private MyTpService myTpService;

    @GetMapping("/finished")
    public BizResponse<Page<MyTpFinishedVO>> getFinishedTp(@RequestParam Integer pageIndex,
        @RequestParam Integer pageSize) {
        Page<MyTpFinishedVO> page = myTpService.getFinishedTp(pageIndex, pageSize);
        return BizResponse.ok(page);
    }

    @GetMapping("/un/finished")
    public BizResponse<Page<MyTpUnFinishedVO>> getUnFinishedTp(
        @RequestParam(required = false) @ApiParam("0:未开始，1：进行中，2：已结束") Integer status,
        @RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        Page<MyTpUnFinishedVO> page = myTpService.getunFinishedTp(pageIndex, pageSize, status);
        return BizResponse.ok(page);
    }

}
