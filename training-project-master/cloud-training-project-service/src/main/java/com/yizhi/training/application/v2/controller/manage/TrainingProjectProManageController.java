package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TrainingProjectProBizService;
import com.yizhi.training.application.v2.vo.TpBaseInfoVO;
import com.yizhi.training.application.v2.vo.TrainingProjectProVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.*;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目pro")
@RestController
@RequestMapping("/v2/manage/trainingProjectPro")
public class TrainingProjectProManageController {

    @Autowired
    private TrainingProjectProBizService trainingProjectProBizService;

    @ApiOperation("将项目升级为项目pro")
    @PostMapping("/mapping/addProjectToPros")
    public BizResponse<Boolean> addProjectToPros(@RequestBody AddProjectToProsRequestVO requestVO) {
        return BizResponse.ok(trainingProjectProBizService.addProjectToPros(requestVO));
    }

    @ApiOperation("为项目Pro添加项目")
    @PostMapping("/mapping/addProjectsToPro")
    public BizResponse<Boolean> addProjectsToPro(@RequestBody AddProjectsToProRequestVO requestVO) {
        return BizResponse.ok(trainingProjectProBizService.addProjectsToPro(requestVO));
    }

    @ApiOperation("从项目pro删除项目")
    @PostMapping("/mapping/deleteProjectFromPro")
    public BizResponse<Boolean> deleteProjectFromPro(@RequestBody DeleteProjectsFromProRequestVO requestVO) {
        return BizResponse.ok(trainingProjectProBizService.deleteProjectsFromPro(requestVO));
    }

    @ApiOperation("查询项目Pro列表")
    @PostMapping("/list/get")
    public BizResponse<PageDataVO<TrainingProjectProVO>> getTpProList(@RequestBody SearchTpProVO request) {
        return BizResponse.ok(trainingProjectProBizService.getTpProList(request));
    }

    @ApiOperation("新增项目pro")
    @PostMapping("/add")
    public BizResponse<Boolean> addTrainingProjectPro(@RequestBody TrainingProjectProVO tpProVo) {
        return BizResponse.ok(trainingProjectProBizService.addTrainingProjectPro(tpProVo));
    }

    @ApiOperation("更新项目pro")
    @PostMapping("/update")
    public BizResponse<Boolean> updateTrainingProjectPro(@RequestBody TrainingProjectProVO tpProVo) {
        return BizResponse.ok(trainingProjectProBizService.updateTrainingProjectPro(tpProVo));
    }

    @ApiOperation("删除项目pro")
    @GetMapping("/delete")
    public BizResponse<Boolean> deleteTrainingProjectPro(@RequestParam("tpProId") Long tpProId) {
        return BizResponse.ok(trainingProjectProBizService.deleteTrainingProjectPro(tpProId));
    }

    @ApiOperation("查询项目pro下的学习项目")
    @PostMapping("/projects/list/get")
    public BizResponse<PageDataVO<TpBaseInfoVO>> getTpListOfTpPro(@RequestBody SearchProjectOfProVO requestVO) {
        return BizResponse.ok(trainingProjectProBizService.getTpListOfPro(requestVO));
    }

    @ApiOperation("查询项目pro可以添加的项目列表")
    @PostMapping("/projects/canBeAdd/list/get")
    public BizResponse<PageDataVO<TpBaseInfoVO>> getTpListCanBeAdd(@RequestBody SearchProjectToProVO requestVO) {
        return BizResponse.ok(trainingProjectProBizService.getTpListCanBeAdd(requestVO));
    }

    @ApiOperation("拖动排序")
    @GetMapping("/mapping/sort/update")
    public BizResponse<Boolean> updateProjectSortOfPro(@RequestParam("tpProId") Long tpProId,
        @RequestParam("moveTpId") Long moveTpId, @RequestParam("preTpId") Long preTpId) {
        return BizResponse.ok(trainingProjectProBizService.updateProjectSortOfPro(tpProId, moveTpId, preTpId));
    }

}
