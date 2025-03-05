package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpPlanConditionPre;
import com.yizhi.training.application.v2.mapper.TpPlanConditionPreMapperV2;
import com.yizhi.training.application.v2.service.TpPlanConditionPreService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TpPlanConditionPreServiceImplV2 extends ServiceImpl<TpPlanConditionPreMapperV2, TpPlanConditionPre>
    implements TpPlanConditionPreService {

    @Autowired
    private TpPlanConditionPreMapperV2 tpPlanConditionPreMapperV2;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 逻辑删除
     *
     * @param tpPlanIds
     * @return
     */
    @Override
    public Integer deleteBatchByTpPlanId(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return 0;
        }
        return tpPlanConditionPreMapperV2.deleteBatchByTpPlanId(tpPlanIds);
    }

    /**
     * 通过学习计划id查询
     *
     * @param tpPlanId
     * @return
     */
    @Override
    public List<TpPlan> getPrePlans(Long tpPlanId) {
        return tpPlanConditionPreMapperV2.getPrePlans(tpPlanId);
    }

    /**
     * 删除
     *
     * @param tpPlanId
     */
    @Override
    public Boolean removeByPlanId(Long tpPlanId) {
        QueryWrapper<TpPlanConditionPre> wrapper = new QueryWrapper<>();
        wrapper.eq("plan_id", tpPlanId);
        return remove(wrapper);
    }

    @Override
    public List<TpPlanConditionPre> getPreConditionByTpId(Long tpId) {
        QueryWrapper<TpPlanConditionPre> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap) {
        List<TpPlanConditionPre> oldPreCondition = getPreConditionByTpId(oldTpId);
        List<TpPlanConditionPre> newPreCondition =
            BeanCopyListUtil.copyListProperties(oldPreCondition, TpPlanConditionPre::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setPlanId(planIdOldToNewMap.get(s.getPlanId()));
                t.setPrePlanId(planIdOldToNewMap.get(s.getPrePlanId()));
            });
        if (CollectionUtils.isNotEmpty(newPreCondition)) {
            saveBatch(newPreCondition);
        }
    }

    @Override
    public Integer getFinishCount(Long tpPlanId) {
        QueryWrapper<TpPlanConditionPre> wrapper = new QueryWrapper<>();
        wrapper.eq("plan_id", tpPlanId);
        wrapper.eq("deleted", 0);
        wrapper.last("LIMIT 1");

        TpPlanConditionPre conditionPre = getOne(wrapper);
        return conditionPre == null || conditionPre.getFinishCount() == null ? 0 : conditionPre.getFinishCount();
    }

    @Override
    public List<TpPlanConditionPre> getPreConditionByPlanId(Long tpPlanId) {
        QueryWrapper<TpPlanConditionPre> wrapper = new QueryWrapper<>();
        wrapper.eq("plan_id", tpPlanId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }
}
