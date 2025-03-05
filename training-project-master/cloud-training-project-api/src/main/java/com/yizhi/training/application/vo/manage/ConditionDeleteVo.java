package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/8 11:16
 */
@Data
@ApiModel("删除条件（可以是培训计划条件，可以是培训活动条件集合）")
public class ConditionDeleteVo {

    @ApiModelProperty(value = "前置条件id集合")
    private List<Long> preConditionIds;

    @ApiModelProperty(value = "后置条件id集合")
    private List<Long> postConditionIds;

}
