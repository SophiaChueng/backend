package com.yizhi.training.application.util;

import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.core.application.publish.CloudEventPublisher;
import com.yizhi.message.application.constans.Constans;
import com.yizhi.message.application.enums.RelationType;
import com.yizhi.message.application.vo.MessageRemindVo;
import com.yizhi.message.application.vo.TaskVo;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.vo.EvenType;
import com.yizhi.training.application.vo.manage.MessageTaskRemindVo;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TrainingEvenSendMessage {

    @Autowired
    private CloudEventPublisher cloudEventPublisher;

    private Logger logger = LoggerFactory.getLogger(TrainingEvenSendMessage.class);

    /**
     * 触发事件,根据可见范围发消息
     *
     * @param trainingProject 业务参数对象
     * @param accountId       用户id为空
     */
    public void evenSendMessage(TrainingProject trainingProject, Long accountId, EvenType evenType) {

        if (trainingProject != null) {
            TaskVo taskVo = new TaskVo();
            taskVo.setTaskName(trainingProject.getName());
            taskVo.setTaskStratTime(trainingProject.getStartTime());
            taskVo.setTaskEndTime(trainingProject.getEndTime());

            MessageRemindVo vo = new MessageRemindVo();
            vo.setTaskVo(taskVo);
            vo.setVisibleRange(trainingProject.getVisibleRange());
            vo.setMessageId(evenType.getKey());
            vo.setMessageType(3);
            vo.setRelationId(trainingProject.getId());
            vo.setRelationType(RelationType.XM.getKey());
            vo.setSendType(1);
            //定时任务默认五分钟执行一次   这里默认6分钟
            vo.setSendTime(DateUtils.addMinutes(new Date(), 6));
            if (accountId != null) {
                vo.setAccountId(accountId);
            }
            RequestContext requestContext = new RequestContext();
            requestContext.setCompanyId(trainingProject.getCompanyId());
            requestContext.setSiteId(trainingProject.getSiteId());
            requestContext.setAccountId(trainingProject.getCreateById());
            requestContext.setAccountName(trainingProject.getCreateByName());
            vo.setRequestContext(requestContext);
            try {
                //临时取消触发功能
                //                cloudEventPublisher.publish(Constans.MESSAGE_QUEUE, new
                //                EventWrapper<MessageRemindVo>(null, vo));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("发送消息失败=====================", e);
            }
        }
    }

    /**
     * 系统模板,发消息
     *
     * @param trainingProject 业务参数对象
     */
    public void systemSendMessage(TrainingProject trainingProject, Long trPlanId,
        com.yizhi.training.application.vo.manage.MessageRemindVo remindVo, RequestContext context) {
        MessageRemindVo vo = new MessageRemindVo();

        vo.setRelationId(trainingProject.getId());
        vo.setRelationType(RelationType.XM.getKey());
        //这里有可能是学习计划提醒，有可能是项目提醒
        if (trPlanId != null) {
            vo.setRelationId(trPlanId);
            vo.setRelationType(RelationType.XX.getKey());
        }
        //共享参数
        vo.setMessageType(2);
        if (context == null) {
            context = new RequestContext();
            context.setCompanyId(trainingProject.getCompanyId());
            context.setSiteId(trainingProject.getSiteId());
            context.setAccountName(trainingProject.getCreateByName());
            context.setAccountId(trainingProject.getCreateById());
        }
        vo.setRequestContext(context);

        if (vo.getMessageType() == null || vo.getRelationId() == null || vo.getRelationType() == null) {
            logger.info(
                "messageType:" + vo.getMessageType() + "||" + "RelationId:" + vo.getRelationId() + "||" +
                    "RelationType:" + vo.getRelationType());
            logger.info("相关参数缺失！！");
            return;
        }
        if (remindVo.getHasDeleted()) {
            vo.setHasDeleted(remindVo.getHasDeleted());
            cloudEventPublisher.publish(Constans.MESSAGE_QUEUE, new EventWrapper<MessageRemindVo>(null, vo));
            logger.info("发送删除消息成功！！");
        } else {
            if (remindVo.getVisibleRangeUpdate()) {
                vo.setVisibleRangeUpdate(remindVo.getVisibleRangeUpdate());
                vo.setVisibleRange(trainingProject.getVisibleRange());
                cloudEventPublisher.publish(Constans.MESSAGE_QUEUE, new EventWrapper<MessageRemindVo>(null, vo));
                logger.info("发送计划同步可见范围消息成功！！");
                return;
            }
            //修改培训的状态
            if (remindVo.getTaskStatusUpdate()) {
                //1 为消息业务可发送状态  0 则不行
                vo.setTaskStatus(trainingProject.getStatus().equals(ProjectConstant.PROJECT_STATUS_ENABLE) ? 1 : 0);
                vo.setTaskStatusUpdate(remindVo.getTaskStatusUpdate());
                cloudEventPublisher.publish(Constans.MESSAGE_QUEUE, new EventWrapper<MessageRemindVo>(null, vo));
                logger.info("发送修改业务状态消息成功=====================");
                return;
            }
            if (trainingProject != null) {
                if (!CollectionUtils.isEmpty(remindVo.getMessageTaskRemindVos())) {
                    TaskVo taskVo = new TaskVo();
                    taskVo.setTaskName(trainingProject.getName());
                    taskVo.setTaskStratTime(trainingProject.getStartTime());
                    taskVo.setTaskEndTime(trainingProject.getEndTime());

                    vo.setMessageType(remindVo.getMessageType());
                    vo.setMessageId(remindVo.getMessageId());
                    vo.setIsChangge(remindVo.getIsChangge());
                    List<MessageTaskRemindVo> m = remindVo.getMessageTaskRemindVos();
                    List<com.yizhi.message.application.vo.MessageTaskRemindVo> m2 = new ArrayList<>();
                    for (MessageTaskRemindVo m1 : m) {
                        com.yizhi.message.application.vo.MessageTaskRemindVo mx =
                            new com.yizhi.message.application.vo.MessageTaskRemindVo();
                        BeanUtils.copyProperties(m1, mx);
                        m2.add(mx);
                    }
                    vo.setMessageTaskRemindVos(m2);
                    vo.setSendType(remindVo.getSendType());
                    vo.setVisibleRange(trainingProject.getVisibleRange());
                    vo.setTaskStatus(trainingProject.getStatus().equals(ProjectConstant.PROJECT_STATUS_ENABLE) ? 1 : 0);
                    vo.setTaskVo(taskVo);
                    if (vo.getMessageId() == null || vo.getSendType() == null || vo.getVisibleRange() == null) {
                        logger.info(
                            "messageID:" + vo.getMessageId() + "||" + "sendType:" + vo.getSendType() + "||" +
                                "VisibleRange:" + vo.getVisibleRange());
                        logger.info("相关参数缺失！！!!!");
                        return;
                    }

                    try {
                        cloudEventPublisher.publish(Constans.MESSAGE_QUEUE,
                            new EventWrapper<MessageRemindVo>(null, vo));
                        logger.info("发送消息成功=====================");
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("发送消息失败=====================", e);
                    }
                }
            }
        }
    }
}
