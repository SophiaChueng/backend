package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "评论分页参数说明")
public class TrCommentVo {

    @ApiModelProperty(value = "培训项目Id 必传", required = true)
    Long trainingProjectId;

    @ApiModelProperty(value = "评论分页参数")
    Integer pageNo = 1;

    @ApiModelProperty(value = "评论分页参数")
    Integer pageSize = 10;

    @ApiModelProperty(value = "回复列表分页参数")
    Integer replyPageNo = 1;

    @ApiModelProperty(value = "回复列表分页参数")
    Integer replyPageSize = 5;
}
