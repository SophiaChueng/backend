package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "签到设置")
public class TpSignVO implements Serializable {

    @ApiModelProperty("是否开启定位（0：否，1：是）")
    private Integer enablePosition;

    @ApiModelProperty("是否开启补签（0：否，1：是）")
    private Integer enableRetroactive;

    @ApiModelProperty("签到时间")
    private List<TpSignTimeVO> tpSignTimes;
}
