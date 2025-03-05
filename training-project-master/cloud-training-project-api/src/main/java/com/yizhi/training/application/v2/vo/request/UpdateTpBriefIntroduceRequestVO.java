package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "更新项目简介入参")
@Data
public class UpdateTpBriefIntroduceRequestVO implements Serializable {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("项目简介")
    private String description;
}
