package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TrainingProjectGroupViewExportVO1 {

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "用户名")
    private String accountName;

    @ApiModelProperty(value = "姓名")
    private String accountFullName;

    @ApiModelProperty(value = "开始时间--第一次学习时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间--如果这个人培训项目没有结束，那就没有结束时间")
    private Date endTime;

    @ApiModelProperty(value = "获得积分")
    private Integer point;

    @ApiModelProperty(value = "学习状态")
    private Integer studeyState;

    @ApiModelProperty(value = "用户状态")
    private Integer accountState;

    @ApiModelProperty(value = "所在部门")
    private String orgName;

    @ApiModelProperty(value = "所在组织架构")
    private String orgNames;

    @ApiModelProperty(value = "已完成学习活动数量")
    private Integer hasFinishedActivitiesNum;

    @ApiModelProperty(value = "未完成学习活动数量")
    private Integer notFinishedActivitiesNum;

    //返回时拿出来，方便反查
    private Long tpId;

    private Long accountId;

}
