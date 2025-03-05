package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpEnroll;
import org.apache.ibatis.annotations.Param;

public interface TpEnrollMapperV2 extends BaseMapper<TpEnroll> {

    Integer getEnrollUserCount(@Param("tpId") Long tpId, @Param("needAudit") Integer needAudit);

    Integer getEnrollStatus(@Param("tpId") Long tpId, @Param("accountId") Long accountId);
}
