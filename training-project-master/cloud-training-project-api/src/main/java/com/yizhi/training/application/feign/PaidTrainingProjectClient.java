package com.yizhi.training.application.feign;

import com.yizhi.training.application.param.PaidTrainingProjectQO;
import com.yizhi.training.application.vo.api.PaidTrainingProjectVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName: PaidTrainingProjectClient
 * @author: zjl
 * @date: 2021/1/12  16:27
 */
@FeignClient(name = "trainingProject", contextId = "PaidTrainingProjectClient")
public interface PaidTrainingProjectClient {

    /**
     * 按条件获取付费课程
     *
     * @param qo
     * @return
     */
    @PostMapping("/trainingProject/paid/get")
    public List<PaidTrainingProjectVO> getPaidTrainingProject(@Valid @RequestBody PaidTrainingProjectQO qo);

}
