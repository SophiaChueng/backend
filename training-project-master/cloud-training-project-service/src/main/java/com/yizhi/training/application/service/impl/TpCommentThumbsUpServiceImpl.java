package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpCommentThumbsUp;
import com.yizhi.training.application.mapper.TpCommentThumbsUpMapper;
import com.yizhi.training.application.service.ITpCommentThumbsUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 评论点赞记录 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Service
public class TpCommentThumbsUpServiceImpl extends ServiceImpl<TpCommentThumbsUpMapper, TpCommentThumbsUp>
    implements ITpCommentThumbsUpService {

    @Autowired
    TpCommentThumbsUpMapper commentThumbsUpMapper;

    @Override
    public Integer countThumbsUp(Long commenId) {

        return commentThumbsUpMapper.countThumbsUp(commenId);
    }

    @Override
    public Boolean judgeHasThumbsUp(Long commentId) {
        RequestContext context = ContextHolder.get();
        TpCommentThumbsUp thumbsUp = new TpCommentThumbsUp();
        thumbsUp.setAccountId(context.getAccountId());
        thumbsUp.setTpCommentId(commentId);
        QueryWrapper<TpCommentThumbsUp> QueryWrapper = new QueryWrapper<>(thumbsUp);

        if (this.baseMapper.selectCount(QueryWrapper) > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<TpCommentThumbsUp> judgeHasThumbsUps(List<Long> commentIds) {
        RequestContext context = ContextHolder.get();
        TpCommentThumbsUp thumbsUp = new TpCommentThumbsUp();
        thumbsUp.setAccountId(context.getAccountId());
        QueryWrapper<TpCommentThumbsUp> QueryWrapper = new QueryWrapper<>(thumbsUp);
        QueryWrapper.in("tp_comment_id", commentIds);
        return this.baseMapper.selectList(QueryWrapper);
    }
}
