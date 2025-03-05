package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TpStatusDetailsVO {

    @ApiModelProperty("项目ID")
    private Long tpId;

    @ApiModelProperty("项目名")
    private String tpName;

    @ApiModelProperty("项目PRO名 只有查询类型为1时，才返回项目PRO名")
    private String tpProName;

    private String logo;

    private Date tpStartAt;

    private Date tpEndAt;

    @ApiModelProperty("当为空时，表示没有人数限制。具体数值代表报名人数限制")
    private Integer tpEnrollUserLimit;

    @ApiModelProperty("已报名人数")
    private Integer tpEnrollUserCount;

    @ApiModelProperty("报名状态 1：已报名；0：未报名")
    private Integer enrollStatus;

    @ApiModelProperty("报名审批状态 0：不需要审核，1：待审核，2：审核通过，3：审核不通过，4:名额已满")
    private Integer enrollAuditStatus;

    @ApiModelProperty("报名的实际价格")
    private Integer enrollActualPrice;

    @ApiModelProperty("报名的原价")
    private Integer enrollOriginalPrice;

    private Date enrollStartAt;

    private Date enrollEndAt;

    @ApiModelProperty("活动数")
    private Integer activityCount;

    @ApiModelProperty("积分数")
    private Integer pointCount;

    @ApiModelProperty("证书数")
    private Integer certificateCount;

    @ApiModelProperty("学时数")
    private String studyHourCount;

    @ApiModelProperty("1:需要报名 0：不需要报名")
    private Integer enableEnroll;

    @ApiModelProperty("付费报名情况  0：非付费 1：虚拟币，2：兑换码，3：虚拟币/兑换码 4.会员")
    private Integer payType;

    @ApiModelProperty("系统时间时间戳")
    private Long systemTime;

}
