package com.yizhi.training.application.feign.v2;

import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.util.application.domain.BizResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainingProject", contextId = "TpPlanV2Client")
public interface TpPlanV2Client {

    @GetMapping("/v2/remote/tpPlan/get")
    TpPlanVO getTpPlan(@RequestParam("tpPlanId") Long tpPlanId);

    @GetMapping("/v2/remote/tpPlan/studyCondition/get")
    BizResponse<String> getStudyTimeConditionStr(@RequestParam("tpPlanId") Long tpPlanId);
}
