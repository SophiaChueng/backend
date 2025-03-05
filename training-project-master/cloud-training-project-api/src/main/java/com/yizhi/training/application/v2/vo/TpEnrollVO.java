package com.yizhi.training.application.v2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(description = "项目签到设置")
public class TpEnrollVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("是否开启付费（0：关闭，1：开启）")
    private Integer enablePay;

    @ApiModelProperty("付费方式 0：非付费，1：虚拟币，2：兑换码，3：虚拟币/兑换码 4.会员 4.会员")
    private Integer payType;

    @ApiModelProperty("购买价格")
    private Integer actualPrice;

    @ApiModelProperty("原价")
    private Integer originalPrice;

    @ApiModelProperty("限制人数（无限制为0）")
    private Integer personLimitNum;

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("报名须知")
    private String notice;

    @ApiModelProperty("是否需要审核（0：否，1：是）")
    private Integer needAudit;
}
