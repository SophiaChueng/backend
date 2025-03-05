package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 按部门分组统计
 *
 * @author mei
 */

@Data
public class ReportStudyTrainingProjectOrgVo {

    @ApiModelProperty(value = "部门id")
    private Long orgId;

    @ApiModelProperty(value = "部门编码")
    private String orgNo;

    @ApiModelProperty(value = "部门名称")
    private String orgName;

    @ApiModelProperty(value = "所在组织架构")
    private String orgNames;

    @ApiModelProperty(value = "部门总人数")
    private Integer accountNum = 0;

    @ApiModelProperty(value = "可参加项目数（空返回0）")
    private Integer totalAskProject = 0;

    @ApiModelProperty(value = "可参加人数（空返回0）")
    private Integer totalAskAccount = 0;

    @ApiModelProperty(value = "实际参加人数（空返回0）")
    private Integer totalIn = 0;

    @ApiModelProperty(value = "完成人数（空返回0）")
    private Integer totalFinish = 0;

    @ApiModelProperty(value = "完成率（空返回0），保留小时2位（98.00），前端补%")
    private BigDecimal avgFinish;

}
