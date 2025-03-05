package com.yizhi.training.application.vo.third;//package com.yizhi.xxl.job.executor.service.third.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AiaTrainingDetailVO {

    @ApiModelProperty(value = "9位营销员code")
    private String agtCode;

    @ApiModelProperty(value = "通过时间")
    private String effDate;

    @ApiModelProperty(value = "枚举值")
    private String trainType;

    @ApiModelProperty(value = "是否通过培训 .Y是;N否")
    private String trainValue;

}
