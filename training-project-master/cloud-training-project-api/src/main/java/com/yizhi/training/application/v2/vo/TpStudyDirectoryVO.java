package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "学习页目录项")
@Data
public class TpStudyDirectoryVO implements Serializable {

    @ApiModelProperty("学习页目录项id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("目录类型（0：学习单元，1：简介，2：资料，3：评论，4：考试与作业，5：公告，6：讨论，7：富文本（学习页））")
    private Integer itemType;

    @ApiModelProperty("目录项名称")
    private String itemName;

    @ApiModelProperty("排序")
    private Integer sort;
}
