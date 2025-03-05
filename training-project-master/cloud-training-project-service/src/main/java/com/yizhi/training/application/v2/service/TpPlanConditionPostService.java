package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanConditionPost;

import java.util.List;
import java.util.Map;

public interface TpPlanConditionPostService extends IService<TpPlanConditionPost> {

    @Override
    default TpPlanConditionPost getOne(Wrapper<TpPlanConditionPost> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Integer deleteBatchByTpPlanId(List<Long> tpPlanIds);

    List<TpPlanConditionPost> getConditionPosts(Long tpPlanId);

    List<TpPlanConditionPost> getPostConditionByTpId(Long tpId);

    Boolean removeByPlanId(Long tpPlanId);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap, Map<Long, Long> actIdOldToNewMap);
}
