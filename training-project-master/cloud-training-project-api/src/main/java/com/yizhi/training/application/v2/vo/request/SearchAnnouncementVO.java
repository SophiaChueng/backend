package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "查询公告入参")
public class SearchAnnouncementVO implements Serializable {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("页号")
    private Integer pageNo;

    @ApiModelProperty("页大小")
    private Integer pageSize;
}
