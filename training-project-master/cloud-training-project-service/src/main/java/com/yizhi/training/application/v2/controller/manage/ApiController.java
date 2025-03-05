package com.yizhi.training.application.v2.controller.manage;

import com.yizhi.training.application.v2.service.biz.TpPlanActivityBizService;
import com.yizhi.training.application.v2.service.biz.TpStudyDirectoryBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2/manage/api")
public class ApiController {

    @Autowired
    private TpStudyDirectoryBizService tpStudyDirectoryBizService;

    @Autowired
    private TpPlanActivityBizService tpPlanActivityBizService;

    @GetMapping("/initStudyDirectory")
    public Boolean initStudyDirectory(@RequestParam(value = "companyId", required = false) Long companyId,
        @RequestParam(value = "siteId", required = false) Long siteId) {
        return tpStudyDirectoryBizService.initStudyDirectory(companyId, siteId);
    }

    @GetMapping("/reverseActivitySort")
    public Boolean reverseActivitySort(@RequestParam(value = "companyId", required = false) Long companyId,
        @RequestParam(value = "siteId", required = false) Long siteId) {
        return tpPlanActivityBizService.reverseActivitySort(companyId, siteId);
    }

}
