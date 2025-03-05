package com.yizhi.training.application.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author kjc
 * @Classname CourseProjectNameSyncVO
 * @Description: TODO
 * @date 2022/1/48:38 下午
 */
@Data
@Api(value = "课程关联项目名称同步VO", tags = "课程关联项目名称同步VO")
public class CourseProjectNameSyncVO {

    @ApiModelProperty("培训计划活动Ids")
    private ArrayList<Long> tpPlanActivityIds;

    @ApiModelProperty("课程id")
    private Long courseId;
}
