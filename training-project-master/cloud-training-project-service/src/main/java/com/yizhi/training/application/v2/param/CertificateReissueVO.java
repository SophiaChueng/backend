package com.yizhi.training.application.v2.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CertificateReissueVO {

    @ApiModelProperty("证书ID")
    private Long certificateId;

    @ApiModelProperty("证书名称")
    private String cetificateName;

    @ApiModelProperty("项目ID")
    private Long tpId;

    @ApiModelProperty("项目名")
    private String tpName;

    @ApiModelProperty("计划ID")
    private Long planId;

    @ApiModelProperty("计划名")
    private String planName;

    @ApiModelProperty("站点ID")
    private Long siteId;

    private List<Long> accountIds;

    private Integer bizType;

}
