package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TrainingProjectGroupViewExportVO4 {

    @ApiModelProperty(value = "培训项目ID")
    private Long projectId;

    @ApiModelProperty(value = "培训项目名称")
    private String projectName;

    @ApiModelProperty(value = "应该参加人数")
    private Integer totalAskNum = 0;

    @ApiModelProperty(value = "参加人数的账号id")
    private List<Long> accountIds;

    @ApiModelProperty(value = "活动数量,活动会重复")
    private Integer activityNum = 0;

}
