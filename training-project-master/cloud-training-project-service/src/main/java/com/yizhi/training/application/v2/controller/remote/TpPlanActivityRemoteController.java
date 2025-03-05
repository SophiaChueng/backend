package com.yizhi.training.application.v2.controller.remote;

import com.yizhi.training.application.v2.service.biz.TpPlanActivityBizService;
import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/remote/tpPlanActivity")
public class TpPlanActivityRemoteController {

    @Autowired
    private TpPlanActivityBizService tpPlanActivityBizService;

    @GetMapping("/list/get")
    public List<TpPlanActivityVO> getActivitiesOfTp(@RequestParam("trainingProjectId") Long trainingProjectId,
        @RequestParam(value = "tpPlanId", required = false) Long tpPlanId,
        @RequestParam(value = "type", required = false) Integer type) {
        return tpPlanActivityBizService.getActivities(trainingProjectId, tpPlanId, type);
    }
}
