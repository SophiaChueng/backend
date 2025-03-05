package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.domain.TpCommentReply;
import com.yizhi.training.application.service.ITpCommentReplyService;
import com.yizhi.training.application.service.ITpCommentService;
import com.yizhi.training.application.vo.domain.TpCommentReplyVo;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 培训项目 - 评论回复 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@RestController
@RequestMapping("/tpCommentReply")
public class TpCommentReplyController {

    @Autowired
    ITpCommentReplyService iTpCommentReplyService;

    @Autowired
    IdGenerator idGenerator;

    @Autowired
    AccountClient accountClient;

    @Autowired
    ITpCommentService iTpCommentService;

    @GetMapping("/list")
    public Page<TpCommentReply> replyList(@RequestParam("commmentId") Long commmentId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize,
        @RequestParam("type") Integer type) {
        Page<TpCommentReply> page = new Page<TpCommentReply>(pageNo, pageSize);
        TpCommentReply reply = new TpCommentReply();
        if (1 == type) {
            reply.setState(0);
        }
        reply.setTpCommentId(commmentId);
        reply.setAuditStatus("0");
        QueryWrapper<TpCommentReply> wrapper = new QueryWrapper<TpCommentReply>(reply);
        iTpCommentReplyService.page(page, wrapper);
        String fullName;
        String name;
        for (TpCommentReply listCommentVo : page.getRecords()) {
            listCommentVo.setContent(StringEscapeUtils.unescapeJava(listCommentVo.getContent()));
            if (null != listCommentVo.getReplyParentId() && 0 != listCommentVo.getReplyParentId()) {
                TpCommentReply reply2 = iTpCommentReplyService.getById(listCommentVo.getReplyParentId());
                if (null != reply2) {
                    AccountVO findById = accountClient.findById(reply2.getCreateById());
                    listCommentVo.setParentAccountFullName(
                        null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                            : findById.getFullName());
                } else {
                    AccountVO findById = accountClient.findById(
                        iTpCommentService.getById(listCommentVo.getTpCommentId()).getCreateById());
                    listCommentVo.setParentAccountFullName(
                        null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                            : findById.getFullName());
                }
            } else {
                AccountVO findById = accountClient.findById(
                    iTpCommentService.getById(listCommentVo.getTpCommentId()).getCreateById());
                listCommentVo.setParentAccountFullName(
                    null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                        : findById.getFullName());
            }
            AccountVO accountVO = accountClient.findById(listCommentVo.getCreateById());
            fullName = accountVO.getFullName();
            name = accountVO.getName();
            listCommentVo.setCreateByName(name);
            listCommentVo.setCreateByFullName(null == fullName || "" == fullName ? name : fullName);
        }
        return page;
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody TpCommentReplyVo tpCommentReply) {
        tpCommentReply.setId(idGenerator.generate());
        //        tpCommentReply.setContent(StringEscapeUtils.escapeJava(tpCommentReply.getContent()));
        TpCommentReply tcp = new TpCommentReply();
        BeanUtils.copyProperties(tpCommentReply, tcp);
        Boolean f = iTpCommentReplyService.save(tcp);
        return f;
    }

    @GetMapping("/delete")
    public Boolean delete(@RequestParam Long tpCommentReplyId) {
        RequestContext context = ContextHolder.get();
        TpCommentReply commentReply = iTpCommentReplyService.getById(tpCommentReplyId);
        commentReply.setAuditorId(context.getAccountId());
        commentReply.setAuditStatus("1");
        commentReply.setAuditorTime(new Date());
        return iTpCommentReplyService.updateById(commentReply);
    }

    /**
     * 项目回复上架
     *
     * @param id
     * @param type
     * @return boolean
     */
    @GetMapping("/up")
    public Boolean up(@RequestParam("id") Long id) {
        TpCommentReply tpCommentReply = iTpCommentReplyService.getById(id);
        if (null != tpCommentReply) {
            tpCommentReply.setState(0);
            iTpCommentReplyService.updateById(tpCommentReply);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 项目回复下架
     *
     * @param id
     * @param type
     * @return
     */
    @GetMapping("/down")
    public Boolean down(@RequestParam("id") Long id) {
        TpCommentReply tpCommentReply = iTpCommentReplyService.getById(id);
        if (null != tpCommentReply) {
            tpCommentReply.setState(1);
            iTpCommentReplyService.updateById(tpCommentReply);
            return true;
        } else {
            return false;
        }
    }
}

