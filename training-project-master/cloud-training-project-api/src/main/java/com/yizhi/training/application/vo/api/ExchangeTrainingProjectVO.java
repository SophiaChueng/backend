package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: PaidTrainingProjectVO
 * @author: zjl
 * @date: 2021/1/12  16:13
 */
@Data
@ApiModel(value = "兑换码兑换返回")
public class ExchangeTrainingProjectVO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "培训项目||课程id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "logo图片")
    private String logoImg;

    @ApiModelProperty(value = "项目兑换时间")
    private Date exchangeTime;

    @ApiModelProperty(value = "兑换码")
    private String code;

    @ApiModelProperty(value = "类别")
    private Integer refType;

    @ApiModelProperty(value = "项目状态")
    private Integer status;

}

