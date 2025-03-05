package com.yizhi.training.application.vo.domain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author kjc
 * @Classname CourseRelateProjectVO
 * @Description: TODO
 * @date 2022/1/210:12 上午
 */
@Data
@Api(value = "课程关联项目VO", tags = "课程关联项目VO")
public class CourseRelateProjectVO {

    @ApiModelProperty("项目课程关联表id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("课程id")
    private Long courseId;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("项目名称")
    private String projectName;

}
