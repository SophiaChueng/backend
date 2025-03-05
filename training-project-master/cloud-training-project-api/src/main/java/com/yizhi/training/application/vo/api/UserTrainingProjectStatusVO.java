package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "根据指定的培训项目名称查询指定用户的学习完成状态")
public class UserTrainingProjectStatusVO {

    @ApiModelProperty(value = "培训项目名称 必传，完全匹配", required = true)
    private String trainingProjectName;

    @ApiModelProperty(value = "用户列表")
    private List<Long> userIds;

    @ApiModelProperty(value = "站点id")
    private Long siteId;

    @ApiModelProperty(value = "startTime")
    private Date startTime;

    @ApiModelProperty(value = "endTime")
    private Date endTime;
}
