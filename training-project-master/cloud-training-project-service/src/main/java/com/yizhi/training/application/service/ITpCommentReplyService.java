package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpCommentReply;

/**
 * <p>
 * 培训项目 - 评论回复 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpCommentReplyService extends IService<TpCommentReply> {

    @Override
    default TpCommentReply getOne(Wrapper<TpCommentReply> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
