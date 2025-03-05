package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpPlanConditionPost;
import com.yizhi.training.application.v2.mapper.TpPlanConditionPostMapperV2;
import com.yizhi.training.application.v2.service.TpPlanConditionPostService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TpPlanConditionPostServiceImplV2 extends ServiceImpl<TpPlanConditionPostMapperV2, TpPlanConditionPost>
    implements TpPlanConditionPostService {

    @Autowired
    private TpPlanConditionPostMapperV2 tpPlanConditionPostMapperV2;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 删除
     *
     * @param tpPlanIds
     * @return
     */
    @Override
    public Integer deleteBatchByTpPlanId(List<Long> tpPlanIds) {
        if (CollectionUtils.isEmpty(tpPlanIds)) {
            return 0;
        }
        return tpPlanConditionPostMapperV2.deleteBatchByTpPlanId(tpPlanIds);
    }

    @Override
    public List<TpPlanConditionPost> getConditionPosts(Long tpPlanId) {
        QueryWrapper<TpPlanConditionPost> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_plan_id", tpPlanId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public List<TpPlanConditionPost> getPostConditionByTpId(Long tpId) {
        QueryWrapper<TpPlanConditionPost> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    /**
     * 删除
     *
     * @param tpPlanId
     * @return
     */
    @Override
    public Boolean removeByPlanId(Long tpPlanId) {
        QueryWrapper<TpPlanConditionPost> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_plan_id", tpPlanId);
        return remove(wrapper);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> planIdOldToNewMap,
        Map<Long, Long> actIdOldToNewMap) {
        List<TpPlanConditionPost> oldPostCondition = getPostConditionByTpId(oldTpId);
        List<TpPlanConditionPost> newPostCondition =
            BeanCopyListUtil.copyListProperties(oldPostCondition, TpPlanConditionPost::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setTpPlanId(planIdOldToNewMap.get(s.getTpPlanId()));
                t.setTpPlanActivityId(actIdOldToNewMap.get(s.getTpPlanActivityId()));
            });
        if (CollectionUtils.isNotEmpty(newPostCondition)) {
            saveBatch(newPostCondition);
        }
    }
}
