package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.v2.mapper.TpPlanMapperV2;
import com.yizhi.training.application.v2.service.TpPlanService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TpPlanServiceImplV2 extends ServiceImpl<TpPlanMapperV2, TpPlan> implements TpPlanService {

    @Autowired
    private TpPlanMapperV2 tpPlanMapper;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 逻辑删除学习计划
     *
     * @param tpPlanIds
     * @return
     */
    @Override
    public Boolean deleteBatchById(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return false;
        }
        RequestContext context = ContextHolder.get();
        return tpPlanMapper.deleteBatch(tpPlanIds, context.getAccountId(), context.getAccountName(), new Date());
    }

    /**
     * 查询学习计划ID列表
     *
     * @param tpIds
     * @return
     */
    @Override
    public List<Long> getTpPlanIds(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<TpPlan> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.in("training_project_id", tpIds);
        List<TpPlan> list = list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(TpPlan::getId).collect(Collectors.toList());
    }

    /**
     * 查询最大排序值
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @return
     */
    @Override
    public Integer getMaxSort(Long trainingProjectId, Long directoryItemId) {
        Integer maxSort = tpPlanMapper.getMaxSort(trainingProjectId, directoryItemId);
        return maxSort == null ? 0 : maxSort;
    }

    /**
     * 查询项目的学习计划数
     *
     * @param tpId
     * @return
     */
    @Override
    public Integer getTpPlanCount(Long tpId) {
        QueryWrapper<TpPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("deleted", 0);
        return (int)count(wrapper);
    }

    /**
     * 更新学习计划所在目录项ID
     *
     * @param tpId
     * @param oldDirectoryItemId
     * @param directoryItemId
     */
    @Override
    public Boolean updateDirectoryItemId(Long tpId, Long oldDirectoryItemId, Long directoryItemId) {
        return tpPlanMapper.updateDirectoryItemId(tpId, oldDirectoryItemId, directoryItemId);
    }

    @Override
    public List<TpPlan> getTpPlansByTpId(Long tpId) {
        QueryWrapper<TpPlan> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public List<Long> getTpPlanIdsByItem(Long trainingProjectId, Long directoryItemId) {
        return tpPlanMapper.getTpPlanIdsByItem(trainingProjectId, directoryItemId);
    }

    @Override
    public List<TpPlan> getTpPlans(Long trainingProjectId, Long directoryItemId) {
        if (directoryItemId != null && directoryItemId > 0) {
            QueryWrapper<TpPlan> wrapper = new QueryWrapper<>();
            wrapper.eq("training_project_id", trainingProjectId);
            wrapper.eq("directory_item_id", directoryItemId);
            wrapper.eq("deleted", 0);
            wrapper.orderByAsc("sort");
            return list(wrapper);
        } else {
            return tpPlanMapper.getTpPlansByTpOrderByDir(trainingProjectId);
        }
    }

    /**
     * 更新directoryItemId下的学习单元的排序，使排序值大于等于#{sort}的排序值加一
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @param sort
     * @return
     */
    @Override
    public Boolean addTpSort(Long trainingProjectId, Long directoryItemId, Integer sort) {
        return tpPlanMapper.addTpSort(trainingProjectId, directoryItemId, sort);
    }

    @Override
    public Map<Long, Long> copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap) {
        List<TpPlan> oldTpPlans = getTpPlansByTpId(oldTpId);
        Map<Long, Long> planIdOldToNewMap = new HashMap<>();
        List<TpPlan> newTpPlans = BeanCopyListUtil.copyListProperties(oldTpPlans, TpPlan::new, (s, t) -> {
            t.setId(idGenerator.generate());
            t.setTrainingProjectId(newTpId);
            t.setDirectoryItemId(studyDirIdOldToNewMap.get(s.getDirectoryItemId()));
            planIdOldToNewMap.put(s.getId(), t.getId());
        });
        if (CollectionUtils.isNotEmpty(newTpPlans)) {
            saveBatch(newTpPlans);
        }
        return planIdOldToNewMap;
    }

    @Override
    public Long getMaxStudyTimePlanId(Long accountId, Long siteId, Long tpId) {
        return tpPlanMapper.getMaxStudyTimePlanId(accountId, siteId, tpId);
    }

    @Override
    public List<TpPlan> getAllTpPlan(Long companyId, Long siteId, Integer pageNo, Integer pageSize) {
        QueryWrapper<TpPlan> wrapper = new QueryWrapper<>();
        if (companyId != null && companyId > 0) {
            wrapper.eq("company_id", companyId);
        }
        if (siteId != null && siteId > 0) {
            wrapper.eq("site_id", siteId);
        }
        wrapper.eq("deleted", 0);
        wrapper.last("LIMIT " + (pageNo - 1) * pageSize + "," + pageSize);
        return list(wrapper);
    }

    @Override
    public List<TpPlan> getTpPlansOrderByDir(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return Collections.emptyList();
        }
        return tpPlanMapper.getTpPlansOrderByDir(tpPlanIds);
    }
}
