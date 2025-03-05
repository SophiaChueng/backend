package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Ding
 * @className TpPlanListVo
 * @description TODO
 * @date 2018/12/4
 **/
@Data
@ApiModel("培训项目任务列表VO-任务完成情况API使用")
public class TpPlanFinishedListVo {

    @ApiModelProperty(value = "计划id")
    private Long TpPlanId;

    @ApiModelProperty(value = "计划名称")
    private String TpPlanName;

    @ApiModelProperty("是否完成--1完成,0未完成")
    private Integer pass;

    @ApiModelProperty(value = "完成状态0:未解锁 1:已完成 2:未完成")
    private Integer state;

    @ApiModelProperty(value = "计划未完成时间")
    private Date finishDate;

    @ApiModelProperty(value = "计划开始时间")
    private Date startTime;

    @ApiModelProperty(value = "计划结束时间")
    private Date endTime;
}
