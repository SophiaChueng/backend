package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "富文本")
public class TpRichTextVO {

    @ApiModelProperty("富文本id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("目录项id（介绍页和学习页均可）")
    private Long directoryItemId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("文本内容")
    private String content;

    @ApiModelProperty("排序")
    private Integer sort;

}
