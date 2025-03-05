package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpRichText;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpRichTextMapperV2 extends BaseMapper<TpRichText> {

    Boolean deleteBatchByTpIds(@Param("tpIds") List<Long> tpIds);

    Boolean deleteByItemId(@Param("trainingProjectId") Long trainingProjectId,
        @Param("directoryType") Integer directoryType, @Param("directoryItemId") Long directoryItemId);
}
