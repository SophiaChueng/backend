package com.yizhi.training.application.vo.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/3 10:47
 */
@Data
@ApiModel("培训计划修改vo")
public class TpPlanUpdateVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "学习计划名称")
    private String name;

    @ApiModelProperty(value = "学习计划开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "学习计划结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "学习计划所有的前置计划")
    private List<Long> conditionPreIds;

    @ApiModelProperty(value = "学习计划完成条件--指定活动数")
    private Integer conditionPostFinishNum;

    @ApiModelProperty(value = "学习计划完成条件--完成指定活动")
    private List<Long> conditionPostActivityIds;

    @ApiModelProperty(value = "提醒时间")
    private TpRemindVo remindVo;

    @ApiModelProperty(value = "各个业务设置提醒时的数据")
    private MessageRemindVo messageRemindVo;

}
