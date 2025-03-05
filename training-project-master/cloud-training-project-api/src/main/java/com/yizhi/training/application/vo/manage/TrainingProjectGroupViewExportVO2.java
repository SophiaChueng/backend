package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TrainingProjectGroupViewExportVO2 {

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "学习计划名称")
    private String planName;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    @ApiModelProperty(value = "活动名称")
    private String activityName;

    @ApiModelProperty(value = "可参加人数")
    private Integer totalAskAccount = 0;

    @ApiModelProperty(value = "实际参加人数")
    private Integer totalJoinAccount = 0;

    @ApiModelProperty(value = "完成人数")
    private Integer totalFinishAccount = 0;

    @ApiModelProperty(value = "完成率")
    private BigDecimal avgFinish;
}
