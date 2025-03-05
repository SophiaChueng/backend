package com.yizhi.training.application.vo.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 18:48
 */
@Data
@ApiModel("培训计划vo")
public class TpPlanVo {

    @ApiModelProperty(value = "所属培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "学习计划名称")
    private String name;

    @ApiModelProperty(value = "学习计划开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "学习计划结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "学习活动集合")
    private List<TpPlanActivityVo> activities;

    @ApiModelProperty(value = "学习计划条件")
    private TpPlanConditionVo condition;

    @ApiModelProperty(value = "提醒时间")
    private TpRemindVo remindVo;

    @ApiModelProperty(value = "各个业务设置提醒时的数据")
    private MessageRemindVo messageRemindVo;

}
