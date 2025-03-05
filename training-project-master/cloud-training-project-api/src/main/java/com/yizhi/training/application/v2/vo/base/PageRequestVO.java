package com.yizhi.training.application.v2.vo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class PageRequestVO implements Serializable {

    @ApiModelProperty("页号")
    private Integer pageNo;

    @ApiModelProperty("页大小")
    private Integer pageSize;

}
