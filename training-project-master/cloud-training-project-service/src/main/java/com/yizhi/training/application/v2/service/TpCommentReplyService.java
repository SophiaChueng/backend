package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpCommentReply;

import java.util.List;

public interface TpCommentReplyService extends IService<TpCommentReply> {

    @Override
    default TpCommentReply getOne(Wrapper<TpCommentReply> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<TpCommentReply> getReplies(Long trainingProjectId, List<Long> commentIds, Integer state);
}
