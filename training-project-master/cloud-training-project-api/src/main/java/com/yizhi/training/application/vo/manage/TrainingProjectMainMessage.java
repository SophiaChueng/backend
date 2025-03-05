package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目图表数据
 *
 * @author mei
 */

@Data
public class TrainingProjectMainMessage {

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "数据量")
    private Integer total;

}
