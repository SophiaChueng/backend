package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class RecentStudyTrainingVO {

    private Long trainingProjectId;

    private String trainingProjectName;

    private String image;

    private Date lastStudyTime;

    private Date startTime;

    private Date endTime;

    private Integer joinNumber;
    @ApiModelProperty("0 免费 1 虚拟币 2 兑换码 3 虚拟币/兑换码 4 会员")
    private Integer payType;
    @ApiModelProperty("是否开启报名")
    private Integer enableEnroll;
    @ApiModelProperty("0:项目 1：项目PRO")
    private Integer tpType;
}
