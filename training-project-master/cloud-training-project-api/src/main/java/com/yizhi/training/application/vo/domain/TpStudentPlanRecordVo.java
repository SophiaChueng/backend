package com.yizhi.training.application.vo.domain;

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
public class TpStudentPlanRecordVo extends Model<TpStudentPlanRecordVo> {

    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty(value = "学习计划id")
    private Long tpPlanId;

    @ApiModelProperty(value = "是否完成（1是，0否），默认完成。")
    private Integer finished;

    @ApiModelProperty(value = "完成时间")
    private Date finishDate;

    @ApiModelProperty(value = "用户id")
    private Long accountId;

    @ApiModelProperty(value = "所属站点id")
    private Long siteId;

    @ApiModelProperty(value = "所属培训项目id")
    private Long trainingProjectId;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
