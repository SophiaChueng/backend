package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/5/11 16:48
 */
@Data
@ApiModel("可见范围")
public class VisibleRangeVo {

    @ApiModelProperty(value = "培训项目id", notes = "编辑培训项目第三步可不传")
    private Long trainingProjectId;

    @ApiModelProperty("可见范围关联Vo")
    private List<RelationIdVo> list;

}
