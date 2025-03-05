package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/26 10:55
 */
@Data
@Api(tags = "TpRemindVo", description = "提醒vo")
public class TpRemindVo {

    @ApiModelProperty(value = "培训项目id", notes = "上级结构中有该值，可以不传")
    private Long trainingProjectId;

    @ApiModelProperty(value = "开启邮件提醒（0：否，1：是）")
    private Integer enableMail;

    @ApiModelProperty(value = "开启站内消息提醒（0：否，1：是）")
    private Integer enableApp;

    @ApiModelProperty(value = "邮件模板id")
    private Long mailTemplateId;

    @ApiModelProperty(value = "站内消息模板id")
    private Long appTemplateId;

    @ApiModelProperty(value = "提醒内容")
    private String content;

    @ApiModelProperty(value = "提醒时间集合")
    private List<TpRemindTimeVo> reminds;

}