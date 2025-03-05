package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/5/11 10:04
 */
@Data
@ApiModel(value = "培训项目学习进度统计")
public class TrainingProjectProgressVo {

    @ApiModelProperty("总学习时长")
    private Long totalStudySeconds = 0L;

    @ApiModelProperty(value = "最近7天课程学习时长", notes = "按照时间顺序")
    private List<TrainingProjectCourseRecentVo> courseRecentVos = new ArrayList<>();

    @ApiModelProperty("总课程次数")
    private Integer totalCourse = 0;

    @ApiModelProperty("总考试次数")
    private Integer totalExam = 0;

    @ApiModelProperty("总调研次数")
    private Integer totalResearch = 0;

    @ApiModelProperty("总投票次数")
    private Integer totalVote = 0;

    @ApiModelProperty("总作业次数")
    private Integer totalAssignment = 0;

    @ApiModelProperty("总签到次数")
    private Integer totalSign = 0;

    /************************************   PC 端添加完成字段  *************************/

    @ApiModelProperty("完成课程次数")
    private Integer finishedCourseNum = 0;

    @ApiModelProperty("完成考试次数")
    private Integer finishedExamNum = 0;

    @ApiModelProperty("完成调研次数")
    private Integer finishedResearchNum = 0;

    @ApiModelProperty("完成投票次数")
    private Integer finishedVoteNum = 0;

    @ApiModelProperty("完成总作业次数")
    private Integer finishedAssignmentNum = 0;

    @ApiModelProperty("完成签到次数")
    private Integer finishedSignNum = 0;

    @ApiModelProperty("总学习时长  (转换成小时)")
    private String totalStudyHours = "";

}
