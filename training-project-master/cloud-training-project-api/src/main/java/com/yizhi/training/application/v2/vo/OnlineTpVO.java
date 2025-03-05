package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class OnlineTpVO {

    @ApiModelProperty("项目名")
    private String name;

    @ApiModelProperty("logo")
    private String logo;

    @ApiModelProperty("开始时间")
    private Date startAt;

    @ApiModelProperty("结束时间")
    private Date endAt;

    @ApiModelProperty("项目")
    private Long tpId;

    @ApiModelProperty("活动数/期数（项目PRO）")
    private Integer activityCount;

    @ApiModelProperty("0:项目 1：项目PRO")
    private Integer tpType;

    @ApiModelProperty(value = "0：指定范围，1平台用户", hidden = true)
    private Integer visibleRange;

    @ApiModelProperty("付费报名情况 1：付费，2：兑换码，3：付费/兑换码 4.会员")
    private Integer payType;

    @ApiModelProperty("权重")
    private Integer sort;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createdAt;

    @ApiModelProperty(value = "是否显示项目介绍页（0：不显示 1：默认 显示）")
    private Integer projectDescriptionFlag;

    @ApiModelProperty("参加人数")
    private Integer joinNumber;
    @ApiModelProperty("项目完成状态")
    private Integer status;
    @ApiModelProperty("是否开启报名")
    private Integer enableEnroll;

}
