package com.yizhi.training.application.feign.third;

import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "AiaKmhClient", path = "/aia/kmh")
public interface AiaKmhClient {

    @GetMapping("/get/activity")
    public List<TpPlanActivityVo> searchCourseByNameTpList();

}
