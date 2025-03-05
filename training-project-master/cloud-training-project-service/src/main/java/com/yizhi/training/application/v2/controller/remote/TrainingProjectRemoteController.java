package com.yizhi.training.application.v2.controller.remote;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.v2.service.biz.TrainingProjectBizService;
import com.yizhi.training.application.v2.vo.RecentStudyTrainingVO;
import com.yizhi.training.application.v2.vo.TpBaseInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/remote/trainingProject")
public class TrainingProjectRemoteController {

    @Autowired
    private TrainingProjectBizService trainingProjectBizService;

    @GetMapping("/get")
    public TpBaseInfoVO getTrainingProject(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return trainingProjectBizService.getProjectBaseInfo(trainingProjectId);
    }

    @GetMapping("/recentStudy/list")
    public Page<RecentStudyTrainingVO> getRecentStudyList(@RequestParam("pageNo") Integer pageNo,
        @RequestParam("pageSize") Integer pageSize) {
        return trainingProjectBizService.getRecentStudyList(pageNo, pageSize);
    }

}
