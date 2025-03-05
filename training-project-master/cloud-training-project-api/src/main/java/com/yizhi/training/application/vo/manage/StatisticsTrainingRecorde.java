package com.yizhi.training.application.vo.manage;

import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.domain.TpStudentActivityRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StatisticsTrainingRecorde {

    Long id;

    @ApiModelProperty("项目和计划以及活动的对应关系")
    List<TpPlanActivityVo> listTpPlanActivity;

    @ApiModelProperty("项目完成情况")
    List<TpStudentProjectRecordVo> listTpStudentProjectRecord;

    @ApiModelProperty("计划完成情况")
    List<TpStudentPlanRecordVo> listTpStudentPlanRecord;

    @ApiModelProperty("活动完成情况")
    List<TpStudentActivityRecordVo> listActivityRecord;

}
