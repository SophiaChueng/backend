package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author meicunzhi
 */
@Data
@ApiModel("培训项目课程进度")
public class StudyPlanProjectProgressVo {

    @ApiModelProperty("学员ID")
    private Long accountId = 0L;

    @ApiModelProperty("总课程次数")
    private Integer totalCourse = 0;

    @ApiModelProperty("完成课程次数")
    private Integer finishedCourseNum = 0;

    @ApiModelProperty("完成课程进度")
    private String finishedProgress = "";

    @ApiModelProperty("总学习时长  (转换成小时)")
    private String totalStudyHours = "";
}
