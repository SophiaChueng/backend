package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Api(tags = "TrainingProjectAccountGroupParams", description = "报表---按照用户统计入参")
public class TrainingProjectAccountGroupParams {

    @ApiModelProperty(value = "开始日期", notes = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期", notes = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "部门编码或者名称", notes = "部门编码或者名称")
    private String departMessage;

    @ApiModelProperty(value = "部门编码或者名称", notes = "部门编码或者名称")
    private String userName;

    @ApiModelProperty(value = "站点id", notes = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "公司id", notes = "公司id")
    private Long companyId;

    @ApiModelProperty(value = "组织id", notes = "组织id")
    private Long orgId;

    @ApiModelProperty(value = "页数", notes = "页数")
    private Integer pageNo;

    @ApiModelProperty(value = "大小", notes = "大小")
    private Integer pageSize;

}
