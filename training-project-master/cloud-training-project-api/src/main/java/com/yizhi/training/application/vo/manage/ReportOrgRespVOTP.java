package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wangfeida
 */
@Data
public class ReportOrgRespVOTP {

    @ApiModelProperty(value = "部门id")
    private Long orgId;

    @ApiModelProperty(value = "部门编码")
    private String orgCode;

    @ApiModelProperty(value = "部门名称")
    private String orgName;

    @ApiModelProperty(value = "部门人数")
    private Long orgAccountNum;

    @ApiModelProperty(value = "父节组织名称")
    private List<String> parentOrgNames;

    @ApiModelProperty(value = "部门下的人员的id")
    private List<Long> listAccount;
}
