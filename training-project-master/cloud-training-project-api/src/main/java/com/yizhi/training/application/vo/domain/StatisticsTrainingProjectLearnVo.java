package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
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
public class StatisticsTrainingProjectLearnVo extends Model<StatisticsTrainingProjectLearnVo> {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "用户Id")
    private Long accountId;

    @ApiModelProperty(value = "首次学习时间")
    private Date first_learn_time;

    @ApiModelProperty(value = "完成时间")
    private Date finish_time;

    @ApiModelProperty(value = "1未完成 2已完成")
    private Integer finished;

    @ApiModelProperty(value = "获取积分")
    private Integer learnPoint;

    @ApiModelProperty(value = "记录创建时间")
    private Date recordCreateTime;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
