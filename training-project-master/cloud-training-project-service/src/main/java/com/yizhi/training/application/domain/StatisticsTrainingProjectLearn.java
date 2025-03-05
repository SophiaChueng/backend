package com.yizhi.training.application.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 学员学习记录
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
@Data
@Api(tags = "StatisticsTrainingProjectLearnVo", description = "学员学习记录")
@TableName("statistics_training_project_learn")
public class StatisticsTrainingProjectLearn extends Model<StatisticsTrainingProjectLearn> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    @TableField("training_project_id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "用户Id")
    @TableField("account_id")
    private Long accountId;

    @ApiModelProperty(value = "首次学习时间")
    @TableField("firstLearnTime")
    private Date first_learn_time;

    @ApiModelProperty(value = "完成时间")
    @TableField("finishTime")
    private Date finish_time;

    @ApiModelProperty(value = "1未完成 2已完成")
    @TableField("finished")
    private Integer finished;

    @ApiModelProperty(value = "获取积分")
    @TableField("learn_point")
    private Integer learnPoint;

    @ApiModelProperty(value = "记录创建时间")
    @TableField("record_create_time")
    private Date recordCreateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
