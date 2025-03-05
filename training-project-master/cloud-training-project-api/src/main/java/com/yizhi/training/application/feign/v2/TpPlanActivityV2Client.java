package com.yizhi.training.application.feign.v2;

import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "TpPlanActivityV2Client")
public interface TpPlanActivityV2Client {

    @GetMapping("/list/get")
    List<TpPlanActivityVO> getActivitiesOfTp(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam(value = "tpPlanId", required = false) Long tpPlanId,
        @RequestParam(value = "type", required = false) Integer type);
}
