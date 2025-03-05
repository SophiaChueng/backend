package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpAnnouncement;

import java.util.List;
import java.util.Map;

public interface TpAnnouncementService extends IService<TpAnnouncement> {

    @Override
    default TpAnnouncement getOne(Wrapper<TpAnnouncement> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 查询项目的公告数量
     *
     * @param trainingProjectId
     * @return
     */
    Integer getAnnouncementCount(Long trainingProjectId);

    /**
     * 分页查询公告
     *
     * @param trainingProjectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<TpAnnouncement> getAnnouncements(Long trainingProjectId, Integer pageNo, Integer pageSize);

    Integer getMaxSort(Long trainingProjectId);

    Integer getSort(Long id);

    Boolean addSortValue(Long trainingProjectId, int sort);

    void copyByTp(Long oldTpId, Long newTpId, Map<Long, Long> studyDirIdOldToNewMap);
}
