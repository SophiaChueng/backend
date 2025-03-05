package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanConditionPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpPlanConditionPostMapperV2 extends BaseMapper<TpPlanConditionPost> {

    Integer deleteBatchByTpPlanId(@Param("tpPlanIds") List<Long> tpPlanIds);
}
