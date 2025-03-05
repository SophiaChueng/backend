package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TpIntroduceBaseVO {

    @ApiModelProperty("大标题")
    private String title;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty(
        "0：学习单元，1：简介，2：资料，3：评论，4：考试与作业，5：公告，6：讨论，7：富文本（学习页），8：咨询，9：富文本（介绍页）;根据type取对应字段")
    private Integer itemType;

    @ApiModelProperty("学习单元ID")
    private Long itemId;

}
