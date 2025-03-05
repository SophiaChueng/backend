package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.domain.TpAnnouncement;
import com.yizhi.training.application.v2.mapper.TpAnnouncementMapperV2;
import com.yizhi.training.application.v2.service.TpAnnouncementService;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service(value = "")
public class TpAnnouncementServiceImplV2 extends ServiceImpl<TpAnnouncementMapperV2, TpAnnouncement>
    implements TpAnnouncementService {

    @Autowired
    private TpAnnouncementMapperV2 tpAnnouncementMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public Integer getAnnouncementCount(Long trainingProjectId) {
        QueryWrapper<TpAnnouncement> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("deleted", 0);
        return (int)count(wrapper);
    }

    @Override
    public List<TpAnnouncement> getAnnouncements(Long trainingProjectId, Integer pageNo, Integer pageSize) {
        QueryWrapper<TpAnnouncement> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("sort");
        wrapper.last("LIMIT " + (pageNo - 1) * pageSize + "," + pageSize);
        return list(wrapper);
    }

    @Override
    public Integer getMaxSort(Long trainingProjectId) {
        Integer maxSort = tpAnnouncementMapper.getMaxSort(trainingProjectId);
        return maxSort == null ? 0 : maxSort;
    }

    @Override
    public Integer getSort(Long id) {
        QueryWrapper<TpAnnouncement> wrapper = new QueryWrapper<>();
        wrapper.select("sort");
        wrapper.eq("id", id);
        TpAnnouncement tpAnnouncement = getOne(wrapper);
        return tpAnnouncement == null ? 0 : tpAnnouncement.getSort();
    }

    @Override
    public Boolean addSortValue(Long trainingProjectId, int sort) {
        return tpAnnouncementMapper.updateSortValue(trainingProjectId, sort);
    }

    @Override
    public void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap) {
        QueryWrapper<TpAnnouncement> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", oldTpId);
        wrapper.eq("deleted", 0);
        List<TpAnnouncement> oldList = list(wrapper);
        if (CollectionUtils.isEmpty(oldList)) {
            return;
        }
        List<TpAnnouncement> newList = BeanCopyListUtil.copyListProperties(oldList, TpAnnouncement::new, (s, t) -> {
            t.setId(idGenerator.generate());
            t.setTrainingProjectId(newTpId);

            t.setCreateById(null);
            t.setCreateByName(null);
            t.setCreateTime(null);
        });
        saveBatch(newList);
    }
}
