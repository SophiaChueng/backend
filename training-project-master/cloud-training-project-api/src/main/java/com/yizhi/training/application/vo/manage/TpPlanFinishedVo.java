package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Ding
 * @className TpPlanFinishedVo
 * @description TODO
 * @date 2018/12/4
 **/
@Data
@ApiModel("培训计划完成情况VO-任务完成情况API使用")
public class TpPlanFinishedVo {

    @ApiModelProperty(value = "计划任务ID")
    private Long TpPlanId;

    @ApiModelProperty(value = "计划任务名称")
    private String name;

    @ApiModelProperty(value = "设置完成活动数")
    private Integer num;

    @ApiModelProperty(value = "设置指定活动ID")
    private Long relationId;

    @ApiModelProperty(value = "计划关联活动")
    private List<TpPlanFinishedActivityVo> activityList;

    @ApiModelProperty(value = "项目是否过期")
    private boolean tpInTime;

    @ApiModelProperty(value = "计划是否过期")
    private boolean tpPlanINTime;

}
