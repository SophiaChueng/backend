package com.yizhi.training.application.controller.fo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.controller.TrainingProjectController;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.fo.TrainingProjectFoVo;
import com.yizhi.training.application.vo.manage.SearchProjectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fo/trainingProject")
public class TrainingProjectFOController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingProjectController.class);

    @Autowired
    private ITrainingProjectService trainingProjectService;

    //此接口为fo商学院提供
    @GetMapping(value = "/list")
    public Page<TrainingProjectFoVo> folist(@RequestBody SearchProjectVo searchProjectVo) {
        Page<TrainingProjectFoVo> page = trainingProjectService.searchFoPage(searchProjectVo);
        LOGGER.info("返回值{}" + page);
        return page;
    }
}
