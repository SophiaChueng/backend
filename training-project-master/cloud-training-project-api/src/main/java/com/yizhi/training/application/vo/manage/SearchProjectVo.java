package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@Api(tags = "SearchProjectVo", description = "项目查询参数vo")
public class SearchProjectVo {

    @ApiModelProperty(value = "培训项目分类id")
    private Long tpClassificationId;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "项目状态（0：草稿、1：启用、2：停用）")
    private Integer status;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    private List<Long> orgId;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    @ApiModelProperty(value = "分页数")
    private Integer pageNo;

    @ApiModelProperty(value = "分页数量")
    private Integer pageSize;

    @ApiModelProperty("培训项目IDs")
    private List<Long> listIds;

    @ApiModelProperty(value = "报名条件;10:无需报名；20:免费报名；30:收费报名 TrEnrollStatusEnum")
    private Integer enrollStatus;

}
