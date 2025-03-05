package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/11 14:40
 */
@Data
@ApiModel(value = "培训项目详情--培训内容vo")
public class TrainingProjectContentVo {

    @ApiModelProperty(value = "培训计划集合")
    private List<TrainingProjectContentPlanVo> plans;

}
