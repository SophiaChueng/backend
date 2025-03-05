package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学习单元")
public class TpIntroduceContentItemVO {

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty(
        "类型 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程  11：案例活动、12：精选案例 、13、资料 14：论坛帖子")
    private Integer type;

    @ApiModelProperty("1：必学；0：非必学")
    private Integer mustStudy = 0;
}
