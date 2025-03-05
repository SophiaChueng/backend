package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 14:11
 */
@Data
@ApiModel("培训项目相关学习计划")
public class StudyPlanProjectVo extends TrainingProjectVo {

    @ApiModelProperty(value = "学习计划", notes = "学习计划")
    List<TpPlanVo> plans;

    Long studyPlanCode;

    Long accountId;

    String studyStartTime;

    Integer completionStatus;

    String completionTime;
}
