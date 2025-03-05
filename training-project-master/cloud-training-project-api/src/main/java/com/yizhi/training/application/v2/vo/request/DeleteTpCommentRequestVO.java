package com.yizhi.training.application.v2.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class DeleteTpCommentRequestVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("评论id")
    private Long tpCommentId;

    @ApiModelProperty("下架意见")
    private String auditContent;
}
