package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("学习计划详细信息")
public class TpPlanDetailVO implements Serializable {

    @ApiModelProperty("学习单元id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("学习页目录id")
    private Long directoryItemId;

    @ApiModelProperty("单元名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否按顺序学习（0：否，1：是）")
    private Integer enableStudyInSequence;

    @ApiModelProperty("是否到期可继续学习（0：否，1：是）")
    private Integer enableContinueStudy;

    @ApiModelProperty("学习活动列表")
    private List<TpPlanActivityVO> activities;

    @ApiModelProperty("学习时间条件")
    private TpPlanStudyTimeConditionVO studyTimeCondition;

    @ApiModelProperty("前置条件")
    private TpPlanConditionPreVO conditionPre;

    @ApiModelProperty("完成条件")
    private TpPlanConditionPostVO conditionPost;

    @ApiModelProperty("资料数量")
    private Integer documentCount;

    @ApiModelProperty("证书数量")
    private Integer certificateCount;
}
