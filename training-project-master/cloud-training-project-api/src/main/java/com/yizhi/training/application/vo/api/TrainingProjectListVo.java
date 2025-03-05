package com.yizhi.training.application.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 20:41
 */
@Data
@ApiModel(value = "培训项目列表vo")
public class TrainingProjectListVo {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "培训项目名称")
    private String name;

    @ApiModelProperty(value = "logo图片")
    private String logoImg;

    @ApiModelProperty(value = "学习项目开始时间")
    private Date startTime;

    @ApiModelProperty(value = "学习项目结束时间")
    private Date endTime;

    @ApiModelProperty(value = "多少人已经参与")
    private Integer JoinNumber;

    @ApiModelProperty(value = "是否已经完成")
    private Boolean finished;

    @ApiModelProperty(value = "学习项目开始时间-String类型")
    private String startTimeString;

    @ApiModelProperty(value = "学习项目结束时间-String类型")
    private String endTimeString;

    @ApiModelProperty(value = "课程数量")
    private Integer activitieNum;

    @ApiModelProperty(value = "学时")
    private Float period;

    @ApiModelProperty("培训项目学时显示 true 显示，false不显示")
    private Boolean trainingDurationShow;

    @ApiModelProperty("所有課程id")
    private List<Long> listCourseIds;

    @ApiModelProperty(value = "是否付费报名，默认0否，1是")
    private Integer enablePay;

    @ApiModelProperty(value = "付费类别;1：虚拟币，2：兑换码，3：虚拟币/兑换码 4.会员")
    private Integer payType;

}
