package com.yizhi.training.application.feign;

import com.yizhi.training.application.vo.domain.TpCommentThumbsUpVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/23 22:20
 */
@FeignClient(name = "trainingProject", contextId = "TpCommentThumbsUpClient")
public interface TpCommentThumbsUpClient {

    @PostMapping("/tpCommentThumbsUp/save")
    Boolean save(@RequestBody TpCommentThumbsUpVo tpCommentThumbsUp);

    @PostMapping("/tpCommentThumbsUp/delete")
    Boolean delete(@RequestBody TpCommentThumbsUpVo tpCommentThumbsUp);

    @GetMapping("/tpCommentThumbsUp/count")
    Integer countThumbsUp(@RequestParam(value = "commentId 评论id") Long commentId);
}
