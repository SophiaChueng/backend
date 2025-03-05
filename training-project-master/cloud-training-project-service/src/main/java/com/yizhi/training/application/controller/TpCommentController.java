package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.training.application.domain.TpComment;
import com.yizhi.training.application.domain.TpCommentReply;
import com.yizhi.training.application.domain.TpCommentThumbsUp;
import com.yizhi.training.application.service.ITpCommentReplyService;
import com.yizhi.training.application.service.ITpCommentService;
import com.yizhi.training.application.service.ITpCommentThumbsUpService;
import com.yizhi.training.application.task.CommentListExportAsync;
import com.yizhi.training.application.vo.domain.TpCommentReplyVo;
import com.yizhi.training.application.vo.domain.TpCommentVo;
import com.yizhi.training.application.vo.manage.PageCommentVo;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 培训项目 - 评论 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@RestController
@RequestMapping("/tpComment")
public class TpCommentController {

    @Autowired
    ITpCommentService iTpCommentService;

    @Autowired
    ITpCommentReplyService iTpCommentReplyService;

    @Autowired
    IdGenerator idGenerator;

    @Autowired
    ITpCommentThumbsUpService tpCommentThumbsUpService;

    @Autowired
    AccountClient accountClient;

    @Autowired
    CommentListExportAsync commentListExportAsync;

    /**
     * 评论保存
     *
     * @param tpComment
     * @return
     */
    @PostMapping("/save")
    public Boolean save(@RequestBody TpCommentVo tpComment) {
        tpComment.setId(idGenerator.generate());
        //        tpComment.setContent(StringEscapeUtils.escapeJava(tpComment.getContent()));
        TpComment tp = new TpComment();
        BeanUtils.copyProperties(tpComment, tp);
        Boolean f = iTpCommentService.save(tp);
        return f;
    }

    /**
     * 培训项目 评论列表
     *
     * @param trainingProjectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Page<PageCommentVo> list(@RequestParam(name = "trainingProjectId") Long trainingProjectId,
        @RequestParam(name = "accountId") Long accountId, @RequestParam(name = "pageNo") Integer pageNo,
        @RequestParam(name = "pageSize") Integer pageSize, @RequestParam(name = "type") Integer type

    ) {
        Page<PageCommentVo> page =
            iTpCommentService.getCommentPage(trainingProjectId, accountId, pageNo, pageSize, type);
        String fullName;
        String name;
        TpCommentReply reply = new TpCommentReply();
        if (type != 0) {
            reply.setState(0);
        }
        reply.setTrainingProjectId(trainingProjectId);
        reply.setAuditStatus("0");
        QueryWrapper<TpCommentReply> wrapper = new QueryWrapper<TpCommentReply>(reply);
        wrapper.orderByDesc("create_time");
        List<TpCommentReply> replies = iTpCommentReplyService.list(wrapper);

        List<Long> commentIds = new ArrayList<>();
        Map<Long, List<TpCommentReply>> listMap = new HashMap<>();
        List<Long> replyAndCommentCreateIds = new ArrayList<>();
        Map<Long, TpCommentReply> replyMap = new HashMap<>();
        //组装 评论和回复得创建人id
        if (!CollectionUtils.isEmpty(replies)) {
            replyAndCommentCreateIds = replies.stream().map(a -> a.getCreateById()).collect(Collectors.toList());
            replyMap = replies.stream().collect(Collectors.toMap(key -> key.getId(), val -> val));
        }
        List<PageCommentVo> pageCommentVos = page.getRecords();
        Map<Long, PageCommentVo> commentVoMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(pageCommentVos)) {
            replyAndCommentCreateIds.addAll(
                pageCommentVos.stream().map(a -> a.getAccountId()).collect(Collectors.toList()));
            commentVoMap = pageCommentVos.stream().collect(Collectors.toMap(a -> a.getId(), val -> val));
            commentIds = pageCommentVos.stream().map(key -> key.getId()).collect(Collectors.toList());
        }
        List<AccountVO> accountByIds = accountClient.findByIds(replyAndCommentCreateIds);
        Map<Long, AccountVO> accountVOMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(accountByIds)) {
            accountVOMap = accountByIds.stream().collect(Collectors.toMap(key -> key.getId(), val -> val));
        }
        for (TpCommentReply revert : replies) {
            revert.setContent(StringEscapeUtils.unescapeJava(revert.getContent()));
            if (listMap.containsKey(revert.getTpCommentId())) {
                listMap.get(revert.getTpCommentId()).add(revert);
            } else {
                List<TpCommentReply> repliesResult = new ArrayList<>();
                repliesResult.add(revert);
                listMap.put(revert.getTpCommentId(), repliesResult);
            }
            if (null != revert.getReplyParentId() && 0 != revert.getReplyParentId()) {
                //                TpCommentReply reply2 = iTpCommentReplyService.getById(revert.getReplyParentId());
                TpCommentReply reply2 = replyMap.get(revert.getReplyParentId());
                if (null != reply2) {
                    //                    AccountVO findById = accountClient.findById(reply2.getCreateById());
                    AccountVO findById = accountVOMap.get(reply2.getCreateById());
                    if (findById != null) {
                        revert.setParentAccountFullName(
                            null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                                : findById.getFullName());
                    }
                } else {
                    //                    AccountVO findById = accountClient.findById(iTpCommentService.selectById
                    //                    (revert.getTpCommentId()).getCreateById());
                    PageCommentVo pageCommentVo = commentVoMap.get(revert.getTpCommentId());
                    AccountVO findById = accountVOMap.get(pageCommentVo == null ? 0L : pageCommentVo.getAccountId());
                    if (findById != null) {
                        revert.setParentAccountFullName(
                            null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                                : findById.getFullName());
                    }
                }
            } else {
                //                AccountVO findById = accountClient.findById(iTpCommentService.getById(revert
                //                .getTpCommentId()).getCreateById());
                //                revert.setParentAccountFullName(null == findById.getFullName() || "" == findById
                //                .getFullName() ? findById.getName() : findById.getFullName());
                PageCommentVo pageCommentVo = commentVoMap.get(revert.getTpCommentId());
                AccountVO findById = accountVOMap.get(pageCommentVo == null ? 0L : pageCommentVo.getAccountId());
                if (findById != null) {
                    revert.setParentAccountFullName(
                        null == findById.getFullName() || "" == findById.getFullName() ? findById.getName()
                            : findById.getFullName());
                }
            }
            //            AccountVO accountVO = accountClient.findById(revert.getCreateById());
            AccountVO accountVO = accountVOMap.get(revert.getCreateById());
            fullName = accountVO.getFullName();
            name = accountVO.getName();
            revert.setCreateByName(name);
            revert.setCreateByFullName(null == fullName || "" == fullName ? name : fullName);
        }
        Map<Long, TpCommentThumbsUp> upMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(commentIds)){

            List<TpCommentThumbsUp> tpCommentThumbsUps = tpCommentThumbsUpService.judgeHasThumbsUps(commentIds);
            if (!CollectionUtils.isEmpty(tpCommentThumbsUps)) {
                for (TpCommentThumbsUp ttu : tpCommentThumbsUps) {
                    if (upMap.get(ttu.getTpCommentId()) != null) {
                        continue;
                    }
                    upMap.put(ttu.getTpCommentId(), ttu);
                }
            }
        }
        for (PageCommentVo listCommentVo : pageCommentVos) {
            List<TpCommentReply> list1 = listMap.get(listCommentVo.getId());
            List<TpCommentReplyVo> list2 = new ArrayList<>();
            if (!CollectionUtils.isEmpty(list1)) {
                for (TpCommentReply tr : list1) {
                    TpCommentReplyVo trv = new TpCommentReplyVo();
                    BeanUtils.copyProperties(tr, trv);
                    list2.add(trv);
                }
            }
            listCommentVo.setContent(StringEscapeUtils.unescapeJava(listCommentVo.getContent()));
            if (listMap.containsKey(listCommentVo.getId())) {
                listCommentVo.setTpCommentReplies(list2);
                listCommentVo.setReplys(listMap.get(listCommentVo.getId()).size());
            }
            //            AccountVO accountVO = accountClient.findById(listCommentVo.getAccountId());
            AccountVO accountVO = accountVOMap.get(listCommentVo.getAccountId());
            fullName = accountVO.getFullName();
            name = accountVO.getName();
            listCommentVo.setCommentator(name);
            listCommentVo.setReplyName("--");
            listCommentVo.setCommentatorName(null == fullName || "" == fullName ? name : fullName);
            //            Boolean hasThumbsUp = tpCommentThumbsUpService.judgeHasThumbsUp(listCommentVo.getId());
            if (upMap.get(listCommentVo.getId()) != null) {
                listCommentVo.setHasThumbsUp(true);
            }
        }
        return page;
    }

    @PostMapping("/delete")
    public Boolean delete(@RequestBody TpCommentVo param) {
        TpComment tp = new TpComment();
        BeanUtils.copyProperties(param, tp);
        TpComment tpComment = iTpCommentService.getById(tp.getId());
        tpComment.setAuditorId(param.getAuditorId());
        tpComment.setAuditStatus("1");
        tpComment.setAuditorTime(new Date());
        tpComment.setAuditContent(param.getAuditContent());
        Boolean f = iTpCommentService.updateById(tpComment);
        return f;
    }

    /**
     * 项目评论上架
     *
     * @param id
     * @param type
     * @return boolean
     */
    @GetMapping("/up")
    public Boolean up(@RequestParam("id") Long id) {
        TpComment tpComment = iTpCommentService.getById(id);
        if (null != tpComment) {
            tpComment.setState(0);
            iTpCommentService.updateById(tpComment);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 项目评论下架
     *
     * @param id
     * @param type
     * @return
     */
    @GetMapping("/down")
    public Boolean down(@RequestParam("id") Long id) {
        TpComment tpComment = iTpCommentService.getById(id);
        if (null != tpComment) {
            tpComment.setState(1);
            iTpCommentService.updateById(tpComment);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/list/export")
    public Map<String, Object> export(@ApiParam(name = "trainingProjectId", value = "项目id") @RequestParam(
        name = "trainingProjectId") Long trainingProjectId,
        @ApiParam(name = "trainingProjectName", value = "项目名称") @RequestParam(
            name = "trainingProjectName") String trainingProjectName) {
        RequestContext requestContext = ContextHolder.get();
        Date submitTime = new Date();
        Long taskId = submitTime.getTime();
        String serialNo = "USER-EXPORT-" + taskId;
        String taskName = "导出评论信息-" + taskId;
        String result = "任务编号：" + serialNo + "。任务名称：" + taskName;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("accountId", requestContext.getAccountId());
        map.put("siteId", requestContext.getSiteId());
        map.put("companyId", requestContext.getCompanyId());
        map.put("serialNo", serialNo);
        map.put("taskId", taskId);
        map.put("submitTime", submitTime);
        map.put("taskName", taskName);
        map.put("trainingProjectId", trainingProjectId);
        map.put("trainingProjectName", trainingProjectName);
        map.put("result", result);
        commentListExportAsync.execute(map, true);
        return map;
    }
}

