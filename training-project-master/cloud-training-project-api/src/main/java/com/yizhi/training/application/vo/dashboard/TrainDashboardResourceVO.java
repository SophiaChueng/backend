package com.yizhi.training.application.vo.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TrainDashboardResourceVO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    private String logoImg;

    @ApiModelProperty(value = "参加人数")
    private Long joinCount;

    @ApiModelProperty(value = "参加人次")
    private Long joinPersonTime;

    private Date updateTime;

}
