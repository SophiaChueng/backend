package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TpMessageRangeVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("计划id")
    private Long tpPlanId;

    @ApiModelProperty("发送范围")
    private Integer sendRange;

}
