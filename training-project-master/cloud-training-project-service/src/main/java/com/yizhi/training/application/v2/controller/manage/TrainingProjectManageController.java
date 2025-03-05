package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.lecturer.application.vo.LecturerListVO;
import com.yizhi.training.application.v2.service.biz.TrainingProjectBizService;
import com.yizhi.training.application.v2.vo.TpBaseInfoVO;
import com.yizhi.training.application.v2.vo.TpDetailInfoVO;
import com.yizhi.training.application.v2.vo.TrainingProjectVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.SearchProjectVO;
import com.yizhi.training.application.v2.vo.request.UpdateTpBriefIntroduceRequestVO;
import com.yizhi.training.application.v2.vo.request.UpdateTpLecturerRequestVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "项目")
@RestController
@RequestMapping("/v2/manage/trainingProject")
public class TrainingProjectManageController {

    @Autowired
    private TrainingProjectBizService trainingProjectBizService;

    @ApiOperation("查询项目列表")
    @PostMapping("/list/get")
    public BizResponse<PageDataVO<TrainingProjectVO>> getProjectList(@RequestBody SearchProjectVO request) {
        return BizResponse.ok(trainingProjectBizService.getProjectList(request));
    }

    @ApiOperation("更新项目排序")
    @GetMapping("/sort/update")
    public BizResponse<Boolean> updateSort(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("sort") Integer sort) {
        return BizResponse.ok(trainingProjectBizService.updateSort(trainingProjectId, sort));
    }

    @ApiOperation("查询项目基本信息")
    @GetMapping("/baseInfo/get")
    public BizResponse<TpBaseInfoVO> getProjectBaseInfo(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.getProjectBaseInfo(trainingProjectId));
    }

    @ApiOperation("上架")
    @GetMapping("/shelf/putOn")
    public BizResponse<Boolean> putOnShelf(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.putOnShelf(trainingProjectId));
    }

    @ApiOperation("下架")
    @GetMapping("/shelf/putOff")
    public BizResponse<Boolean> putOffShelf(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.putOffShelf(trainingProjectId));
    }

    @ApiOperation("复制")
    @GetMapping("/copy")
    public BizResponse<Boolean> copyProject(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.copyProject(trainingProjectId));
    }

    @ApiOperation("删除")
    @PostMapping("/deleteBatch")
    public BizResponse<Integer> batchDeleteProject(@RequestBody List<Long> trainingProjectIds) {
        return BizResponse.ok(trainingProjectBizService.batchDeleteProject(trainingProjectIds));
    }

    @ApiOperation("保存基本信息")
    @PostMapping("/baseInfo/save")
    public BizResponse<TpBaseInfoVO> saveTrainingProject(@RequestBody TpBaseInfoVO trainingProject) {
        return BizResponse.ok(trainingProjectBizService.saveTrainingProject(trainingProject));
    }

    @ApiOperation("更新基本信息")
    @PostMapping("/baseInfo/update")
    public BizResponse<Boolean> updateTrainingProject(@RequestBody TpBaseInfoVO trainingProject) {
        return BizResponse.ok(trainingProjectBizService.updateTrainingProject(trainingProject));
    }

    @ApiOperation("查询项目详细信息（高级设置）")
    @GetMapping("/detailInfo/get")
    public BizResponse<TpDetailInfoVO> getTpDetailInfo(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.getTpDetailInfo(trainingProjectId));
    }

    @ApiOperation("更新项目高级设置")
    @PostMapping("/detailInfo/update")
    public BizResponse<Boolean> updateTpDetailInfo(@RequestBody TpDetailInfoVO request) {
        return BizResponse.ok(trainingProjectBizService.updateTpDetailInfo(request));
    }

    @ApiOperation("查询项目简介")
    @GetMapping("/introduce/get")
    public BizResponse<String> getTpBriefIntroduce(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.getTpBriefIntroduce(trainingProjectId));
    }

    @ApiOperation("更新项目简介")
    @PostMapping("/introduce/update")
    public BizResponse<Boolean> updateTpBriefIntroduce(@RequestBody UpdateTpBriefIntroduceRequestVO request) {
        return BizResponse.ok(trainingProjectBizService.updateTpBriefIntroduce(request));
    }

    @ApiOperation("查询项目的讲师列表")
    @GetMapping("/lecturer/get")
    public BizResponse<List<LecturerListVO>> getTpLecturers(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(trainingProjectBizService.getTpLecturers(trainingProjectId));
    }

    @ApiOperation("更新项目的讲师列表")
    @PostMapping("/lecturer/update")
    public BizResponse<Boolean> updateTpLecturers(@RequestBody UpdateTpLecturerRequestVO requestVO) {
        return BizResponse.ok(
            trainingProjectBizService.updateTpLecturer(requestVO.getTrainingProjectId(), requestVO.getLecturerIds()));
    }
}
