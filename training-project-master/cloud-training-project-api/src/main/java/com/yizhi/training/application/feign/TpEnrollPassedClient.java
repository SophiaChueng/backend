package com.yizhi.training.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainingProject", contextId = "TpEnrollPassedClient")
public interface TpEnrollPassedClient {

    @GetMapping("/TpStudentEnrollPassedVo/selectTpIdByCondition")
    Long selectTpIdByCondition(@RequestParam(value = "tpProjrctId") Long tpProjrctId);
}
