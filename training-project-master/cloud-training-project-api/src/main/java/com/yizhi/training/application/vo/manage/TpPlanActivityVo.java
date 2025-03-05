package com.yizhi.training.application.vo.manage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 18:51
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("培训活动vo--新增培训计划时使用")
public class TpPlanActivityVo {

    @ApiModelProperty(value = "活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程")
    private Integer type;

    @ApiModelProperty(value = "关联的活动的id")
    private Long relationId;

    @ApiModelProperty(value = "活动名称，从活动那边取过来，不能自定义（外部链接除外）")
    private String name;

    @ApiModelProperty(value = "外部链接地址")
    private String address;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    //    @ApiModelProperty(value = "是否被指定是培训计划必须完成的活动，0：否，1：是")
    //    private Integer mustComplete;

    @ApiModelProperty(value = "活动开启、完成条件")
    private TpPlanActivityConditionVo condition;

}
