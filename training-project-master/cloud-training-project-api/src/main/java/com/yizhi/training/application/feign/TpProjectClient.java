package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.v2.vo.request.OnLineTpVO;
import com.yizhi.training.application.vo.StuMemberResourceTpVo;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.training.application.vo.fo.TrainingProjectFoVo;
import com.yizhi.training.application.vo.manage.SearchProjectVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/23 22:20
 */
@FeignClient(name = "trainingProject", contextId = "TpProjectClient")
public interface TpProjectClient {

    @GetMapping(value = "/trainingProject/list")
    Page<TrainingProjectVo> list(@RequestBody SearchProjectVo searchProjectVo);

    /**
     * 查询项目列表  版本2 对比版本1 新增筛选条件查询：无需报名；免费报名；收费报名
     *
     * @param searchProjectVo 入参
     */
    @GetMapping(value = "/trainingProject/list/v2")
    Page<TrainingProjectVo> listV2(@RequestBody SearchProjectVo searchProjectVo);

    @PostMapping("/trainingProject/up")
    Integer up(@RequestBody Map<String, Long> map);

    @PostMapping("/trainingProject/down")
    Boolean down(@RequestBody Map<String, Long> map);

    /**
     * 王飞达
     *
     * @param searchProjectVo
     * @return
     */
    @GetMapping(value = "/trainingProject/list/notIds")
    Page<TrainingProjectVo> listNotIds(@RequestBody SearchProjectVo searchProjectVo);

    /**
     * 自定义项目h5接口培训项目验证用户权限
     *
     * @return
     */
    @GetMapping("/trainingProject/getCustomProjectRange")
    Integer getCustomProjectRange(@RequestParam("projectId") Long projectId);

    /**
     * 案例库获取培训项目信息
     *
     * @param idList
     * @return
     * @author ding
     */
    @GetMapping("/trainingProject/getCaseLibraryProject")
    Map<Long, TrainingProjectVo> getCaseLibraryProject(@RequestParam("idList") List<Long> idList);

    /**
     * 我的案例获取可见范围id
     *
     * @param res
     * @return
     */
    @PostMapping("/trainingProject/caseLibrary/getRangeProjects")
    List<TrainingProjectVo> getCaseLibraryRangeProjects(@RequestBody RequestContext res);

    @GetMapping({"/fo/trainingProject/list"})
    Page<TrainingProjectFoVo> folist(@RequestBody SearchProjectVo searchProjectVo);

    @GetMapping("/trainingProject/get/project/finish/records")
    TpStudentProjectRecordVo getProjectFinishRecords(@RequestParam("accountId") Long accountId,
        @RequestParam("tpId") Long tpId);

    @GetMapping("/trainingProject/get/plan/finish/records")
    TpStudentPlanRecordVo getPlanFinishRecords(@RequestParam("accountId") Long accountId,
        @RequestParam("planId") Long planId);

    @PostMapping("/v2/student/home/member/online/tp")
    @ApiOperation("在线项目")
    List<StuMemberResourceTpVo> memberOnLineTp(@RequestBody OnLineTpVO tpVO);

}
