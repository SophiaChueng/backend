package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/5/11 10:31
 */
@Data
@ApiModel(value = "最近7天课程学习时长")
public class TrainingProjectCourseRecentVo {

    @ApiModelProperty("日期")
    private Date date;

    @ApiModelProperty("学习时长")
    private Long seconds = 0L;

}
