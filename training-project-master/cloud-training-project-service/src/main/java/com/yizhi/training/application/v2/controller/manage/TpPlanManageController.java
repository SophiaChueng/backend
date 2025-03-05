package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TpPlanBizService;
import com.yizhi.training.application.v2.vo.TpPlanDetailVO;
import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "学习单元")
@RestController
@RequestMapping("/v2/manage/tpPlan")
public class TpPlanManageController {

    @Autowired
    private TpPlanBizService tpPlanBizService;

    @ApiOperation("新增学习单元")
    @PostMapping("/add")
    public BizResponse<TpPlanDetailVO> addTpPlan(@RequestBody TpPlanDetailVO request) {
        return BizResponse.ok(tpPlanBizService.addTpPlan(request));
    }

    @ApiOperation("查询学习单元详细信息")
    @GetMapping("/get")
    public BizResponse<TpPlanDetailVO> getTpPlan(@RequestParam("tpPlanId") Long tpPlanId) {
        return BizResponse.ok(tpPlanBizService.getTpPlan(tpPlanId));
    }

    @ApiOperation("更新学习单元信息")
    @PostMapping("/update")
    public BizResponse<Boolean> updateTpPlan(@RequestBody TpPlanDetailVO request) {
        return BizResponse.ok(tpPlanBizService.updateTpPlan(request));
    }

    @ApiOperation("删除学习单元")
    @PostMapping("/delete")
    public BizResponse<Boolean> deleteBatchTpPlan(@RequestBody List<Long> tpPlanIds) {
        return BizResponse.ok(tpPlanBizService.deleteBatchTpPlan(tpPlanIds));
    }

    @ApiOperation("查询学习单元列表（详细）")
    @GetMapping("/detail/list/get")
    public BizResponse<List<TpPlanDetailVO>> getTpPlanDetailList(
        @RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId) {
        return BizResponse.ok(tpPlanBizService.getTpPlanDetailList(trainingProjectId, directoryItemId));
    }

    @ApiOperation("查询学习单元列表（简略）")
    @GetMapping("/baseInfo/list/get")
    public BizResponse<List<TpPlanVO>> getTpPlanSimpleList(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam(value = "directoryItemId", required = false) Long directoryItemId) {
        return BizResponse.ok(tpPlanBizService.getTpPlanSimpleList(trainingProjectId, directoryItemId));
    }

    @ApiOperation("更新学习单元排序（拖动排序）")
    @GetMapping("/sort/update")
    public BizResponse<Boolean> updateTpPlanSort(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId, @RequestParam("moveId") Long moveId,
        @RequestParam("preId") Long preId) {
        return BizResponse.ok(tpPlanBizService.updateTpPlanSort(trainingProjectId, directoryItemId, moveId, preId));
    }
}
