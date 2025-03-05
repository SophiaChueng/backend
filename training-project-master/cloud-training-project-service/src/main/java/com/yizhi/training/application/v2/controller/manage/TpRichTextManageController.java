package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.enums.TpDirectoryTypeEnum;
import com.yizhi.training.application.v2.service.biz.TpRichTextBizService;
import com.yizhi.training.application.v2.vo.TpRichTextVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "富文本")
@RestController
@RequestMapping("/v2/manage/richContext")
public class TpRichTextManageController {

    @Autowired
    private TpRichTextBizService tpRichTextBizService;

    @ApiOperation("更新学习页富文本")
    @PostMapping("/studyPage/update")
    public BizResponse<Boolean> updateStudyPageRichText(@RequestBody TpRichTextVO request) {
        return BizResponse.ok(tpRichTextBizService.updateStudyPageRichText(request));
    }

    @ApiOperation("更新介绍页富文本")
    @PostMapping("/introducePage/update")
    public BizResponse<Boolean> updateIntroducePageRichText(@RequestBody TpRichTextVO request) {
        return BizResponse.ok(tpRichTextBizService.updateIntroducePageRichText(request));
    }

    @ApiOperation("查询学习页富文本")
    @GetMapping("/studyPage/get")
    public BizResponse<TpRichTextVO> getStudyPageRichText(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId) {
        return BizResponse.ok(
            tpRichTextBizService.getRichText(trainingProjectId, TpDirectoryTypeEnum.STUDY_PAGE.getCode(),
                directoryItemId));
    }

    @ApiOperation("查询介绍页富文本")
    @GetMapping("/IntroducePage/get")
    public BizResponse<TpRichTextVO> getIntroducePageRichText(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("directoryItemId") Long directoryItemId) {
        return BizResponse.ok(
            tpRichTextBizService.getRichText(trainingProjectId, TpDirectoryTypeEnum.INTRODUCE_PAGE.getCode(),
                directoryItemId));
    }
}
