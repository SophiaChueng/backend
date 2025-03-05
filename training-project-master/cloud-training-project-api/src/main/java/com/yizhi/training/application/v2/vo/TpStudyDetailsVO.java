package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TpStudyDetailsVO {

    @ApiModelProperty("项目ID")
    private Long tpId;

    @ApiModelProperty("项目名")
    private String tpName;

    private String logo;

    @ApiModelProperty("项目开始时间")
    private Date tpStartAt;

    @ApiModelProperty("项目结束时间")
    private Date tpEndAt;

    @ApiModelProperty("活动数")
    private Integer activityCount;

    @ApiModelProperty("积分数")
    private Integer pointCount;

    @ApiModelProperty("证书数")
    private Integer certificateCount;

    @ApiModelProperty("学时数")
    private String studyHourCount;

    @ApiModelProperty("true：项目已完成，false：未完成")
    private Boolean finishedTp = false;

    @ApiModelProperty("true：展示签到，false：不展示")
    private Boolean showSign = false;

    @ApiModelProperty("true：有结业证书，false：没结业证书")
    private Boolean showTpCertificate = false;

    @ApiModelProperty("已获得证书集合，图片地址")
    private List<String> tpCertificateList;

    @ApiModelProperty("true: 需要申请，false：不需要申请;")
    private Boolean certificateApplyStatus = false;

    @ApiModelProperty("0:待审批，1：通过（自动发放，已获得证书），2：不通过,null：待申请；")
    private Integer certificateAuditStatus = null;

    @ApiModelProperty("展示班主任统计按钮")
    private Boolean showTotal = false;

    @ApiModelProperty("展示学员个人统计按钮")
    private Boolean showPersonTotal = false;

    @ApiModelProperty("系统时间时间戳")
    private Long systemTime;

    @ApiModelProperty(value = "项目AI助手地址")
    private String tpAiUrl;

    @ApiModelProperty(value = "项目AI助手开关")
    private Boolean tpAiOpen;
}
