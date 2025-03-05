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
 * 培训项目完成情况，由学习计划完成记录计算得出
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-09
 */
@Data
@Api(tags = "TpStudentProjectRecordVo", description = "培训项目完成情况，由学习计划完成记录计算得出")
@TableName("tp_student_project_record")
public class TpStudentProjectRecord extends Model<TpStudentProjectRecord> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    private Long companyId;

    @ApiModelProperty(value = "对应的培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

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

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
