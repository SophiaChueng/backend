package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TpAnnouncementBizService;
import com.yizhi.training.application.v2.vo.TpAnnouncementVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.SearchAnnouncementVO;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目公告")
@RestController
@RequestMapping("v2/manage/announcement")
public class TpAnnouncementManageController {

    @Autowired
    private TpAnnouncementBizService tpAnnouncementBizService;

    @ApiOperation("查询项目公告列表")
    @PostMapping("/list/get")
    public BizResponse<PageDataVO<TpAnnouncementVO>> getTpAnnouncementList(@RequestBody SearchAnnouncementVO request) {
        return BizResponse.ok(tpAnnouncementBizService.getTpAnnouncementList(request));
    }

    @ApiOperation("添加项目公告")
    @PostMapping("/add")
    public BizResponse<Boolean> addTpAnnouncement(@RequestBody TpAnnouncementVO request) {
        return BizResponse.ok(tpAnnouncementBizService.addTpAnnouncement(request));
    }

    @ApiOperation("更新项目公告")
    @PostMapping("/update")
    public BizResponse<Boolean> editTpAnnouncement(@RequestBody TpAnnouncementVO request) {
        return BizResponse.ok(tpAnnouncementBizService.editTpAnnouncement(request));
    }

    @ApiOperation("查询项目公告")
    @GetMapping("/get")
    public BizResponse<TpAnnouncementVO> getTpAnnouncement(@RequestParam("announcementId") Long announcementId) {
        return BizResponse.ok(tpAnnouncementBizService.getTpAnnouncement(announcementId));
    }

    @ApiOperation("更新项目公告排序")
    @GetMapping("/sort/update")
    public BizResponse<Boolean> updateTpAnnouncementSort(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("moveId") Long moveId, @RequestParam("preId") Long preId) {
        return BizResponse.ok(tpAnnouncementBizService.updateTpAnnouncementSort(trainingProjectId, moveId, preId));
    }

    @ApiOperation("删除项目公告")
    @GetMapping("delete")
    public BizResponse<Boolean> removeTpAnnouncement(@RequestParam("announcementId") Long announcementId) {
        return BizResponse.ok(tpAnnouncementBizService.removeTpAnnouncement(announcementId));
    }

}
