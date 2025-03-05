package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.domain.TpCommentReplyVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/23 22:20
 */
@FeignClient(name = "trainingProject", contextId = "TpCommentReplyClient")
public interface TpCommentReplyClient {

    @GetMapping("/tpCommentReply/list")
    Page<TpCommentReplyVo> replyList(@RequestParam("commmentId") Long commmentId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize,
        @RequestParam("type") Integer type);

    @PostMapping("/tpCommentReply/save")
    Boolean save(@RequestBody TpCommentReplyVo tpCommentReply);

    @GetMapping("/tpCommentReply/delete")
    Boolean delete(@RequestParam("tpCommentReplyId") Long tpCommentReplyId);

    @GetMapping("/tpCommentReply/up")
    Boolean up(@RequestParam("id") Long id);

    @GetMapping("/tpCommentReply/down")
    Boolean down(@RequestParam("id") Long id);

    /**
     * 交银康联中调用的老接口
     *
     * @param commmentId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Deprecated
    @GetMapping("/tpCommentReply/listjykl")
    Page<TpCommentReplyVo> replyList(@RequestParam("commmentId") Long commmentId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize);
}
