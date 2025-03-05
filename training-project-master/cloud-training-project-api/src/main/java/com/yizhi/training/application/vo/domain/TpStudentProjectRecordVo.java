package com.yizhi.training.application.vo.domain;

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
public class TpStudentProjectRecordVo extends Model<TpStudentProjectRecordVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "对应的培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "是否完成（1是，0否），默认完成。")
    private Integer finished;

    @ApiModelProperty(value = "完成时间")
    private Date finishDate;

    @ApiModelProperty(value = "用户id")
    private Long accountId;

    @ApiModelProperty(value = "所属站点id")
    private Long siteId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
