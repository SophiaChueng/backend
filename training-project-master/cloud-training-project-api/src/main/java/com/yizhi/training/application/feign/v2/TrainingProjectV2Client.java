package com.yizhi.training.application.feign.v2;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.v2.vo.RecentStudyTrainingVO;
import com.yizhi.training.application.v2.vo.TpBaseInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trainingProject", contextId = "TrainingProjectV2Client")
public interface TrainingProjectV2Client {

    @GetMapping("/v2/remote/trainingProject/get")
    TpBaseInfoVO getTrainingProject(@RequestParam("trainingProjectId") Long trainingProjectId);

    @GetMapping("/v2/remote/trainingProject/recentStudy/list")
    Page<RecentStudyTrainingVO> getRecentStudyList(@RequestParam("pageNo") Integer pageNo,
        @RequestParam("pageSize") Integer pageSize);
}
