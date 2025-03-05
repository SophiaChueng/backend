package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.v2.service.biz.TpStudyDirectoryBizService;
import com.yizhi.training.application.v2.vo.TpConsultEntranceVO;
import com.yizhi.training.application.v2.vo.TpIntroduceDirectoryVO;
import com.yizhi.training.application.v2.vo.TpStudyDirectoryVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "目录")
@RestController
@RequestMapping("/v2/manage/directory")
public class TpStudyDirectoryManageController {

    @Autowired
    private TpStudyDirectoryBizService tpStudyDirectoryBizService;

    @ApiOperation("新增学习页目录项")
    @PostMapping("/studyPage/add")
    public BizResponse<TpStudyDirectoryVO> addStudyDirectoryItem(@RequestBody TpStudyDirectoryVO item) {
        RequestContext context = ContextHolder.get();
        return BizResponse.ok(
            tpStudyDirectoryBizService.addStudyDirectoryItem(context.getCompanyId(), context.getSiteId(), item));
    }

    @ApiOperation("批量新增学习也目录项")
    @PostMapping("/studyPage/addBatch")
    public BizResponse<Boolean> addBatchStudyDirectoryItem(@RequestBody List<TpStudyDirectoryVO> items) {
        return BizResponse.ok(tpStudyDirectoryBizService.addBatchStudyDirectoryItem(items));
    }

    @ApiOperation("更新学习页目录项")
    @PostMapping("/studyPage/update")
    public BizResponse<Boolean> updateStudyDirectoryItem(@RequestBody TpStudyDirectoryVO item) {
        return BizResponse.ok(tpStudyDirectoryBizService.updateStudyDirectoryItem(item));
    }

    @ApiOperation("查询学习页目录列表")
    @GetMapping("/studyPage/get")
    public BizResponse<List<TpStudyDirectoryVO>> getTpStudyDirectory(
        @RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpStudyDirectoryBizService.getTpStudyDirectory(trainingProjectId));
    }

    @ApiOperation("更新学习页目录项排序")
    @GetMapping("/studyPage/sort/update")
    public BizResponse<Boolean> updateStudyDirectorySort(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("moveId") Long moveId, @RequestParam("preId") Long preId) {
        return BizResponse.ok(tpStudyDirectoryBizService.updateStudyDirectorySort(trainingProjectId, moveId, preId));
    }

    @ApiOperation("删除学习页目录项")
    @GetMapping("/studyPage/delete")
    public BizResponse<Boolean> deleteStudyDirectoryItem(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId) {
        return BizResponse.ok(tpStudyDirectoryBizService.deleteStudyDirectoryItem(trainingProjectId, directoryItemId));
    }

    // =======================介绍页=============================

    @ApiOperation("查询介绍页目录列表")
    @GetMapping("/introducePage/list/get")
    public BizResponse<List<TpIntroduceDirectoryVO>> getTpIntroduceDirectory(
        @RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpStudyDirectoryBizService.getTpIntroduceDirectory(trainingProjectId));
    }

    @ApiOperation("新增介绍页目录项")
    @PostMapping("/introducePage/add")
    public BizResponse<TpIntroduceDirectoryVO> addIntroduceDirectory(@RequestBody TpIntroduceDirectoryVO item) {
        RequestContext context = ContextHolder.get();
        return BizResponse.ok(
            tpStudyDirectoryBizService.addIntroduceDirectory(context.getCompanyId(), context.getSiteId(), item));
    }

    @ApiOperation("批量新增介绍页目录项")
    @PostMapping("/introducePage/addBatch")
    public BizResponse<Boolean> addBatchIntroduceDirectory(@RequestBody List<TpIntroduceDirectoryVO> items) {
        return BizResponse.ok(tpStudyDirectoryBizService.addBatchIntroduceDirectory(items));
    }

    @ApiOperation("更新介绍页目录项信息")
    @PostMapping("/introducePage/update")
    public BizResponse<Boolean> updateIntroduceDirectory(@RequestBody TpIntroduceDirectoryVO item) {
        return BizResponse.ok(tpStudyDirectoryBizService.updateIntroduceDirectory(item));
    }

    @ApiOperation("更新咨询")
    @PostMapping("/introducePage/consultEntrance/update")
    public BizResponse<Boolean> updateConsultEntrance(@RequestBody TpConsultEntranceVO request) {
        return BizResponse.ok(tpStudyDirectoryBizService.updateConsultEntrance(request));
    }

    @ApiOperation("查询咨询入口")
    @GetMapping("/introducePage/consultEntrance/get")
    public BizResponse<TpConsultEntranceVO> getConsultEntrance(
        @RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId) {
        return BizResponse.ok(tpStudyDirectoryBizService.getConsultEntrance(trainingProjectId, directoryItemId));
    }

    @ApiOperation("更新介绍页目录项排序")
    @GetMapping("/introducePage/sort/update")
    public BizResponse<Boolean> updateIntroduceDirectorySort(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("moveId") Long moveId, @RequestParam("preId") Long preId) {
        return BizResponse.ok(
            tpStudyDirectoryBizService.updateIntroduceDirectorySort(trainingProjectId, moveId, preId));
    }

    @ApiOperation("删除介绍页目录项")
    @GetMapping("/IntroducePage/delete")
    public BizResponse<Boolean> deleteIntroduceDirectoryItem(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId) {
        return BizResponse.ok(
            tpStudyDirectoryBizService.deleteIntroduceDirectoryItem(trainingProjectId, directoryItemId));
    }
}
