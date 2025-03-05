package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/10 16:59
 */
@Data
@ApiModel(value = "培训项目详情--培训简介vo")
public class TrainingProjectIntroductionVo {

    @ApiModelProperty(value = "培训项目主键id")
    private Long id;

    @ApiModelProperty(value = "图片")
    private String logoImg;

    @ApiModelProperty(value = "项目名称")
    private String name;

    @ApiModelProperty(value = "项目介绍")
    private String description;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "是否需要报名")
    private Boolean needEnroll = false;

    @ApiModelProperty(value = "报名名额")
    private Integer enrollLimit = 0;

    @ApiModelProperty(value = "已经报名人数")
    private Integer hasEnrolledNum = 0;

    @ApiModelProperty(value = "是否已经报名")
    private Boolean hasEnrolled = false;

    @ApiModelProperty(value = "是否需要审核")
    private Boolean needAudit = false;

    @Deprecated
    @ApiModelProperty(value = "是否已经审核")
    private Boolean audited = false;

    @ApiModelProperty(value = "审核状态：1：待审核，2：审核通过，3：审核不通过。")
    private Integer auditStatus = 0;

    @ApiModelProperty(value = "报名须知，不需则为null")
    private TrainingProjectIntroductionEnrollVo enroll = new TrainingProjectIntroductionEnrollVo();

    @ApiModelProperty(value = "根据参与人数与培训人数的比较： 立即报名-1、名额已满-2")
    private Integer memberStutas;

    @ApiModelProperty(value = "根据参与人数与培训人数的比较： 立即报名-msg.immediately.enroll、名额已满-msg.quota.full")
    private String memberStutasCodeName;

    @ApiModelProperty(
        value = "审核状态码：1：待审核-msg.audit.ing，2：审核通过-msg.audit.success，3：审核不通过-msg.audit.fail")
    private String auditStutasCodeName;

    @ApiModelProperty(value = "灯箱效果提示框")
    private String titleStutasCodeName;

    @ApiModelProperty(value = "项目可见范围类型==0：指定学员可见，1平台用户可见（创建人管理权限范围）")
    private Integer type;

    @ApiModelProperty(value = "培训名额 与项目可见范围有关")
    private Integer trMembers;

    @ApiModelProperty(
        value = "培训项目所有情况的列举" + "1-培训时间未开始，请等待！  2-开始学习 3-报名尚未开始，无法进行项目学习！ 4-报名已结束，无法进行项目学习！" + "5-您尚未报名，报名成功后可进行学习。 "
            + "6-名额已满,无法进行项目学习！ 7-报名审核中,无法进行项目学习！ 8-报名审核未通过,无法进行项目学习！9-已完成")
    private Integer status;

    @ApiModelProperty(value = "项目是否已完成")
    private Boolean hasFinished;

    @ApiModelProperty(value = "是否显示积分")
    private Boolean enablePoint;

    @ApiModelProperty(value = "培训项目主体积分")
    private Integer point;

    @ApiModelProperty(value = "所有积分最小总数")
    private Integer minPoint;

    @ApiModelProperty(value = "所有积分最大总数")
    private Integer maxPoint;

    @ApiModelProperty("学时")
    private Float period;

    @ApiModelProperty("培训项目学时显示 true 显示，false不显示")
    private Boolean trainingDurationShow;

    @ApiModelProperty(value = "多少人已经参与")
    private Integer JoinNumber;

    @ApiModelProperty(value = "是否开启定位（0：否，1：是），默认否")
    private Integer enablePosition;

    @ApiModelProperty(value = "是否付费报名，0否；1是")
    private Integer enablePay;

    @ApiModelProperty(value = "购买实际价格")
    private Integer actualPrice;

    @ApiModelProperty(value = "购买原价格")
    private Integer originalPrice;

    @ApiModelProperty(value = "付费类型 4.会员")
    private Integer payType;
}
