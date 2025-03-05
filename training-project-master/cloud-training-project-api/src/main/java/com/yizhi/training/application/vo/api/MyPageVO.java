package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 我的页面VO
 * @author: lly
 * @date: 2019-01-03 20:07
 **/
@Data
public class MyPageVO implements Serializable {

    /**
     * 未开始记录总数
     */
    @ApiModelProperty(value = "记录总数")
    private Integer unStartRecords;

    /**
     * 进行中记录总数
     */
    @ApiModelProperty(value = "进行中记录总数")
    private Integer processRecords;

    /**
     * 完成记录总数
     */
    @ApiModelProperty(value = "已经完成记录总数")
    private Integer finishRecords;

}
