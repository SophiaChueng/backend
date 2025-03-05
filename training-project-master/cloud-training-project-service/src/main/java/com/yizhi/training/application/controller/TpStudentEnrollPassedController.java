package com.yizhi.training.application.controller;

import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.service.ITpStudentEnrollPassedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/TpStudentEnrollPassed")
public class TpStudentEnrollPassedController {

    @Autowired
    ITpStudentEnrollPassedService studentEnrollPassedService;

    @GetMapping("/selectTpIdByCondition")
    Long selectTpIdByCondition(@RequestParam(value = "tpProjrctId") Long tpProjrctId) {
        RequestContext context = ContextHolder.get();
        return studentEnrollPassedService.selectTpIdByCondition(tpProjrctId, context.getAccountId(),
            context.getSiteId());
    }
}
