package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "判断是否需要展示项目介绍页入参")
public class ProjectJudgeAO {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "终端类型 MOBILE/PC", required = true, allowableValues = "MOBILE, PC")
    private String terminalType;

    @ApiModelProperty("站点id前端不需传")
    private Long siteId;

    @ApiModelProperty("公司id前端不需传")
    private Long companyId;

    @ApiModelProperty("账号id前端不需传")
    private Long accountId;
}
