package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.cache.CacheNamespace;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.domain.TpPlanActivityViewRecord;
import com.yizhi.training.application.event.TpEventHandler;
import com.yizhi.training.application.mapper.TpPlanActivityViewRecordMapper;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.TpPlanActivityViewRecordService;
import com.yizhi.util.application.constant.TpActivityType;
import com.yizhi.util.application.event.TrainingProjectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
@Service
public class TpPlanActivityViewRecordServiceImpl
    extends ServiceImpl<TpPlanActivityViewRecordMapper, TpPlanActivityViewRecord>
    implements TpPlanActivityViewRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TpPlanActivityViewRecordServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpEventHandler tpEventHandler;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RedisCache redisCache;

    @Override
    public Integer addViewRecord(BaseModel<Long> model) {
        TpPlanActivity activity = new TpPlanActivity();
        activity.setId(model.getObj());
        activity = activity.selectById();

        if (null == activity) {
            return 0;
        }

        RequestContext context = model.getContext();
        TpPlanActivityViewRecord record = new TpPlanActivityViewRecord();
        record.setCompanyId(context.getCompanyId());
        record.setAccountId(context.getAccountId());
        record.setId(idGenerator.generate());
        record.setOrgId(context.getOrgId());
        record.setSiteId(context.getSiteId());
        record.setTime(model.getDate());
        record.setTpPlanActivityId(model.getObj());
        record.setTpPlanActivityRelationId(activity.getRelationId());
        record.setTpPlanActivityType(activity.getType());
        record.setTrainingProjectId(activity.getTrainingProjectId());

        int num = this.baseMapper.insert(record);

        if (num == 1) {
            // 缓存点击记录
            redisCache.hset(CacheNamespace.TP_ACTIVITY_CLICKED.concat(String.valueOf(record.getAccountId())),
                String.valueOf(record.getTpPlanActivityId()).concat("_")
                    .concat(String.valueOf(activity.getTrainingProjectId())), String.valueOf(new Integer(1)));
            try {
                // 发送消息（只有外部链接、直播、资料有该情况） 点击及完成
                boolean flag =
                    record.getTpPlanActivityType().equals(TpActivityType.TYPE_LINK) || record.getTpPlanActivityType()
                        .equals(TpActivityType.TYPE_LIVE) || record.getTpPlanActivityType()
                        .equals(TpActivityType.TYPE_DOCUMENT) || record.getTpPlanActivityType()
                        .equals(TpActivityType.TYPE_FORUM_REAL);
                if (flag) {
                    taskExecutor.asynExecute(new AbstractTaskHandler() {
                        @Override
                        public void handle() {
                            try {
                                tpEventHandler.handle(new EventWrapper<>(record.getTpPlanActivityRelationId(),
                                    TrainingProjectEvent.getInstance(record.getTpPlanActivityRelationId(),
                                        record.getTpPlanActivityType(), context.getAccountId(), model.getDate(),
                                        context.getSiteId())));
                            } catch (Exception e) {
                                LOGGER.error("发送消息（只有外部链接、直播、资料、帖子有该情况）异常：", e);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("外部链接、直播、资料、帖子请求计算完成状态异常：", e);
            }
        }
        return num;
    }
}
