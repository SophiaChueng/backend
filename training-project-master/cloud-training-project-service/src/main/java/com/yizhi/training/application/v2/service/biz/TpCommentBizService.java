package com.yizhi.training.application.v2.service.biz;

import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.domain.TpComment;
import com.yizhi.training.application.domain.TpCommentReply;
import com.yizhi.training.application.v2.service.TpCommentReplyService;
import com.yizhi.training.application.v2.service.TpCommentService;
import com.yizhi.training.application.v2.service.TpCommentThumbsUpService;
import com.yizhi.training.application.v2.vo.TpCommentReplyVO;
import com.yizhi.training.application.v2.vo.TpCommentVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.DeleteTpCommentRequestVO;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TpCommentBizService {

    @Autowired
    private TpCommentService tpCommentService;

    @Autowired
    private TpCommentReplyService tpCommentReplyService;

    @Autowired
    private TpCommentThumbsUpService tpCommentThumbsUpService;

    @Autowired
    private AccountClient accountClient;

    public PageDataVO<TpCommentVO> getTpComments(Long trainingProjectId, Integer pageNo, Integer pageSize) {
        PageDataVO<TpCommentVO> pageDataVO = new PageDataVO<>();
        pageDataVO.setPageNo(pageNo);
        pageDataVO.setPageSize(pageSize);

        Integer total = tpCommentService.getTpCommentCount(trainingProjectId, null);
        pageDataVO.setTotal(total);
        if (total == 0) {
            return pageDataVO;
        }
        List<TpComment> commentList = tpCommentService.getTpComments(trainingProjectId, null, pageNo, pageSize);
        List<Long> commentIds = commentList.stream().map(TpComment::getId).collect(Collectors.toList());
        Map<Long, TpComment> tpCommentMap = commentList.stream().collect(Collectors.toMap(TpComment::getId, o -> o));

        List<TpCommentReply> tpCommentReplies = tpCommentReplyService.getReplies(trainingProjectId, commentIds, null);

        List<Long> accountIds =
            commentList.stream().map(TpComment::getCreateById).distinct().collect(Collectors.toList());
        accountIds.addAll(tpCommentReplies.stream().map(TpCommentReply::getCreateById).collect(Collectors.toList()));
        List<AccountVO> accounts = accountClient.findByIds(accountIds);
        Map<Long, AccountVO> accountMap = accounts == null ? Collections.emptyMap()
            : accounts.stream().collect(Collectors.toMap(AccountVO::getId, o -> o));

        Map<Long, List<TpCommentReplyVO>> replyListMap = new HashMap<>();
        Map<Long, TpCommentReply> replyMap =
            tpCommentReplies.stream().collect(Collectors.toMap(TpCommentReply::getId, o -> o));

        // 组装评论回复数据
        tpCommentReplies.forEach(reply -> {
            TpCommentReplyVO replyVO = new TpCommentReplyVO();
            BeanUtils.copyProperties(reply, replyVO);
            AccountVO acc = accountMap.get(reply.getCreateById());
            if (acc != null) {
                replyVO.setCreateByName(acc.getName());
                replyVO.setCreateByFullName(StringUtils.isBlank(acc.getFullName()) ? acc.getName() : acc.getFullName());
            }

            AccountVO parentAcc = null;
            if (reply.getReplyParentId() != null && reply.getReplyParentId() > 0) {
                TpCommentReply parentReply = replyMap.get(reply.getReplyParentId());
                parentAcc = accountMap.get(parentReply == null ? 0L : parentReply.getCreateById());
            } else {
                TpComment comment = tpCommentMap.get(reply.getTpCommentId());
                parentAcc = accountMap.get(comment == null ? 0L : comment.getCreateById());
            }
            if (parentAcc != null) {
                replyVO.setParentAccountName(parentAcc.getName());
                replyVO.setParentAccountFullName(
                    StringUtils.isBlank(parentAcc.getFullName()) ? parentAcc.getName() : parentAcc.getFullName());
            }

            List<TpCommentReplyVO> replyVOS = replyListMap.getOrDefault(reply.getTpCommentId(), new ArrayList<>());
            replyVOS.add(replyVO);
            replyListMap.put(reply.getTpCommentId(), replyVOS);
        });

        // 查询点赞数
        Map<Long, Integer> thumbsUpCountMap = tpCommentThumbsUpService.getThumbsUpCountMap(commentIds);

        List<TpCommentVO> commentVOS = BeanCopyListUtil.copyListProperties(commentList, TpCommentVO::new, (s, t) -> {
            // 评论人名称
            AccountVO acc = accountMap.get(s.getCreateById());
            if (acc != null) {
                t.setCreateByName(acc.getName());
                t.setCreateByFullName(StringUtils.isBlank(acc.getFullName()) ? acc.getName() : acc.getFullName());
            }
            // 点赞数
            t.setThumbsUpCount(thumbsUpCountMap.getOrDefault(s.getId(), 0));

            List<TpCommentReplyVO> replyVOS = replyListMap.get(s.getId());
            t.setReplyCount(CollectionUtils.isEmpty(replyVOS) ? 0 : replyVOS.size());
            t.setTpCommentReplies(replyVOS);

            t.setReplyName("--");
        });

        pageDataVO.setRecords(commentVOS);
        return pageDataVO;
    }

    public Boolean putOnShelf(Long trainingProjectId, Long tpCommentId) {
        TpComment tpComment = tpCommentService.getById(tpCommentId);
        if (tpComment != null && Objects.equals(tpComment.getState(), 1)) {
            TpComment updateComment = new TpComment();
            updateComment.setId(tpCommentId);
            updateComment.setState(0);
            return tpCommentService.updateById(updateComment);
        }
        return false;
    }

    public Boolean putOffShelf(Long trainingProjectId, Long tpCommentId) {
        TpComment tpComment = tpCommentService.getById(tpCommentId);
        if (tpComment != null && Objects.equals(tpComment.getState(), 0)) {
            TpComment updateComment = new TpComment();
            updateComment.setId(tpCommentId);
            updateComment.setState(1);
            return tpCommentService.updateById(updateComment);
        }
        return false;
    }

    public Boolean deleteTpComment(DeleteTpCommentRequestVO requestVO) {
        RequestContext context = ContextHolder.get();

        TpComment tpComment = tpCommentService.getById(requestVO.getTpCommentId());
        if (tpComment != null && Objects.equals(tpComment.getState(), 0)) {
            TpComment updateComment = new TpComment();
            updateComment.setId(requestVO.getTpCommentId());
            updateComment.setAuditStatus("1");
            updateComment.setAuditorId(context.getAccountId());
            updateComment.setAuditorTime(new Date());
            return tpCommentService.updateById(updateComment);
        }
        return false;
    }
}
