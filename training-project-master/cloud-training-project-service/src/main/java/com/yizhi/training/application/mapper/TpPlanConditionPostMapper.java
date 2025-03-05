package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanConditionPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学习计划完成条件 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface TpPlanConditionPostMapper extends BaseMapper<TpPlanConditionPost> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpPlanConditionPost> list);

    /**
     * 根据学习计划id集合删除相应条件
     *
     * @param planIds
     * @return
     */
    Integer deleteByPlanIds(@Param("list") List<Long> planIds);

}
