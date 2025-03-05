package com.yizhi.training.application.v2.vo.request;

import com.yizhi.training.application.v2.vo.base.PageRequestVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OnLineTpVO extends PageRequestVO {

    @ApiModelProperty(value = "状态 1:已完成，0：未完成，不传：全部", required = false, allowableValues = "0, 1")
    private Integer status;

    @ApiModelProperty(value = "终端类型 MOBILE/PC", required = true, allowableValues = "MOBILE, PC")
    private String terminalType;

    private List<Long> tpIds;

}
