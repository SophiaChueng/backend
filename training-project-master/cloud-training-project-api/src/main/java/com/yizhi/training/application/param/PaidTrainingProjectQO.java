package com.yizhi.training.application.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: PaidTrainingProjectQO
 * @author: zjl
 * @date: 2021/1/12  16:28
 */
@Data
@ApiModel(value = "付费课程入参")
public class PaidTrainingProjectQO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "搜索关键字")
    private String keyword;

    @ApiModelProperty(value = "排序字段 actualPrice(实际价格) exchangeCount(兑换量) releaseTime(上架时间)")
    private String orderField;

    @ApiModelProperty(value = "排序顺序 asc:升序 desc:倒序")
    private String order;

    @ApiModelProperty(value = "分页参数：每页条数，默认10条")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "分页参数：跳转页，默认1")
    private Integer pageNo = 1;

    @ApiModelProperty(value = "兑换状态：0(全部) 1（已兑换） 2（未兑换） 3（可兑换）")
    private Integer exchangeState = 0;

    @ApiModelProperty(value = "站点id", hidden = true)
    private Long siteId;

}
