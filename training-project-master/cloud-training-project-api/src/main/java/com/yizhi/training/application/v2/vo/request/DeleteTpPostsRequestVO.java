package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class DeleteTpPostsRequestVO implements Serializable {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("帖子ID")
    private List<Long> postsIds;
}
