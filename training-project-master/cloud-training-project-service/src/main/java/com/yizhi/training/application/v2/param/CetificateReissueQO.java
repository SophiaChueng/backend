package com.yizhi.training.application.v2.param;

import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TrainingProject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CetificateReissueQO {

    @ApiModelProperty("项目")
    private TrainingProject project;

    @ApiModelProperty("计划")
    private List<TpPlan> tpPLan;
}
