package com.yizhi.training.application.vo.manage;

import com.yizhi.core.application.context.RequestContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hutao123
 * @since 2019-09-09
 */
@Data
@Api(tags = "MessageRemindVo", description = "各个业务设置提醒时的数据")
public class MessageRemindVo implements Serializable {

    private static final long serialVersionUID = -7621642684091133619l;

    @ApiModelProperty(value = "提醒id  ")
    private Long id;

    @ApiModelProperty(value = "消息id")
    private Long messageId;

    @ApiModelProperty(value = "消息类型：1、自定义消息；2、系统消息；3、事件触发消息")
    private Integer messageType;

    @ApiModelProperty(value = "用户id  主要用于触发消息 个人完成发消息类型")
    private Long accountId;

    @ApiModelProperty(value = "消息内容（完整版）")
    private String messageContext;

    @ApiModelProperty(value = "关联模块类型(1：学习计划、2：考试、3：调研、4、投票5：报名、6：作业、7：签到、8：项目、9：直播、10：积分)")
    private Integer relationType;

    @ApiModelProperty(value = "关联的业务id: 比如调研id")
    private Long relationId;

    @ApiModelProperty(value = "发送方式：1、站内信；2、短信；3、邮件")
    private Integer sendType;

    @ApiModelProperty(value = "该业务提醒是被关闭，关闭则为true，默认false")
    private Boolean hasDeleted = false;

    @ApiModelProperty(value = "该业务提醒是否有变化，有则为true，默认false")
    private Boolean isChangge = false;

    @ApiModelProperty(value = "专门存放提醒时间设置")
    private List<MessageTaskRemindVo> messageTaskRemindVos = new ArrayList<>();

    @ApiModelProperty(value = "目前只有培训项目需要，计划同步项目可见范围")
    private Boolean visibleRangeUpdate = false;

    @ApiModelProperty(value = "指定范围(0:全平台,1:指定用户)")
    private Integer visibleRange;

    @ApiModelProperty(value = "业务参数对象")
    private TaskVo taskVo;

    @ApiModelProperty(value = "触发消息专用 发送时间")
    private Date sendTime;

    @ApiModelProperty(value = "是否设置为上架状态")
    private Boolean hasUp = false;

    @ApiModelProperty(value = "是否是 修改业务状态 ")
    private Boolean taskStatusUpdate = false;

    @ApiModelProperty(value = "业务状态  1：才允上架许发送（业务上架）0：不允许发送（业务非上架）  仅针对于系统消息")
    private Integer taskStatus;

    @ApiModelProperty(value = "上下文 必传，主要需要 siteId companyId accountId  accountName 都不能是空")
    private RequestContext requestContext;

    @ApiModelProperty(value = "调研是否为复制类型")
    private Boolean isCopy = false;

    @ApiModelProperty(value = "复制调研时，旧的调研id")
    private Long oldRelationId;

}
