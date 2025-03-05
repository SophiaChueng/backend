package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanActivity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TpPlanActivityMapperV2 extends BaseMapper<TpPlanActivity> {

    Integer getMaxSort(@Param("tpPlanId") Long tpPlanId);

    Integer getTpPlanCount(@Param("trainingProjectId") Long trainingProjectId);

    Boolean deleteBatch(@Param("ids") List<Long> ids, @Param("accountId") Long accountId,
        @Param("accountName") String accountName, @Param("now") Date now);

    Boolean deleteBatchByTpPlan(@Param("tpPlanIds") List<Long> tpPlanIds);
}
