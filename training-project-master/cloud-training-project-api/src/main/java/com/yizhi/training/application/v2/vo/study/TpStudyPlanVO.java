package com.yizhi.training.application.v2.vo.study;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("学习页计划")
public class TpStudyPlanVO<T> {

    @ApiModelProperty("单元名")
    private String name;

    @ApiModelProperty("当true时，需要展示活动；false：默认不展开")
    private Boolean showActivity = false;

    private Long id;

    //① 随到随学（对应管理端 无时间限制）
    //
    //       ② 开始时间~结束时间
    //
    //       ③X天（X小时）内可学习（倒计时形式，即开始学习后X天内或单元元解锁后X天内）
    //
    //       ④ 前置单元完成后X天内可学习 （有前置单元，未完成前置单元的情况下）
    //
    //       ⑤ X天（X小时）内可学习 （倒计时形式，前置单元完成后）
    //       6 已过期
    @ApiModelProperty("学习时间")
    private String studyTime;

    @ApiModelProperty("学习单元中的活动列表")
    private List<T> activityList;

    @ApiModelProperty("已获得证书的地址")
    private List<String> certificateUrl;

    @ApiModelProperty("true:完成 false:未完成")
    private Boolean finished = false;

    @ApiModelProperty("true:关联了证书 false:未关联证书")
    private Boolean showCertificate = false;

    @ApiModelProperty("true: 需要申请，false：不需要申请;")
    private Boolean certificateApplyStatus = false;

    @ApiModelProperty("0:待审批，1：通过（自动发放，已获得证书），2：不通过,null：待申请；")
    private Integer certificateAuditStatus = null;

    @ApiModelProperty("true：学习单元有关联资料；false：无关联资料")
    private Boolean showDocument;

    /**
     * n个活动未完成（对应条件为 完成所有活动）
     *
     * n个必修活动未完成（对应条件为 完成指定学习活动）
     *
     * n个选修活动未完成（对应条件为 完成X个学习活动）
     *
     * n个必修、m个选修未完成（对应条件为 完成指定学习活动+完成X个学习活动）
     *
     * 其中n、m根据完成条件的要求数量与实际已完成的数量计算得到
     */
    @ApiModelProperty("学习进度")
    private String finishedProgressMsg;

    @ApiModelProperty("学习单元未锁定状态，true:待解锁。false：已解锁")
    private Boolean lockStatus = false;

    @ApiModelProperty("学习单元未解锁描述")
    private String lockMsg;

    @ApiModelProperty("计划开始时间")
    private Date startTime;

    @ApiModelProperty("计划结束时间")
    private Date endTime;

    @ApiModelProperty("到期后可以继续学习 ture：可以继续学习；false:不能学")
    private Boolean continueStudy = true;

    @ApiModelProperty("是否开启顺序学习； true：开启；false：未开启")
    private Boolean sequenceStudy = true;

    @ApiModelProperty(value = "项目完成时间", hidden = true)
    private Date finishedDate;

    @ApiModelProperty("本次查询导致项目完成")
    private Boolean tpFinished = false;

    @ApiModelProperty(value = "查询导致项目完成", hidden = true)
    private Boolean selectPlanFinished = false;

}

