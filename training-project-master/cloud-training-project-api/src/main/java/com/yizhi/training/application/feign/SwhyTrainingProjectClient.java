package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.SwhyTrainingProject;
import com.yizhi.training.application.vo.SwhyTrainingProjectMem;
import com.yizhi.training.application.vo.SwhyTrainingProjectMemReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "SwhyTrainingProjectClient")
public interface SwhyTrainingProjectClient {

    @PostMapping("/sw/relevance")
    List<SwhyTrainingProjectMem> relevance(@RequestBody SwhyTrainingProjectMemReq req);

    @GetMapping("/sw/query/record")
    Page<SwhyTrainingProject> queryRecord(@RequestParam("companyId") Long companyId,
        @RequestParam("siteId") Long siteId, @RequestParam("pageNo") Integer pageNo,
        @RequestParam("pageSize") Integer pageSize,
        @RequestParam(value = "queryDate", required = false) String queryDate);
}
