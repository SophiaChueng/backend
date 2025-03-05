package com.yizhi.training.application.vo.manage;

import com.yizhi.training.application.vo.EvenType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 消息
 * </p>
 *
 * @author hutao123
 * @since 2019-09-09
 */
@Data
@Api(tags = "TaskVo", description = "业务参数对象")
public class TaskVo implements Serializable {

    @ApiModelProperty(value = "业务名称")
    private String taskName;

    @ApiModelProperty(value = "业务开始时间")
    private Date taskStratTime;

    @ApiModelProperty(value = "业务结束时间")
    private Date taskEndTime;

    @ApiModelProperty(value = "业务得分")
    private Double taskScore;

    @ApiModelProperty(value = "业务发生原因（主要用于积分）")
    private String reason;

    @ApiModelProperty(value = "业务时间（主要用于积分）")
    private Date taskTime;

    @ApiModelProperty(value = "事件类型")
    private EvenType evenType;

}
