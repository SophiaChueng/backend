package com.yizhi.training.application.vo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 20:47
 */
@Data
@ApiModel(value = "首页--训项目列表查询参数vo")
public class TrainingProjectParamVo {

    @ApiModelProperty(value = "当前时间，必传")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date now;

    @ApiModelProperty(value = "关键字，搜索用，非必传")
    private String keyword;

    @ApiModelProperty(value = "分页参数：每页条数，默认20条")
    private Integer pageSize = 20;

    @ApiModelProperty(value = "分页参数：跳转页，默认1")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "是否付费筛选，默认0否，1是")
    private Integer enablePay;

}
