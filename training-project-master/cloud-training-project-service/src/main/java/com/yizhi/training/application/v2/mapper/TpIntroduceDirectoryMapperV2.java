package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpIntroduceDirectory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpIntroduceDirectoryMapperV2 extends BaseMapper<TpIntroduceDirectory> {

    Integer getMaxSort(@Param("trainingProjectId") Long trainingProjectId);

    Boolean updateSortValue(@Param("trainingProjectId") Long trainingProjectId, @Param("sort") Integer sort);

    Boolean deleteBatchByTpIds(@Param("tpIds") List<Long> tpIds);

    Boolean deleteByStudyItem(@Param("trainingProjectId") Long trainingProjectId,
        @Param("studyDirItemId") Long studyDirItemId);

    Boolean deleteByItemId(@Param("trainingProjectId") Long trainingProjectId,
        @Param("directoryItemId") Long directoryItemId);
}
