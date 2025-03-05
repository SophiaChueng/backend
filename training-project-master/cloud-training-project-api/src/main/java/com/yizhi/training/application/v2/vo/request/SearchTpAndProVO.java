package com.yizhi.training.application.v2.vo.request;

import com.yizhi.training.application.v2.vo.base.PageRequestVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchTpAndProVO extends PageRequestVO {

    @ApiModelProperty(value = "名字", required = true)
    private String name;

    @ApiModelProperty(value = "终端类型 MOBILE/PC", required = true, allowableValues = "MOBILE, PC")
    private String terminalType;

}
