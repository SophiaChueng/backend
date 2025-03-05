package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/11 14:26
 */
@Data
@ApiModel(value = "培训项目id vo")
public class IdVo {

    @ApiModelProperty(value = "培训项目id")
    private Long id;

}
