package com.yizhi.training.application.controller.third;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.service.ThirdAiaSyncTrainDataService;
import com.yizhi.training.application.vo.third.AiaProductVO;
import com.yizhi.training.application.vo.third.AiaTrainingVO;
import com.yizhi.training.application.vo.third.SyncTrainingReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/third/aia/train")
public class AiaSyncDataController {

    @Autowired
    ThirdAiaSyncTrainDataService thirdAiaSyncDataService;

    @PostMapping(value = "/sync/train/record/data")
    public Page<AiaTrainingVO> getTrainingStudyPassedRecords(@RequestBody SyncTrainingReq syncTrainingReq) {

        return thirdAiaSyncDataService.getTrainingStudyPassedRecords(syncTrainingReq);
    }

    @PostMapping(value = "/sync/product/record/data")
    public Page<AiaProductVO> getProductStudyPassedRecords(@RequestBody SyncTrainingReq syncTrainingReq) {

        return thirdAiaSyncDataService.getProductStudyPassedRecords(syncTrainingReq);
    }
}
