package com.yizhi.training.application.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.cache.distributedlock.impl.RedisDistributedLock;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.exception.CustomException;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.core.application.vo.DroolsVo;
import com.yizhi.enroll.application.feign.EnrollFeignClient;
import com.yizhi.system.application.system.remote.ReportClient;
import com.yizhi.system.application.vo.ReportAccountRespVO;
import com.yizhi.system.application.vo.ReportRangeAccountReqVO;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.enums.ExchangeCodeErrorEnum;
import com.yizhi.training.application.enums.TrainingCommonEnums;
import com.yizhi.training.application.job.Process2PlatTrainingProject;
import com.yizhi.training.application.job.ProcessFinishedRecordCacheJob;
import com.yizhi.training.application.mapper.TpPlanMapper;
import com.yizhi.training.application.mapper.TpViewRecordMapper;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.param.PaidTrainingProjectQO;
import com.yizhi.training.application.param.TrainingProjectParam;
import com.yizhi.training.application.service.*;
import com.yizhi.training.application.util.CacheUtil;
import com.yizhi.training.application.util.TrainingEvenSendMessage;
import com.yizhi.training.application.v2.enums.TpVisibleRangeEnum;
import com.yizhi.training.application.vo.api.*;
import com.yizhi.training.application.vo.dashboard.TrainDashboardResourceVO;
import com.yizhi.training.application.vo.domain.TpAuthorizationRangeVo;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.training.application.vo.manage.*;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表） 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@RestController
@RequestMapping("/trainingProject")
public class TrainingProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingProjectController.class);

    public static String VISIBLE_RANGE_KEY = "tp:visible:rangee";

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private ITpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private ITrainingProjectPcService trainingProjectPcService;

    @Autowired
    private ProcessFinishedRecordCacheJob processFinishedRecordCacheJob;

    @Autowired
    private Process2PlatTrainingProject process2PlatTrainingProject;

    @Autowired
    private ReportClient reportClient;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private TrainingEvenSendMessage trainingEvenSendMessage;

    @Autowired
    private TpPlanMapper tpPlanMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Autowired
    private RedisDistributedLock redisDistributedLock;

    @Autowired
    private IExchangeCodeService exchangeCodeService;

    @Autowired
    private EnrollFeignClient enrollFeignClient;

    @Autowired
    private ITpStudentProjectRecordService studentProjectRecordService;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private TpViewRecordService tpViewRecordService;

    @GetMapping(value = "/list")
    public Page<TrainingProject> list(@RequestBody SearchProjectVo searchProjectVo) {
        Page<TrainingProject> page =
            trainingProjectService.searchPage(searchProjectVo.getName(), searchProjectVo.getTpClassificationId(),
                searchProjectVo.getStatus(), searchProjectVo.getCompanyId(), searchProjectVo.getSiteId(),
                searchProjectVo.getOrgId(), searchProjectVo.getPageNo(), searchProjectVo.getPageSize());
        return page;
    }

    /**
     * 查询项目列表  版本2 对比版本1 新增筛选条件查询：无需报名；免费报名；收费报名
     *
     * @param searchProjectVo 入参
     */
    @GetMapping(value = "/list/v2")
    public Page<TrainingProjectVo> listV2(@RequestBody SearchProjectVo searchProjectVo) {

        Page<TrainingProjectVo> page =
            trainingProjectService.searchPageV2(searchProjectVo.getName(), searchProjectVo.getTpClassificationId(),
                searchProjectVo.getStatus(), searchProjectVo.getEnrollStatus(), searchProjectVo.getCompanyId(),
                searchProjectVo.getSiteId(), searchProjectVo.getOrgId(), searchProjectVo.getPageNo(),
                searchProjectVo.getPageSize());

        return page;
    }

    @PostMapping(value = "/up")
    public Integer up(@RequestBody Map<String, Long> map) {
        Long id = map.get("id");
        // 检查是否有学习活动
        TpPlanActivity activity = new TpPlanActivity();
        activity.setTrainingProjectId(id);
        activity.setDeleted(ProjectConstant.DELETED_NO);
        if (tpPlanActivityService.count(new QueryWrapper<>(activity)) < 1) {
            LOGGER.error("没有添加学习活动，不能上架！");
            return -1;
        }
        TrainingProject trainingProject = trainingProjectService.getById(id);
        if (trainingProject.getStatus() == 0 || trainingProject.getStatus() == 2) {
            trainingProject.setStatus(1);
            trainingProject.setReleaseTime(new Date());
            if (trainingProjectService.updateById(trainingProject)) {
                RequestContext context = ContextHolder.get();
                //判断项目开启了提醒功能需要更新消息那边的状态
                TrainingProjectVo trp = new TrainingProjectVo();
                BeanUtils.copyProperties(trainingProject, trp);
                trainingUpdateStatus(trp, context);
                //开启了提醒功能需要更新消息那边计划的状态
                updatePlanMessageStatus(trp, context);
            }
            return 1;
        }
        return 0;
    }

    /**
     * 发消息告知业务状态改变
     *
     * @param trainingProject
     * @param context
     */
    public void trainingUpdateStatus(TrainingProjectVo trainingProject, RequestContext context) {
        try { //发消息告知业务状态有变化
            if (trainingProject != null) {
                if (trainingProject.getEnableRemindApp() == 1) {
                    MessageRemindVo remindVo = new MessageRemindVo();
                    remindVo.setTaskStatusUpdate(true);
                    taskExecutor.asynExecute(new AbstractTaskHandler() {
                        @Override
                        public void handle() {
                            TrainingProject trp = new TrainingProject();
                            BeanUtils.copyProperties(trainingProject, trp);
                            trainingEvenSendMessage.systemSendMessage(trp, null, remindVo, context);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新计划消息那边的业务状态
     *
     * @param trainingProject
     * @param context
     */
    public void updatePlanMessageStatus(TrainingProjectVo trainingProject, RequestContext context) {

        TpPlan tpPlan = new TpPlan();
        tpPlan.setTrainingProjectId(trainingProject.getId());
        tpPlan.setDeleted(0);
        QueryWrapper wrapper = new QueryWrapper(tpPlan);
        List<TpPlan> tpPlans = tpPlanMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(tpPlans)) {
            TrainingProject trp = new TrainingProject();
            BeanUtils.copyProperties(trainingProject, trp);
            trainingProjectService.trPlanUpdateStatus(tpPlans, trp, context, false);
        }
    }

    @PostMapping("/down")
    public Boolean down(@RequestBody Map<String, Long> map) {
        Long id = map.get("id");
        TrainingProject trainingProject = trainingProjectService.getById(id);
        TrainingProjectVo trp = new TrainingProjectVo();
        BeanUtils.copyProperties(trainingProject, trp);
        if (trainingProject.getStatus() == 1) {
            trainingProject.setStatus(2);
            if (trainingProjectService.updateById(trainingProject)) {
                RequestContext context = ContextHolder.get();
                //开启了提醒功能需要更新消息那边项目的状态
                trainingUpdateStatus(trp, context);
                //开启了提醒功能需要更新消息那边计划的状态
                updatePlanMessageStatus(trp, context);
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 新增培训项目--基本信息
     *
     * @param trainingProject
     * @return
     */
    @PostMapping("/save")
    public TrainingProject save(@RequestBody TrainingProjectVo trainingProject) {
        if (trainingProject == null) {
            return null;
        }
        TrainingProject t = new TrainingProject();
        BeanUtils.copyProperties(trainingProject, t);
        return trainingProjectService.saveTrPro(t);
    }

    /**
     * 新增培训项目--第三步：可见范围、提醒
     *
     * @param model
     * @return
     */
    @PostMapping("/step/three/save")
    public Integer save(@RequestBody BaseModel<TrainingProjectStepThreeVo> model) throws Exception {
        return trainingProjectService.saveStepThree(model);
    }

    /**
     * 新增培训项目--第三步：可见范围、提醒，查看
     *
     * @param id
     * @return
     */
    @GetMapping("/step/three/view")
    public TrainingProjectStepThreeVo sepThreeView(@RequestParam("id") Long id) {
        return trainingProjectService.stepThreeView(id);
    }

    /**
     * 修改培训项目--基本信息
     *
     * @param trainingProject
     * @return
     * @throws Exception
     */
    @PostMapping("/update")
    public TrainingProject update(@RequestBody TrainingProjectVo trainingProject) throws Exception {
        TrainingProject trainingProject1 = new TrainingProject();
        BeanUtils.copyProperties(trainingProject, trainingProject1);
        return trainingProjectService.update(trainingProject1);
    }

    @PostMapping("/batch/delete")
    public Integer batchDelete(@RequestBody BaseModel<List<Long>> model) {
        return trainingProjectService.batchDelete(model);
    }

    /**
     * 设置可见范围
     *
     * @param model
     * @return
     */
    @PostMapping("/visibleRange/set")
    public Integer setVisibleRange(@RequestBody BaseModel<VisibleRangeVo> model) {
        String[] s = new String[1];
        s[0] = String.valueOf(model.getObj().getTrainingProjectId());
        redisCache.hdel(VISIBLE_RANGE_KEY, s);
        return tpAuthorizationRangeService.batchInsert(model);
    }

    /**
     * 新增可见范围
     *
     * @return
     */
    @PostMapping("/visibleRange/insert")
    public Boolean insertVisibleRange(@RequestBody List<TpAuthorizationRangeVo> tpAuthorizationRanges) {
        List<TpAuthorizationRange> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tpAuthorizationRanges)) {
            for (TpAuthorizationRangeVo tr : tpAuthorizationRanges) {
                TpAuthorizationRange t = new TpAuthorizationRange();
                BeanUtils.copyProperties(tr, t);
                list.add(t);
            }
            String[] s = new String[1];
            s[0] = String.valueOf(tpAuthorizationRanges.get(0).getBizId());
            redisCache.hdel(VISIBLE_RANGE_KEY, s);
        }
        Boolean insertVisibleRange = tpAuthorizationRangeService.insertVisibleRange(list);
        if (insertVisibleRange) {
            TrainingProject byId = trainingProjectService.getById(tpAuthorizationRanges.get(0).getId());
            //可见范围内用户id缓存 导入数据目前只有用户
            //tpAuthorizationRanges提取relationId 集合
            Set<Long> relationIds =
                tpAuthorizationRanges.stream().map(TpAuthorizationRangeVo::getRelationId).collect(Collectors.toSet());
            cacheUtil.addAuthorizationAccountIdList(relationIds, tpAuthorizationRanges.get(0).getBizId(),
                TpVisibleRangeEnum.SPECIFIC_USER.getCode());
        }
        return insertVisibleRange;
    }

    /**
     * 查看可见范围
     *
     * @param siteId
     * @return
     */
    @PostMapping("/visibleRange/view/by/site")
    public List<TpAuthorizationRange> VisibleRangeBySiteId(@RequestBody List<Long> siteId) {
        List<TpAuthorizationRange> list = tpAuthorizationRangeService.selectBySiteIds(siteId);
        return list;
    }

    /**
     * 根据参数查询
     *
     * @param param
     * @return
     */
    @GetMapping("/param/list")
    public List<TrainingProject> listByParam(@RequestBody TrainingProjectParam param) {
        TrainingProject example = new TrainingProject();
        example.setOrgId(param.getOrgId());
        example.setCompanyId(param.getCompanyId());
        example.setSiteId(param.getSiteId());
        example.setDeleted(0);
        if (null != param.getStatus()) {
            example.setStatus(param.getStatus().getCode());
        }
        LambdaQueryWrapper<TrainingProject> queryWrapper = new LambdaQueryWrapper<>(example);
        queryWrapper.like(TrainingProject::getName, param.getName());
        queryWrapper.orderByDesc(TrainingProject::getCreateTime);
        return trainingProjectService.list(queryWrapper);
    }

    /**
     * 学员端分页列表 -- 培训项目首页
     *
     * @param model
     * @return
     */
    @PostMapping("/api/page/list")
    public Page<TrainingProjectListVo> apiPageList(@RequestBody BaseModel<TrainingProjectParamVo> model)
        throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Page<TrainingProjectListVo> page = trainingProjectService.apiPageList(model);
        List<TrainingProjectListVo> list = page.getRecords();
        for (TrainingProjectListVo vo : list) {
            vo.setStartTimeString(sdf.format(vo.getStartTime()));//时间格式化
            vo.setEndTimeString(sdf.format(vo.getEndTime()));
            Integer activityNum = tpPlanActivityService.getExcCertificateActivityNumByTpId(vo.getId());
            List<Long> courseIds = tpPlanActivityService.getAllCourseIdByTrainingProjectId(vo.getId());
            vo.setListCourseIds(null == courseIds ? new ArrayList<>() : courseIds);
            vo.setActivitieNum((null == activityNum ? 0 : activityNum));

        }
        return page;
    }

    // *************************************************************************

    /**
     * 火热报名列表
     *
     * @param model
     * @return
     */
    @GetMapping("/api/hot/page/list")
    public Page<HotEnrollListVo> apiHotPageList(@RequestBody BaseModel<HotEnrollParamVo> model) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Page<HotEnrollListVo> page = trainingProjectService.apiHotPageList(model);
        List<HotEnrollListVo> listVo = page.getRecords();
        if (null != listVo && listVo.size() > 0) {
            for (HotEnrollListVo vo : listVo) {
                vo.setEnrollStartTimeString(sdf.format(vo.getEnrollStartTime()));//对时间格式化
                vo.setEnrollEndTimeString(sdf.format(vo.getEnrollEndTime()));
                Integer activityNum =
                    tpPlanActivityService.getactivityNumByTrainingProjectId(vo.getTrainingProjectId());
                List<Long> courseIds =
                    tpPlanActivityService.getAllCourseIdByTrainingProjectId(vo.getTrainingProjectId());

                vo.setListCourseIds(null == courseIds ? new ArrayList<>() : courseIds);
                vo.setActivitieNum((null == activityNum ? 0 : activityNum));

            }
        }
        return page;
    }

    /**
     * 火热报名列表 v3 版本 修改点： 返回Vo内容新增付费类别
     */
    @GetMapping("/api/v2/hot/page/list")
    public Page<HotEnrollListVo> apiHotPageListV2(@RequestBody BaseModel<HotEnrollParamVo> model) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Page<HotEnrollListVo> page = trainingProjectService.apiHotPageList(model);
        List<HotEnrollListVo> listVo = page.getRecords();
        if (null != listVo && listVo.size() > 0) {
            for (HotEnrollListVo vo : listVo) {
                vo.setEnrollStartTimeString(sdf.format(vo.getEnrollStartTime()));//对时间格式化
                vo.setEnrollEndTimeString(sdf.format(vo.getEnrollEndTime()));
                Integer activityNum =
                    tpPlanActivityService.getExcCertificateActivityNumByTpId(vo.getTrainingProjectId());
                List<Long> courseIds =
                    tpPlanActivityService.getAllCourseIdByTrainingProjectId(vo.getTrainingProjectId());

                vo.setListCourseIds(null == courseIds ? new ArrayList<>() : courseIds);
                vo.setActivitieNum((null == activityNum ? 0 : activityNum));
            }
        }
        return page;
    }

    /**
     * 学员端分页列表 -- 培训项目首页
     *
     * @param model
     * @return
     */
    @PostMapping("/api/page/noCondition/list")
    public Page<TrainingProject> apiPageListNoCondition(@RequestBody BaseModel<Page> model) {
        model.setDate(new Date());
        return trainingProjectService.apiPageListNoCondition(model);
    }

    /**
     * 分页列表 -- 我的培训项目
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/api/page/my/list")
    public Page<TrainingProjectListVo> apiMyPageList(@RequestBody BaseModel<TrainingProjectMyParamVo> model)
        throws Exception {
        return trainingProjectService.apiMyPageList(model);
    }

    /**
     * 获取培训条数
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/api/page/my/list/count")
    public MyPageVO apiMyPageListCount(@RequestBody BaseModel<TrainingProjectMyParamVo> model) throws Exception {
        return trainingProjectService.getTrainingCount(model);
    }

    /**
     * 将以上两个接口合并 查询培训过详情
     *
     * @param model
     * @return
     */
    @PostMapping("/api/detail/get")
    public TrainingProjectDetailVo getTpDetail(@RequestBody BaseModel<Long> model) throws ParseException {
        TrainingProjectDetailVo vo =
            trainingProjectService.getTpDetail(model.getObj(), model.getContext(), model.getDate(), false);
        if (null != vo.getIntroductionVo().getEnroll()) {
            vo.getIntroductionVo().setEnrollLimit(vo.getIntroductionVo().getEnroll().getLimit());
        }
        return vo;
    }

    /**
     * 我的培训，添加上培训学习统计 查询培训过详情
     *
     * @param model
     * @return
     */
    @PostMapping("/api/detail/progress/get")
    public TrainingProjectDetailVo getTpDetailWithProgress(@RequestBody BaseModel<Long> model) throws ParseException {
        TrainingProjectDetailVo vo =
            trainingProjectService.getTpDetail(model.getObj(), model.getContext(), model.getDate(), true);
        if (null != vo.getIntroductionVo().getEnroll()) {
            vo.getIntroductionVo().setEnrollLimit(vo.getIntroductionVo().getEnroll().getLimit());
        }
        return vo;
    }

    /**
     * 查询一个培训项目实体
     *
     * @param id
     * @return
     */
    @GetMapping("/one/get")
    public TrainingProject getOne(@RequestParam("id") Long id) {
        return trainingProjectService.getByIdWithJoinNumber(id);
    }

    /**
     * 获取我的培训项目
     *
     * @return
     */
    @GetMapping("/my/countNum/get")
    public Integer getMyTrainingProjectCountNum(@RequestBody BaseModel<TrainingProjectParamVo> model)
        throws IOException {
        return trainingProjectService.getMyTrainingProjectCountNum(model);
    }

    @ApiOperation(value = "可见范围导出", notes = "可见范围导出")
    @GetMapping("/export/visiblRange")
    public VisibleRangeExport vsibleRangeExport(
        @ApiParam(value = "培训项目的Id", name = "培训项目的Id", required = true) @RequestParam(
            name = "trainingProjectId", required = true) Long trainingProjectId) {
        return trainingProjectService.vsibleRangeExport(trainingProjectId);
    }

    @ApiOperation(value = "培训项目列表", notes = "培训项目列表")
    @GetMapping("/training/list")
    public List<TrainingProjectVoPortalVo> getTrainingListByIds(@RequestParam(name = "ids") List<Long> ids) {
        return trainingProjectService.getTrainingListByIds(ids);
    }

    @GetMapping(value = "/list/notIds")
    public Page<TrainingProject> listNotIds(@RequestBody SearchProjectVo searchProjectVo) {
        return trainingProjectService.listNotIds(searchProjectVo.getListIds(), searchProjectVo.getName(),
            searchProjectVo.getSiteId(), searchProjectVo.getPageNo(), searchProjectVo.getPageSize());
    }

    /**
     * PC端火热报名列表
     *
     * @param model
     * @return
     */
    @GetMapping("/pc/hot/page/list")
    public Page<HotEnrollListVo> pcHotPageList(@RequestBody BaseModel<HotEnrollParamVo> model) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Page<HotEnrollListVo> page = trainingProjectService.apiHotPageList(model);
        List<HotEnrollListVo> listVo = page.getRecords();
        if (null != listVo && listVo.size() > 0) {
            for (HotEnrollListVo vo : listVo) {
                vo.setEnrollStartTimeString(sdf.format(vo.getEnrollStartTime()));//对时间格式化
                vo.setEnrollEndTimeString(sdf.format(vo.getEnrollEndTime()));
                Integer activityNum =
                    tpPlanActivityService.getactivityNumByTrainingProjectId(vo.getTrainingProjectId());
                List<Long> courseIds =
                    tpPlanActivityService.getAllCourseIdByTrainingProjectId(vo.getTrainingProjectId());

                vo.setListCourseIds(null == courseIds ? new ArrayList<>() : courseIds);
                vo.setActivitieNum((null == activityNum ? 0 : activityNum));

            }
        }
        return page;
    }

    //************************************************-----PC
    // 端接口----*********************************************************************************************

    /**
     * PC端分页列表 -- 培训项目首页
     *
     * @param model
     * @return
     */
    @PostMapping("/pc/page/list")
    public Page<TrainingProjectListVo> pcPageList(@RequestBody BaseModel<TrainingProjectParamVo> model)
        throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Page<TrainingProjectListVo> page = trainingProjectService.apiPageList(model);
        List<TrainingProjectListVo> list = page.getRecords();
        for (TrainingProjectListVo vo : list) {
            vo.setStartTimeString(sdf.format(vo.getStartTime()));//时间格式化
            vo.setEndTimeString(sdf.format(vo.getEndTime()));
            Integer activityNum = tpPlanActivityService.getactivityNumByTrainingProjectId(vo.getId());
            List<Long> courseIds = tpPlanActivityService.getAllCourseIdByTrainingProjectId(vo.getId());
            vo.setListCourseIds(null == courseIds ? new ArrayList<>() : courseIds);
            vo.setActivitieNum((null == activityNum ? 0 : activityNum));

        }

        return page;
    }

    /**
     * 查询需要报名培训项目的培训过详情
     *
     * @param model
     * @return
     */
    @PostMapping("/pc/detail/get")
    public TrainingProjectDetailVo getPcTpDetail(@RequestBody BaseModel<Long> model) throws ParseException {
        TrainingProjectDetailVo vo =
            trainingProjectService.getTpDetail(model.getObj(), model.getContext(), model.getDate(), false);
        if (vo != null) {
            if (null != vo.getIntroductionVo().getEnroll()) {
                vo.getIntroductionVo().setEnrollLimit(vo.getIntroductionVo().getEnroll().getLimit());
            }
        }
        return vo;
    }

    /**
     * 分页列表 -- Pc端我的培训项目
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/pc/page/my/list")
    public Page<TrainingProjectListVo> pcMyPageList(@RequestBody BaseModel<TrainingProjectMyParamVo> model)
        throws Exception {
        return trainingProjectService.apiMyPageList(model);
    }

    @GetMapping("/pc/train/portal/train/relation")
    public List<TrainingProject> pcTrainingProjectList(@RequestParam(name = "relationIds") List<Long> relationIds,
        @RequestParam(name = "num") Integer num, @RequestParam(name = "listIds", required = false) List<Long> listIds) {
        return trainingProjectService.getTrainingListByRelationIds(relationIds, num, listIds);
    }

    /**
     * 培训，添加上培训学习记录 查询培训过详情
     *
     * @param model
     * @return
     */
    @PostMapping("/pc/detail/progress/get")
    public TrainingProjectDetailVo getPcTpDetailWithProgress(@RequestBody BaseModel<Long> model) throws ParseException {
        TrainingProjectDetailVo vo =
            trainingProjectPcService.getPcTpDetail(model.getObj(), model.getContext(), model.getDate());
        if (vo != null) {
            if (null != vo.getIntroductionVo().getEnroll()) {
                vo.getIntroductionVo().setEnrollLimit(vo.getIntroductionVo().getEnroll().getLimit());
            }
        }

        return vo;
    }

    /**
     * 学员端获取用户的列表
     *
     * @param ids
     * @return
     */
    @GetMapping("/api/student/trainingList")
    public List<TrainingProject> getTrainingList(@RequestParam(name = "ids") List<Long> ids) {
        return trainingProjectService.getTrainingList(ids);
    }

    /**
     * 缓存处理
     *
     * @return
     */
    @GetMapping("/cache/init")
    public boolean cacheInit(@RequestParam(name = "subDate", required = false) Date subDate) {
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {
                processFinishedRecordCacheJob.processClicked(subDate);
                processFinishedRecordCacheJob.processActivityUnfinished(subDate);
                processFinishedRecordCacheJob.processActivityFinished(subDate, null);
                processFinishedRecordCacheJob.processPlan(subDate);
                processFinishedRecordCacheJob.processTrainingProject(subDate);
            }
        });
        return true;
    }

    /**
     * 缓存处理
     *
     * @return
     */
    @GetMapping("/cache/plan/finished")
    public String cachePlan(@RequestParam(name = "subDate", required = false) String subDate,
        @RequestParam(name = "siteId", required = false) Long siteId) {
        Date date = new Date(DateUtil.parse(subDate).getTime());
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {
                processFinishedRecordCacheJob.processActivityFinished(date, siteId);
            }
        });
        return DateUtil.formatDate(date);
    }

    /**
     * 完成记录处理
     *
     * @return
     */
    @GetMapping("/record/cache/init")
    public boolean recordInit(@RequestParam(name = "siteId", required = false) Long siteId) {
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {
                process2PlatTrainingProject.startProcess(siteId);
            }
        });
        return true;
    }

    @PostMapping("/getPageToCalendar")
    public Page<TrainingProjectVo> getPageToCalendar(@ApiParam("paramVo") @RequestBody CalendarTaskParamVo paramVo) {
        Page<TrainingProject> page = new Page(paramVo.getPageNo(), paramVo.getPageSize());
        return trainingProjectService.getPageToCalendar(paramVo.getDate(), page);
    }

    @GetMapping("/getJoinNumber")
    public void getJoinNumber() {
        String lockName = "getJoinNumber";
        if (redisDistributedLock.lock(lockName)) {
            try {
                TrainingProject project = new TrainingProject();
                project.setStatus(1);
                project.setDeleted(0);
                List<TrainingProject> list = trainingProjectService.list(new QueryWrapper<TrainingProject>(project));
                int count = 0;
                if (!CollectionUtils.isEmpty(list)) {
                    for (TrainingProject tr : list) {
                        String key = "tp:page:joinNumber:count:" + tr.getCompanyId();
                        String item = tr.getSiteId() + tr.getId().toString();
                        count = tpViewRecordService.getViewNumRange(tr);
                        //                        if (tr.getVisibleRange() == 1) {
                        //                            count = tpViewRecordMapper.getViewNum(tr);
                        //                        } else {
                        //                            count = tpViewRecordMapper.getViewNumRange(tr);
                        //                        }
                        redisCache.hset(key, item, count + "", 86400);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                LOGGER.info("更新培训项目已学习人数异常", e);
            } finally {
                redisDistributedLock.releaseLock(lockName);
                LOGGER.info("释放reids锁" + lockName);
            }
        }

    }

    @PostMapping("/realtime/joinNumber/get")
    public List<TrainJoinNumVO> getTrainsJoinNumber(@RequestBody IdsQueryVO queryVO) {

        return trainingProjectService.getTrainJoinNumber(queryVO.getIds());
    }

    /**
     * 查询一个培训项目详情 包括项目和付费详情
     *
     * @param projectId 项目id
     */
    @GetMapping("/description/get")
    public BizResponse<TrainingProjectVo> getProjectDescription(@RequestParam("projectId") Long projectId) {
        TrainingProjectVo projectVo = trainingProjectService.getProjectDescription(projectId);
        return BizResponse.ok(projectVo);
    }

    /**
     * 按条件获取付费课程
     *
     * @param qo
     * @return
     */
    @PostMapping("/paid/get")
    public List<PaidTrainingProjectVO> getPaidTrainingProject(@Valid @RequestBody PaidTrainingProjectQO qo) {
        return trainingProjectService.getPaidTrainingProject(qo);
    }

    /**
     * 查询项目是否上架
     *
     * @param trainingProjectId
     * @return
     */
    @PostMapping("/shelfUp/get")
    public boolean getShelfUp(@RequestParam("id") Long trainingProjectId) {
        QueryWrapper<TrainingProject> ew = new QueryWrapper<>();
        ew.eq("id", trainingProjectId);
        ew.eq("status", 1);
        TrainingProject trainingProject = trainingProjectService.getOne(ew);
        if (null == trainingProject) {
            return false;
        }
        return true;
    }

    /**
     * 分页获取能赚取积分的项目 可见范围内，项目时间内，未参加过的项目（配了积分）
     *
     * @param title 项目名
     * @return 项目列表
     */
    @GetMapping("/gainPoint/list")
    public Page<GainPointProjectVo> pageGainPointCourses(
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        @RequestParam(value = "title", required = false) String title) {

        Page<GainPointProjectVo> page = trainingProjectService.getGainPointCourses(pageNo, pageSize, title);
        return page;
    }

    @GetMapping("/exchangeCode/get")
    public CustomException<PaidTrainingProjectVO> getProjectForExchangCode(
        @RequestParam("exchangeCode") String exchangeCode) {
        ExchangeCode exchangeEntity = new ExchangeCode();
        exchangeEntity.setCode(exchangeCode);
        exchangeEntity.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
        QueryWrapper<ExchangeCode> wrapper = new QueryWrapper<>(exchangeEntity);
        exchangeEntity = exchangeCodeService.getOne(wrapper);
        if (ObjectUtil.isEmpty(exchangeEntity)) {
            return CustomException.fail(ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getCode(),
                ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getMsg());
        }
        if (exchangeEntity.getState().equals(TrainingCommonEnums.USED.getCode())) {
            return CustomException.fail(ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getCode(),
                ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getMsg());
        }
        Long tpId = exchangeEntity.getRefId();
        TrainingProject trainingProject = trainingProjectService.getById(tpId);
        if (ObjectUtil.isEmpty(trainingProject) || trainingProject.getDeleted()
            .equals(TrainingCommonEnums.DELETED.getCode()) || !trainingProject.getStatus()
            .equals(ProjectConstant.PROJECT_STATUS_ENABLE)) {
            return CustomException.fail(
                ExchangeCodeErrorEnum.THE_PROJECT_NOT_EXIST_OR_THE_PROJECT_HAS_EXPIRED.getCode(),
                ExchangeCodeErrorEnum.THE_PROJECT_NOT_EXIST_OR_THE_PROJECT_HAS_EXPIRED.getMsg());
        }
        PaidTrainingProjectVO ptpv = new PaidTrainingProjectVO();
        BeanUtils.copyProperties(trainingProject, ptpv);
        return CustomException.ok(ptpv);
    }

    @GetMapping("/by/code/list")
    public List<Long> getProjectForCodeExchange() {
        return trainingProjectService.getProjectForCodeExchange(true);
    }

    @ApiOperation("查询最后更新的列表")
    @GetMapping(value = "/dashboard/latest/update/list")
    public Page<TrainDashboardResourceVO> dashboardLatestUpdateList(
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {

        return trainingProjectService.dashboardLatestUpdateList(pageNo, pageSize);
    }

    /**
     * 获取所有项目完成的记录
     *
     * @return
     */
    @PostMapping("/stu/finished/records/list")
    public List<BaseViewRecordVO> getTrainFinishedRecordsList(@RequestBody BaseParamVO baseParamVO) {

        return studentProjectRecordService.getFinishedRecordsListGroupByAccountId(baseParamVO);
    }

    @GetMapping(value = "/weight/set")
    boolean setWeight(@RequestParam("bizId") Long bizId, @RequestParam("weight") Integer weight) {
        return trainingProjectService.setWeight(bizId, weight);
    }

    /**
     * 查看可见范围
     *
     * @param trainingProjectId
     * @return
     */
    @GetMapping("/visibleRange/view")
    public List<TpAuthorizationRange> VisibleRange(@RequestParam("trainingProjectId") Long trainingProjectId) {
        String item = String.valueOf(trainingProjectId);
        Object obj = redisCache.hget(VISIBLE_RANGE_KEY, item);
        if (obj != null) {
            String s = (String)obj;
            List<TpAuthorizationRange> list = JSON.parseArray(s, TpAuthorizationRange.class);
            return list;
        }
        TpAuthorizationRange range = new TpAuthorizationRange();
        range.setBizId(trainingProjectId);
        List<TpAuthorizationRange> list = tpAuthorizationRangeService.list(new QueryWrapper<>(range));
        String s = JSON.toJSONString(list);
        redisCache.hset(VISIBLE_RANGE_KEY, item, s, 600);
        return list;
    }

    /**
     * 自定义项目h5接口培训项目验证用户权限
     *
     * @return
     * @author ding
     */
    @GetMapping("/getCustomProjectRange")
    Integer getRange(@RequestParam("projectId") Long projectId) {
        RequestContext requestContext = ContextHolder.get();
        Long accountId = requestContext.getAccountId();
        TrainingProject trainingProject = trainingProjectService.getById(projectId);
        if (null == trainingProject) {
            return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
        }
        if (trainingProject.getVisibleRange() == 1) {
            return ProjectConstant.TP_PLAN_CUSTOMPROJECT_PASS;
        } else {
            List<TpAuthorizationRange> list = this.VisibleRange(projectId);
            List<Long> accountIds = new ArrayList<>();
            List<Long> orgIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(list)) {
                for (TpAuthorizationRange tpAuthorizationRange : list) {
                    //1是部门2是用户
                    if (tpAuthorizationRange.getType() == 1) {
                        orgIds.add(tpAuthorizationRange.getRelationId());
                    } else if (tpAuthorizationRange.getType() == 2) {
                        accountIds.add(tpAuthorizationRange.getRelationId());
                    }
                }
            }

            ReportRangeAccountReqVO rrar = new ReportRangeAccountReqVO();
            rrar.setSiteId(requestContext.getSiteId());
            rrar.setAccountIds(accountIds);
            rrar.setOrgIds(orgIds);
            List<ReportAccountRespVO> list1 = reportClient.getRangeAccounts(rrar);
            //            if (!CollectionUtils.isEmpty(list1)) {
            //                List<ReportAccountRespVO> resultList = list1;
            //                if (!CollectionUtils.isEmpty(resultList)) {
            //                    for (ReportAccountRespVO reportAccountRespVO : resultList) {
            //                        if (reportAccountRespVO.getUserId().equals(accountId)) {
            //                            return ProjectConstant.TP_PLAN_CUSTOMPROJECT_PASS;
            //                        } else {
            //                            continue;
            //                        }
            //                    }
            //                    return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            //                } else {
            //                    return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            //                }
            //            } else {
            //                return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            //            }
            //            ReportRangeAccountReqVO rrar = new ReportRangeAccountReqVO();
            //            rrar.setIsParentOrg(false);
            //            rrar.setSiteId(requestContext.getSiteId());
            //            rrar.setAccountIds(accountIds);
            //            rrar.setOrgIds(orgIds);
            //            List<ReportAccountRespVO> list1 = reportClient.getRangeAccounts(rrar);
            //            if (!CollectionUtils.isEmpty(list1)) {
            //                List<ReportAccountRespVO> resultList = list1;
            //                if (!CollectionUtils.isEmpty(resultList)) {
            //                    for (ReportAccountRespVO reportAccountRespVO : resultList) {
            //                        if (reportAccountRespVO.getUserId().equals(accountId)) {
            //                            return ProjectConstant.TP_PLAN_CUSTOMPROJECT_PASS;
            //                        } else {
            //                            continue;
            //                        }
            //                    }
            //                    return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            //                } else {
            //                    return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            //                }
            //            } else {
            //                return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            //            }
            if (!CollectionUtils.isEmpty(list1)) {
                List<Long> accountIds1 =
                    list1.stream().map(ReportAccountRespVO::getUserId).collect(Collectors.toList());
                if (accountIds1.contains(accountId)) {
                    return ProjectConstant.TP_PLAN_CUSTOMPROJECT_PASS;
                } else {
                    return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
                }
            } else {
                return ProjectConstant.TP_PLAN_CUSTOMPROJECT_FAIL;
            }
        }
    }

    @PostMapping("/list/ids")
    List<TrainingProject> getByIds(@RequestBody Collection<Long> ids) {
        if (!CollectionUtils.isEmpty(ids)) {
            TrainingProject trainingProject = new TrainingProject();
            trainingProject.setDeleted(ProjectConstant.DELETED_NO);
            trainingProject.setStatus(ProjectConstant.PROJECT_STATUS_ENABLE);
            QueryWrapper<TrainingProject> ew = new QueryWrapper<>(trainingProject);
            ew.in("id", ids);
            return trainingProjectPcService.list(ew);
        }
        return null;
    }

    @GetMapping("/list/siteId")
    List<TrainingProject> getBySiteId(@RequestParam("siteId") Long siteId,
        @RequestParam(name = "name", required = false) String name) {
        TrainingProject trainingProject = new TrainingProject();
        trainingProject.setDeleted(ProjectConstant.DELETED_NO);
        trainingProject.setStatus(ProjectConstant.PROJECT_STATUS_ENABLE);
        trainingProject.setSiteId(siteId);
        QueryWrapper<TrainingProject> ew = new QueryWrapper<>(trainingProject);
        if (!StrUtil.isBlank(name)) {
            ew.and(e -> e.like("name", name).or().like("key_words", name));
        }
        ew.orderByDesc("create_time");
        List<TrainingProject> tps = trainingProject.selectList(ew);
        return tps;
    }

    @GetMapping("/getCaseLibraryProject")
    Map<Long, TrainingProject> getCaseLibraryProject(@RequestParam("idList") List<Long> idList) {
        return tpPlanService.getCaseLibraryProject(idList);
    }

    /**
     * 我的案例获取可见范围id
     *
     * @param res
     * @return
     */
    @PostMapping("/caseLibrary/getRangeProjects")
    List<TrainingProject> getCaseLibraryRangeProjects(@RequestBody RequestContext res) {
        return trainingProjectService.getCaseLibraryRangeProjects(res);
    }

    @GetMapping("/getPageByDrools")
    Page<DroolsVo> getPageByDrools(@RequestParam("field") String field,
        @RequestParam(value = "value", required = false) String value, @RequestParam("pageNo") Integer pageNo,
        @RequestParam("pageSize") Integer pageSize) {
        Page<DroolsVo> page = new Page<>(pageNo, pageSize);
        return trainingProjectService.getPageByDrools(field, value, page);
    }

    @GetMapping("/get/project/finish/records")
    TpStudentProjectRecordVo getProjectFinishRecords(@RequestParam("accountId") Long accountId,
        @RequestParam("tpId") Long tpId) {
        return trainingProjectService.getProjectFinishRecords(accountId, tpId);
    }

    @GetMapping("/get/plan/finish/records")
    TpStudentPlanRecordVo getPlanFinishRecords(@RequestParam("accountId") Long accountId,
        @RequestParam("planId") Long planId) {
        return trainingProjectService.getPlanFinishRecords(accountId, planId);
    }
}

