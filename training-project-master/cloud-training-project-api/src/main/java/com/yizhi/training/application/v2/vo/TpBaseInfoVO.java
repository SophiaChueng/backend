package com.yizhi.training.application.v2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目基本信息
 */
@ApiModel(description = "项目基本信息")
@Data
public class TpBaseInfoVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long id;

    @ApiModelProperty("项目分类id")
    private Long tpClassificationId;

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目logo")
    private String logoImg;

    @ApiModelProperty("项目开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("项目结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("项目状态")
    private Integer status;

    @ApiModelProperty("项目排序")
    private Integer sort;

    @ApiModelProperty("关键词")
    private String keyWords;

    @ApiModelProperty("发布终端")
    private Integer publishTerminal;

    @ApiModelProperty("是否开启报名")
    private Integer enableEnroll;

    @ApiModelProperty("是否开启签到")
    private Integer enableSign;

    @ApiModelProperty("是否开启消息提醒")
    private Integer enableMsgRemind;

    @ApiModelProperty("报名设置")
    private TpEnrollVO enrollInfo;

    @ApiModelProperty(value = "开启付费的项目是否在项目列表中显示；1:显示；0:不显示")
    private Integer enableQueue;

    @ApiModelProperty(value = "是否显示项目介绍页（0：不显示 1：默认 显示）")
    private Integer projectDescriptionFlag;
}
