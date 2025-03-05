package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author meicunzhi
 */
@Data
@ApiModel("培训项目相关考试学习")
public class StudyExamPlanProjectVo {

    Long studyPlanCode;

    Long accountId;

    String studyStartTime;

    Integer completionStatus;

    String completionTime;

    @ApiModelProperty(value = "学习计划ID")
    private String planActivityCode;

    @ApiModelProperty(value = "学习计划名称")
    private String planActivityname;

    @ApiModelProperty(value = "关联的活动的id")
    private Long relationId;

    @ApiModelProperty(value = "活动名称")
    private String relationName;

    @ApiModelProperty(value = "分数")
    private BigDecimal passedScore;
}
