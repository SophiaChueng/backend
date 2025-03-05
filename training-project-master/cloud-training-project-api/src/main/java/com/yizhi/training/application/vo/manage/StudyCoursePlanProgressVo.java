package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author meicunzhi
 */
@Data
@ApiModel("培训项目相关考试学习")
public class StudyCoursePlanProgressVo {

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

    @ApiModelProperty("总学习时长  (转换成小时)")
    private String totalStudyHours = "";
}
