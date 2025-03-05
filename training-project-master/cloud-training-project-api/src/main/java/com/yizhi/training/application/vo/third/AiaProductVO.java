package com.yizhi.training.application.vo.third;//package com.yizhi.xxl.job.executor.service.third.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AiaProductVO {

    @ApiModelProperty(value = "机构code")
    private String co;

    @ApiModelProperty(value = "list")
    private List<AiaProductDetailVO> productTrainResultList;

}
