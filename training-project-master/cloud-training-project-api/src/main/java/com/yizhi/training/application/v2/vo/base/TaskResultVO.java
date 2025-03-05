package com.yizhi.training.application.v2.vo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class TaskResultVO implements Serializable {

    @ApiModelProperty("")
    private Long taskId;

    @ApiModelProperty("")
    private String serialNo;

    @ApiModelProperty("")
    private String taskName;
}
