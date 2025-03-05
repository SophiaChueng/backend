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
 * 学习活动（考试、证书）完成条件
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
@Data
@Api(tags = "TpPlanActivityConditionPostVo", description = "学习活动（考试、证书）完成条件")
@TableName("tp_plan_activity_condition_post")
public class TpPlanActivityConditionPost extends Model<TpPlanActivityConditionPost> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属学习活动的id")
    @TableField("tp_plan_activity_id")
    private Long tpPlanActivityId;

    @ApiModelProperty(value = "所属活动类型：1考试，2证书")
    private Integer type;

    @ApiModelProperty(value = "相关考试的id", notes = "考试、证书类型必传")
    @TableField("exam_id")
    private Long examId;

    @ApiModelProperty(value = "相关考试的得分", notes = "考试、证书类型必传")
    @TableField("exam_score")
    private Float examScore;

    @ApiModelProperty(value = "type=2时，证书id")
    @TableField("certificate_id")
    private Long certificateId;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
