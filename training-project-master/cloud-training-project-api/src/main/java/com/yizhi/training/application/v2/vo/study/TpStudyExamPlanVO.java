package com.yizhi.training.application.v2.vo.study;

import com.yizhi.training.application.v2.vo.TpPlanActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "学习单元/计划基本信息")
public class TpStudyExamPlanVO implements Serializable {

    @ApiModelProperty("学习单元/计划id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("学习页目录项ID")
    private Long directoryItemId;

    @ApiModelProperty("单元名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否按顺序（0：否，1：是）")
    private Integer enableStudyInSequence;

    @ApiModelProperty("到期是否可继续学习（0：否，1：是）")
    private Integer enableContinueStudy;

    @ApiModelProperty("学习活动列表")
    private List<TpPlanActivityVO> activities;
}
