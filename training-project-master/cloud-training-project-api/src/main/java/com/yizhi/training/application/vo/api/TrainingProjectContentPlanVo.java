package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/11 14:42
 */
@Data
@ApiModel(value = "培训内容--学习计划vo")
public class TrainingProjectContentPlanVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "学习计划名称")
    private String name;

    @ApiModelProperty(value = "学习计划开始时间")
    private Date startTime;

    @ApiModelProperty(value = "学习计划结束时间")
    private Date endTime;

    @ApiModelProperty(value = "活动集合")
    private List<TrainingProjectContentActivityVo> activities = new ArrayList<>();

    @ApiModelProperty(value = "开启当前学习计划：需要完成以下前置计划")
    private List<String> prePlanNames = new ArrayList<>();

    @ApiModelProperty(value = "当前计划是否开启")
    private Boolean started = false;

    @ApiModelProperty(value = "当前状态：未开始，进行中，已完成")
    private String status;

    @ApiModelProperty(value = "当前时间是否在计划时间段内")
    private Boolean inTime;

    @ApiModelProperty(value = "完成百分比")
    private String percentageOfCompletion;

    private Boolean finished;

    @ApiModelProperty(hidden = true)
    private List<Long> conditionPrePlanIds = new ArrayList<>();

    @ApiModelProperty(hidden = true)
    private Boolean inPlanStartTime;

    @ApiModelProperty(hidden = true)
    private Boolean inPlanEndTime;

    @ApiModelProperty(value = "是否只有证书")
    private Boolean isOnly = true;

    /**
     * 已经完成的
     */
    @ApiModelProperty(hidden = true)
    private List<Long> FinishedActivityIds = new ArrayList<>();

    /**
     * 完成条件指定要完成的
     */
    @ApiModelProperty(hidden = true)
    private List<Long> toFinishedActivityIds = new ArrayList<>();

    /**
     * 完成条件指定要完成的
     */
    @ApiModelProperty(hidden = true)
    private int toFinishedActivityNum = 0;

    /**
     * 所有的活动 id
     */
    @ApiModelProperty(hidden = true)
    private List<Long> allActivityIds = new ArrayList<>();

    /**
     * 需要参与完成率计算的 活动 id    只有证书是不参与的
     */
    @ApiModelProperty(hidden = true)
    private List<Long> needCalculateActivityIds = new ArrayList<>();

    @ApiModelProperty("是否有关联资料")
    private Boolean thereAnyData = false;

}
