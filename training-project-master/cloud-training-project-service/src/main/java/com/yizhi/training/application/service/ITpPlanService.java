package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.exception.TrainingProjectException;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.vo.manage.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 培训项目 - 学习计划 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpPlanService extends IService<TpPlan> {

    @Override
    default TpPlan getOne(Wrapper<TpPlan> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 查询培训项目下的所有培训计划
     *
     * @param trainingProjectId
     * @return
     */
    List<TpPlan> listAll(Long trainingProjectId);

    /**
     * 更新排序
     *
     * @param list
     * @return
     */
    Integer updateSort(List<TpPlan> list);

    /**
     * 新增培训计划（包括计划中的活动和条件等）
     *
     * @param model
     * @return
     */
    com.yizhi.training.application.vo.domain.TpPlanVo save(BaseModel<TpPlanVo> model) throws TrainingProjectException;

    /**
     * 修改培训计划
     *
     * @param model
     * @return
     */
    com.yizhi.training.application.vo.domain.TpPlanVo update(BaseModel<TpPlanUpdateVo> model)
        throws TrainingProjectException;

    /**
     * 删除条件
     *
     * @param model
     * @return
     */
    Integer deleteConditions(BaseModel<ConditionDeleteVo> model);

    /**
     * 根据id删除
     *
     * @param model
     * @return
     */
    Integer deleteByIds(BaseModel<List<Long>> model);

    /**
     * 查看一个计划的细节
     *
     * @param planId
     * @return
     */
    TpPlan viewDetail(Long planId);

    /**
     * 根据计划id清空计划活动（包括条件等）
     *
     * @param model
     * @return
     */
    Integer truncateActivity(BaseModel<Long> model);

    /**
     * 任务完成情况API获取计划列表
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<TpPlanFinishedListVo> finishedList(Long projectId, Integer pageNo, Integer pageSize);

    /**
     * 任务完成情况计划关联活动信息
     *
     * @param projectId
     * @param tpPlanId
     * @return
     */
    TpPlanFinishedVo getTpPlanActivity(Long projectId, Long tpPlanId);

    /**
     * 友邦电子书
     *
     * @param projectId
     * @param tpPlanId
     * @return
     */
    TpPlanFinishedVo getTpPlanActivityAia(Long projectId, Long tpPlanId);

    /**
     * 任务完成情况点击活动
     *
     * @param activityId
     * @param tpPlanId
     * @return
     */
    Map<String, Object> ClickActivity(Long activityId, Long tpPlanId);

    /**
     * 报表需要所有的计划包括删掉的计划
     *
     * @param tpId
     * @return
     */
    List<TpPlan> getListByStatistics(Long tpId);

    /**
     * 报表需要的所有的计划包括删掉的计划 -- 通过siteId列表查询
     *
     * @param siteIds
     * @return
     */
    List<TpPlan> getListBySiteIds(List<Long> siteIds);

    /**
     * 案例库获取培训项目信息
     *
     * @param idList
     * @return
     */
    Map<Long, TrainingProject> getCaseLibraryProject(List<Long> idList);

    Integer move(Integer type, Long id);
}
