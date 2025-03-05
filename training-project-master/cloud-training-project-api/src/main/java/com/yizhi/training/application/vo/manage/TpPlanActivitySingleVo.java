package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/3 10:54
 */
@Data
@ApiModel("培训活动添加vo--修改计划时，单独维护培训活动时使用")
public class TpPlanActivitySingleVo {

    @ApiModelProperty(value = "所属培训项目的id", required = true)
    private Long projectId;

    @ApiModelProperty(value = "所属培训计划的id", required = true)
    private Long planId;

    @ApiModelProperty(value = "活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程")
    private Integer type;

    @ApiModelProperty(value = "关联的活动的id", required = true)
    private Long relationId;

    @ApiModelProperty(value = "活动名称，从活动那边取过来，不能自定义（外部链接除外）")
    private String name;

    @ApiModelProperty(value = "外部链接地址")
    private String address;

    @ApiModelProperty(value = "排序", required = true)
    private Integer sort;
}
