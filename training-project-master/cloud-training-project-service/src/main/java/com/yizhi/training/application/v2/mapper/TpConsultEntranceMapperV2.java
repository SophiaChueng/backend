package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpConsultEntrance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpConsultEntranceMapperV2 extends BaseMapper<TpConsultEntrance> {

    Boolean deleteBatchByTpIds(@Param("tpIds") List<Long> tpIds);

    Boolean deleteByItemId(@Param("trainingProjectId") Long trainingProjectId,
        @Param("directoryItemId") Long directoryItemId);
}
