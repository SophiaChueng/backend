package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpConditionPost;
import com.yizhi.training.application.v2.vo.TpCompleteConditionVO;

import java.util.List;
import java.util.Map;

public interface TpConditionPostService extends IService<TpConditionPost> {

    @Override
    default TpConditionPost getOne(Wrapper<TpConditionPost> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Boolean deleteBatchByTpId(List<Long> tpIds);

    Boolean updateCompleteCondition(Long trainingProjectId, TpCompleteConditionVO completeCondition);

    List<TpConditionPost> getCompleteConditions(Long trainingProjectId);

    List<TpConditionPost> getNeedActivityId(Long trainingProjectId, Long planId);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap);
}
