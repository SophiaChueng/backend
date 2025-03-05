package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.vo.domain.CourseRelateProjectVO;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.manage.TpPlanFinishedActivityVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 学习计划中的活动 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TpPlanActivityMapper extends BaseMapper<TpPlanActivity> {

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpPlanActivity> list);

    /**
     * 根据id批量查询
     *
     * @param ids
     * @return
     */
    List<TpPlanActivity> selectListByIds(@Param("list") List<Long> ids);

    /**
     * 根据计划id批量删除
     *
     * @param tpPlanIds
     * @param accountId
     * @param accountName
     * @param now
     * @return
     */
    Integer deleteByTpPlanIds(@Param("tpPlanIds") List<Long> tpPlanIds, @Param("accountId") Long accountId,
        @Param("accountName") String accountName, @Param("now") Date now);

    /**
     * 根据主键批量删除
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
     * 根据计划id获取主键
     *
     * @param tpPlanIds
     * @return
     */
    List<Long> getIdsByTpPlanIds(@Param("tpPlanIds") List<Long> tpPlanIds);

    /**
     * 根据计划id获取relationId
     *
     * @param tpPlanIds
     * @return
     */
    List<Long> getRelationIdsByTpPlanIds(@Param("tpPlanIds") List<Long> tpPlanIds);

    /**
     * 根据计划id获取relationId，去除证书
     *
     * @param tpPlanId
     * @return
     */
    List<Long> getRelationIdsByTpPlanId(@Param("tpPlanId") Long tpPlanId);

    /**
     * 检查业务是否在培训项目中存在
     *
     * @param relationIds
     * @return 存在的业务id数组
     */
    List<Long> checkBizIsExistInTp(@Param("ids") List<Long> relationIds);

    Set<String> checkBizIsExistInTpNames(@Param("ids") List<Long> relationIds);

    /**
     * 根据 relationId 获取 活动id
     *
     * @param relationIds
     * @return
     */
    List<Long> getIdsByRelationIds(@Param("relationIds") List<Long> relationIds, @Param("planId") Long planId);

    /**
     * 根据培训项目id查询出活动relationId
     *
     * @param tpId
     * @return
     */
    List<Long> getRelationIdsByTpId(@Param("tpId") Long tpId);

    /**
     * 查出包含 relationId 活动的计划集合
     *
     * @param relationId
     * @return
     */
    Set<Long> getPlanIdsByRelationId(@Param("relationId") Long relationId);

    List<TpPlanFinishedActivityVo> getActivities(@Param("tpPlanId") Long tpPlanId);

    /**
     * 获取所有 relationId
     *
     * @param siteId
     * @param planId
     * @return
     */
    Set<Long> getAllRelationIdsByPlanId(@Param("siteId") Long siteId, @Param("planId") Long planId);

    /**
     * 报表模块根据培训项目id找到几乎和活动关联信息，记得groupBy，一定要groupby
     *
     * @param id
     * @return
     */
    List<TpPlanActivity> listTpPlanActivityByTpId(@Param("id") Long id);

    Integer checkBizCanDown(@Param("bizType") Integer bizType, @Param("relationId") Long relationId);

    /**
     * 课程关联项目列表
     *
     * @param courseId
     * @param page
     * @return
     */
    List<CourseRelateProjectVO> courseRelateProjectList(@Param("courseId") Long courseId,
        Page<CourseRelateProjectVO> page);

    /**
     * 获取项目中的全部课程
     *
     * @param tpIds     项目ids
     * @param companyId 公司id
     * @param siteId    站点id
     * @return
     */
    List<TpPlanActivity> getTpCourseList(@Param("tpIds") List<Long> tpIds, @Param("companyId") Long companyId,
        @Param("siteId") Long siteId);

    List<TpPlanActivityVo> selectBySiteIds(@Param("siteIds") List<Long> siteIds);

    List<TpPlanActivityVo> getAllAiaActivity(@Param("aiaProjectKmhTpIdList") List<Long> aiaProjectKmhTpIdList,
        @Param("siteId") Long siteId);

    Set<String> checkExistRelatedProject(@Param("id") Long id, @Param("type") Integer type);
}
