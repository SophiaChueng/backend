package com.yizhi.training.application.v2.vo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class PageDataVO<T> implements Serializable {

    @ApiModelProperty("总数")
    private Integer total;

    @ApiModelProperty("当前页")
    private Integer pageNo;

    @ApiModelProperty("页面大小")
    private Integer pageSize;

    @ApiModelProperty("数据列表")
    private List<T> records;

    public PageDataVO(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageDataVO() {
    }
}
