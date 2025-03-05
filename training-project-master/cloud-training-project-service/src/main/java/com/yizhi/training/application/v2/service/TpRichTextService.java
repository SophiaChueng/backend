package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpRichText;

import java.util.List;
import java.util.Map;

public interface TpRichTextService extends IService<TpRichText> {

    @Override
    default TpRichText getOne(Wrapper<TpRichText> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Boolean existRichText(Long trainingProjectId, Integer directoryType, Long directoryItemId);

    Boolean updateRichText(TpRichText text);

    /**
     * 逻辑删除
     *
     * @param tpIds
     * @return
     */
    Boolean deleteBatchByTpIds(List<Long> tpIds);

    TpRichText getRichText(Long trainingProjectId, Integer directoryType, Long directoryItemId);

    Boolean deleteByItemId(Long trainingProjectId, Integer directoryType, Long directoryItemId);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap,
        Map<Long, Long> introDirIdOldToNewMap);
}
