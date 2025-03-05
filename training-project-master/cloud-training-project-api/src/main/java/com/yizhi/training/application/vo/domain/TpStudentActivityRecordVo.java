package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 学员完成活动记录（这里无论有没有被设置成别的活动的开启条件，都记录）
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
@Data
@Api(tags = "TpStudentActivityRecordVo",
    description = "学员完成活动记录（这里无论有没有被设置成别的活动的开启条件，都记录）")
public class TpStudentActivityRecordVo extends Model<TpStudentActivityRecordVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7报名 8签到 9外部链接")
    private Integer type;

    @ApiModelProperty(value = "学员id")
    private Long accountId;

    @ApiModelProperty(value = "课程、考试、调研、直播、投票、作业、证书、外部链接的id，由type决定类型。")
    private Long relationId;

    @ApiModelProperty(value = "若是考试（type=1）的话，需要记录分数")
    private Float score;

    @ApiModelProperty(value = "若是课程，需要记录学习时长")
    private Long seconds;

    @ApiModelProperty(value = "是否完成（1是，0否），默认完成。")
    private Integer finished;

    @ApiModelProperty(value = "完成时间")
    private Date finishDate;

    @ApiModelProperty(value = "所属站点id")
    private Long siteId;

    @ApiModelProperty(value = "原始数据id")
    private Long oId;

    @ApiModelProperty(value = "原始数据id")
    private Integer isCustom;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
