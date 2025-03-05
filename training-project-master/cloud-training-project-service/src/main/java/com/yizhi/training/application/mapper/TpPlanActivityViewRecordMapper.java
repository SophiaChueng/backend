package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpPlanActivityViewRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
public interface TpPlanActivityViewRecordMapper extends BaseMapper<TpPlanActivityViewRecord> {

    /**
     * 根据 项目 id 查询已经点击的活动 activityId
     *
     * @param tpId
     * @param context
     * @return
     */
    Set<Long> getClickedRelationIdByTpId(@Param("tpId") Long tpId, @Param("context") RequestContext context);

}
