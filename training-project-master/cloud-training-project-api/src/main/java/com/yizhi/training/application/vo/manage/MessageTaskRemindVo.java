package com.yizhi.training.application.vo.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author hutao123
 * @since 2019-09-09
 */
@Data
@Api(tags = "MessageTaskRemindVo", description = "各个业务设置提醒时的数据")
public class MessageTaskRemindVo implements Serializable {

    @ApiModelProperty(value = "待发消息id")
    private Long messageRemindId;

    @ApiModelProperty(value = "提醒时间事件类型 1：业务开始时间、 2：业务结束时间、3：自定义时间")
    private Integer timeEventType;

    @ApiModelProperty(
        value = "发生时间枚举：1：五分钟前、2：十分钟前、3：三十分钟前、4：一个小时前、5：两个小时前、6：一天前、7：两天前")
    private Integer timeType;

    @ApiModelProperty(value = "最终发送时间")
    private Date sendTime;

}
