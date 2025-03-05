package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.vo.api.TpStudentProjectRecordVoVO;
import com.yizhi.training.application.vo.manage.TpPlanFinishedListVo;
import com.yizhi.training.application.vo.manage.TpPlanFinishedVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author Ding
 * @className TpPlanFinishedApiClient
 * @description 自定义项目, 项目/计划 完成情况
 * @date 2018/12/4
 **/
@FeignClient(name = "trainingProject", contextId = "TpPlanFinishedApiClient")
public interface TpPlanFinishedApiClient {

    /**
     * 任务完成情况API获取计划列表
     *
     * @param projectId
     * @return
     */
    @GetMapping("/tpPlan/all/finishedList")
    Page<TpPlanFinishedListVo> tpPlanList(@RequestParam("projectId") Long projectId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize);

    /**
     * 任务完成情况计划信息
     *
     * @param projectId
     * @param tpPlanId
     * @return
     */
    @GetMapping("/tpPlan/getTpPlanActivity")
    TpPlanFinishedVo getTpPlanActivity(@RequestParam("projectId") Long projectId,
        @RequestParam("tpPlanId") Long tpPlanId);

    @GetMapping("/tpPlan/aia/getTpPlanActivity")
    TpPlanFinishedVo getTpPlanActivityAia(@RequestParam("projectId") Long projectId,
        @RequestParam("tpPlanId") Long tpPlanId);

    /**
     * 任务完成情况点击活动
     *
     * @param activityId
     * @param tpPlanId
     * @return
     */
    @GetMapping("/tpPlan/clickActivity")
    Map<String, Object> ClickActivity(@RequestParam("activityId") Long activityId,
        @RequestParam("tpPlanId") Long tpPlanId);

    /**
     * 复旦mini mba项目获取项目完成情况
     *
     * @param projectIds
     * @return
     */
    @PostMapping("/tpPlan/getProjectsStatus")
    List<TpStudentProjectRecordVoVO> getProjectsStatus(@RequestBody List<Long> projectIds);

    /**
     * 复旦mini mba项目获取正在学习人数
     *
     * @param projectIds
     * @return
     */
    @PostMapping("/tpPlan/getProjectsStudyingNum")
    Integer getProjectsStudyingNum(@RequestBody List<Long> projectIds);

    /**
     * 复旦mini mba项目获取学习记录
     *
     * @param projectIds
     * @return
     */
    @PostMapping("/tpPlan/getProjectsStudyingRecords")
    List<TpStudentProjectRecordVoVO> getProjectsStudyingRecords(@RequestBody List<Long> projectIds);
}
