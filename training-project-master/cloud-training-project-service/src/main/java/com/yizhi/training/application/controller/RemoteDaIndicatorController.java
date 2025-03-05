package com.yizhi.training.application.controller;

import com.yizhi.training.application.service.using.RemoteDaIndicatorServiceUsing;
import com.yizhi.training.application.vo.RemoteDaIndicatorVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DaIndicatorAccountController
 * @Description TODO
 * @Author shengchenglong
 * @DATE 2019-10-09 16:52
 * @Version 1.0
 */
@RestController
@RequestMapping("/remote/da/tp/")
public class RemoteDaIndicatorController {

    @Autowired
    private RemoteDaIndicatorServiceUsing remoteDaIndicatorServiceUsing;

    /**
     * dashboard - 完成课程数
     *
     * @return
     */
    @PostMapping("tpFinish")
    public boolean tpFinish(@RequestBody RemoteDaIndicatorVo vo) {
        return remoteDaIndicatorServiceUsing.tpFinish(vo.getSiteId(), vo.getStartDate(), vo.getEndDate(),
            vo.getProcessTime());
    }

}
