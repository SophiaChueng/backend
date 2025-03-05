package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TrainJoinNumVO {

    @ApiModelProperty(value = "项目id")
    private Long trainProjectId;

    @ApiModelProperty(value = "参加人数")
    private Integer joinNum;
}
