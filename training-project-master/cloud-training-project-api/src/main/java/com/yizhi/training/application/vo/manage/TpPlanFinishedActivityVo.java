package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Ding
 * @className TpPlanFinishedActivityVo
 * @description TODO
 * @date 2018/12/4
 **/
@Data
@ApiModel("培训计划完成情况活动列表实例信息-任务完成情况API使用")
public class TpPlanFinishedActivityVo {

    @ApiModelProperty(value = "活动ID")
    private Long activityId;

    @ApiModelProperty(value = "名称")
    private String title;

    @ApiModelProperty(value = "活动类型")
    private Integer type;

    @ApiModelProperty(value = "关联的活动的ID")
    private Long relationId;

    @ApiModelProperty(value = "外部链接地址")
    private String address;

    @ApiModelProperty(value = "是否完成活动--0未完成,1已完成")
    private Integer isFinished;

    @ApiModelProperty(value = "课程logo URL")
    private String logoUrl;

    @ApiModelProperty(value = "作者单位")
    private String authorUnit;

    @ApiModelProperty(value = "备注")
    private String remark;
}
