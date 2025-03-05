package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目图表数据
 *
 * @author mei
 */

@Data
public class TrainingProjectDataChartsVo {

    @ApiModelProperty(value = "显示内容")
    private String value;

    @ApiModelProperty(value = "参加人数（空返回0）")
    private Integer totalJoin = 0;

    @ApiModelProperty(value = "完成人数（空返回0）")
    private Integer totalFinish = 0;

    @ApiModelProperty(value = "项目数（空返回0）")
    private Integer totalProject = 0;
}
