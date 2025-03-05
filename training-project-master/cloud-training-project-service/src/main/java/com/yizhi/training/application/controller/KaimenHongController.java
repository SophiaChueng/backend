package com.yizhi.training.application.controller;

import com.yizhi.training.application.service.KmhService;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/aia/kmh")
public class KaimenHongController {

    @Value("${aia.project.kaimenhong.tpid}")
    List<Long> aiaProjectKmhTpIdList;

    @Autowired
    KmhService kmhService;

    @GetMapping("/get/activity")
    public List<TpPlanActivityVo> searchCourseByNameTpList() {
        return kmhService.getActivityByTpList(aiaProjectKmhTpIdList);
    }
}
