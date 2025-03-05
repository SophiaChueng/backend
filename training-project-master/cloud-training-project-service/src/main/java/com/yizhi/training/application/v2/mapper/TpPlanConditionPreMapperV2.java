package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpPlanConditionPre;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpPlanConditionPreMapperV2 extends BaseMapper<TpPlanConditionPre> {

    Integer deleteBatchByTpPlanId(@Param("tpPlanIds") List<Long> tpPlanIds);

    List<TpPlan> getPrePlans(@Param("tpPlanId") Long tpPlanId);
}
