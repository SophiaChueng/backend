package com.yizhi.training.application.feign;

import com.yizhi.training.application.vo.api.TpStudentProjectRecordEbscnVO;
import com.yizhi.training.application.vo.api.UserTrainingProjectStatusVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trainingProject", contextId = "OutsideEbscnClient")
public interface OutsideTrainingProjectEbscnClient {

    /**
     * 根据培训项目名称获取指定用户的完成状态
     */
    @PostMapping("/outside/ebscn/users/study/status")
    TpStudentProjectRecordEbscnVO geUserTrainingProjectStatus(@RequestBody UserTrainingProjectStatusVO vo);
}
