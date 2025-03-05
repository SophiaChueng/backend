package com.yizhi.training.application.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.SwhyTrainingProject;
import com.yizhi.training.application.vo.SwhyTrainingProjectMem;
import com.yizhi.training.application.vo.SwhyTrainingProjectMemReq;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/sw")
public class SwhyTrainingProjectController {

    @Autowired
    private ITrainingProjectService trainingProjectService;

    private Logger logger = LoggerFactory.getLogger(SwhyTrainingProjectController.class);

    @PostMapping("/relevance")
    public List<SwhyTrainingProjectMem> relevance(@RequestBody SwhyTrainingProjectMemReq req) {
        List<SwhyTrainingProjectMem> data =
            this.trainingProjectService.relevance(req.getCompanyId(), req.getSiteId(), req.getType(),
                req.getRelationIds());

        if (CollectionUtils.isNotEmpty(data)) {
            for (SwhyTrainingProjectMem item : data) {
                byte[] bytes = item.getProjectNamesBlob();
                if (bytes != null) {
                    item.setProjectNamesBlob(null);
                    item.setProjectNames(new String(bytes, StandardCharsets.UTF_8));
                } else {
                    item.setProjectNames("");
                }
            }
        }
        logger.info("查询课程关联的项目信息 = {}", JSON.toJSONString(data));
        return data;
    }

    @GetMapping("/query/record")
    public Page<SwhyTrainingProject> queryRecord(@RequestParam("companyId") Long companyId,
        @RequestParam("siteId") Long siteId, @RequestParam("pageNo") Integer pageNo,
        @RequestParam("pageSize") Integer pageSize,
        @RequestParam(value = "queryDate", required = false) String queryDate) {
        return this.trainingProjectService.queryRecord(companyId, siteId, pageNo, pageSize, queryDate);
    }
}
