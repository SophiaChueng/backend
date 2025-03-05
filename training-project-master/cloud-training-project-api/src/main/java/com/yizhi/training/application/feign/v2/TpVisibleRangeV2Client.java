package com.yizhi.training.application.feign.v2;

import com.yizhi.training.application.v2.vo.request.TpMessageRangeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "TpVisibleRangeV2Client")
public interface TpVisibleRangeV2Client {

    @GetMapping("/v2/remote/visibleRange/accountIds/get")
    List<Long> getVisibleAccounts(@RequestParam("trainingProjectId") Long trainingProjectId);

    @PostMapping("/v2/remote/visibleRange/message/accountIds/get")
    List<Long> getMessageRange(@RequestBody TpMessageRangeVO vo);

    @GetMapping("/v2/remote/visibleRange/accountId/check")
    Boolean checkAccountIdVisible(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam("accountId") Long accountId);
}
