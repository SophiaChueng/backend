package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanActivity;

import java.util.List;
import java.util.Map;

public interface TpPlanActivityService extends IService<TpPlanActivity> {

    @Override
    default TpPlanActivity getOne(Wrapper<TpPlanActivity> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<Long> getTpPlanActivityIds(List<Long> tpPlanIds);

    List<TpPlanActivity> getExamAndAssignment(Long tpId);

    List<TpPlanActivity> getActivities(Long tpPlanId);

    Integer getMaxSort(Long tpPlanId);

    Boolean deleteBatch(List<Long> ids);

    Integer getTpPlanCount(Long tpId);

    Boolean deleteBatchByTpPlan(List<Long> tpPlanIds);

    /**
     * 查询项目下的学习活动列表
     *
     * @param trainingProjectId
     * @param tpPlanId
     * @param type
     * @return
     */
    List<TpPlanActivity> getActivitiesBy(Long trainingProjectId, Long tpPlanId, Integer type);

    Map<Long, Long> copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap);

    /**
     * 按照旧的排序规则查询
     *
     * @param tpPlanId
     * @return
     */
    List<TpPlanActivity> getActivitiesBySortAsc(Long tpPlanId);
}
