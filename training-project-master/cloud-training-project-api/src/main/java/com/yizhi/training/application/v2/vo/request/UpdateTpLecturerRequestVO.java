package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class UpdateTpLecturerRequestVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("讲师id列表")
    private List<Long> lecturerIds;
}
