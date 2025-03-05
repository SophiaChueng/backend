package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpConsultEntrance;
import com.yizhi.training.application.v2.mapper.TpConsultEntranceMapperV2;
import com.yizhi.training.application.v2.service.TpConsultEntranceService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TpConsultEntranceServiceImplV2 extends ServiceImpl<TpConsultEntranceMapperV2, TpConsultEntrance>
    implements TpConsultEntranceService {

    @Autowired
    private TpConsultEntranceMapperV2 tpConsultEntranceMapperV2;

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public TpConsultEntrance getOne(Long trainingProjectId, Long directoryItemId) {
        QueryWrapper<TpConsultEntrance> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("directory_item_id", directoryItemId);
        wrapper.eq("deleted", 0);
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Boolean deleteBatchByTpIds(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return false;
        }
        return tpConsultEntranceMapperV2.deleteBatchByTpIds(tpIds);
    }

    @Override
    public Boolean deleteByItemId(Long trainingProjectId, Long directoryItemId) {
        return tpConsultEntranceMapperV2.deleteByItemId(trainingProjectId, directoryItemId);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> introDirIdOldToNewMap) {
        QueryWrapper<TpConsultEntrance> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", oldTpId);
        wrapper.eq("deleted", 0);
        List<TpConsultEntrance> oldList = list(wrapper);
        if (CollectionUtils.isEmpty(oldList)) {
            return;
        }
        List<TpConsultEntrance> newList =
            BeanCopyListUtil.copyListProperties(oldList, TpConsultEntrance::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setDirectoryItemId(introDirIdOldToNewMap.get(s.getDirectoryItemId()));
                t.setCreateById(null);
                t.setCreateByName(null);
                t.setCreateTime(null);
            });
        saveBatch(newList);
    }
}
