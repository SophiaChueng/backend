package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpIntroduceDirectory;
import com.yizhi.training.application.v2.mapper.TpIntroduceDirectoryMapperV2;
import com.yizhi.training.application.v2.service.TpIntroduceDirectoryService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TpIntroduceDirectoryServiceImplV2 extends ServiceImpl<TpIntroduceDirectoryMapperV2, TpIntroduceDirectory>
    implements TpIntroduceDirectoryService {

    @Autowired
    private TpIntroduceDirectoryMapperV2 tpIntroduceDirectoryMapper;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 查询介绍页目录
     *
     * @param tpId
     * @return
     */
    @Override
    public List<TpIntroduceDirectory> getIntroduceDirectory(Long tpId) {
        QueryWrapper<TpIntroduceDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("sort");
        return list(wrapper);
    }

    /**
     * 查询是否已经存在相同的来源于学习页目录的目录项
     *
     * @param trainingProjectId
     * @param itemId
     * @return
     */
    @Override
    public Boolean existSameItem(Long trainingProjectId, Long itemId) {
        QueryWrapper<TpIntroduceDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("item_id", itemId);
        wrapper.eq("deleted", 0);
        int count = (int)count(wrapper);
        return count > 0;
    }

    /**
     * 查询是否已经存在相同类型的目录项
     *
     * @param trainingProjectId
     * @param itemType
     * @return
     */
    @Override
    public Boolean existSameItemType(Long trainingProjectId, Integer itemType) {
        QueryWrapper<TpIntroduceDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("item_type", itemType);
        wrapper.eq("deleted", 0);
        int count = (int)count(wrapper);
        return count > 0;
    }

    /**
     * 查询目录项最大排序
     *
     * @param trainingProjectId
     * @return
     */
    @Override
    public Integer getMaxSort(Long trainingProjectId) {
        Integer maxSort = tpIntroduceDirectoryMapper.getMaxSort(trainingProjectId);
        return maxSort == null ? 0 : maxSort;
    }

    /**
     * 查询指定目录项的sort
     *
     * @param id
     * @return
     */
    @Override
    public Integer getSort(Long id) {
        TpIntroduceDirectory directory = getById(id);
        return directory == null ? 0 : directory.getSort();
    }

    /**
     * 将相同项目下的介绍页目录的sort大于等于${sort}的sort加一
     *
     * @param trainingProjectId
     * @param sort
     */
    @Override
    public Boolean addSortValue(Long trainingProjectId, Integer sort) {
        return tpIntroduceDirectoryMapper.updateSortValue(trainingProjectId, sort);
    }

    /**
     * 逻辑删除
     *
     * @param tpIds
     * @return
     */
    @Override
    public Boolean deleteBatchByTpIds(List<Long> tpIds) {
        if (CollectionUtils.isNotEmpty(tpIds)) {
            return false;
        }
        return tpIntroduceDirectoryMapper.deleteBatchByTpIds(tpIds);
    }

    @Override
    public Boolean deleteByStudyItem(Long trainingProjectId, Long studyDirItemId) {
        return tpIntroduceDirectoryMapper.deleteByStudyItem(trainingProjectId, studyDirItemId);
    }

    @Override
    public Boolean deleteByItemId(Long trainingProjectId, Long directoryItemId) {
        return tpIntroduceDirectoryMapper.deleteByItemId(trainingProjectId, directoryItemId);
    }

    @Override
    public Map<Long, Long> copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap) {
        List<TpIntroduceDirectory> oldIntroduceDirItems = getIntroduceDirectory(oldTpId);
        Map<Long, Long> dirIdOldToNewMap = new HashMap<>();
        List<TpIntroduceDirectory> newIntroduceDirItems =
            BeanCopyListUtil.copyListProperties(oldIntroduceDirItems, TpIntroduceDirectory::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                t.setItemId(studyDirIdOldToNewMap.get(s.getItemId()));
                dirIdOldToNewMap.put(s.getId(), t.getId());
            });
        if (CollectionUtils.isNotEmpty(newIntroduceDirItems)) {
            saveBatch(newIntroduceDirItems);
        }
        return dirIdOldToNewMap;
    }

    @Override
    public Boolean updateItemName(Long trainingProjectId, Long studyItemId, String itemName) {
        QueryWrapper<TpIntroduceDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("item_id", studyItemId);
        TpIntroduceDirectory updateItem = new TpIntroduceDirectory();
        updateItem.setItemName(itemName);
        return update(updateItem, wrapper);
    }
}
