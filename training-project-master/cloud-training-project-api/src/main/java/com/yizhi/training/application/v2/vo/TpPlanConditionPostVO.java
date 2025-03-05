package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "学习计划/单元完成条件")
public class TpPlanConditionPostVO implements Serializable {

    @ApiModelProperty(
        "单元完成条件类型：（-1:完成所有活动 0：完成学习活动数，1：完成指定学习活动， 2：完成指定学习活动并完成指定数量学习活动）")
    private Integer conditionPostType;

    @ApiModelProperty("完成学习活动数")
    private Integer completeCount;

    @ApiModelProperty("完成指定学习活动")
    private List<TpPlanActivityVO> activities;
}
