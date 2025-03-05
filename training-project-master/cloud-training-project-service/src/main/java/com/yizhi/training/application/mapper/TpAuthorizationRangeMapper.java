package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 授权范围 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-19
 */
public interface TpAuthorizationRangeMapper extends BaseMapper<TpAuthorizationRange> {

    List<Long> selectBizIdByRelationId(@Param("relationIds") List<Long> relationIds, @Param("siteId") Long siteId);

    Integer batchInsert(@Param("list") List<TpAuthorizationRange> list);

    List<Long> getUsefulIds(@Param("ids") List<Long> ids, @Param("relationIds") List<Long> relationIds,
        @Param("siteId") Long siteId);

    List<TpAuthorizationRange> selectBySiteIds(@Param("siteIds") List<Long> siteIds);
}
