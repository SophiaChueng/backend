package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.forum.application.vo.forum.PostsManageVo;
import com.yizhi.training.application.v2.service.biz.TpForumBizService;
import com.yizhi.training.application.v2.vo.request.AddTpPostsRequestVO;
import com.yizhi.training.application.v2.vo.request.DeleteTpPostsRequestVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/manage/forum")
@Api(tags = "帖子（讨论）")
public class TpForumManageController {

    @Autowired
    private TpForumBizService tpForumBizService;

    @ApiOperation("查询项目绑定的帖子列表")
    @GetMapping("/bind/list/get")
    public BizResponse<List<PostsManageVo>> getTpPosts(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return BizResponse.ok(tpForumBizService.getTpPosts(trainingProjectId));
    }

    @ApiOperation("删除帖子和项目的关联关系")
    @PostMapping("/bind/deleteBatch")
    public BizResponse<Boolean> deleteTpPosts(@RequestBody DeleteTpPostsRequestVO requestVO) {
        return BizResponse.ok(tpForumBizService.deleteTpPosts(requestVO));
    }

    @ApiOperation("添加帖子和项目的关联关系")
    @PostMapping("/bind/addBatch")
    public BizResponse<Boolean> addTpPosts(@RequestBody AddTpPostsRequestVO requestVO) {
        return BizResponse.ok(tpForumBizService.addTpPosts(requestVO));
    }

    @ApiOperation("更新排序")
    @GetMapping("/sort/update")
    public BizResponse<Boolean> updatePostsSort(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("movePostId") Long movePostId, @RequestParam("prePostId") Long prePostId) {
        return BizResponse.ok(tpForumBizService.updateSort(trainingProjectId, movePostId, prePostId));
    }
}
