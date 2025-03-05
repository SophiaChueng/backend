package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/26 19:42
 */
@Data
@ApiModel(value = "培训项目分类vo")
public class TpClassificationVo {

    @ApiModelProperty(value = "名称", notes = "新增必填")
    private String name;

    @ApiModelProperty(value = "描述", notes = "")
    private String description;
}
