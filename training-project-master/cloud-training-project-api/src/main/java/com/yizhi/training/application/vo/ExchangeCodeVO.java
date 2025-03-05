package com.yizhi.training.application.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("兑换码VO")
public class ExchangeCodeVO {

    private Long id;

    @ApiModelProperty(value = "兑换码")
    private String code;

    @ApiModelProperty(value = "状态 0：未使用，1：已使用")
    private Integer state;

    @ApiModelProperty(value = "兑换时间")
    private Date exchangeTime;

    @ApiModelProperty(value = "兑换人ID")
    private Long accountId;

    @ApiModelProperty("用户名")
    private String account;

    @ApiModelProperty("用户姓名")
    private String fullName;
}
