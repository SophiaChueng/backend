package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 20:12
 */
@Data
@ApiModel("培训活动排序更改vo")
public class TpPlanActivitySortVo {

    @ApiModelProperty(value = "培训活动id", required = true)
    private Long id;

    @ApiModelProperty(value = "培训活动排序", required = true)
    private Integer sort;

}
