package com.yizhi.training.application.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.yizhi.util.application.easyexcel.converter.CommonDateTimeConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("兑换码导出VO")
public class ExchangeCodeExportVO {

    @ApiModelProperty(value = "兑换码")
    @ExcelProperty(value = "兑换码", index = 0)
    private String code;

    @ApiModelProperty(value = "状态 0：未使用，1：已使用")
    @ExcelProperty(value = "兑换状态", index = 1)
    private String stateString;

    @ApiModelProperty(value = "兑换时间")
    @ExcelProperty(value = "兑换时间", index = 2, converter = CommonDateTimeConverter.class)
    private Date exchangeTime;

    @ApiModelProperty("兑换人用户名")
    @ExcelProperty(value = "兑换人用户名", index = 3)
    private String account;

    @ApiModelProperty("兑换人姓名")
    @ExcelProperty(value = "兑换人姓名", index = 4)
    private String fullName;
}
