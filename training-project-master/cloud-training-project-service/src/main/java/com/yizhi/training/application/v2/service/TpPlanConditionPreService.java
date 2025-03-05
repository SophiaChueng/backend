package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpPlanConditionPre;

import java.util.List;
import java.util.Map;

public interface TpPlanConditionPreService extends IService<TpPlanConditionPre> {

    @Override
    default TpPlanConditionPre getOne(Wrapper<TpPlanConditionPre> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Integer deleteBatchByTpPlanId(List<Long> tpPlanIds);

    List<TpPlan> getPrePlans(Long tpPlanId);

    Boolean removeByPlanId(Long tpPlanId);

    List<TpPlanConditionPre> getPreConditionByTpId(Long tpId);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap);

    Integer getFinishCount(Long tpPlanId);

    List<TpPlanConditionPre> getPreConditionByPlanId(Long tpPlanId);
}
