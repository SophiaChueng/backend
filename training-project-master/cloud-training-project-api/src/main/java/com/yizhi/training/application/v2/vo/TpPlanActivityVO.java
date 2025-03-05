package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "学习活动")
public class TpPlanActivityVO implements Serializable {

    @ApiModelProperty("学习活动id")
    private Long id;

    @ApiModelProperty("项目id")
    private Long trainingProjectId;

    @ApiModelProperty("学习单元id")
    private Long tpPlanId;

    /**
     * @see com.yizhi.training.application.v2.enums.TpActivityTypeEnum
     */
    @ApiModelProperty(
        "活动类型：（0：课程 1：考试 2：调研 3：直播 4：投票 5：作业 7：外部链接 8：报名 9：签到 10：线下课程 11:案例活动 12：精选案例 13：资料 14：帖子  18：智能陪练 19：答题活动）")
    private Integer type;

    @ApiModelProperty("关联的业务id")
    private Long relationId;

    @ApiModelProperty("活动名称")
    private String name;

    @ApiModelProperty("自定义名称")
    private String customizeName;

    @ApiModelProperty("外链地址")
    private String address;

    @ApiModelProperty("logoUrl")
    private String logoUrl;

    @ApiModelProperty("排序")
    private Integer sort;

    // ==============扩展字段================
    @ApiModelProperty("学习计划名称")
    private String tpPlanName;
}
