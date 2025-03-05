package com.yizhi.training.application.mapstruct;

import com.yizhi.training.application.domain.ExchangeCode;
import com.yizhi.training.application.vo.ExchangeCodeExportVO;
import com.yizhi.training.application.vo.ExchangeCodeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExchangeCodeVOMapper {

    ExchangeCodeVOMapper INSTANCE = Mappers.getMapper(ExchangeCodeVOMapper.class);

    ExchangeCodeVO do2vo(ExchangeCode source);

    ExchangeCodeExportVO do2export(ExchangeCode source);
}
