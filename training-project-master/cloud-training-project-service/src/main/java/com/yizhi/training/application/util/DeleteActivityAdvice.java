package com.yizhi.training.application.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.assignment.application.feign.AssignmentClient;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.library.application.feign.CaseLibraryClient;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.mapper.TpPlanActivityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class DeleteActivityAdvice {

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private AssignmentClient assignmentClient;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private CaseLibraryClient caseLibraryClient;

    /**
     * 删除活动 触发通知需要的业务删除关联关系
     */
    public int deletedActivityAdvice(List<Long> activityIds, RequestContext context, Date date) {
        if (CollectionUtils.isEmpty(activityIds)) {
            log.info("ids为空，结束执行");
            return 0;
        }
        int num = tpPlanActivityMapper.deleteByIds(activityIds, context.getAccountId(), context.getAccountName(), date);
        if (num > 0) {
            updateRelationIdField(activityIds);
        }
        return num;
    }

    /**
     * 删除活动 触发通知需要的业务删除关联关系 目前作业、案例活动 为一对一的关系
     */
    private void updateRelationIdField(List<Long> activityIds) {
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {
                TpPlanActivity activity = new TpPlanActivity();
                //对活动的删除还未入库 所以用deleted=0筛选出活动id
                //activity.setDeleted(0);
                QueryWrapper<TpPlanActivity> ew = new QueryWrapper<>(activity);
                ew.in("id", activityIds);
                //获取的活动
                List<TpPlanActivity> tpPlanActivities = tpPlanActivityMapper.selectList(ew);
                if (CollectionUtils.isNotEmpty(tpPlanActivities)) {
                    tpPlanActivities.parallelStream().forEach(a -> {
                        switch (a.getType()) {
                            //作业
                            case 5:
                                log.info("活动删除。删除作业项目关联关系，relationId:" + a.getRelationId());
                                assignmentClient.deleteRelation(a.getRelationId());
                                break;
                            //案例活动
                            case 11:
                                log.info("活动删除。删除案例活动项目关联关系，relationId:" + a.getRelationId());
                                caseLibraryClient.cancelRelateProject(a.getRelationId());
                                break;
                        }
                    });
                }
            }
        });
    }
}
