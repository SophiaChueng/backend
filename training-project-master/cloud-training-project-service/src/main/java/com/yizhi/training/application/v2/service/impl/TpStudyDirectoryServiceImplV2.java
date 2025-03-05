package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpStudyDirectory;
import com.yizhi.training.application.v2.enums.TpDirectoryItemTypeEnum;
import com.yizhi.training.application.v2.mapper.TpStudyDirectoryMapperV2;
import com.yizhi.training.application.v2.service.TpStudyDirectoryService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TpStudyDirectoryServiceImplV2 extends ServiceImpl<TpStudyDirectoryMapperV2, TpStudyDirectory>
    implements TpStudyDirectoryService {

    @Autowired
    private TpStudyDirectoryMapperV2 tpStudyDirectoryMapper;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 查询最大排序值
     *
     * @param trainingProjectId
     * @return
     */
    @Override
    public Integer getMaxSort(Long trainingProjectId) {
        Integer maxSort = tpStudyDirectoryMapper.getMaxSort(trainingProjectId);
        return maxSort == null ? 0 : maxSort;
    }

    /**
     * 判断是否存在相同的类型
     *
     * @param trainingProjectId
     * @param itemType
     * @return
     */
    @Override
    public Boolean existSameType(Long trainingProjectId, Integer itemType) {
        QueryWrapper<TpStudyDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("item_type", itemType);
        wrapper.eq("deleted", 0);
        int count = (int)count(wrapper);
        return count > 0;
    }

    /**
     * 查询学习页目录
     *
     * @param trainingProjectId
     * @return
     */
    @Override
    public List<TpStudyDirectory> getStudyDirectory(Long trainingProjectId) {
        QueryWrapper<TpStudyDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("deleted", 0);
        wrapper.orderByAsc("sort");
        return list(wrapper);
    }

    /**
     * 查询单个目录项的id
     *
     * @param id
     * @return
     */
    @Override
    public Integer getSort(Long id) {
        QueryWrapper<TpStudyDirectory> wrapper = new QueryWrapper<>();
        wrapper.select("sort");
        wrapper.eq("id", id);
        TpStudyDirectory directory = getOne(wrapper);
        return directory == null ? 0 : directory.getSort();
    }

    /**
     * @param trainingProjectId
     * @param sort
     * @return
     */
    @Override
    public Boolean addSortValue(Long trainingProjectId, Integer sort) {
        return tpStudyDirectoryMapper.updateSortValue(trainingProjectId, sort);
    }

    /**
     * 是否存在默认的学习单元目录项
     *
     * @param tpId
     * @return
     */
    @Override
    public Integer getStudyUnitCount(Long tpId) {
        QueryWrapper<TpStudyDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("item_type", TpDirectoryItemTypeEnum.STUDY_UNIT.getCode());
        List<TpStudyDirectory> list = list(wrapper);
        Integer cnt = (int)count(wrapper);
        return cnt;
    }

    /**
     * 逻辑删除
     *
     * @param tpIds
     * @return
     */
    @Override
    public Boolean deleteBatchByTpIds(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return false;
        }
        return tpStudyDirectoryMapper.deleteBatchByTpIds(tpIds);
    }

    @Override
    public Boolean deleteByItemId(Long trainingProjectId, Long directoryItemId) {
        return tpStudyDirectoryMapper.deleteByItemId(trainingProjectId, directoryItemId);

    }

    @Override
    public List<Integer> getCanNotAddType(Long trainingProjectId) {
        QueryWrapper<TpStudyDirectory> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.notIn("item_type", TpDirectoryItemTypeEnum.canBeAddRepeatedlyTypes);
        wrapper.eq("deleted", 0);
        List<TpStudyDirectory> list = list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(TpStudyDirectory::getItemType).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> copyByTp(Long oldTpId, Long newTpId) {
        List<TpStudyDirectory> oldStudyDirItems = getStudyDirectory(oldTpId);
        Map<Long, Long> dirIdOldToNewMap = new HashMap<>();
        List<TpStudyDirectory> newStudyDirItems =
            BeanCopyListUtil.copyListProperties(oldStudyDirItems, TpStudyDirectory::new, (s, t) -> {
                t.setId(idGenerator.generate());
                t.setTrainingProjectId(newTpId);
                dirIdOldToNewMap.put(s.getId(), t.getId());
            });
        if (CollectionUtils.isNotEmpty(newStudyDirItems)) {
            saveBatch(newStudyDirItems);
        }
        return dirIdOldToNewMap;
    }
}
