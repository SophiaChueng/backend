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
 * 学习计划完成条件
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
@Data
@Api(tags = "TpPlanConditionPostVo", description = "学习计划完成条件")
@TableName("tp_plan_condition_post")
public class TpPlanConditionPost extends Model<TpPlanConditionPost> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "所属学习计划id")
    @TableField("tp_plan_id")
    private Long tpPlanId;

    @ApiModelProperty(value = "0：设置完成活动数，1：指定学习活动")
    private Integer type;

    @ApiModelProperty(value = "type=0时，设置的完成的数目")
    private Integer num;

    @ApiModelProperty(value = "type=1时，指定的活动id")
    @TableField("tp_plan_activity_id")
    private Long tpPlanActivityId;

    @ApiModelProperty(value = "type=1时，指定的活动相关联的id（比如：考试活动即考试的id，课程活动即课程的id）")
    @TableField("tp_plan_activity_relation_id")
    private Long tpPlanActivityRelationId;

    private Long companyId;

    private Long siteId;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
