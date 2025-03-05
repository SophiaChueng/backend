package com.yizhi.training.application.v2;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("完成的项目")
@Data
public class MyTpFinishedVO {

    @ApiModelProperty("项目ID或proID")
    private Long tpId;

    @ApiModelProperty("0:项目 1：项目PRO")
    private Integer tpType;

    @ApiModelProperty("图片地址")
    private String logo;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("完成时间")
    private Date finishedAt;

    @ApiModelProperty("开始时间")
    private Date startAt;

    @ApiModelProperty("结束时间")
    private Date endAt;
    @ApiModelProperty("是否开启报名")
    private Integer enableEnroll;
    @ApiModelProperty("报名方式")
    private Integer payType;

    @ApiModelProperty("期数")
    private Integer activityCount;
}
