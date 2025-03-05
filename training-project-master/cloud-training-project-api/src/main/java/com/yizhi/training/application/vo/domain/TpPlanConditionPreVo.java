package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 学习计化前置条件
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
@Data
@Api(tags = "TpPlanConditionPreVo", description = "学习计化前置条件")
public class TpPlanConditionPreVo extends Model<TpPlanConditionPreVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "所属学习计划的id")
    private Long planId;

    @ApiModelProperty(value = "前置学习计划的id")
    private Long prePlanId;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
