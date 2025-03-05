package com.yizhi.training.application.v2.vo.study;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("学习页 考试与作业")
public class TpStudyExamVO {

    @ApiModelProperty("活动Id")
    private Long activityId;

    @ApiModelProperty("1：考试 5：作业 ")
    private Integer relationType;

    @ApiModelProperty("业务Id,eg：课程Id、考试Id")
    private Long relationId;

    @ApiModelProperty("业务名")
    private String relationName;

    @ApiModelProperty("及格分")
    private BigDecimal passLimitScore;

    @ApiModelProperty(" 0: 未提交、1: 批阅中 2:已通过 9：未通过")
    private Integer status;

    @ApiModelProperty("开始时间")
    private Date startAt;

    @ApiModelProperty("结束时间")
    private Date endAt;

    @ApiModelProperty("提交次数")
    private Integer submitCount;

    @ApiModelProperty("剩余次数")
    private Integer surplusCount;

    @ApiModelProperty("次数是否有限制 true:无限制 false：有限制")
    private Boolean unLimitCount = false;

    @ApiModelProperty("时间是否有限制 true:无限制 false：有限制")
    private Boolean unLimitDate = false;

    @ApiModelProperty("前一个活动是否完成；（当计划开启顺序学习时，前一个完成，才能学习当前任务）true：完成；fasle：未完成")
    private Boolean preActivityFinished = true;

}
