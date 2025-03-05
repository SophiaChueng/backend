package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class DeleteProjectsFromProRequestVO implements Serializable {

    @ApiModelProperty("项目proId")
    private Long tpProId;

    @ApiModelProperty("项目id列表")
    private List<Long> trainingProjectIds;
}
