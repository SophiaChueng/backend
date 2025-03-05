package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 按用户分组统计
 *
 * @author mei
 */

@Data
public class ReportStudyTrainingProjectAccountVo {

    @ApiModelProperty(value = "用户ID")
    private Long accountId;

    @ApiModelProperty(value = "用户名")
    private String accountName;

    @ApiModelProperty(value = "姓名")
    private String accountFullName;

    @ApiModelProperty(value = "所在部门")
    private String orgName;

    @ApiModelProperty(value = "所在组织架构")
    private String orgNames;

    @ApiModelProperty(value = "应参加项目数（空返回0）")
    private Integer totalAsk = 0;

    @ApiModelProperty(value = "实际参加项目数（空返回0）")
    private Integer totalIn = 0;

    @ApiModelProperty(value = "已完成项目数（空返回0）")
    private Integer totalFinish = 0;

}
