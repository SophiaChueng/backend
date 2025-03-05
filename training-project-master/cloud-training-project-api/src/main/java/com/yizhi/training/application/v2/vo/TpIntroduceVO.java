package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TpIntroduceVO extends TpIntroduceBaseVO {

    @ApiModelProperty("简介内容/富文本内容")
    private String content;

    @ApiModelProperty("讲师")
    private List<TpIntroduceLecturerVO> lecturer;

    @ApiModelProperty("咨询名")
    private String serviceName;

    @ApiModelProperty("咨询Logo")
    private String serviceLogo;

    @ApiModelProperty("学习单元")
    private List<TpIntroduceContentVO> studyItmes;

    @ApiModelProperty("展示评论")
    private Boolean showComment;

    @ApiModelProperty("展示资询")
    private Boolean showService;

}
