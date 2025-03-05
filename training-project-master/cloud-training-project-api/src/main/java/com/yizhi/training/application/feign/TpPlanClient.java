package com.yizhi.training.application.feign;

import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.vo.domain.TpPlanVo;
import com.yizhi.training.application.vo.manage.ConditionDeleteVo;
import com.yizhi.training.application.vo.manage.TpPlanUpdateVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 16:03
 */
@FeignClient(name = "trainingProject", contextId = "TpPlanClient")
public interface TpPlanClient {

    /**
     * 查询培训项目下的所有培训计划
     *
     * @param trainingProjectId
     * @return
     */
    @GetMapping("/tpPlan/all/list")
    List<TpPlanVo> listAll(@RequestParam("trainingProjectId") Long trainingProjectId);

    /**
     * 更新排序
     *
     * @param list
     * @return
     */
    @PostMapping("/tpPlan/sort/update")
    Integer updateSort(@RequestBody List<TpPlanVo> list);

    /**
     * 新增培训计划（包括计划中的活动和条件等）
     *
     * @param model
     * @return
     */
    @PostMapping("/tpPlan/save")
    TpPlanVo save(@RequestBody BaseModel<com.yizhi.training.application.vo.manage.TpPlanVo> model);

    /**
     * 修改培训计划
     *
     * @param model
     * @return
     */
    @PostMapping("/tpPlan/update")
    TpPlanVo update(@RequestBody BaseModel<TpPlanUpdateVo> model);

    /**
     * 删除前置 或 后置条件
     *
     * @param model 包含的是id
     * @return
     */
    @PostMapping("/tpPlan/condition/delete")
    Integer deleteConditions(@RequestBody BaseModel<ConditionDeleteVo> model);

    /**
     * 根据id批量删除
     *
     * @param ids
     * @return
     */
    @PostMapping("/tpPlan/batch/delete")
    Integer batchDelete(@RequestBody BaseModel<List<Long>> ids);

    /**
     * 查看培训计划的内容（包括提醒、前置计划、完成条件）
     *
     * @param planId
     * @return
     */
    @GetMapping("/tpPlan/detail/view")
    TpPlanVo viewDetail(@RequestParam("planId") Long planId);

    /**
     * 根据计划id清空计划活动（包括条件等）
     *
     * @param model
     * @return
     */
    @PostMapping("/tpPlan/truncate/activity")
    Integer truncateActivity(@RequestBody BaseModel<Long> model);

    /**
     * 获取计划实体
     *
     * @param tpPlanId
     * @return
     */
    @GetMapping("/tpPlan/getOne")
    public TpPlanVo getOne(@RequestParam("tpPlanId") Long tpPlanId);

    /**
     * type；1-上移  2-下移 必填
     */
    @GetMapping("/tpPlan/move")
    public Integer move(@RequestParam("type") Integer type, @RequestParam("id") Long id);

}
