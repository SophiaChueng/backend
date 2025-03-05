package com.yizhi.training.application.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel
@Data
public class StudentTpEntriy {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("报名时间或最后学习时间")
    private Date time;

    @ApiModelProperty("项目开始时间")
    private Date startAt;

    @ApiModelProperty("项目结束时间")
    private Date endAt;
}
