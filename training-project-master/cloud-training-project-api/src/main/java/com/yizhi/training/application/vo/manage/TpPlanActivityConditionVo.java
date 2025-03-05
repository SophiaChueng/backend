package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/29 15:36
 */
@Data
@ApiModel("培训活动条件vo")
public class TpPlanActivityConditionVo {

    @ApiModelProperty(value = "设置的完成活动数目", notes = "未设置不传")
    private Integer preNum;

    @ApiModelProperty(value = "指定要完成的活动", notes = "未设置不传")
    private List<Long> preActivityRelationIds;

    @ApiModelProperty(value = "设置的及格分", notes = "不传即是不设置")
    private Float postExamScore;

    @ApiModelProperty(value = "考试id", notes = "证书指定考试得分时用")
    private Long examId;

}
