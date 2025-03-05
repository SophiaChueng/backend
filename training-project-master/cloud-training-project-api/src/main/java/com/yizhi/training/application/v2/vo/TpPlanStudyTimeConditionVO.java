package com.yizhi.training.application.v2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(description = "学习单元学习时间条件")
public class TpPlanStudyTimeConditionVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("学习单元/计划id")
    private Long tpPlanId;

    @ApiModelProperty("条件类型（0：无时间限制，1：指定时间段，2：开始学习后n天，3：完成前置单元后n天）")
    private Integer conditionType;

    @ApiModelProperty("指定时间段：开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("指定时间段：结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("开始学习后n天")
    private Integer afterStartDate;

    @ApiModelProperty("完成前置单元后n天")
    private Integer afterPrePlanDate;

}
