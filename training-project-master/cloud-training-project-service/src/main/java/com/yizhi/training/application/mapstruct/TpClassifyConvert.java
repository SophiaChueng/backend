package com.yizhi.training.application.mapstruct;

import com.yizhi.core.application.classify.vo.ClassifyDetailSimpleVO;
import com.yizhi.training.application.domain.TpClassification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author : Gxr
 * @since 2024-05-07 18:32
 */
@Mapper
public interface TpClassifyConvert {

    TpClassifyConvert INSTANCE = Mappers.getMapper(TpClassifyConvert.class);

    @Mapping(source = "id", target = "classifyId")
    @Mapping(source = "name", target = "classifyName")
    ClassifyDetailSimpleVO do2vo(TpClassification src);

    List<ClassifyDetailSimpleVO> do2vo(List<TpClassification> src);
}
