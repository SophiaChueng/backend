package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpCommentThumbsUp;

import java.util.List;

/**
 * <p>
 * 评论点赞记录 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpCommentThumbsUpService extends IService<TpCommentThumbsUp> {

    @Override
    default TpCommentThumbsUp getOne(Wrapper<TpCommentThumbsUp> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 给某条评论计算点赞数
     *
     * @param commenId
     * @return
     */
    public Integer countThumbsUp(Long commenId);

    /**
     * 判断是否已经点赞
     *
     * @param commentId 评论Id
     * @return
     */
    Boolean judgeHasThumbsUp(Long commentId);

    List<TpCommentThumbsUp> judgeHasThumbsUps(List<Long> commentIds);
}
