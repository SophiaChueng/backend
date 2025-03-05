package com.yizhi.training.application.vo.api;

import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 培训项目完成情况，由学习计划完成记录计算得出
 * </p>
 */
@Data
@Api(tags = "TpStudentProjectRecordEbscnVO", description = "培训项目完成情况，由学习计划完成记录计算得出")
public class TpStudentProjectRecordEbscnVO extends TpStudentProjectRecordVo {

    @ApiModelProperty(value = "培训项目信息")
    private TrainingProjectVo trainingProject;

    @ApiModelProperty(value = "用户对该培训项目的学习状态记录")
    private List<TpStudentProjectRecordVo> data;

    @ApiModelProperty(value = "该培训项目关联的课程id")
    private List<Long> CourseIds;
}
