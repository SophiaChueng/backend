package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 16:12
 */
@Data
@ApiModel("培训计划排序更改vo")
public class TpPlanSortVo {

    @ApiModelProperty(value = "培训计划id", required = true)
    private Long id;

    @ApiModelProperty(value = "培训计划排序", required = true)
    private Integer sort;
}
