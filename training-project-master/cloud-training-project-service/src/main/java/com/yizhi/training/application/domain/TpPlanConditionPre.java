package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("tp_plan_condition_pre")
public class TpPlanConditionPre extends Model<TpPlanConditionPre> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "所属学习计划的id")
    @TableField("plan_id")
    private Long planId;

    @ApiModelProperty(value = "前置学习计划的id")
    @TableField("pre_plan_id")
    private Long prePlanId;

    private Integer finishCount;

    private Long companyId;

    private Long siteId;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
