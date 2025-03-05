package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.vo.api.TpStudentProjectRecordVoVO;
import com.yizhi.training.application.vo.manage.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 培训项目 - 学习计划 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@RestController
@RequestMapping("/tpPlan")
public class TpPlanController {

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private ITpStudentProjectRecordService studentProjectRecordService;

    /**
     * 新增培训计划（包括计划中的活动和条件等）
     *
     * @param model
     * @return
     */
    @PostMapping("/save")
    public com.yizhi.training.application.vo.domain.TpPlanVo save(@RequestBody BaseModel<TpPlanVo> model)
        throws Exception {
        return tpPlanService.save(model);
    }

    /**
     * 查询培训项目下的所有培训计划
     *
     * @param trainingProjectId
     * @return
     */
    @GetMapping("/all/list")
    public List<TpPlan> listAll(@RequestParam("trainingProjectId") Long trainingProjectId) {
        return tpPlanService.listAll(trainingProjectId);
    }

    /**
     * 更新排序
     *
     * @param list
     * @return
     */
    @PostMapping("/sort/update")
    public Integer updateSort(@RequestBody List<com.yizhi.training.application.vo.domain.TpPlanVo> list) {
        List<TpPlan> list1 = new ArrayList<>();
        for (com.yizhi.training.application.vo.domain.TpPlanVo t : list) {
            TpPlan tpPlan = new TpPlan();
            BeanUtils.copyProperties(t, tpPlan);
            list1.add(tpPlan);
        }
        return tpPlanService.updateSort(list1);
    }

    /**
     * 修改培训计划
     *
     * @param model
     * @return
     */
    @PostMapping("/update")
    public com.yizhi.training.application.vo.domain.TpPlanVo update(@RequestBody BaseModel<TpPlanUpdateVo> model)
        throws Exception {
        return tpPlanService.update(model);
    }

    /**
     * 删除前置 或 后置条件
     *
     * @param model 包含的是id
     * @return
     */
    @PostMapping("/condition/delete")
    public Integer deleteConditions(@RequestBody BaseModel<ConditionDeleteVo> model) {
        return tpPlanService.deleteConditions(model);
    }

    @PostMapping("batch/delete")
    public Integer batchDelete(@RequestBody BaseModel<List<Long>> model) {
        return tpPlanService.deleteByIds(model);
    }

    /**
     * 查看培训计划的内容（包括提醒、前置计划、完成条件）
     *
     * @param planId
     * @return
     */
    @GetMapping("detail/view")
    public com.yizhi.training.application.vo.domain.TpPlanVo viewDetail(@RequestParam("planId") Long planId) {
        TpPlan plan = tpPlanService.viewDetail(planId);
        com.yizhi.training.application.vo.domain.TpPlanVo p = new com.yizhi.training.application.vo.domain.TpPlanVo();
        BeanUtils.copyProperties(plan, p);
        return p;
    }

    /**
     * 根据计划id清空计划活动（包括条件等）
     *
     * @param model
     * @return
     */
    @PostMapping("/truncate/activity")
    public Integer truncateActivity(@RequestBody BaseModel<Long> model) {
        return tpPlanService.truncateActivity(model);
    }

    /**
     * 任务完成情况API获取计划列表
     *
     * @param projectId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/all/finishedList")
    public Page<TpPlanFinishedListVo> finishedList(@RequestParam("projectId") Long projectId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {
        return tpPlanService.finishedList(projectId, pageNo, pageSize);
    }

    /**
     * 任务完成情况计划关联活动信息
     *
     * @param projectId
     * @param tpPlanId
     * @return
     */
    @GetMapping("/getTpPlanActivity")
    public TpPlanFinishedVo getTpPlanActivity(@RequestParam("projectId") Long projectId,
        @RequestParam("tpPlanId") Long tpPlanId) {
        return tpPlanService.getTpPlanActivity(projectId, tpPlanId);
    }

    @GetMapping("/aia/getTpPlanActivity")
    public TpPlanFinishedVo getTpPlanActivityAia(@RequestParam("projectId") Long projectId,
        @RequestParam("tpPlanId") Long tpPlanId) {
        return tpPlanService.getTpPlanActivityAia(projectId, tpPlanId);
    }

    @GetMapping("/getOne")
    public com.yizhi.training.application.vo.domain.TpPlanVo getOne(@RequestParam("tpPlanId") Long tpPlanId) {
        TpPlan plan = tpPlanService.getById(tpPlanId);
        com.yizhi.training.application.vo.domain.TpPlanVo p = new com.yizhi.training.application.vo.domain.TpPlanVo();
        BeanUtils.copyProperties(plan, p);
        return p;
    }

    /**
     * type；1-上移  2-下移 必填
     */
    @GetMapping("/move")
    public Integer move(@RequestParam("type") Integer type, @RequestParam("id") Long id) {
        return tpPlanService.move(type, id);
    }

    /**
     * 任务完成情况点击活动
     *
     * @param activityId
     * @param tpPlanId
     * @return
     */
    @GetMapping("/clickActivity")
    Map<String, Object> ClickActivity(@RequestParam("activityId") Long activityId,
        @RequestParam("tpPlanId") Long tpPlanId) {
        return tpPlanService.ClickActivity(activityId, tpPlanId);
    }

    /**
     * 复旦mini mba项目获取项目完成情况
     *
     * @param projectIds
     * @return
     */
    @PostMapping("/getProjectsStatus")
    List<TpStudentProjectRecordVoVO> getProjectsStatus(@RequestBody List<Long> projectIds) {
        return studentProjectRecordService.getProjectsStatus(projectIds);
    }

    /**
     * 复旦mini mba项目获取正在学习人数
     *
     * @param projectIds
     * @return
     */
    @PostMapping("/getProjectsStudyingNum")
    Integer getProjectsStudyingNum(@RequestBody List<Long> projectIds) {
        return studentProjectRecordService.getProjectsStudyingNum(projectIds);
    }

    /**
     * 复旦mini mba项目获取学习记录
     *
     * @param projectIds
     * @return
     */
    @PostMapping("/getProjectsStudyingRecords")
    List<TpStudentProjectRecordVoVO> getProjectsStudyingRecords(@RequestBody List<Long> projectIds) {
        return studentProjectRecordService.getProjectsStudyingRecords(projectIds);
    }

}

