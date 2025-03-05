package com.yizhi.training.application.feign;

import com.yizhi.training.application.vo.RemoteDaIndicatorVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName DaIndicatorAccountController
 * @Description TODO
 * @Author shengchenglong
 * @DATE 2019-10-09 16:52
 * @Version 1.0
 */
@FeignClient(name = "trainingProject", contextId = "RemoteDaIndicatorTpClient")
public interface RemoteDaIndicatorTpClient {

    /**
     * dashboard - 完成课程数
     *
     * @return
     */
    @PostMapping("/remote/da/tp/tpFinish")
    public boolean tpFinish(@RequestBody RemoteDaIndicatorVo vo);

}
