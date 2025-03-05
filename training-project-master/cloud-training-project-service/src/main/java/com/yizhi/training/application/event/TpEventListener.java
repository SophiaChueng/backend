package com.yizhi.training.application.event;

import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.util.application.constant.QueueConstant;
import com.yizhi.util.application.event.TrainingProjectEvent;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: shengchenglong
 * @Date: 2018/4/18 11:14
 */
@Component
@RabbitListener(queues = QueueConstant.TRAINING_PROJECT_EVENT_QUEUE)
public class TpEventListener {

    @Autowired
    private TpEventHandler tpEventHandler;

    @RabbitHandler
    public void processBizEvent(EventWrapper<TrainingProjectEvent> ew) {
        tpEventHandler.handle(ew);
    }

}
