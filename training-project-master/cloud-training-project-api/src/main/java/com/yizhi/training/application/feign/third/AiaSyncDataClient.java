package com.yizhi.training.application.feign.third;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.third.AiaProductVO;
import com.yizhi.training.application.vo.third.AiaTrainingVO;
import com.yizhi.training.application.vo.third.SyncTrainingReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trainingProject", contextId = "AiaSyncDataClient")
public interface AiaSyncDataClient {

    @PostMapping(value = "/third/aia/train/sync/train/record/data")
    Page<AiaTrainingVO> getTrainingStudyPassedRecords(@RequestBody SyncTrainingReq syncTrainingReq);

    @PostMapping(value = "/third/aia/train/sync/product/record/data")
    Page<AiaProductVO> getProductStudyPassedRecords(@RequestBody SyncTrainingReq syncTrainingReq);
}
