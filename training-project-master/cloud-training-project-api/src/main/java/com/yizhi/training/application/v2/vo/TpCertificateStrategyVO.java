package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@ApiModel("证书策略")
@Data
public class TpCertificateStrategyVO implements Serializable {

    @ApiModelProperty("绑定的证书列表")
    private List<TpCertificateVO> certificates;

    @ApiModelProperty("证书发放策略")
    private Integer issueStrategy;

}
