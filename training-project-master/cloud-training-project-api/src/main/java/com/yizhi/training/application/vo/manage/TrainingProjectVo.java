package com.yizhi.training.application.vo.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 14:11
 */
@Data
@ApiModel("培训项目分类vo")
public class TrainingProjectVo {

    @ApiModelProperty(value = "培训id", notes = "修改时必传")
    private Long id;

    @ApiModelProperty(value = "培训项目分类id")
    private Long tpClassificationId;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "logo图片")
    private String logoImg;

    @ApiModelProperty(value = "学习项目开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "学习项目结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "关键字，英文逗号分隔")
    private String keyWords;

    @ApiModelProperty(value = "项目介绍，富文本")
    private String description;

    @ApiModelProperty(value = "是否启用报名（0：否，1：是），默认否")
    private Integer enableEnroll;

    @ApiModelProperty(value = "是否启用签到（0：否，1：是），默认否")
    private Integer enableSign;

    @ApiModelProperty(value = "是否开启定位（0：否，1：是），默认否")
    private Integer enablePosition;

    @ApiModelProperty(value = "权重")
    private Integer weight;
}
