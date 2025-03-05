package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpPlanActivityConditionPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 学习活动（考试、证书）完成条件 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
public interface TpPlanActivityConditionPostMapper extends BaseMapper<TpPlanActivityConditionPost> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpPlanActivityConditionPost> list);

    /**
     * 根据活动id集合查询活动完成条件
     *
     * @param activityIds
     * @return
     */
    List<TpPlanActivityConditionPost> selectListByActivityIds(@Param("list") List<Long> activityIds);

    /**
     * 根据活动id删除完成条件
     *
     * @param activityIds
     * @return
     */
    Integer deleteByActivityIds(@Param("activityIds") List<Long> activityIds);
}
