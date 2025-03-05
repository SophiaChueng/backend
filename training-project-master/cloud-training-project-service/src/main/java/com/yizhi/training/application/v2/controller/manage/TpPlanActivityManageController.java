package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TpPlanActivityBizService;
import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.training.application.v2.vo.request.UpdateActivitiesRequestVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "学习活动")
@RestController
@RequestMapping("/v2/manage/tpPlanActivity")
public class TpPlanActivityManageController {

    @Autowired
    private TpPlanActivityBizService tpPlanActivityBizService;

    @ApiOperation("更新学习单元的学习活动")
    @PostMapping("/update")
    public BizResponse<Integer> updateTpPlanActivities(@RequestBody UpdateActivitiesRequestVO request) {
        return BizResponse.ok(tpPlanActivityBizService.updateTpPlanActivities(request));
    }

    @ApiOperation("查询学习单元的学习活动")
    @GetMapping("/list/get")
    public BizResponse<List<TpPlanActivityVO>> getTpPlanActivities(@RequestParam("tpPlanId") Long tpPlanId) {
        return BizResponse.ok(tpPlanActivityBizService.getTpPlanActivities(tpPlanId));
    }

    @ApiOperation("查询学习活动中的考试和作业")
    @GetMapping("/examAndAssignment/list/get")
    public BizResponse<List<TpPlanVO>> getExamAndAssignment(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpPlanActivityBizService.getExamAndAssignment(trainingProjectId));
    }

    @ApiOperation("查询学习活动中的帖子")
    @GetMapping("/posts/list/get")
    public BizResponse<List<TpPlanActivityVO>> getPostsActivity(
        @RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpPlanActivityBizService.getPostsActivity(trainingProjectId));
    }

    @ApiOperation("更新学习活动信息")
    @PostMapping("/info/update")
    public BizResponse<Boolean> updateActivityInfo(@RequestBody TpPlanActivityVO request) {
        return BizResponse.ok(tpPlanActivityBizService.updateTpPlanActivityInfo(request));
    }

    @ApiOperation("批量删除学习活动")
    @PostMapping("/deleteBatch")
    public BizResponse<Boolean> deleteBatchActivity(@RequestBody List<Long> activityIds) {
        return BizResponse.ok(tpPlanActivityBizService.deleteBatchByIds(activityIds));
    }

    @ApiOperation("刷新活动图片")
    @GetMapping("/logoUrl/refresh")
    public BizResponse<Boolean> refreshActivityLogoUrl(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpPlanActivityBizService.refreshLogoUrl(trainingProjectId));
    }
}
