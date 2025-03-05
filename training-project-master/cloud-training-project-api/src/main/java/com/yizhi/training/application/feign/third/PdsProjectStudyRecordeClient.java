package com.yizhi.training.application.feign.third;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.PdsProjectStudyRecordeVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "PdsProjectStudyRecordeClient")
public interface PdsProjectStudyRecordeClient {

    @PostMapping("/pdsProjectStudyRecorde/point/add")
    Boolean pointAdd(@RequestBody PdsProjectStudyRecordeVo projectStudyRecordeVo);

    @GetMapping("/pdsProjectStudyRecorde/point/get")
    PdsProjectStudyRecordeVo pointGet(@RequestParam("uid") Long uid, @RequestParam("pid") Long pid);

    @PostMapping("/pdsProjectStudyRecorde/study/period/get")
    Float studyPeriodGet(@RequestBody PdsProjectStudyRecordeVo projectStudyRecordeVo);

    @GetMapping("/pdsProjectStudyRecorde/study/period/ranking/list")
    Page<PdsProjectStudyRecordeVo> studyPeriodRankingPage(@RequestParam("pageSize") Integer pageSize,
        @RequestParam("pageNum") Integer pageNum, @RequestParam("companyId") Long companyId,
        @RequestParam("siteId") Long siteId, @RequestParam("pid") Long pid);

    @GetMapping("/pdsProjectStudyRecorde/study/period/ranking/get")
    List<PdsProjectStudyRecordeVo> studyPeriodRankingList(@RequestParam("uid") Long uid,
        @RequestParam("companyId") Long companyId, @RequestParam("siteId") Long siteId, @RequestParam("pid") Long pid);
}
