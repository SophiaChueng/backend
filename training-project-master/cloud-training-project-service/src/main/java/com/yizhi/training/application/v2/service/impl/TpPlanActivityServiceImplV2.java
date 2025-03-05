package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.feign.CommitmentLettersClient;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.v2.enums.TpActivityTypeEnum;
import com.yizhi.training.application.v2.mapper.TpPlanActivityMapperV2;
import com.yizhi.training.application.v2.service.TpPlanActivityService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TpPlanActivityServiceImplV2 extends ServiceImpl<TpPlanActivityMapperV2, TpPlanActivity>
    implements TpPlanActivityService {

    @Autowired
    private TpPlanActivityMapperV2 tpPlanActivityMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private CommitmentLettersClient commitmentLettersClient;

    /**
     * 根据学习计划ID查询学习活动ID
     *
     * @param tpPlanIds
     * @return
     */
    @Override
    public List<Long> getTpPlanActivityIds(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.in("tp_plan_id", tpPlanIds);
        wrapper.eq("deleted", 0);
        List<TpPlanActivity> list = list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(TpPlanActivity::getId).collect(Collectors.toList());
    }

    /**
     * 查询类型为考试和作业的学习活动
     *
     * @param tpId
     * @return
     */
    @Override
    public List<TpPlanActivity> getExamAndAssignment(Long tpId) {
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.in("type", Arrays.asList(TpActivityTypeEnum.EXAM.getCode(), TpActivityTypeEnum.ASSIGNMENT.getCode()));
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("sort");
        return list(wrapper);
    }

    @Override
    public List<TpPlanActivity> getActivities(Long tpPlanId) {
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_plan_id", tpPlanId);
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("sort");
        wrapper.orderByAsc("create_time");
        return list(wrapper);
    }

    @Override
    public Integer getMaxSort(Long tpPlanId) {
        Integer maxSort = tpPlanActivityMapper.getMaxSort(tpPlanId);
        return maxSort == null ? 0 : maxSort;
    }

    /**
     * 批量逻辑删除
     *
     * @param ids
     */
    @Override
    public Boolean deleteBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }

        RequestContext context = ContextHolder.get();
        for (Long id : ids) {
            TpPlanActivity byId = this.getById(id);
            if (byId != null && byId.getType() == 20) {
                //承诺书类型删除关联关系
                commitmentLettersClient.deleteCommitmentLetterAssociation(byId.getRelationId(),
                    byId.getTrainingProjectId(), byId.getTpPlanId(), 2);
            }
        }
        return tpPlanActivityMapper.deleteBatch(ids, context.getAccountId(), context.getAccountName(), new Date());
    }

    /**
     * 查询有学习活动的学习计划的数量
     *
     * @param tpId
     * @return
     */
    @Override
    public Integer getTpPlanCount(Long tpId) {
        return tpPlanActivityMapper.getTpPlanCount(tpId);
    }

    @Override
    public Boolean deleteBatchByTpPlan(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return false;
        }
        return tpPlanActivityMapper.deleteBatchByTpPlan(tpPlanIds);
    }

    @Override
    public List<TpPlanActivity> getActivitiesBy(Long trainingProjectId, Long tpPlanId, Integer type) {
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        if (tpPlanId != null && tpPlanId > 0) {
            wrapper.eq("tp_plan_id", tpPlanId);
        }
        if (type != null) {
            wrapper.eq("type", type);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("sort");
        return list(wrapper);
    }

    @Override
    public Map<Long, Long> copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap) {
        List<TpPlanActivity> oldActivities = getActivitiesBy(oldTpId, null, null);
        Map<Long, Long> actIdOldToNewMap = new HashMap<>();
        List<TpPlanActivity> newActivities =
            BeanCopyListUtil.copyListProperties(oldActivities, TpPlanActivity::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setTpPlanId(planIdOldToNewMap.get(s.getTpPlanId()));
                actIdOldToNewMap.put(s.getId(), t.getId());
            });
        if (CollectionUtils.isNotEmpty(newActivities)) {
            saveBatch(newActivities);
            //复制承诺书关联关系
            for (TpPlanActivity newActivity : newActivities) {
                if (newActivity.getType() != null && newActivity.getType() == 20) {
                    commitmentLettersClient.saveCommitmentLetterAssociation(1, newActivity.getRelationId(), newTpId,
                        newActivity.getTpPlanId(), 2);
                }
            }
        }
        return actIdOldToNewMap;
    }

    @Override
    public List<TpPlanActivity> getActivitiesBySortAsc(Long tpPlanId) {
        QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<>();
        wrapper.select("id, sort");
        wrapper.eq("tp_plan_id", tpPlanId);
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("sort");
        return list(wrapper);
    }
}
