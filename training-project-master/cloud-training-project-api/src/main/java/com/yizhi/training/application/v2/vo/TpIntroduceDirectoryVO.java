package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "介绍页目录项")
public class TpIntroduceDirectoryVO {

    @ApiModelProperty("介绍页目录项id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    /**
     * @see com.yizhi.training.application.v2.enums.TpDirectoryItemTypeEnum
     */
    @ApiModelProperty(
        "目录类型（0：学习单元，1：简介，2：资料，3：评论，4：考试与作业，5：公告，6：讨论，7：富文本（学习页），8：咨询，9：富文本（介绍页））")
    private Integer itemType;

    @ApiModelProperty("关联的学习页目录项id")
    private Long itemId;

    @ApiModelProperty("目录项名称")
    private String itemName;

    @ApiModelProperty("排序")
    private Integer sort;
}