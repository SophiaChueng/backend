package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpConditionPost;
import com.yizhi.training.application.v2.enums.TpCompleteConditionTypeEnum;
import com.yizhi.training.application.v2.mapper.TpConditionPostMapperV2;
import com.yizhi.training.application.v2.service.TpConditionPostService;
import com.yizhi.training.application.v2.vo.TpCompleteConditionVO;
import com.yizhi.training.application.v2.vo.TpPlanVO;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TpConditionPostServiceImplV2 extends ServiceImpl<TpConditionPostMapperV2, TpConditionPost>
    implements TpConditionPostService {

    @Autowired
    private TpConditionPostMapperV2 tpConditionPostMapperV2;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 批量逻辑删除项目的完成条件
     *
     * @param tpIds
     * @return
     */
    @Override
    public Boolean deleteBatchByTpId(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return false;
        }
        QueryWrapper<TpConditionPost> wrapper = new QueryWrapper<>();
        wrapper.in("training_project_id", tpIds);
        return tpConditionPostMapperV2.delete(wrapper) > 0;
    }

    /**
     * 更新项目完成条件 先物理删除旧的，再新增
     *
     * @param trainingProjectId
     * @param completeCondition
     * @return
     */
    @Override
    public Boolean updateCompleteCondition(Long trainingProjectId, TpCompleteConditionVO completeCondition) {

        RequestContext context = ContextHolder.get();

        QueryWrapper<TpConditionPost> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        remove(wrapper);

        List<TpConditionPost> list = new ArrayList<>();
        if (completeCondition.getCompleteCount() != null && completeCondition.getCompleteCount() > 0) {
            TpConditionPost condition = new TpConditionPost();
            condition.setId(idGenerator.generate());
            condition.setCompanyId(context.getCompanyId());
            condition.setSiteId(context.getSiteId());
            condition.setTrainingProjectId(trainingProjectId);
            condition.setConditionType(TpCompleteConditionTypeEnum.SPECIFIC_COUNT.getCode());
            condition.setCompleteCount(completeCondition.getCompleteCount());

            list.add(condition);
        }
        if (CollectionUtils.isNotEmpty(completeCondition.getTpPlans())) {
            for (TpPlanVO tpPlanVO : completeCondition.getTpPlans()) {
                TpConditionPost condition = new TpConditionPost();
                condition.setId(idGenerator.generate());
                condition.setCompanyId(context.getCompanyId());
                condition.setSiteId(context.getSiteId());
                condition.setTrainingProjectId(trainingProjectId);
                condition.setConditionType(TpCompleteConditionTypeEnum.SPECIFIC_PLANS.getCode());
                condition.setTpPlanId(tpPlanVO.getId());

                list.add(condition);
            }
        }
        if (CollectionUtils.isNotEmpty(list)) {
            saveBatch(list);
        }
        return true;
    }

    @Override
    public List<TpConditionPost> getCompleteConditions(Long trainingProjectId) {
        QueryWrapper<TpConditionPost> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public List<TpConditionPost> getNeedActivityId(Long trainingProjectId, Long planId) {
        QueryWrapper<TpConditionPost> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("tp_plan_id", planId);
        wrapper.eq("type", 1);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap) {
        List<TpConditionPost> oldCondition = getCompleteConditions(oldTpId);
        List<TpConditionPost> newCondition =
            BeanCopyListUtil.copyListProperties(oldCondition, TpConditionPost::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setTpPlanId(planIdOldToNewMap.get(s.getTpPlanId()));
            });
        if (CollectionUtils.isNotEmpty(newCondition)) {
            saveBatch(newCondition);
        }
    }
}
