package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class StatisticsTrainingProjectParamVO {

    @ApiModelProperty("站点id集合")
    private List<Long> siteIds;

    @ApiModelProperty("开始日期字符串yyyy-MM-dd")
    private String startDateString;

    @ApiModelProperty("结束日期字符串yyyy-MM-dd")
    private String endDateString;
}
