package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/30 09:46
 */
@Data
@ApiModel("培训计划条件vo")
public class TpPlanConditionVo {

    @ApiModelProperty(value = "前置条件的id集合，未设置不传")
    private List<Long> prePlanIds;

    @ApiModelProperty(value = "设置完成活动数")
    private Integer postActivityNum;

    @ApiModelProperty(value = "指定的完成活动")
    private List<Long> postActivityRelationIds;

}
