package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 按用户分组统计
 *
 * @author mei
 */

@Data
public class ReportStudyTrainingProjectAccountViewVo {

    @ApiModelProperty(value = "用户账号")
    private Long accountId;

    @ApiModelProperty(value = "用户名")
    private String accountName;

    @ApiModelProperty(value = "姓名")
    private String accountFullName;

    @ApiModelProperty(value = "所在部门")
    private String orgName;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "学习状态 -1 未参加 0未完成 1 已完成")
    private String state;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "完成时间")
    private Date endTime;

    /**
     * 以下字段报表用到
     */

    @ApiModelProperty(value = "最早完成时间")
    private Date firstFinishTime;

    @ApiModelProperty(value = "获得积分")
    private Integer point;

    @ApiModelProperty(value = "用户当前状态")
    private Integer accountState;

    // 新增学习时长 -@2019年12月3日
    @ApiModelProperty(value = "学习时长")
    private Integer seconds;

}
