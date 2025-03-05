package com.yizhi.training.application.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.course.application.vo.CourseVo;
import com.yizhi.training.application.constant.CertificateGrantStatus;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.download.ActivitiesExport;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.TpPlanActivityViewRecordService;
import com.yizhi.training.application.vo.CourseProjectNameSyncVO;
import com.yizhi.training.application.vo.domain.CourseRelateProjectVO;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.manage.ConditionDeleteVo;
import com.yizhi.training.application.vo.manage.TpPlanActivityConditionUpdateVo;
import com.yizhi.training.application.vo.manage.TpPlanActivitySingleVo;
import com.yizhi.util.application.enums.i18n.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 学习计划中的活动 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@RestController
@RequestMapping("/tpPlanActivity")
public class TpPlanActivityController {

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private TpPlanActivityViewRecordService tpPlanActivityViewRecordService;

    @Autowired
    private ActivitiesExport activitiesExport;

    @Autowired
    private CourseClient courseClient;

    /**
     * 更新排序
     *
     * @param list
     * @return
     */
    @PostMapping("/sort/update")
    public Integer updateSort(@RequestBody List<TpPlanActivityVo> list) {
        List<TpPlanActivity> list1 = new ArrayList<>();
        for (TpPlanActivityVo tpc : list) {
            TpPlanActivity t = new TpPlanActivity();
            BeanUtils.copyProperties(tpc, t);
            list1.add(t);
        }
        return tpPlanActivityService.updateSort(list1);
    }

    /**
     * 查询培训活动列表
     *
     * @param tpPlanId 所属培训计划id
     * @param name     模糊查询name
     * @param type     活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7签到 8外部链接
     * @return
     */
    @GetMapping("/all/list")
    public List<TpPlanActivity> allList(@RequestParam("tpPlanId") Long tpPlanId,
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "type", required = false) Integer type) {
        return tpPlanActivityService.allList(tpPlanId, name, type);
    }

    /**
     * 查询培训活动列表
     *
     * @param tpId 所属培训计划id
     * @return
     */
    @GetMapping("/tp/all/list")
    public List<TpPlanActivity> allListByTp(@RequestParam("tpId") Long tpId) {
        return tpPlanActivityService.allListByTp(tpId);
    }

    /**
     * 编辑培训活动开启、完成条件
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/condition/edit")
    public Integer conditionUpdate(@RequestBody BaseModel<TpPlanActivityConditionUpdateVo> model) throws Exception {
        return tpPlanActivityService.conditionEdit(model);
    }

    /**
     * 在修改培训计划时，若有移除培训活动，使用该接口
     *
     * @param model
     * @return
     */
    @PostMapping("/batch/delete")
    public Integer batchDelete(@RequestBody BaseModel<List<Long>> model) {
        return tpPlanActivityService.deleteByIds(model);
    }

    /**
     * 修改培训计划时，添加学习活动
     *
     * @param model
     * @return
     */
    @PostMapping("/save")
    public Integer addOne(@RequestBody BaseModel<List<TpPlanActivitySingleVo>> model) throws Exception {
        return tpPlanActivityService.addActivity(model);
    }

    /**
     * 删除前置 或 后置条件
     *
     * @param model 包含的是id
     * @return
     */
    @PostMapping("/condition/delete")
    public Integer deleteConditions(@RequestBody BaseModel<ConditionDeleteVo> model) {
        return tpPlanActivityService.deleteConditions(model);
    }

    @GetMapping("get")
    public TpPlanActivity getOne(@RequestParam("id") Long id) {
        TpPlanActivity tpPlanActivity = new TpPlanActivity();
        tpPlanActivity.setId(id);
        tpPlanActivity.setDeleted(Config.DEFAULT_MATCH_STACK_LIMIT_SIZE);
        return tpPlanActivityService.getOne(QueryUtil.condition(tpPlanActivity));
    }

    /**
     * 检查业务是否在培训项目中存在
     *
     * @param relationIds
     * @return 存在的业务id数组
     */
    @GetMapping("/biz/exist/check")
    public List<Long> checkBizIsExistInTp(@RequestBody List<Long> relationIds) {
        return tpPlanActivityService.checkBizIsExistInTp(relationIds);
    }

    @GetMapping("/biz/exist/names/check")
    public Set<String> checkBizIsExistInTpNames(@RequestBody List<Long> relationIds) {
        return tpPlanActivityService.checkBizIsExistInTpNames(relationIds);
    }

    @PostMapping("/view/record/add")
    public Integer addViewRecord(@RequestBody BaseModel<Long> model) {
        return tpPlanActivityViewRecordService.addViewRecord(model);
    }

    @PostMapping("/certificate/grant")
    public CertificateGrantStatus certificateGrant(@RequestBody Map<String, Long> param) {
        return tpPlanActivityService.certificateGrant(param);

    }

    /*
     * 通过 作业id去查询相应的 计划内容
     * */
    @PostMapping("/tpPlan/list")
    public List<Long> allListByAssignmentIds(@RequestBody List<TpPlanActivityVo> tpPlanActivityVos) {
        return tpPlanActivityService.allListByAssignmentIds(tpPlanActivityVos);
    }

    /************************************************************************  PC端 获取证书
     *  ****************************************************/

    @PostMapping("/pc/certificate/grant")
    public Constants certificatePcGrant(@RequestBody Map<String, Long> param) {
        return tpPlanActivityService.certificatePcGrant(param);

    }

    /**
     * 获取培训项目下的活动个数
     *
     * @param id 培训id
     * @return
     */
    @GetMapping("/get/courseIds")
    public List<Long> getActiveCountById(@RequestParam(name = "id") Long id) {
        return tpPlanActivityService.getcourseIdsByTrainingProjectId(id);
    }

    /**
     * 获取培训项目下的活动个数 v2 版本 修改：活动数去掉证书
     */
    @ApiOperation(value = "获取培训项目下的活动个数 v2 版本  修改：活动数去掉证书")
    @GetMapping("/get/v2/courseIds")
    public List<Long> getExcCertiferActiveCountById(@RequestParam(name = "id") Long id) {
        return tpPlanActivityService.getExcCertifercourseIdsByTrainingProjectId(id);
    }

    @GetMapping("/activitiesExport")
    public String activitiesExport(@RequestBody Map<String, Object> map) {
        activitiesExport.execute(map, true);
        return "ok";
    }

    /**
     * 检查业务是否被培训项目关联（不存在或者包含该业务所在计划被删除了或者包含该业务所在培训项目下架或删除啦）
     *
     * @param bizType    活动类型：0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到 10线下课程  11：案例活动、12：精选案例 、13：论坛帖子
     * @param relationId 业务id
     * @return
     */
    @GetMapping("/biz/canDown")
    public Boolean checkBizCanDown(@RequestParam("bizType") Integer bizType,
        @RequestParam("relationId") Long relationId) {
        return tpPlanActivityService.checkBizCanDown(bizType, relationId);
    }

    @GetMapping("/course/relate/project/list")
    @ApiOperation(value = "课程关联课程列表", notes = "课程关联课程列表", response = CourseRelateProjectVO.class)
    public Page<CourseRelateProjectVO> courseRelateProjectList(
        @ApiParam(name = "courseId", value = "课程id", required = true) @RequestParam(name = "courseId",
            required = true) Long courseId,
        @ApiParam(name = "pageNo", value = "跳转页数,默认第一页", required = true) @RequestParam(name = "pageNo",
            defaultValue = "1") Integer pageNo,
        @ApiParam(name = "pageSize", value = "每页条数,默认20条", required = true) @RequestParam(name = "pageSize",
            defaultValue = "20") Integer pageSize) {

        final Page<CourseRelateProjectVO> page = new Page<>(pageNo, pageSize);
        List<CourseRelateProjectVO> result = tpPlanActivityService.courseRelateProjectList(courseId, page);
        page.setRecords(result);
        return page;

    }

/*    @PostMapping("/course/ids")
    @ApiOperation(value = "通过培训项目ids获取课程id集合", notes = "通过培训项目ids获取课程id集合")
    Set<Long> getCourseIdsByIds(@RequestBody CourseProjectNameSyncVO courseProjectNameSyncVO) {
        Set<Long> courseIds;
        final List<TpPlanActivity> tpPlanActivities;
        if (CollectionUtils.isNotEmpty(courseProjectNameSyncVO.getTpPlanActivityIds())) {
            final QueryWrapper<TpPlanActivity> qw = new QueryWrapper<>();
            qw.eq("relation_id", courseProjectNameSyncVO.getCourseId());
            tpPlanActivities = tpPlanActivityService.list(qw);
        } else {
            tpPlanActivities = tpPlanActivityService.listByIds(courseProjectNameSyncVO.getTpPlanActivityIds());
        }
        return tpPlanActivities.stream().map(TpPlanActivity::getRelationId).collect(Collectors.toSet());
    }*/

    /**
     * 进行名称同步
     *
     * @param courseProjectNameSyncVO
     * @return
     */
    @PostMapping("/course/name/sync")
    Boolean courseProjectNameSync(@RequestBody CourseProjectNameSyncVO courseProjectNameSyncVO) {
        //通过courseId查询courseName
        final Long courseId = courseProjectNameSyncVO.getCourseId();
        final CourseVo courseVo = courseClient.getOne(courseProjectNameSyncVO.getCourseId());
        if (Objects.isNull(courseVo)) {
            throw new BizException("600001", "改课程不存在");
        }
        final QueryWrapper<TpPlanActivity> QueryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isEmpty(courseProjectNameSyncVO.getTpPlanActivityIds())) {
            QueryWrapper.eq("relation_id", courseId);
            final TpPlanActivity tpPlanActivity = new TpPlanActivity();
            tpPlanActivity.setName(courseVo.getName());
            tpPlanActivity.setCustomizeName(courseVo.getName());
            tpPlanActivityService.update(tpPlanActivity, QueryWrapper);
        } else {
            QueryWrapper.in("id", courseProjectNameSyncVO.getTpPlanActivityIds());
            final TpPlanActivity tpPlanActivity = new TpPlanActivity();
            tpPlanActivity.setName(courseVo.getName());
            tpPlanActivity.setCustomizeName(courseVo.getName());
            tpPlanActivityService.update(tpPlanActivity, QueryWrapper);
        }
        return true;
    }

    /**
     * 查询活动完成记录
     */
    @PostMapping("/finished/list")
    List<BaseViewRecordVO> getFinishedActivityList(@RequestBody BaseParamVO baseParamVO) {

        return tpPlanActivityService.getFinishedActivityList(baseParamVO);
    }


    @GetMapping("/biz/exist/names/check/related")
    public Set<String> checkExistRelatedProject(@RequestParam(name = "id") Long id,@RequestParam(name = "type") Integer type) {
        return tpPlanActivityService.checkExistRelatedProject(id,type);
    }

}

