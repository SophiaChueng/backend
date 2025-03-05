package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/28 10:50
 */
@Data
@ApiModel("培训活动条件修改vo")
public class TpPlanActivityConditionUpdateVo {

    @ApiModelProperty(value = "所属活动id")
    private Long activityId;

    @ApiModelProperty(value = "设置的完成活动数目", notes = "未设置不传")
    private Integer preNum;

    @ApiModelProperty(value = "指定要完成的活动id", notes = "未设置不传，这里前端误传了relationId，相应更改了程序")
    private List<Long> preActivityIds;

    @ApiModelProperty(value = "设置的及格分", notes = "考试、证书类型，若指定考试有及格分，必传")
    private Float postExamScore;

    @ApiModelProperty(value = "考试id", notes = "考试、证书类型必传")
    private Long examId;

}
