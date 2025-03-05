package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TpCommentBizService;
import com.yizhi.training.application.v2.vo.TpCommentVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.DeleteTpCommentRequestVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/manage/comment")
@Api(tags = "评论")
public class TpCommentManageController {

    @Autowired
    private TpCommentBizService tpCommentBizService;

    @ApiOperation("查询评论列表")
    @GetMapping("/list/get")
    public BizResponse<PageDataVO<TpCommentVO>> getTpComments(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        return BizResponse.ok(tpCommentBizService.getTpComments(trainingProjectId, pageNo, pageSize));
    }

    @ApiOperation("上架")
    @GetMapping("/shelf/putOn")
    public BizResponse<Boolean> putOnShelf(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("tpCommentId") Long tpCommentId) {
        return BizResponse.ok(tpCommentBizService.putOnShelf(trainingProjectId, tpCommentId));
    }

    @ApiOperation("下架")
    @GetMapping("/shelf/putOff")
    public BizResponse<Boolean> putOffShelf(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("tpCommentId") Long tpCommentId) {
        return BizResponse.ok(tpCommentBizService.putOffShelf(trainingProjectId, tpCommentId));
    }

    @ApiOperation("删除")
    @GetMapping("/delete")
    public BizResponse<Boolean> deleteTpComment(@RequestBody DeleteTpCommentRequestVO requestVO) {
        return BizResponse.ok(tpCommentBizService.deleteTpComment(requestVO));
    }
}
