package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpPlanStudyTimeCondition;
import com.yizhi.training.application.v2.mapper.TpPlanStudyTimeConditionMapperV2;
import com.yizhi.training.application.v2.service.TpPlanStudyTimeConditionService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TpPlanStudyTimeConditionServiceImplV2
    extends ServiceImpl<TpPlanStudyTimeConditionMapperV2, TpPlanStudyTimeCondition>
    implements TpPlanStudyTimeConditionService {

    @Autowired
    private TpPlanStudyTimeConditionMapperV2 tpPlanStudyTimeConditionMapper;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 逻辑删除学习计划的学习时间条件
     *
     * @param tpPlanIds
     * @return
     */
    @Override
    public Boolean deleteBatchByTpPlanId(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return false;
        }

        return tpPlanStudyTimeConditionMapper.deleteBatchByTpPlanId(tpPlanIds);
    }

    @Override
    public TpPlanStudyTimeCondition getTimeCondition(Long tpPlanId) {
        QueryWrapper<TpPlanStudyTimeCondition> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_plan_id", tpPlanId);
        wrapper.eq("deleted", 0);
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Boolean updateTimeCondition(TpPlanStudyTimeCondition timeCondition) {
        // 先物理删除，然后新增
        QueryWrapper<TpPlanStudyTimeCondition> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_plan_id", timeCondition.getTpPlanId());
        if (remove(wrapper)) {
            return save(timeCondition);
        }
        return false;
    }

    @Override
    public List<TpPlanStudyTimeCondition> getTimeConditionByTpId(Long tpId) {
        QueryWrapper<TpPlanStudyTimeCondition> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap) {
        List<TpPlanStudyTimeCondition> oldTimeCondition = getTimeConditionByTpId(oldTpId);
        List<TpPlanStudyTimeCondition> newTimeCondition =
            BeanCopyListUtil.copyListProperties(oldTimeCondition, TpPlanStudyTimeCondition::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setTpPlanId(planIdOldToNewMap.get(s.getTpPlanId()));
            });
        if (CollectionUtils.isNotEmpty(newTimeCondition)) {
            saveBatch(newTimeCondition);
        }
    }

}
