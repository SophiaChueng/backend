package com.yizhi.training.application.v2.controller.remote;

import com.yizhi.training.application.v2.service.biz.TpAuthorizationRangeBizService;
import com.yizhi.training.application.v2.vo.request.TpMessageRangeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/remote/visibleRange")
public class TpVisibleRangeRemoteController {

    @Autowired
    private TpAuthorizationRangeBizService tpAuthorizationRangeBizService;

    @GetMapping("/accountIds/get")
    public List<Long> getVisibleAccounts(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return tpAuthorizationRangeBizService.getVisibleAccounts(trainingProjectId);
    }

    @PostMapping("/message/accountIds/get")
    public List<Long> getMessageRange(@RequestBody TpMessageRangeVO vo) {
        return tpAuthorizationRangeBizService.getMessageRange(vo);
    }

    /**
     * 检查账号是否可见
     *
     * @param trainingProjectId 项目ID
     * @param accountId         用户id
     * @return 状态
     */
    @GetMapping("/accountId/check")
    public Boolean checkAccountIdVisible(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("accountId") Long accountId) {
        return tpAuthorizationRangeBizService.checkAccountIdVisible(trainingProjectId, accountId);
    }
}
