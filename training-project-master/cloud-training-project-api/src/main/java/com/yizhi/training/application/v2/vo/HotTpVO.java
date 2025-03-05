package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class HotTpVO {

    @ApiModelProperty("项目名")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("报名截止时间")
    private Date enrollEndTime;

    @ApiModelProperty("项目")
    private Long tpId;

    @ApiModelProperty(value = "0：指定范围，1平台用户", hidden = true)
    private Integer visibleRange;

    @ApiModelProperty(value = "活动数")
    private Integer activityCount;

}
