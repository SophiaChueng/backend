package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "可见范围")
public class TpVisibleRangeVO implements Serializable {

    @ApiModelProperty("1：部门，2：用户")
    private Integer type;

    @ApiModelProperty("关联id")
    private Long relationId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty(value = "用户名")
    private String fullName;

    @ApiModelProperty(value = "工号")
    private String workNum;
}
