package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class SearchProjectToProVO implements Serializable {

    @ApiModelProperty("项目")
    private Long tpProId;

    @ApiModelProperty("筛选的项目名称")
    private String searchTpName;

    @ApiModelProperty("页码")
    private Integer pageNo;

    @ApiModelProperty("页面大小")
    private Integer pageSize;
}
