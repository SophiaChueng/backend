package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/26 15:42
 */
@Data
@ApiModel(value = "培训项目详情vo")
public class TrainingProjectDetailVo {

    @ApiModelProperty("培训详情--培训介绍")
    private TrainingProjectIntroductionVo introductionVo;

    @ApiModelProperty("培训详情--培训内容")
    private TrainingProjectContentVo contentVo;

    @ApiModelProperty("培训项目学习进度统计")
    private TrainingProjectProgressVo progressVo = new TrainingProjectProgressVo();

}
