package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpCommentReply;
import com.yizhi.training.application.v2.mapper.TpCommentReplyMapperV2;
import com.yizhi.training.application.v2.service.TpCommentReplyService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TpCommentReplyServiceImplV2 extends ServiceImpl<TpCommentReplyMapperV2, TpCommentReply>
    implements TpCommentReplyService {

    @Override
    public List<TpCommentReply> getReplies(Long trainingProjectId, List<Long> commentIds, Integer state) {
        if (CollectionUtils.isEmpty(commentIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<TpCommentReply> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.in("tp_comment_id", commentIds);
        if (state != null) {
            wrapper.eq("state", state);
        }
        return list(wrapper);
    }
}
