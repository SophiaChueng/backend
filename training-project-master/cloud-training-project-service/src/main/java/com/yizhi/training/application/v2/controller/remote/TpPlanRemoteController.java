package com.yizhi.training.application.v2.controller.remote;

import com.yizhi.training.application.v2.service.biz.TpPlanBizService;
import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.util.application.domain.BizResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/remote/tpPlan")
public class TpPlanRemoteController {

    @Autowired
    private TpPlanBizService tpPlanBizService;

    @GetMapping("/get")
    public TpPlanVO getTpPlan(@RequestParam("tpPlanId") Long tpPlanId) {
        return tpPlanBizService.getTpPlanSimple(tpPlanId);
    }

    @GetMapping("/studyCondition/get")
    public BizResponse<String> getStudyTimeConditionStr(@RequestParam("tpPlanId") Long tpPlanId) {
        String date = tpPlanBizService.getTimeConditionStr(tpPlanId);
        return BizResponse.ok(date);
    }

}
