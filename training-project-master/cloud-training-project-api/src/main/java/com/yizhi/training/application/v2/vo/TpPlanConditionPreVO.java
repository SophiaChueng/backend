package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "学习单元/计划前置条件")
public class TpPlanConditionPreVO implements Serializable {

    @ApiModelProperty("前置学习单元")
    private List<TpPlanVO> prePlans;

    @ApiModelProperty("完成前置单元的n个")
    private Integer finishCount;
}
