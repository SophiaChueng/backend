package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpRichText;
import com.yizhi.training.application.v2.enums.TpDirectoryTypeEnum;
import com.yizhi.training.application.v2.mapper.TpRichTextMapperV2;
import com.yizhi.training.application.v2.service.TpRichTextService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TpRichTextServiceImplV2 extends ServiceImpl<TpRichTextMapperV2, TpRichText> implements TpRichTextService {

    @Autowired
    private TpRichTextMapperV2 tpRichTextMapperV2;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 判断是否已存在富文本
     *
     * @param trainingProjectId
     * @param directoryType
     * @param directoryItemId
     * @return
     */
    @Override
    public Boolean existRichText(Long trainingProjectId, Integer directoryType, Long directoryItemId) {
        QueryWrapper<TpRichText> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("directory_type", directoryType);
        wrapper.eq("directory_item_id", directoryItemId);
        wrapper.eq("deleted", 0);
        return count(wrapper) > 0;
    }

    @Override
    public Boolean updateRichText(TpRichText text) {
        QueryWrapper<TpRichText> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", text.getTrainingProjectId());
        wrapper.eq("directory_type", text.getDirectoryType());
        wrapper.eq("directory_item_id", text.getDirectoryItemId());
        return update(text, wrapper);
    }

    @Override
    public Boolean deleteBatchByTpIds(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return false;
        }
        return tpRichTextMapperV2.deleteBatchByTpIds(tpIds);
    }

    @Override
    public TpRichText getRichText(Long trainingProjectId, Integer directoryType, Long directoryItemId) {
        QueryWrapper<TpRichText> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        if (directoryType != null) {
            wrapper.eq("directory_type", directoryType);
        }
        wrapper.eq("directory_item_id", directoryItemId);
        wrapper.eq("deleted", 0);
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Boolean deleteByItemId(Long trainingProjectId, Integer directoryType, Long directoryItemId) {
        return tpRichTextMapperV2.deleteByItemId(trainingProjectId, directoryType, directoryItemId);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap,
        Map<Long, Long> introDirIdOldToNewMap) {
        QueryWrapper<TpRichText> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", oldTpId);
        wrapper.eq("deleted", 0);
        List<TpRichText> oldList = list(wrapper);
        if (CollectionUtils.isEmpty(oldList)) {
            return;
        }
        List<TpRichText> newList = BeanCopyListUtil.copyListProperties(oldList, TpRichText::new, (s, t) -> {
            t.setId(idGenerator.generate());
            t.setTrainingProjectId(newTpId);
            if (TpDirectoryTypeEnum.STUDY_PAGE.getCode().equals(s.getDirectoryType())) {
                t.setDirectoryItemId(studyDirIdOldToNewMap.get(s.getDirectoryItemId()));
            } else if (TpDirectoryTypeEnum.INTRODUCE_PAGE.getCode().equals(s.getDirectoryType())) {
                t.setDirectoryItemId(introDirIdOldToNewMap.get(s.getDirectoryItemId()));
            }
            t.setCreateById(null);
            t.setCreateByName(null);
            t.setCreateTime(null);
        });
        saveBatch(newList);
    }
}
