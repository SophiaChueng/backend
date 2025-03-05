package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpConsultEntrance;

import java.util.List;
import java.util.Map;

public interface TpConsultEntranceService extends IService<TpConsultEntrance> {

    @Override
    default TpConsultEntrance getOne(Wrapper<TpConsultEntrance> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    TpConsultEntrance getOne(Long trainingProjectId, Long directoryItemId);

    Boolean deleteBatchByTpIds(List<Long> tpIds);

    Boolean deleteByItemId(Long trainingProjectId, Long directoryItemId);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> introDirIdOldToNewMap);
}
