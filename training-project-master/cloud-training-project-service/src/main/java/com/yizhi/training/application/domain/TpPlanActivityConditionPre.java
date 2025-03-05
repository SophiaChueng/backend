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
 * 学习活动前置条件
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
@Data
@Api(tags = "TpPlanActivityConditionPreVo", description = "学习活动前置条件")
@TableName("tp_plan_activity_condition_pre")
public class TpPlanActivityConditionPre extends Model<TpPlanActivityConditionPre> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属学习活动的id")
    @TableField("tp_plan_activity_id")
    private Long tpPlanActivityId;

    @ApiModelProperty(value = "0：设置完成活动数，1：指定学习活动")
    private Integer type;

    @ApiModelProperty(value = "type=0时，设置的完成的数目")
    private Integer num;

    @ApiModelProperty(value = "type=1时，指定的活动id")
    @TableField("pre_tp_plan_activity_id")
    private Long preTpPlanActivityId;

    @ApiModelProperty(value = "type=1时，活动相关联的资源的id")
    @TableField("pre_tp_plan_activity_relation_id")
    private Long preTpPlanActivityRelationId;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
