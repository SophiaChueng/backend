package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanActivityConditionPre;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学习活动前置条件 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface TpPlanActivityConditionPreMapper extends BaseMapper<TpPlanActivityConditionPre> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpPlanActivityConditionPre> list);

    /**
     * 根据活动id集合查询活动开启条件
     *
     * @param activityIds
     * @return
     */
    List<TpPlanActivityConditionPre> selectListByActivityIds(@Param("list") List<Long> activityIds);

    /**
     * 根据活动id删除开启条件
     *
     * @param activityIds
     * @return
     */
    Integer deleteByActivityIds(@Param("activityIds") List<Long> activityIds);

}
