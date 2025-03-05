package com.yizhi.training.application.v2.vo.request;

import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "更新学习单元的学习活动入参")
public class UpdateActivitiesRequestVO implements Serializable {

    @ApiModelProperty("项目ID")
    private Long trainingProjectId;

    @ApiModelProperty("学习单元ID")
    private Long tpPlanId;

    @ApiModelProperty("活动列表")
    private List<TpPlanActivityVO> activities;
}
