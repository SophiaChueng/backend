package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlan;

import java.util.List;
import java.util.Map;

public interface TpPlanService extends IService<TpPlan> {

    @Override
    default TpPlan getOne(Wrapper<TpPlan> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Boolean deleteBatchById(List<Long> tpPlanIds);

    List<Long> getTpPlanIds(List<Long> tpIds);

    Integer getMaxSort(Long trainingProjectId, Long directoryItemId);

    Integer getTpPlanCount(Long tpId);

    Boolean updateDirectoryItemId(Long tpId, Long oldDirectoryItemId, Long directoryItemId);

    List<TpPlan> getTpPlansByTpId(Long tpId);

    List<Long> getTpPlanIdsByItem(Long trainingProjectId, Long directoryItemId);

    List<TpPlan> getTpPlans(Long trainingProjectId, Long directoryItemId);

    Boolean addTpSort(Long trainingProjectId, Long directoryItemId, Integer sort);

    Map<Long, Long> copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap);

    Long getMaxStudyTimePlanId(Long accountId, Long siteId, Long tpId);

    List<TpPlan> getAllTpPlan(Long companyId, Long siteId, Integer pageNo, Integer pageSize);

    List<TpPlan> getTpPlansOrderByDir(List<Long> tpPlanIds);
}
