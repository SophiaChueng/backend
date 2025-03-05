package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 培训计划提醒 物理删除
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Data
@Api(tags = "TpPlanRemindVo", description = "培训计划提醒 物理删除")
@TableName("tp_plan_remind")
public class TpPlanRemind extends Model<TpPlanRemind> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训计划id")
    @TableField("tp_plan_id")
    private Long tpPlanId;

    @ApiModelProperty(value = "提醒方式，0：开始时间，1：结束时间，2：自定义时间")
    private Integer type;

    @ApiModelProperty(value = "type = 0 或 1 时 相差秒数")
    private Long seconds;

    @ApiModelProperty(value = "提醒时间")
    private Date time;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "创建者id")
    @TableField(value = "create_by_id", fill = FieldFill.INSERT)
    private Long createById;

    @ApiModelProperty(value = "创建者名称")
    @TableField(value = "create_by_name", fill = FieldFill.INSERT)
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
