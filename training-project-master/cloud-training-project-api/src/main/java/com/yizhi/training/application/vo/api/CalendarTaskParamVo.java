package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CalendarTaskParamVo {

    @ApiModelProperty("时间参数")
    public Date date;

    @ApiModelProperty("业务类型")
    public Integer taskType = 0;

    @ApiModelProperty("当前页数")
    public Integer pageNo;

    @ApiModelProperty("页内条数")
    public Integer pageSize;

}

