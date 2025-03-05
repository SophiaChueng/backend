package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 项目赚取积分vo
 */
@Data
public class GainPointProjectVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "封面图")
    private String image;

    @ApiModelProperty(value = "总发放积分")
    private Integer totalPoints;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;
}
