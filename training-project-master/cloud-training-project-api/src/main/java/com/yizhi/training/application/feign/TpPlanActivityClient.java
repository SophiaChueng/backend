package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.training.application.constant.CertificateGrantStatus;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.vo.CourseProjectNameSyncVO;
import com.yizhi.training.application.vo.domain.CourseRelateProjectVO;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.manage.ConditionDeleteVo;
import com.yizhi.training.application.vo.manage.TpPlanActivityConditionUpdateVo;
import com.yizhi.training.application.vo.manage.TpPlanActivitySingleVo;
import com.yizhi.util.application.enums.i18n.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 20:19
 */
@FeignClient(name = "trainingProject", contextId = "TpPlanActivityClient")
public interface TpPlanActivityClient {

    /**
     * 更新排序
     *
     * @param list
     * @return
     */
    @PostMapping("/tpPlanActivity/sort/update")
    Integer updateSort(@RequestBody List<TpPlanActivityVo> list);

    /*
     * 通过 作业id去查询相应的 计划内容
     * */
    @PostMapping("/tpPlanActivity/tpPlan/list")
    List<Long> allListByAssignmentIds(@RequestBody List<TpPlanActivityVo> tpPlanActivityVos);

    /**
     * 查询培训活动列表
     *
     * @param tpPlanId 所属培训计划id
     * @return
     */
    @GetMapping("/tpPlanActivity/all/list")
    List<TpPlanActivityVo> allList(@RequestParam("tpPlanId") Long tpPlanId, @RequestParam("name") String name,
        @RequestParam("type") Integer type);

    /**
     * 查询培训活动列表
     *
     * @param tpId 所属培训项目id
     * @return
     */
    @GetMapping("/tpPlanActivity/tp/all/list")
    List<TpPlanActivityVo> allListByTpId(@RequestParam("tpId") Long tpId);

    /**
     * 编辑培训活动开启、完成条件
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/tpPlanActivity/condition/edit")
    Integer conditionEdit(@RequestBody BaseModel<TpPlanActivityConditionUpdateVo> model);

    /**
     * 在修改培训计划时，若有移除培训活动，使用该接口
     *
     * @param model
     * @return
     */
    @PostMapping("/tpPlanActivity/batch/delete")
    Integer batchDelete(@RequestBody BaseModel<List<Long>> model);

    /**
     * 修改培训计划时，单个添加学习活动
     *
     * @param model
     * @return
     */
    @PostMapping("/tpPlanActivity/save")
    Integer addOne(@RequestBody BaseModel<List<TpPlanActivitySingleVo>> model);

    /**
     * 删除前置 或 后置条件
     *
     * @param model 包含的是id
     * @return
     */
    @PostMapping("/tpPlanActivity/condition/delete")
    Integer deleteConditions(@RequestBody BaseModel<ConditionDeleteVo> model);

    @GetMapping("/tpPlanActivity/get")
    TpPlanActivityVo getOne(@RequestParam("id") Long id);

    /**
     * 检查业务是否在培训项目中存在
     *
     * @param relationIds
     * @return 存在的业务id数组
     */
    @Deprecated
    @GetMapping("/tpPlanActivity/biz/exist/check/")
    List<Long> checkBizIsExistInTp(@RequestBody List<Long> relationIds);

    @GetMapping("/tpPlanActivity/biz/exist/names/check")
    Set<String> checkBizIsExistInTpNames(@RequestBody List<Long> relationIds);

    /**
     * 添加一条活动浏览记录
     *
     * @param model
     * @return
     */
    @PostMapping("/tpPlanActivity/view/record/add")
    Integer addViewRecord(@RequestBody BaseModel<Long> model);

    /**
     * 点击发放证书
     *
     * @param param
     * @return
     */
    @PostMapping("/tpPlanActivity/certificate/grant")
    CertificateGrantStatus certificateGrant(@RequestBody Map<String, Long> param);

    /**
     * 点击发放证书  (  PC端  )
     *
     * @param param
     * @return
     */
    @PostMapping("/tpPlanActivity/pc/certificate/grant")
    Constants certificatePcGrant(@RequestBody Map<String, Long> param);

    /**
     * 获取培训项目下的活动个数
     *
     * @param id 培训id
     * @return
     */
    @GetMapping("/tpPlanActivity/get/courseIds")
    List<Long> getActiveCountById(@RequestParam(name = "id") Long id);

    @ApiOperation(value = "获取培训项目下的活动个数 v2 版本  修改：活动数去掉证书")
    @GetMapping("/tpPlanActivity/get/v2/courseIds")
    public List<Long> getExcCertiferActiveCountById(@RequestParam(name = "id") Long id);

    @GetMapping("/tpPlanActivity/activitiesExport")
    public String activitiesExport(@RequestBody Map<String, Object> map);

    /**
     * 检查业务是否被培训项目关联（不存在或者包含该业务所在计划被删除了或者包含该业务所在培训项目下架或删除啦）
     *
     * @param bizType    活动类型：0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程  11：案例活动、12：精选案例 、13：论坛帖子
     * @param relationId 业务id
     * @return
     */
    @GetMapping("/tpPlanActivity/biz/canDown")
    public Boolean checkBizCanDown(@RequestParam("bizType") Integer bizType,
        @RequestParam("relationId") Long relationId);

    /**
     * 查询课程关联的项目列表
     *
     * @param courseId 课程id
     * @return
     */
    @GetMapping("/tpPlanActivity/course/relate/project/list")
    Page<CourseRelateProjectVO> courseRelateProjectList(@RequestParam("courseId") Long courseId,
        @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize);

    /**
     * 进行名称同步
     *
     * @param courseProjectNameSyncVO
     * @return
     */
    @PostMapping("/tpPlanActivity/course/name/sync")
    Boolean courseProjectNameSync(@RequestBody CourseProjectNameSyncVO courseProjectNameSyncVO);

    /**
     * 获取学员的活动完成记录
     */
    @PostMapping("/tpPlanActivity/finished/list")
    List<BaseViewRecordVO> getFinishedActivityList(@RequestBody BaseParamVO baseParamVO);



    @GetMapping("/tpPlanActivity/biz/exist/names/check/related")
    Set<String> checkExistRelatedProject(@RequestParam(name = "id") Long id,@RequestParam(name = "type") Integer type);
}
