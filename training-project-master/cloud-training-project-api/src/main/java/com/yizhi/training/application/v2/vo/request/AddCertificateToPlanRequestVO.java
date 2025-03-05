package com.yizhi.training.application.v2.vo.request;

import com.yizhi.training.application.v2.vo.TpCertificateVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "学习单元绑定证书入参")
public class AddCertificateToPlanRequestVO implements Serializable {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("学习单元ID")
    private Long tpPlanId;

    @ApiModelProperty("证书列表")
    private List<TpCertificateVO> certificates;

    @ApiModelProperty("证书获取策略（0：完成后自动获取，1：学员申请）")
    private Integer issueStrategy;
}
