package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpStudyDirectory;

import java.util.List;
import java.util.Map;

public interface TpStudyDirectoryService extends IService<TpStudyDirectory> {

    @Override
    default TpStudyDirectory getOne(Wrapper<TpStudyDirectory> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Integer getMaxSort(Long trainingProjectId);

    Boolean existSameType(Long trainingProjectId, Integer itemType);

    List<TpStudyDirectory> getStudyDirectory(Long trainingProjectId);

    Integer getSort(Long id);

    Boolean addSortValue(Long trainingProjectId, Integer sort);

    Integer getStudyUnitCount(Long tpId);

    /**
     * 逻辑删除
     *
     * @param tpIds
     * @return
     */
    Boolean deleteBatchByTpIds(List<Long> tpIds);

    Boolean deleteByItemId(Long trainingProjectId, Long directoryItemId);

    /**
     * 查询项目不能添加哪些类型的目录
     *
     * @param trainingProjectId
     * @return
     */
    List<Integer> getCanNotAddType(Long trainingProjectId);

    Map<Long, Long> copyByTp(Long oldTpId, Long newTpId);
}
