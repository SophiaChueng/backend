package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "证书")
public class TpCertificateVO implements Serializable {

    @ApiModelProperty("证书id")
    private Long id;

    @ApiModelProperty("证书标题")
    private String title;
}
