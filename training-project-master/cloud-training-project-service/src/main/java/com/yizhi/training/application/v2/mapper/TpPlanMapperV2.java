package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlan;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TpPlanMapperV2 extends BaseMapper<TpPlan> {

    Boolean deleteBatch(@Param("ids") List<Long> ids, @Param("accountId") Long accountId,
        @Param("accountName") String accountName, @Param("now") Date now);

    Integer getMaxSort(@Param("trainingProjectId") Long trainingProjectId,
        @Param("directoryItemId") Long directoryItemId);

    Boolean updateDirectoryItemId(@Param("trainingProjectId") Long trainingProjectId,
        @Param("oldDirectoryItemId") Long oldDirectoryItemId, @Param("directoryItemId") Long directoryItemId);

    List<Long> getTpPlanIdsByItem(@Param("trainingProjectId") Long trainingProjectId,
        @Param("directoryItemId") Long directoryItemId);

    Boolean addTpSort(@Param("trainingProjectId") Long trainingProjectId,
        @Param("directoryItemId") Long directoryItemId, @Param("sort") Integer sort);

    Long getMaxStudyTimePlanId(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("tpId") Long tpId);

    List<TpPlan> getTpPlansOrderByDir(@Param("tpPlanIds") List<Long> tpPlanIds);

    List<TpPlan> getTpPlansByTpOrderByDir(@Param("trainingProjectId") Long trainingProjectId);
}
