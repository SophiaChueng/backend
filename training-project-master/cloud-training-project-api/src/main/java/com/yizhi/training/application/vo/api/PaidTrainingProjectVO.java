package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: PaidTrainingProjectVO
 * @author: zjl
 * @date: 2021/1/12  16:13
 */
@Data
@ApiModel(value = "付费项目返回vo")
public class PaidTrainingProjectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "logo图片")
    private String logoImg;

    @ApiModelProperty(value = "项目上架时间")
    private Date releaseTime;

    @ApiModelProperty(value = "项目开始时间")
    private Date startTime;

    @ApiModelProperty(value = "项目结束时间")
    private Date endTime;

    @ApiModelProperty(value = "购买金币的实际价格")
    private Integer actualPrice;

    @ApiModelProperty(value = "购买金币的原价")
    private Integer originalPrice;

    @ApiModelProperty(value = "兑换数")
    private Integer exchangeCount;

    @ApiModelProperty(value = "购买方式：0金币 1 兑换码/金币、2兑换码")
    private Integer meansOfPurchase;

}

