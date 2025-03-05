package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TrainingProjectGroupViewExportVO3 {

    @ApiModelProperty(value = "用户名")
    private String accountName;

    @ApiModelProperty(value = "姓名")
    private String accountFullName;

    @ApiModelProperty(value = "培训项目名称")
    private String projectName;

    @ApiModelProperty(value = "学习计划名称")
    private String planName;

    @ApiModelProperty(value = "学习活动名称")
    private String activityName;

    @ApiModelProperty(value = "学习活动类型")
    private String activityType;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "最早完成时间")
    private Date firstFinishTime;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @ApiModelProperty(value = "所获积分")
    private Integer point;

    @ApiModelProperty(value = "完成状态")
    private Integer state;

    @ApiModelProperty(value = "用户当前状态")
    private Integer accountState;

    @ApiModelProperty(value = "所在部门")
    private String orgName;

    @ApiModelProperty(value = "所在组织架构")
    private String orgNames;

    /**
     * 添加课程学习时长 @ 2019-12-7 10:38:57 by lingye
     */
    @ApiModelProperty(value = "课程学习时长")
    private Integer learningTime;

    @ApiModelProperty(value = "活动的id")
    private Long relationId;
}
