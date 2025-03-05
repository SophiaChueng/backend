package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanRemind;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 培训计划提醒 物理删除 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TpPlanRemindMapper extends BaseMapper<TpPlanRemind> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpPlanRemind> list);

    /**
     * 根据培训计划id批量删除
     *
     * @param tpIds
     * @return
     */
    Integer batchDeleteByTpPlanIds(@Param("tpPlanIds") List<Long> tpIds);
}
