package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/25 15:02
 */
@Data
@ApiModel("培训项目保存第三步vo")
public class TrainingProjectStepThreeVo {

    @ApiModelProperty("培训项目id")
    private Long trainingProjectId;

    @ApiModelProperty("提醒vo")
    private TpRemindVo remindVo;

    @ApiModelProperty("可见范围")
    private VisibleRangeVo visibleRangeVo;

    @ApiModelProperty("积分")
    private Integer point;

    @ApiModelProperty(value = "各个业务设置提醒时的数据")
    private MessageRemindVo messageRemindVo;

    @ApiModelProperty(value = "是否启用在日历任务中显示")
    private Integer enableTask;

    @ApiModelProperty(value = "开启付费的项目是否在项目列表中显示；1:显示；0:不显示")
    private Integer enableQueue;

}
