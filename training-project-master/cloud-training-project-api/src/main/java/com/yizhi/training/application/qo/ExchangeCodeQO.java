package com.yizhi.training.application.qo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("兑换码查询")
public class ExchangeCodeQO {

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    @ApiModelProperty("项目ID")
    private Long tpId;

    @ApiModelProperty(
        "关联的项目/活动等等的类型，0：课程，1：考试，2：调研，3：直播，5：作业，10：线下课程，11：原创活动，12：精选作品，14：帖子，15：项目，16：智能陪练，17：专辑")
    private Integer type;

    @ApiModelProperty("兑换码")
    private String code;

    @ApiModelProperty("状态，0：未使用，1：已使用")
    private Integer state;

    @ApiModelProperty("资源名称")
    private String resourceName;

}
