package com.yizhi.training.application.vo.api;

import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 培训项目完成情况，由学习计划完成记录计算得出
 * </p>
 *
 * @author ding
 * @since 2019-10-22
 */
@Data
@Api(tags = "TpStudentProjectRecordVoVO", description = "培训项目完成情况，由学习计划完成记录计算得出")
public class TpStudentProjectRecordVoVO extends TpStudentProjectRecordVo {

    @ApiModelProperty(value = "项目完成状态 0:未解锁(未开始) 1:已完成 2:未完成")
    private Integer state;

    @ApiModelProperty(value = "项目开始时间")
    private Date startTime;

    @ApiModelProperty(value = "项目结束时间")
    private Date endTime;

    @ApiModelProperty("培训项目名称")
    private String trainingProjectName;
}
