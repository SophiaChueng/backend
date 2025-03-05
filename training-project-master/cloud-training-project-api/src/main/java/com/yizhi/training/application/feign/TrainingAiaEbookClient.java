package com.yizhi.training.application.feign;

import com.yizhi.training.application.vo.AiaEbookSearchRsp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "trainingProject", contextId = "TrainingAiaEbookClient")
public interface TrainingAiaEbookClient {

    @GetMapping("/aia/ebook/search")
    List<AiaEbookSearchRsp> searchKey(@RequestParam("tid") Long tid,
        @RequestParam(value = "key", defaultValue = "未搜索") String key);

}
