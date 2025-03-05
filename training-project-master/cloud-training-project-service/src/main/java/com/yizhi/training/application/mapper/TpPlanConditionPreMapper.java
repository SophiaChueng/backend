package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanConditionPre;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学习计化前置条件 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface TpPlanConditionPreMapper extends BaseMapper<TpPlanConditionPre> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpPlanConditionPre> list);

    /**
     * 根据学习计划id集合删除相应条件
     *
     * @param planIds
     * @return
     */
    Integer deleteByPlanIds(@Param("list") List<Long> planIds);

    /**
     * 获取计划的前置计划
     *
     * @param planId
     * @return
     */
    List<Long> getPrePlanIdsByPlanId(@Param("planId") Long planId);

}
