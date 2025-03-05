package com.yizhi.training.application.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "pds 培训项目 vo")
public class PdsProjectStudyRecordeVo {

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "项目id")
    private Long pid;

    @ApiModelProperty(value = "存储点的内容")
    private String pointContext;

    @ApiModelProperty(value = "学习时长")
    private Float period;

    @ApiModelProperty(value = "头像")
    private String headPortrait;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "组织id")
    private Long orgId;

    @ApiModelProperty(value = "企业id")
    private Long companyId;

    @ApiModelProperty(value = "排行榜名次")
    private Integer rank;

    private String userName;
}
