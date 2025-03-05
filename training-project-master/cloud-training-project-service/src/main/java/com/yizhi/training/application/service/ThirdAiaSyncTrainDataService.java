package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.third.AiaProductVO;
import com.yizhi.training.application.vo.third.AiaTrainingVO;
import com.yizhi.training.application.vo.third.SyncTrainingReq;

public interface ThirdAiaSyncTrainDataService {

    /**
     * 获取培训项目通过的学员明细
     *
     * @param syncTrainingReq 请求参数
     * @return 通过的数据
     */
    Page<AiaTrainingVO> getTrainingStudyPassedRecords(SyncTrainingReq syncTrainingReq);

    /**
     * 获取培训项目中课程的学习完成记录
     *
     * @param syncTrainingReq 请求参数
     * @return
     */
    Page<AiaProductVO> getProductStudyPassedRecords(SyncTrainingReq syncTrainingReq);
}
