package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpAnnouncement;
import org.apache.ibatis.annotations.Param;

public interface TpAnnouncementMapperV2 extends BaseMapper<TpAnnouncement> {

    /**
     * 查询项目下公告的最大sort值
     *
     * @param trainingProjectId
     * @return
     */
    Integer getMaxSort(@Param("trainingProjectId") Long trainingProjectId);

    /**
     * 拖动排序 将项目中sort值大于等于${sort}的公告的sort加一
     *
     * @param trainingProjectId
     * @param sort
     * @return
     */
    Boolean updateSortValue(@Param("trainingProjectId") Long trainingProjectId, @Param("sort") Integer sort);
}
