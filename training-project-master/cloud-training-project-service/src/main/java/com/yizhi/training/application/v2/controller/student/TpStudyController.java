package com.yizhi.training.application.v2.controller.student;

import com.yizhi.training.application.v2.service.ITpStudyBizService;
import com.yizhi.training.application.v2.service.biz.TpAnnouncementBizService;
import com.yizhi.training.application.v2.vo.TpAnnouncementVO;
import com.yizhi.training.application.v2.vo.TpIntroduceBaseVO;
import com.yizhi.training.application.v2.vo.TpStudyDetailsVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.ProjectJudgeAO;
import com.yizhi.training.application.v2.vo.request.SearchAnnouncementVO;
import com.yizhi.training.application.v2.vo.study.*;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "项目学习页")
@RestController
@RequestMapping("/v2/student/study")
public class TpStudyController {

    @Autowired
    private ITpStudyBizService tpStudyBizService;

    @Autowired
    private TpAnnouncementBizService tpAnnouncementBizService;

    @GetMapping("/start/tp")
    @ApiOperation("开始学习记录下学习记录")
    public BizResponse<String> startStudySaveLog(@RequestParam Long tpId) {
        tpStudyBizService.startStudySaveLog(tpId);
        return BizResponse.ok();
    }

    @GetMapping("/apply/ceitificate")
    @ApiOperation("证书申请")
    public BizResponse<String> applyCeitificate(@RequestParam @ApiParam("项目ID或计划ID") Long relationId,
        @RequestParam @ApiParam("业务类型 0：项目，1：计划") Integer relationType) {
        tpStudyBizService.applyCeitificate(relationId, relationType);
        return BizResponse.ok();
    }

    @GetMapping("/all/tptab")
    @ApiOperation("返回所有tab栏，默认打开第一个学习目录")
    public BizResponse<List<TpIntroduceBaseVO>> getStudyDirect(@RequestParam Long tpId) {
        List<TpIntroduceBaseVO> vos = tpStudyBizService.getStudyDirect(tpId);
        return BizResponse.ok(vos);
    }

    @GetMapping("/tp/detail")
    @ApiOperation("返回项目的详情，logo，名字，活动数等。。。")
    public BizResponse<TpStudyDetailsVO> getTpDetails(@RequestParam Long tpId) {
        TpStudyDetailsVO vo = tpStudyBizService.getTpDetails(tpId);
        return BizResponse.ok(vo);
    }

    @GetMapping("/tp/introduce/detail")
    @ApiOperation("学习页简介")
    public BizResponse<TpStudyIntroduceVO> getIntroduceDetails(@RequestParam Long tpId) {
        TpStudyIntroduceVO vo = tpStudyBizService.getIntroduceDetails(tpId);
        return BizResponse.ok(vo);
    }

    @GetMapping("/tp/html/detail")
    @ApiOperation("学习页富文本")
    public BizResponse<String> getHtmlDetails(@RequestParam Long tpId, @RequestParam Long itemId) {
        String content = tpStudyBizService.getHtmlDetails(tpId, itemId);
        return BizResponse.ok(content);
    }

    @PostMapping("/tp/notice/detail")
    @ApiOperation("学习页公告")
    public BizResponse<PageDataVO<TpAnnouncementVO>> getNoticeDetails(@RequestBody SearchAnnouncementVO vo) {
        PageDataVO<TpAnnouncementVO> tpAnnouncementList = tpAnnouncementBizService.getTpAnnouncementList(vo);
        return BizResponse.ok(tpAnnouncementList);
    }

    // 资料、评论 复用以前代码
    // （0：学习单元， ❌
    // 1：简介， OK
    // 2：资料， OK 复用以前
    // 3：评论，OK 复用以前
    // 4：考试与作业， OK
    // 5：公告， OK
    // 6：讨论， OK
    // 7：富文本 OK

    @GetMapping("/tp/exam/assignment/detail")
    @ApiOperation("学习页考试与作业")
    public BizResponse<List<TpStudyPlanVO<TpStudyExamVO>>> getExamAndAssignmentDetails(@RequestParam Long tpId) {
        List<TpStudyPlanVO<TpStudyExamVO>> list = tpStudyBizService.getExamAndAssignmentDetails(tpId);
        return BizResponse.ok(list);
    }

    @GetMapping("/tp/content/detail")
    @ApiOperation("学习页学习目录")
    public BizResponse<List<TpStudyPlanVO<TpStudyActivityVO>>> getContentDetails(@RequestParam Long tpId,
        @RequestParam Long itemId) {
        List<TpStudyPlanVO<TpStudyActivityVO>> list = tpStudyBizService.getContentDetails(tpId, itemId);
        return BizResponse.ok(list);
    }

    @GetMapping("/tp/forum/details")
    @ApiOperation("学习页全部讨论/专项讨论")
    public BizResponse<List<TpStudyForumVO>> getTpForumDetails(@RequestParam Long tpId,
        @ApiParam("0:专项讨论，1：全部讨论") @RequestParam Integer forumType) {
        List<TpStudyForumVO> list = tpStudyBizService.getTpForumDetails(tpId, forumType);
        return BizResponse.ok(list);
    }

    @ApiOperation("判断是否需要展示项目介绍页")
    @PostMapping("/lecturer/judgeProjectDesc")
    public BizResponse<String> judgeProjectDesc(@RequestBody ProjectJudgeAO ao) {
        return BizResponse.ok(tpStudyBizService.judgeProjectDesc(ao));
    }
}
