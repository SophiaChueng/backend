package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanStudyTimeCondition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpPlanStudyTimeConditionMapperV2 extends BaseMapper<TpPlanStudyTimeCondition> {

    Boolean deleteBatchByTpPlanId(@Param("tpPlanIds") List<Long> tpPlanIds);
}
