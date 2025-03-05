package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanStudyTimeCondition;

import java.util.List;
import java.util.Map;

public interface TpPlanStudyTimeConditionService extends IService<TpPlanStudyTimeCondition> {

    @Override
    default TpPlanStudyTimeCondition getOne(Wrapper<TpPlanStudyTimeCondition> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 逻辑删除
     *
     * @param tpPlanIds
     * @return
     */
    Boolean deleteBatchByTpPlanId(List<Long> tpPlanIds);

    TpPlanStudyTimeCondition getTimeCondition(Long tpPlanId);

    Boolean updateTimeCondition(TpPlanStudyTimeCondition timeCondition);

    List<TpPlanStudyTimeCondition> getTimeConditionByTpId(Long tpId);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap);
}
