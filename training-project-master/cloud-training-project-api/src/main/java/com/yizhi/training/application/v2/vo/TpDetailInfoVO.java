package com.yizhi.training.application.v2.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 项目高级设置
 *
 * @author Wang
 */
@Data
@ApiModel(description = "项目高级设置信息")
public class TpDetailInfoVO implements Serializable {

    @ApiModelProperty("培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty("积分")
    private Integer point;

    @ApiModelProperty(value = "是否启用在日历任务中显示")
    private Integer enableTask;

    @ApiModelProperty("是否开启数据统计")
    private Integer enableStatistics;

    @ApiModelProperty("班主任列表")
    private List<TpHeadTeacherVO> headTeachers;

    @ApiModelProperty("是否开启签到")
    private Integer enableSign;

    @ApiModelProperty("是否开启消息提醒")
    private Integer enableMsgRemind;

    @ApiModelProperty("可见范围（为null或列表为空视为 平台可见）")
    private List<TpVisibleRangeVO> visibleRanges;

    @ApiModelProperty("项目完成条件")
    private TpCompleteConditionVO completeCondition;

    @ApiModelProperty("证书")
    private TpCertificateStrategyVO certificateStrategy;

    @ApiModelProperty("签到设置")
    private TpSignVO signInfo;

    @ApiModelProperty(value = "是否显示项目介绍页（0：不显示 1：默认 显示）")
    private Integer projectDescriptionFlag;

    @ApiModelProperty(value = "项目AI助手地址")
    private String tpAiUrl;

    @ApiModelProperty(value = "项目AI助手开关")
    private Boolean tpAiOpen;

}
