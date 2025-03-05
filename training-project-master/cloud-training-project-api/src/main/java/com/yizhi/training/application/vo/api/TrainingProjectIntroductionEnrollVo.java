package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/10 17:04
 */
@Data
@ApiModel(value = "培训项目详情--培训简介--报名须知vo")
public class TrainingProjectIntroductionEnrollVo {

    @ApiModelProperty(value = "报名主键id")
    private Long id;

    @ApiModelProperty(value = "报名名额")
    private Integer limit;

    @ApiModelProperty(value = "报名开始时间")
    private Date startTime;

    @ApiModelProperty(value = "报名结束时间")
    private Date endTime;

    @ApiModelProperty(notes = "注意事项")
    private String notice;

}
