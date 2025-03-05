package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "项目完成条件")
public class TpCompleteConditionVO implements Serializable {

    @ApiModelProperty(
        "项目完成条件类型：（-1:完成所有单元 0：完成学习单元数，1：完成指定学习单元， 2：完成指定学习单元并完成指定数量学习单元）")
    private Integer conditionPostType;

    @ApiModelProperty("完成学习单元数")
    private Integer completeCount;

    @ApiModelProperty("完成指定学习单元")
    private List<TpPlanVO> tpPlans;
}
