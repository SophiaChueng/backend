package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpProMapping;
import com.yizhi.training.application.domain.TrainingProject;

import java.util.List;
import java.util.Map;

public interface TpProMappingService extends IService<TpProMapping> {

    @Override
    default TpProMapping getOne(Wrapper<TpProMapping> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Boolean deleteBatchByTpId(Long trainingProjectId);

    Integer getTpCountOfTpPro(Long tpProId);

    Boolean deleteBatchByProId(Long tpProId);

    Integer getTpCountOfPro(Long tpProId, String searchTpName);

    List<TrainingProject> getTpListOfPro(Long tpProId, String searchTpName, Integer pageNo, Integer pageSize,
        Integer status);

    Boolean deleteByProIdAndTpIds(Long tpProId, List<Long> trainingProjectIds);

    List<Long> getTpIds(Long tpProId);

    Integer getOneByTpId(Long tpProId, Long trainingProjectId);

    Boolean addSortValue(Long tpProId, Integer sort);

    Boolean updateBy(TpProMapping mapping, Long tpProId, Long moveTpId);

    Integer getMaxSort(Long tpProId);

    /**
     * key : tpProId value: sort
     *
     * @param trainingProjectId
     * @param tpProIds
     * @return
     */
    Map<Long, Integer> getMaxSortOfTp(Long trainingProjectId, List<Long> tpProIds);

    List<TpProMapping> getMappingListByTpId(Long trainingProjectId);

    List<TpProMapping> getMappingListByTpPro(Long tpProId);

    List<Long> getInTpProIds(List<Long> tpIds);
}
