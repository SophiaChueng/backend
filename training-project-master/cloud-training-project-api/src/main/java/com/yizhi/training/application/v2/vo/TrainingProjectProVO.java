package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TrainingProjectProVO {

    @ApiModelProperty("项目proId")
    private Long id;

    @ApiModelProperty("项目pro名称")
    private String tpProName;

    @ApiModelProperty("项目proLogo")
    private String tpProLogo;

    @ApiModelProperty("项目pro排序")
    private Integer sort;

    @ApiModelProperty("关联项目数量")
    private Integer projectCount;
}
