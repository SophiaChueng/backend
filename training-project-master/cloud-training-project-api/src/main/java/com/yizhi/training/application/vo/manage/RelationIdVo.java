package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("可见范围关联Vo")
@Data
public class RelationIdVo {

    @ApiModelProperty("类型：1用户，2部门，3用户组")
    private Integer type;

    @ApiModelProperty("授权对象id")
    private Long relationId;

    @ApiModelProperty("授权对象的名称")
    private String name;

}