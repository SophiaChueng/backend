package com.yizhi.training.application.vo.manage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/26 10:55
 */
@Data
@Api(tags = "TpRemindVo", description = "提醒时间vo（修改时未改的字段勿传值也勿传空字符串）")
public class TpRemindTimeVo {

    @ApiModelProperty(value = "提醒时间id（仅update时可传）")
    private Long id;

    @ApiModelProperty(value = "1：开始之前，2：开始之后，3：自定义时间")
    private Integer type;

    @ApiModelProperty(value = "开始之前或开始之后：相差秒数")
    private Long seconds;

    @ApiModelProperty(value = "自定义时间：确定时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

}