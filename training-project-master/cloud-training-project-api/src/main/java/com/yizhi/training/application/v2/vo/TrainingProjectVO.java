package com.yizhi.training.application.v2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel
public class TrainingProjectVO implements Serializable {

    @ApiModelProperty("项目id")
    private Long id;

    @ApiModelProperty("项目分类id")
    private Long tpClassificationId;

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("项目logo")
    private String logoImg;

    @ApiModelProperty("项目开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty("项目结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("项目状态")
    private Integer status;

    @ApiModelProperty("可见范围（0:指定用户可见， 1：平台用户可见）")
    private Integer visibleRange;

    @ApiModelProperty("项目排序")
    private Integer sort;

    @ApiModelProperty("关键词")
    private String keyWords;

    @ApiModelProperty("发布终端")
    private Integer publishTerminal;

    @ApiModelProperty("是否开启报名")
    private Integer enableEnroll;

    @ApiModelProperty("是否开启签到")
    private Integer enableSign;

    @ApiModelProperty("是否开启消息提醒")
    private Integer enableMsgRemind;

    @ApiModelProperty("是否开启付费（0：关闭，1：开启）")
    private Integer enablePay;

    @ApiModelProperty("付费方式  0：非付费，1：虚拟币，2：兑换码，3：虚拟币/兑换码 4.会员")
    private Integer payType;
}
