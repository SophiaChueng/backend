package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TpAuthorizationRangeBizService;
import com.yizhi.training.application.v2.vo.TpVisibleRangeVO;
import com.yizhi.training.application.v2.vo.request.SaveTpVisibleRangeRequestVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "项目可见范围")
@RestController
@RequestMapping("/v2/manage/visibleRange")
public class TpAuthorizationRangeManageController {

    @Autowired
    private TpAuthorizationRangeBizService tpAuthorizationRangeBizService;

    @ApiOperation("查询项目可见范围列表")
    @GetMapping("/list/get")
    public BizResponse<List<TpVisibleRangeVO>> getTpAuthorizationRangeList(
        @RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpAuthorizationRangeBizService.getTpAuthorizationRangeList(trainingProjectId));
    }

    @ApiOperation("保存或更新项目可见范围")
    @PostMapping("/save")
    public BizResponse<Boolean> saveTpVisibleRange(@RequestBody SaveTpVisibleRangeRequestVO request) {
        return BizResponse.ok(tpAuthorizationRangeBizService.saveTpVisibleRange(request));
    }

}
