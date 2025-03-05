package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("学习单元")
public class TpIntroduceContentVO {

    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("开始时间")
    private Date startAt;

    @ApiModelProperty("结束时间")
    private Date endAt;

    @ApiModelProperty("随到随学；开始学习n天后可学")
    private String studyDateStr;

    @ApiModelProperty("活动")
    private List<TpIntroduceContentItemVO> itemVOList;
}
