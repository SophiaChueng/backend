package com.yizhi.training.application.domain;

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
 * 学习计划完成记录，由两张条件表和活动记录表计算得出
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
@Data
@Api(tags = "TpStudentPlanRecordVo", description = "学习计划完成记录，由两张条件表和活动记录表计算得出")
@TableName("tp_student_plan_record")
public class TpStudentPlanRecord extends Model<TpStudentPlanRecord> {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long companyId;

    @ApiModelProperty(value = "学习计划id")
    @TableField("tp_plan_id")
    private Long tpPlanId;

    @ApiModelProperty(value = "是否完成（1是，0否），默认完成。")
    private Integer finished;

    @ApiModelProperty(value = "完成时间")
    @TableField("finish_date")
    private Date finishDate;

    @ApiModelProperty(value = "用户id")
    @TableField("account_id")
    private Long accountId;

    @ApiModelProperty(value = "所属站点id")
    @TableField("site_id")
    private Long siteId;

    @ApiModelProperty(value = "所属培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
