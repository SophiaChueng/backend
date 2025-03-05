package com.yizhi.training.application.vo.domain;

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
public class TpPlanActivityConditionPostVo extends Model<TpPlanActivityConditionPostVo> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "所属学习活动的id")
    private Long tpPlanActivityId;

    @ApiModelProperty(value = "所属活动类型：1考试，2证书")
    private Integer type;

    @ApiModelProperty(value = "相关考试的id", notes = "考试、证书类型必传")
    private Long examId;

    @ApiModelProperty(value = "相关考试的得分", notes = "考试、证书类型必传")
    private Float examScore;

    @ApiModelProperty(value = "type=2时，证书id")
    private Long certificateId;

    @ApiModelProperty(value = "是否删除（1是，0否），默认否")
    private Integer deleted;

    @Override
    public Serializable pkVal() {
        return this.id;
    }

}
