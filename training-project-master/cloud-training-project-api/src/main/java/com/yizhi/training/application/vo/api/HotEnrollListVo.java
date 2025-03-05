package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/23 13:52
 */
@Data
@ApiModel(value = "火热报名列表vo")
public class HotEnrollListVo {

    @ApiModelProperty(value = "培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty(value = "培训项目名称")
    private String tpName;

    @ApiModelProperty(value = "培训项目图片")
    private String tpLogoImg;

    @ApiModelProperty(value = "报名id")
    private Long enrollId;

    @ApiModelProperty(value = "报名开始时间")
    private Date enrollStartTime;

    @ApiModelProperty(value = "报名结束时间")
    private Date enrollEndTime;

    @ApiModelProperty(value = "报名开始时间-String类型")
    private String enrollStartTimeString;

    @ApiModelProperty(value = "报名结束时间-String类型")
    private String enrollEndTimeString;

    @ApiModelProperty("课程数量")
    private Integer activitieNum;

    @ApiModelProperty("学时")
    private Float period;

    @ApiModelProperty("培训项目学时显示 true 显示，false不显示")
    private Boolean trainingDurationShow;

    @ApiModelProperty("所有課程id")
    private List<Long> listCourseIds = new ArrayList<>();

    @ApiModelProperty(value = "是否付费报名，默认false")
    private Integer enablePay;

    @ApiModelProperty(value = "付费类别;1：虚拟币，2：兑换码，3：虚拟币/兑换码 4.会员")
    private Integer payType;
}
