package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class AddTpPostsRequestVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目logo")
    private String logoImg;

    @ApiModelProperty("帖子ID列表")
    private List<Long> postsIds;
}
