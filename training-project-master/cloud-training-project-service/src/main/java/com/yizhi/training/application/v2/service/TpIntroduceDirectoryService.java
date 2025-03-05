package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpIntroduceDirectory;

import java.util.List;
import java.util.Map;

public interface TpIntroduceDirectoryService extends IService<TpIntroduceDirectory> {

    @Override
    default TpIntroduceDirectory getOne(Wrapper<TpIntroduceDirectory> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<TpIntroduceDirectory> getIntroduceDirectory(Long tpId);

    Boolean existSameItem(Long trainingProjectId, Long itemId);

    Boolean existSameItemType(Long trainingProjectId, Integer itemType);

    Integer getMaxSort(Long trainingProjectId);

    Integer getSort(Long id);

    Boolean addSortValue(Long trainingProjectId, Integer sort);

    Boolean deleteBatchByTpIds(List<Long> tpIds);

    Boolean deleteByStudyItem(Long trainingProjectId, Long studyDirItemId);

    Boolean deleteByItemId(Long trainingProjectId, Long directoryItemId);

    Map<Long, Long> copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap);

    Boolean updateItemName(Long trainingProjectId, Long studyItemId, String itemName);
}
