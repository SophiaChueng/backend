package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class SearchTpProVO implements Serializable {

    @ApiModelProperty("项目pro名称")
    private String tpProName;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("页码")
    private Integer pageNo;

    @ApiModelProperty("页面大小")
    private Integer pageSize;
}
