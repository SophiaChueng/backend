package com.yizhi.training.application.vo.domain;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 学生参与培训项目记录（只针对需要报名的培训项目，不需要报名的该表不记录）
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-28
 */
@Data
@Api(tags = "TpStudentEnrollPassedVo",
    description = "学生参与培训项目记录（只针对需要报名的培训项目，不需要报名的该表不记录）")
public class TpStudentEnrollPassedVo extends Model<TpStudentEnrollPassedVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "报名id")
    private Long enrollId;

    @ApiModelProperty(value = "学员id")
    private Long accountId;

    @ApiModelProperty(value = "冗余学习项目开始时间")
    private Date startTime;

    @ApiModelProperty(value = "冗余学习项目结束时间")
    private Date endTime;

    @ApiModelProperty(value = "参与时间，两种情况：1报名需审核，2不需审核")
    private Date joinTime;

    @ApiModelProperty(value = "所属站点id")
    private Long siteId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
