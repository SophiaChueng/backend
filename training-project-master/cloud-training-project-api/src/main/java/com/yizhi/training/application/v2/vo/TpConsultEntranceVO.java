package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "咨询")
public class TpConsultEntranceVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("介绍页目录项id")
    private Long directoryItemId;

    @ApiModelProperty("咨询名")
    private String entranceName;

    @ApiModelProperty("咨询入口二维码图片")
    private String entranceImg;

}
