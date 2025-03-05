package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.vo.manage.TpPlanFinishedListVo;
import com.yizhi.training.application.vo.manage.TpPlanFinishedVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 培训项目 - 学习计划 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TpPlanMapper extends BaseMapper<TpPlan> {

    /**
     * 根据id批量删除
     *
     * @param ids
     * @param accountId
     * @param accountName
     * @param now
     * @return
     */
    Integer deleteByIds(@Param("ids") List<Long> ids, @Param("accountId") Long accountId,
        @Param("accountName") String accountName, @Param("now") Date now);

    /**
     * 根据培训项目id获取主键
     *
     * @param tpIds
     * @return
     */
    List<Long> getIdsByTpIds(@Param("tpIds") List<Long> tpIds);

    /**
     * 根据培训项目id获取主键
     *
     * @param tpId
     * @return
     */
    List<Long> getIdsByTpId(@Param("tpId") Long tpId);

    /**
     * 根据活动id查询计划id
     *
     * @param activityId
     * @param siteId
     * @return
     */
    List<Long> getIdsByActivityId(@Param("activityId") Long activityId, @Param("siteId") Long siteId,
        @Param("now") Date now);

    /**
     * 根据计划id获取培训项目id
     *
     * @param planIds
     * @return
     */
    List<Long> getTpIdsByPlanIds(@Param("planIds") Set<Long> planIds);

    /**
     * 获取计划下的活动完成数
     *
     * @param accountId
     * @param planId
     * @return
     */
    Integer getFinishedActivityCountByPlanId(@Param("accountId") Long accountId, @Param("planId") Long planId);

    /**
     * 获取名称
     *
     * @param ids       in
     * @param besideIds not in
     * @return
     */
    List<String> getNames(@Param("ids") List<Long> ids, @Param("besideIds") List<Long> besideIds);

    List<TpPlanFinishedListVo> finishedList(@Param("projectId") Long projectId, @Param("siteId") Long siteId,
        @Param("companyId") Long companyId, @Param("accountId") Long accountId, Page<TpPlanFinishedListVo> page);

    TpPlanFinishedVo geTpPlan(Long tpPlanId);

    //    /**
    //     * 获取计划的前置计划名称
    //     *
    //     * @param planId
    //     * @param finishedPlanIds 去除已经完成的计划 （可不传）
    //     * @return
    //     */
    //    List<String> getPrePlanNames(@Param("planId") Long planId, @Param("finishedPlanIds") List<Long>
    //    finishedPlanIds);

    TpPlan getSort(@Param("id") Long id, @Param("trainingId") Long trainingId, @Param("siteId") Long siteId,
        @Param("type") Integer type);

}
