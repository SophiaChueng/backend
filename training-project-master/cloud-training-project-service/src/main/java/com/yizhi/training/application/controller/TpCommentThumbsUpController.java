package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpCommentThumbsUp;
import com.yizhi.training.application.service.ITpCommentThumbsUpService;
import com.yizhi.training.application.vo.domain.TpCommentThumbsUpVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 评论点赞记录 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@RestController
@RequestMapping("/tpCommentThumbsUp")
public class TpCommentThumbsUpController {

    @Autowired
    ITpCommentThumbsUpService iTpCommentThumbsUpService;

    @Autowired
    IdGenerator idGenerator;

    @PostMapping("/save")
    public Boolean save(@RequestBody TpCommentThumbsUpVo tpCommentThumbsUp) {
        TpCommentThumbsUp tct = new TpCommentThumbsUp();
        BeanUtils.copyProperties(tpCommentThumbsUp, tct);
        QueryWrapper<TpCommentThumbsUp> QueryWrapper = new QueryWrapper<TpCommentThumbsUp>(tct);
        List<TpCommentThumbsUp> list = iTpCommentThumbsUpService.list(QueryWrapper);
        if (list.size() > 0) {
            return Boolean.FALSE;
        }
        tct.setId(idGenerator.generate());
        Boolean f = iTpCommentThumbsUpService.save(tct);
        return f;
    }

    @PostMapping("/delete")
    public Boolean delete(@RequestBody TpCommentThumbsUpVo tpCommentThumbsUp) {
        TpCommentThumbsUp tct = new TpCommentThumbsUp();
        BeanUtils.copyProperties(tpCommentThumbsUp, tct);
        QueryWrapper<TpCommentThumbsUp> QueryWrapper = new QueryWrapper<TpCommentThumbsUp>(tct);
        Boolean f = iTpCommentThumbsUpService.remove(QueryWrapper);
        return f;
    }

    @GetMapping("/count")
    public Integer countThumbsUp(@RequestParam(value = "commentId 评论id") Long commentId) {

        return iTpCommentThumbsUpService.countThumbsUp(commentId);
    }

}

