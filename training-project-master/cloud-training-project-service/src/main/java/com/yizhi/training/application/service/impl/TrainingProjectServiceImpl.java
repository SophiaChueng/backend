package com.yizhi.training.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.hierarchicalauthorization.HQueryUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.enums.TaskParamsEnums;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.core.application.vo.DroolsVo;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.course.application.feign.RecordeClient;
import com.yizhi.course.application.vo.CourseStudyInfoReq;
import com.yizhi.course.application.vo.CourseStudyInfoVo;
import com.yizhi.enroll.application.enums.EnrollCommonEnums;
import com.yizhi.enroll.application.feign.EnrollFeignClient;
import com.yizhi.enroll.application.vo.ProjectAccountVO;
import com.yizhi.enroll.application.vo.domain.Enroll;
import com.yizhi.point.application.feign.PointFeignClients;
import com.yizhi.point.application.vo.PointCommQueryReq;
import com.yizhi.point.application.vo.PointCommQueryVo;
import com.yizhi.sign.application.feign.SignRecordApiClient;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountCommReqVo;
import com.yizhi.system.application.vo.AccountCommVo;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.enums.TrEnrollStatusEnum;
import com.yizhi.training.application.mapper.*;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.param.PaidTrainingProjectQO;
import com.yizhi.training.application.service.*;
import com.yizhi.training.application.util.DeleteActivityAdvice;
import com.yizhi.training.application.util.TrainingEvenSendMessage;
import com.yizhi.training.application.v2.service.TpEnrollService;
import com.yizhi.training.application.v2.service.biz.MyTpService;
import com.yizhi.training.application.vo.SwhyTrainingProject;
import com.yizhi.training.application.vo.SwhyTrainingProjectMem;
import com.yizhi.training.application.vo.api.*;
import com.yizhi.training.application.vo.dashboard.TrainDashboardResourceVO;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.fo.TrainingProjectFoVo;
import com.yizhi.training.application.vo.manage.*;
import com.yizhi.util.application.clazz.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表） 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Slf4j
@Service
public class TrainingProjectServiceImpl extends ServiceImpl<TrainingProjectMapper, TrainingProject>
    implements ITrainingProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingProjectServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private TpStudentEnrollPassedMapper tpStudentEnrollPassedMapper;

    @Autowired
    private ITpStudentEnrollPassedService tpStudentEnrollPassedService;

    @Autowired
    private TpPlanMapper tpPlanMapper;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private TpAuthorizationRangeMapper tpAuthorizationRangeMapper;

    @Autowired
    private TpRemindMapper tpRemindMapper;

    @Autowired
    private TpStudentProjectRecordMapper tpStudentProjectRecordMapper;

    @Autowired
    private TpStudentPlanRecordMapper tpStudentPlanRecordMapper;

    @Autowired
    private TpPlanConditionPreMapper tpPlanConditionPreMapper;

    @Autowired
    private TpPlanConditionPostMapper tpPlanConditionPostMapper;

    @Autowired
    private TpPlanRemindMapper tpPlanRemindMapper;

    @Autowired
    private TpPlanActivityConditionPreMapper tpPlanActivityConditionPreMapper;

    @Autowired
    private TpPlanActivityConditionPostMapper tpPlanActivityConditionPostMapper;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Autowired
    private ITpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private SignRecordApiClient signRecordApiClient;

    @Autowired
    private TrainingEvenSendMessage trainingEvenSendMessage;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private ITpClassificationService tpClassificationService;

    @Autowired
    private RecordeClient recordeClient;

    @Autowired
    private TpContentStudentStatusServiceUsing tpContentStudentStatusServiceUsing;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private EnrollFeignClient enrollFeignClient;

    @Autowired
    private DeleteActivityAdvice deleteActivityAdvice;

    @Autowired
    private IExchangeCodeService exchangeCodeService;

    @Autowired
    private MyTpService myTpService;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private PointFeignClients pointFeignClients;

    @Autowired
    private TpEnrollService tpEnrollService;

    @Autowired
    private TpViewRecordService tpViewRecordService;

    @Override
    public List<SwhyTrainingProjectMem> relevance(Long companyId, Long siteId, Integer type, List<Long> relationIds) {
        return this.baseMapper.relevance(companyId, siteId, type, relationIds);
    }

    @Override
    public Page<SwhyTrainingProject> queryRecord(Long companyId, Long siteId, Integer pageNo, Integer pageSize,
        String queryDate) {
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Page<SwhyTrainingProject> trPage = new Page<>(pageNo, pageSize);
        // 计算时间
        String queryStartTime = "2000-01-01 00:00:00";

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.add(Calendar.DAY_OF_MONTH, -1);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
        calendarEnd.set(Calendar.MINUTE, 59);
        calendarEnd.set(Calendar.SECOND, 59);
        String queryEndTime = formatter.format(calendarEnd.getTime());
        log.info("时间开关状态={}", queryDate);
        if ("on".equals(queryDate)) {
            // https://blog.51cto.com/u_11269274/5129608
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.add(Calendar.DAY_OF_MONTH, -1); //前面带"-"为减去
            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
            calendarStart.set(Calendar.MINUTE, 0);
            calendarStart.set(Calendar.SECOND, 0);
            queryStartTime = formatter.format(calendarStart.getTime());
        } else if (queryDate != null && queryDate.contains("##")) {  // 指定时间查询时候。
            String[] dateArr = queryDate.split("##");
            queryStartTime = dateArr[0];
            queryEndTime = dateArr[1];
        }

        this.baseMapper.queryRecord(siteId, queryStartTime, queryEndTime, trPage);
        if (trPage.getTotal() > 0) {
            List<SwhyTrainingProject> stpList = trPage.getRecords();

            // 查询用户信息
            List<Long> userIds = stpList.stream().map(SwhyTrainingProject::getAccountId).distinct().sorted()
                .collect(Collectors.toList());
            AccountCommReqVo accountCommReqVo = new AccountCommReqVo();
            accountCommReqVo.setAccountIds(userIds);
            accountCommReqVo.setCompanyId(companyId);
            List<AccountCommVo> accountCommVos = accountClient.usersCommQueryByIds(accountCommReqVo);
            Map<Long, AccountCommVo> accountCommVoMap =
                accountCommVos.stream().collect(Collectors.toMap(AccountCommVo::getId, Function.identity()));

            // 查询培训项目信息
            List<Long> tpIdsList = stpList.stream().map(SwhyTrainingProject::getTrainingProjectId).distinct().sorted()
                .collect(Collectors.toList());
            List<TrainingProject> trainingProjects = this.listByIds(tpIdsList);
            Map<Long, TrainingProject> trainingProjectMap =
                trainingProjects.stream().collect(Collectors.toMap(TrainingProject::getId, Function.identity()));

            // 查询培训项目的课程
            QueryWrapper<TpPlanActivity> ewTPA = QueryUtil.condition(new TpPlanActivity());
            ewTPA.eq("company_id", companyId).eq("site_id", siteId).eq("deleted", 0)
                .in("training_project_id", tpIdsList);
            //.eq("type",0);
            List<TpPlanActivity> tpPlanActivities = this.tpPlanActivityService.list(ewTPA);
            Map<Long, List<Long>> courseGroup =
                tpPlanActivities.stream().filter(tpaIt -> Integer.valueOf(0).equals(tpaIt.getType()))
                    .collect(Collectors.groupingBy(TpPlanActivity::getTrainingProjectId,
                        //       LinkedHashMap::new,
                        Collectors.mapping(
                            //op -> {CourseStudyInfoList volt = new CourseStudyInfoList();volt.setAccountId(op
                            // .getTrainingProjectId());return volt;},
                            TpPlanActivity::getRelationId, Collectors.toList())));
            // 课程学习情况
            List<Long> courseIds =
                courseGroup.entrySet().stream().flatMap(itfm -> itfm.getValue().stream()).distinct().sorted()
                    .collect(Collectors.toList());
            log.info("courseIds 查询ids = {}", JSON.toJSONString(courseIds));
            CourseStudyInfoReq req = new CourseStudyInfoReq();
            req.setAccountIds(userIds);
            req.setCompanyId(companyId);
            req.setSiteId(siteId);
            req.setCourseIds(courseIds);
            List<CourseStudyInfoVo> dataCourse = courseClient.commStudyInfoQuery(req);
            Map<String, CourseStudyInfoVo> courseDataMap = dataCourse.stream().collect(
                Collectors.toMap(courseKey -> courseKey.getCourseId() + "##" + courseKey.getAccountId(),
                    Function.identity()));

            // 积分获得情况
            Map<Long, List<Long>> relationIdsGroup = tpPlanActivities.stream().collect(
                Collectors.groupingBy(TpPlanActivity::getTrainingProjectId,
                    Collectors.mapping(TpPlanActivity::getRelationId, Collectors.toList())));
            PointCommQueryReq pointCommQueryReq = new PointCommQueryReq();
            pointCommQueryReq.setAccountIds(userIds);
            pointCommQueryReq.setCompanyId(companyId);
            pointCommQueryReq.setSiteId(siteId);
            pointCommQueryReq.setCourseIds(courseIds);
            List<PointCommQueryVo> pointCommQueryVos = this.pointFeignClients.commInfoQuery(pointCommQueryReq);
            Map<String, Integer> pointCommQueryVoMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(pointCommQueryVos)) {
                pointCommQueryVoMap = pointCommQueryVos.stream().collect(
                    Collectors.toMap(courseKey -> courseKey.getCourseId() + "##" + courseKey.getAccountId(),
                        PointCommQueryVo::getPoints));
            }

            for (SwhyTrainingProject swtp : stpList) {
                Long tpId = swtp.getTrainingProjectId();
                swtp.setId(swtp.getAccountId() + swtp.getTrainingProjectId());
                if (trainingProjectMap.containsKey(tpId)) {
                    TrainingProject trainingProject = trainingProjectMap.get(tpId);
                    swtp.setProjectName(trainingProject.getName());
                    Date startTime = trainingProject.getStartTime();
                    if (startTime != null) {
                        swtp.setProjectStartTime(formatter.format(startTime));
                    }
                    Date endTime = trainingProject.getEndTime();
                    if (endTime != null) {
                        swtp.setProjectEndTime(formatter.format(endTime));
                    }
                }
                Long accountId = swtp.getAccountId();
                if (accountCommVoMap.containsKey(accountId)) {
                    AccountCommVo vo = accountCommVoMap.get(accountId);
                    swtp.setUserName(vo.getName());
                    swtp.setWorkNum(vo.getWorkNum());
                    swtp.setDept(vo.getDept());
                }
                if (courseGroup.containsKey(tpId)) {
                    List<Long> courseIdsMem = courseGroup.get(tpId);
                    long courseStudyDuration = 0;
                    float classHour = 0.0f;
                    for (Long courseId : courseIdsMem) {
                        String courseKey = courseId + "##" + accountId;
                        if (courseDataMap.containsKey(courseKey)) {
                            CourseStudyInfoVo infoVo = courseDataMap.get(courseKey);
                            if (infoVo.getCourseStudyDuration() != null) {
                                courseStudyDuration += infoVo.getCourseStudyDuration();
                            }
                            if (infoVo.getClassHour() != null) {
                                classHour += infoVo.getClassHour();
                            }
                        }
                    }
                    swtp.setCourseStudyDuration(courseStudyDuration);
                    swtp.setClassHour(classHour);
                }
                if (relationIdsGroup.containsKey(tpId)) {
                    List<Long> relationIdsItem = relationIdsGroup.get(tpId);
                    int point = 0;
                    for (Long rii : relationIdsItem) {
                        String relationKey = rii + "##" + accountId;
                        if (pointCommQueryVoMap.containsKey(relationKey)) {
                            point += pointCommQueryVoMap.get(relationKey);
                        }
                    }
                    swtp.setPoints(point);
                }
            }
        }
        return trPage;
    }

    @Override
    public Page<TrainingProject> searchPage(String name, Long tpClassificationId, Integer status, Long companyId,
        Long siteId, List<Long> orgId, int pageNo, int pageSize) {
        HQueryUtil.startHQ(TrainingProject.class);
        Page<TrainingProject> page = new Page<>(pageNo, pageSize);
        baseMapper.searchPage(name, tpClassificationId, status, companyId, siteId, page);
        return page;
    }

    @Override
    public Page<TrainingProjectFoVo> searchFoPage(SearchProjectVo searchProjectVo) {
        HQueryUtil.startHQ(TrainingProjectFoVo.class);
        Page<TrainingProjectFoVo> page = new Page<>(searchProjectVo.getPageNo(), searchProjectVo.getPageSize());
        baseMapper.searchFoPage(searchProjectVo.getSiteId(), page);
        return page;
    }

    @Override
    public Page<com.yizhi.training.application.vo.domain.TrainingProjectVo> searchPageV2(String name,
        Long tpClassificationId, Integer status, Integer enrollStatus, Long companyId, Long siteId, List<Long> orgId,
        int pageNo, int pageSize) {
        HQueryUtil.startHQ(TrainingProject.class);
        Page<com.yizhi.training.application.vo.domain.TrainingProjectVo> page = new Page<>(pageNo, pageSize);
        Integer enableEnroll = null;
        Integer enablePay = null;
        if (TrEnrollStatusEnum.NO_ENROLL.getCode().equals(enrollStatus)) {
            //无需报名
            enableEnroll = 0;
        } else if (TrEnrollStatusEnum.FREE_ENROLL.getCode().equals(enrollStatus)) {
            //免费报名
            enableEnroll = 1;
            enablePay = 0;
        } else if (TrEnrollStatusEnum.PAY_ENROLL.getCode().equals(enrollStatus)) {
            //付费报名
            enableEnroll = 1;
            enablePay = 1;
        }

        List<com.yizhi.training.application.vo.domain.TrainingProjectVo> trainingProjects =
            this.baseMapper.searchPageV2(name, tpClassificationId, status, enableEnroll, enablePay, companyId, siteId,
                page);
        page.setRecords(trainingProjects);
        return page;
    }

    @Override
    public TrainingProject saveTrPro(TrainingProject trainingProject) {
        trainingProject.setId(idGenerator.generate());
        trainingProjectMapper.insert(trainingProject);
        return trainingProject;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TrainingProject update(TrainingProject trainingProject) throws Exception {
        TrainingProject old = trainingProjectMapper.selectById(trainingProject.getId());
        if (old.getStatus().equals(ProjectConstant.PROJECT_STATUS_ENABLE)) {
            throw new Exception("只有草稿、下架状态可以被修改");
        }
        trainingProjectMapper.updateById(trainingProject);
        //判断报名开关是否开启；如果关闭；则删除报名配置信息
        Integer enableEnroll = trainingProject.getEnableEnroll();
        Long trainingProjectId = trainingProject.getId();
        if (null != enableEnroll && enableEnroll.equals(0)) {
            //报名关闭;查询并删除
            Enroll enroll = enrollFeignClient.selectByProjectId(trainingProjectId);
            if (null != enroll) {
                ProjectAccountVO vo = new ProjectAccountVO();
                vo.setProjectId(trainingProjectId);
                Integer result = enrollFeignClient.deleteEnrollByProjectId(vo);
                LOGGER.info("删除项目报名结果;1成功；-1失败: projectId = {} , result = {}", trainingProject, result);
                if (result.equals(-1)) {
                    throw new Exception("Call enroll failed.");
                }
            }
        }
        return trainingProjectMapper.selectById(trainingProject.getId());
    }

    @Override
    public Integer saveStepThree(BaseModel<TrainingProjectStepThreeVo> model) throws Exception {
        Long tpId = model.getObj().getTrainingProjectId();
        TrainingProject trainingProject = trainingProjectMapper.selectById(tpId);

        TpPlan tpPlan = new TpPlan();
        tpPlan.setTrainingProjectId(trainingProject.getId());
        tpPlan.setDeleted(0);
        QueryWrapper wrapper = new QueryWrapper(tpPlan);
        List<TpPlan> tpPlans = tpPlanMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(tpPlans)) {
            for (TpPlan plan : tpPlans) {
                if (DateUtils.ceiling(trainingProject.getStartTime(), Calendar.DATE).getTime() > DateUtils.ceiling(
                    plan.getStartTime(), Calendar.DATE).getTime() || DateUtils.ceiling(trainingProject.getEndTime(),
                    Calendar.DATE).getTime() < DateUtils.ceiling(plan.getEndTime(), Calendar.DATE).getTime()) {
                    return 4000;
                }
            }

        }

        if (null == trainingProject) {
            throw new Exception("没有查询到培训项目，查询id：" + model.getObj().getTrainingProjectId());
        }
        RequestContext context = model.getContext();
        TrainingProjectStepThreeVo vo = model.getObj();
        Date now = model.getDate();
        int result = 0;
        TpRemindVo tpRemindVo = vo.getRemindVo();
        if (tpRemindVo != null) {
            //此处不可删
            trainingProject.setEnableRemindApp(vo.getRemindVo().getEnableApp());
        } else {
            LOGGER.info("remindVo为空！！！");
        }

        //填充积分
        trainingProject.setPoint(vo.getPoint());
        trainingProject.setEnableTask(vo.getEnableTask());
        trainingProject.setEnableQueue(vo.getEnableQueue());
        LOGGER.info("trainingProjectId = {},enableQueue={}", trainingProject.getId(), vo.getEnableQueue());
        trainingProject.updateById();

        //这里是为了把计划的可见范围与培训项目的可见范围同步
        if (trainingProject != null) {
            this.trPlanUpdateStatus(tpPlans, trainingProject, context, true);
        }
        try {
            if (trainingProject != null && vo.getMessageRemindVo() != null) {
                //发消息告知提醒有变化
                taskExecutor.asynExecute(new AbstractTaskHandler() {
                    @Override
                    public void handle() {
                        MessageRemindVo mrv = vo.getMessageRemindVo();
                        MessageRemindVo mrvs = new MessageRemindVo();
                        BeanUtils.copyProperties(mrv, mrvs);
                        trainingEvenSendMessage.systemSendMessage(trainingProject, null, mrvs, context);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("项目保存saveStepThree异常: ", e);
        }
        return result;
    }

    @Override
    public List<Long> getProjectForCodeExchange(Boolean isMall) {
        RequestContext rc = ContextHolder.get();
        Long accountId = rc.getAccountId();
        Long siteId = rc.getSiteId();
        Long companyId = rc.getCompanyId();
        ExchangeCode ec = new ExchangeCode();
        ec.setAccountId(accountId);
        ec.setCompanyId(companyId);
        ec.setSiteId(siteId);
        ec.setDeleted(0);
        List<ExchangeCode> exchangeCodes = exchangeCodeService.list(QueryUtil.condition(ec));
        if (CollectionUtils.isEmpty(exchangeCodes)) {
            LOGGER.info("当前用户{}未使用兑换码兑换任何项目", accountId);
            return new ArrayList<>();
        }
        Set<Long> tpid = exchangeCodes.stream().map(key -> key.getRefId()).collect(Collectors.toSet());
        List<Enroll> enrollList = enrollFeignClient.getEnrollList(new ArrayList<>(tpid));
        Map<Long, Enroll> enrollMap =
            enrollList.stream().collect(Collectors.toMap(key -> key.getTrainingProjectId(), val -> val));
        List<TrainingProject> tps = this.list(new QueryWrapper<TrainingProject>().in("id", new ArrayList<>(tpid)));
        if (CollectionUtils.isEmpty(tps)) {
            return new ArrayList<>();
        }
        List<Long> tpIds = new ArrayList<>();
        for (TrainingProject tp : tps) {
            Enroll enroll = enrollMap.get(tp.getId());
            if (enroll == null || enroll.getPayType().equals(0)) {
                continue;
            }
            if (isMall && enroll.getPayType().equals(EnrollCommonEnums.ENROLL_PAY_TYPE_EXCHANGE_CODE.getCode())) {
                continue;
            }
            tpIds.add(enroll.getTrainingProjectId());
        }
        return tpIds;
    }

    @Override
    public TrainingProjectStepThreeVo stepThreeView(Long id) {
        TrainingProject tp = trainingProjectMapper.selectById(id);
        if (tp != null) {
            TrainingProjectStepThreeVo vo = new TrainingProjectStepThreeVo();
            vo.setTrainingProjectId(id);
            //填充积分设置
            vo.setPoint(tp.getPoint());
            // 组装提醒

            //回显日历任务设vo置
            vo.setEnableTask(tp.getEnableTask());

            TpRemindVo tpRemindVo = new TpRemindVo();
            tpRemindVo.setAppTemplateId(tp.getAppRemindTemplateId());
            //            tpRemindVo.setContent(tp.getRemindContent());
            tpRemindVo.setEnableApp(tp.getEnableRemindApp());
            tpRemindVo.setEnableMail(tp.getEnableRemindMail());
            vo.setRemindVo(tpRemindVo);
            //设置默认不显示‘项目列表显示’参数
            vo.setEnableQueue(0);
            Integer enableEnroll = tp.getEnableEnroll();
            if (null != enableEnroll && enableEnroll.equals(1)) {
                Enroll enroll = enrollFeignClient.selectByProjectId(id);
                if (null != enroll) {
                    Integer enablePay = enroll.getEnablePay();
                    if (null != enablePay && enablePay.equals(1)) {
                        //启动报名配置,才显示项目列表参数
                        vo.setEnableQueue(tp.getEnableQueue());
                    }
                }
            }

            // 组装可见范围
            TpAuthorizationRange range = new TpAuthorizationRange();
            range.setBizId(id);
            range.setDeleted(ProjectConstant.DELETED_NO);
            List<TpAuthorizationRange> ranges = tpAuthorizationRangeMapper.selectList(new QueryWrapper<>(range));
            if (!CollectionUtils.isEmpty(ranges)) {
                VisibleRangeVo rangeVo = new VisibleRangeVo();
                List<RelationIdVo> idVos = new ArrayList<>(ranges.size());
                for (TpAuthorizationRange tpRange : ranges) {
                    RelationIdVo idVo = new RelationIdVo();
                    idVo.setName(tpRange.getName());
                    idVo.setRelationId(tpRange.getRelationId());
                    idVo.setType(tpRange.getType());
                    idVos.add(idVo);
                }
                vo.setVisibleRangeVo(rangeVo);
            }
            return vo;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer batchDelete(BaseModel<List<Long>> model) {
        List<Long> ids = model.getObj();
        if (!CollectionUtils.isEmpty(ids)) {
            int num = trainingProjectMapper.batchDelete(ids, model.getContext().getAccountId(),
                model.getContext().getAccountName(), model.getDate());
            // 再删除培训项目提醒
            tpRemindMapper.batchDeleteByTpIds(ids);
            // 再删除计划
            List<Long> tpPlanIds = tpPlanMapper.getIdsByTpIds(model.getObj());
            if (!CollectionUtils.isEmpty(tpPlanIds)) {
                tpPlanMapper.deleteByIds(tpPlanIds, model.getContext().getAccountId(),
                    model.getContext().getAccountName(), model.getDate());
                // 再删除计划条件
                tpPlanConditionPreMapper.deleteByPlanIds(tpPlanIds);
                tpPlanConditionPostMapper.deleteByPlanIds(tpPlanIds);
                // 再删除计划提醒
                tpPlanRemindMapper.batchDeleteByTpPlanIds(tpPlanIds);
                // 再删除活动
                List<Long> activityIds = tpPlanActivityMapper.getIdsByTpPlanIds(tpPlanIds);
                if (!CollectionUtils.isEmpty(activityIds)) {
                    //删除活动 触发通知需要的业务删除关联关系
                    deleteActivityAdvice.deletedActivityAdvice(ids, model.getContext(), model.getDate());
                    // 再删除活动条件
                    tpPlanActivityConditionPreMapper.deleteByActivityIds(activityIds);
                    tpPlanActivityConditionPostMapper.deleteByActivityIds(activityIds);
                }
            }
            return num;
        }
        return null;
    }

    @Override
    public Page<TrainingProjectListVo> apiPageList(BaseModel<TrainingProjectParamVo> model) throws IOException {
        RequestContext context = model.getContext();
        TrainingProjectParamVo paramVo = model.getObj();
        Integer enablePay = paramVo.getEnablePay();
        if (null != enablePay && enablePay.equals(1)) {
            enablePay = 1;
        } else {
            enablePay = 0;
        }
        // 可见范围
        List<Long> relationIds = context.getRelationIds();
        // 指定范围的可见
        List<Long> visiableTpIds = null;
        if (!CollectionUtils.isEmpty(relationIds)) {
            visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, context.getSiteId());
        }
        // 报名通过的
        List<Long> passEnrollTpIds = tpStudentEnrollPassedMapper.selectTpIds(context.getAccountId());
        // 通过兑换码兑换的
        List<Long> codeExchange = getProjectForCodeExchange(false);
        passEnrollTpIds.addAll(codeExchange);
        Page<TrainingProjectListVo> page = new Page<>(paramVo.getPageNo(), paramVo.getPageSize());
        List<TrainingProjectListVo> pageList = new ArrayList<>();
        List<com.yizhi.training.application.vo.domain.TrainingProjectVo> list =
            trainingProjectMapper.apiPageList(visiableTpIds, passEnrollTpIds, paramVo.getNow(), context.getSiteId(),
                paramVo.getKeyword(), enablePay, page);
        List<Long> finishedTpIds = tpStudentProjectRecordMapper.getByAccountId(context.getAccountId());

        if (!CollectionUtils.isEmpty(list)) {
            List<Long> tpIdsForJoinNum = null;
            List<Long> relationIdsForJoinNum = null;
            for (com.yizhi.training.application.vo.domain.TrainingProjectVo tr : list) {
                TrainingProjectListVo vo = new TrainingProjectListVo();
                vo.setEndTime(tr.getEndTime());
                vo.setId(tr.getId());
                vo.setLogoImg(tr.getLogoImg());
                vo.setName(tr.getName());
                vo.setStartTime(tr.getStartTime());
                vo.setFinished(finishedTpIds.contains(vo.getId()));
                // 参加人数 浏览人次（同一人只算一次）
                vo.setJoinNumber(getJoinNumberV2(tr));
                //是否付费标签
                vo.setEnablePay(tr.getEnablePay());
                //付费方式
                vo.setPayType(tr.getPayType());
                pageList.add(vo);
            }
        }
        //page.setTotal(trainingProjectMapper.apiPageListCount(visiableTpIds, passEnrollTpIds, paramVo.getNow(),
        // context.getSiteId(), paramVo.getKeyword()));
        page.setRecords(pageList);
        return page;
    }

    @Override
    public Page<HotEnrollListVo> apiHotPageList(BaseModel<HotEnrollParamVo> model) {

        // 报名通过的
        List<Long> passEnrollTpIds = tpStudentEnrollPassedMapper.selectTpIds(model.getContext().getAccountId());
        // 通过兑换码兑换的
        List<Long> projectIds = getProjectForCodeExchange(false);
        passEnrollTpIds.addAll(projectIds);
        // 指定范围的可见
        LOGGER.trace("apiHotPageList-RequestContext={}", JSONObject.toJSONString(model.getContext()));
        List<Long> visiableTpIds = null;
        if (!CollectionUtils.isEmpty(model.getContext().getRelationIds())) {
            visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(model.getContext().getRelationIds(),
                model.getContext().getSiteId());
        }
        //前端筛选是否付费条件；付费为1；全部设置为0
        Integer enablePay = model.getObj().getEnablePay();
        if (null != enablePay && enablePay.equals(1)) {
            enablePay = 1;
        } else {
            enablePay = 0;
        }
        Page<HotEnrollListVo> page = new Page<>(model.getObj().getPageNo(), model.getObj().getPageSize());
        page.setRecords(
            trainingProjectMapper.apiHotPageList(model.getContext().getSiteId(), passEnrollTpIds, visiableTpIds,
                new Date(), enablePay, page));
        //page.setTotal(trainingProjectMapper.apiHotPageListNum(model.getContext().getSiteId(), passEnrollTpIds,
        // visiableTpIds, new Date()));
        return page;
    }

    @Override
    public Page<TrainingProjectListVo> apiMyPageList(BaseModel<TrainingProjectMyParamVo> model) throws Exception {
        Long accountId = model.getContext().getAccountId();
        Long siteId = model.getContext().getSiteId();
        Date now = model.getDate();
        TrainingProjectMyParamVo vo = model.getObj();

        // 可见范围授权id
        List<Long> relationIds = model.getContext().getRelationIds();
        // 指定范围的可见
        List<Long> visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, siteId);
        // 报名通过的
        //List<Long> passEnrollTpIds = tpStudentEnrollPassedMapper.selectTpIds(model.getContext().getAccountId());
        // 已经完成的
        List<Long> finishedIds = tpStudentProjectRecordMapper.getByAccountId(accountId);

        List<TrainingProjectListVo> trainingProjectListVos = new ArrayList<>();
        Page<TrainingProject> projectPage = new Page<>(vo.getPageNo(), vo.getPageSize());
        Page<TrainingProjectListVo> page = new Page<>(vo.getPageNo(), vo.getPageSize());
        // 未开始
        // 开始时间大于当前时间 且 可见范围内
        if (vo.getType() == 1) {
            trainingProjectMapper.selectMyCommingPage(now, siteId, vo.getKeyword(), visiableTpIds, projectPage);
        }
        // 已开始
        // 开始时间小于当前时间，结束时间大于当前时间
        else if (vo.getType() == 2) {
            trainingProjectMapper.selectMyJoinedPage(now, siteId, vo.getKeyword(), visiableTpIds, finishedIds,
                projectPage);
        }
        // 已结束
        // 结束时间小于当前时间  加上 进行中且已完成
        else if (vo.getType() == 3) {
            trainingProjectMapper.selectMyFinishedPage(accountId, siteId, now, visiableTpIds, projectPage);
        }
        // 已过期
        else if (vo.getType() == 4) {
            trainingProjectMapper.selectMyExpiredPage(siteId, now, visiableTpIds, finishedIds, projectPage);
        } else {
            throw new Exception("未知的列表类型 - " + vo.getType() + "，请检查：列表类型，1：未开始，2：已开始，3：已完成");
        }
        List<TrainingProject> trainingProjects = projectPage.getRecords();
        if (!CollectionUtils.isEmpty(trainingProjects)) {
            //批量查询 报名类别
            List<Long> tpIds = trainingProjects.stream().map(TrainingProject::getId).collect(Collectors.toList());
            List<Enroll> trEnrolls = enrollFeignClient.getEnrollList(tpIds);
            Map<Long, Enroll> trEnrollMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(trEnrolls)) {
                for (Enroll te : trEnrolls) {
                    if (trEnrollMap.get(te.getTrainingProjectId()) != null) {
                        continue;
                    }
                    trEnrollMap.put(te.getTrainingProjectId(), te);
                }
            }
            for (TrainingProject trainingProject : trainingProjects) {
                TrainingProjectListVo v = new TrainingProjectListVo();
                v.setId(trainingProject.getId());
                v.setName(trainingProject.getName());
                v.setLogoImg(trainingProject.getLogoImg());
                v.setStartTime(trainingProject.getStartTime());
                v.setEndTime(trainingProject.getEndTime());
                // 参加人数 浏览人次（同一人只算一次）
                v.setJoinNumber(getJoinNumber(trainingProject));

                //新增项目包含的活动数 除证书外
                Long trainingProjectId = trainingProject.getId();
                TpPlanActivity tpa = new TpPlanActivity();
                tpa.setTrainingProjectId(trainingProjectId);
                tpa.setDeleted(0);//未被删除
                QueryWrapper<TpPlanActivity> wrapper = new QueryWrapper<>(tpa);
                wrapper.ne("type", 6);
                Integer activitieNum = Math.toIntExact(tpPlanActivityMapper.selectCount(wrapper));
                v.setActivitieNum(activitieNum);
                v.setPayType(0);
                v.setEnablePay(0);
                Enroll tr = trEnrollMap.get(v.getId());
                if (ObjectUtil.isNotEmpty(tr)) {
                    v.setEnablePay(tr.getEnablePay());
                    v.setPayType(tr.getPayType());
                }
                trainingProjectListVos.add(v);
            }
        }
        page.setTotal(projectPage.getTotal());
        page.setRecords(trainingProjectListVos);
        return page;
    }

    @Override
    public Page<TrainingProject> apiPageListNoCondition(BaseModel<Page> model) {
        Page page = model.getObj();
        List<Long> relationIds = model.getContext().getRelationIds();
        Long accountId = model.getContext().getAccountId();
        relationIds.add(accountId);
        List<Long> visiableTpIds =
            tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, model.getContext().getSiteId());
        // 防止外部调用未使用构造函数初始化page，到时 offset 和 limit 为空
        page = new Page(page.getCurrent(), page.getSize());
        trainingProjectMapper.apiPageListNoCondition(visiableTpIds, model.getContext().getSiteId(), model.getDate(),
            page);
        return page;
    }

    @Override
    public TrainingProjectDetailVo getTpDetail(Long trainingProjectId, RequestContext context, Date now,
        Boolean containStatistics) throws ParseException {
        Long accountId = context.getAccountId();
        TrainingProject project = trainingProjectMapper.selectById(trainingProjectId);
        if (null != project) {
            TrainingProjectIntroductionVo introductionVo = getTpIntroduction(project, accountId, context.getSiteId());
            TrainingProjectContentVo contentVo = tpContentStudentStatusServiceUsing.getTpContent(project, accountId);
            TrainingProjectDetailVo vo = new TrainingProjectDetailVo();
            vo.setContentVo(contentVo);
            vo.setIntroductionVo(introductionVo);
            if (containStatistics) {
                TrainingProjectProgressVo progressVo = getProgress(project, accountId, now, context);
                vo.setProgressVo(progressVo);
            }
            addViewRecord(accountId, context, project, now);
            return vo;
        }
        return null;
    }

    @Override
    public Integer getMyTrainingProjectCountNum(BaseModel<TrainingProjectParamVo> model) {

        RequestContext context = model.getContext();
        //        TrainingProjectParamVo paramVo = model.getObj();
        //        // 可见范围
        //        List<Long> relationIds = context.getRelationIds();
        //        // 指定范围的可见
        //        List<Long> visiableTpIds = null;
        //        if (!CollectionUtils.isEmpty(relationIds)) {
        //            visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, context
        //            .getSiteId());
        //        }
        //
        //        TpStudentProjectRecord record = new TpStudentProjectRecord();
        //        record.setAccountId(context.getAccountId());
        //        record.setFinished(1);
        //        List<TpStudentProjectRecord> records = tpStudentProjectRecordMapper.selectList(new QueryWrapper<>
        //        (record));
        //        List<Long> finishedIds = null;
        //        if (!CollectionUtils.isEmpty(records)) {
        //            finishedIds = new ArrayList<>();
        //            for (TpStudentProjectRecord r : records) {
        //                finishedIds.add(r.getTrainingProjectId());
        //            }
        //        }
        //
        //        Date date = model.getDate() == null ? new Date() : model.getDate();
        //        return trainingProjectMapper.getMyTrainingProjectCountNum(date, visiableTpIds, finishedIds, context
        //        .getSiteId());
        Integer count = myTpService.getCount();
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer setVisibleRange(VisibleRangeVo vo, Long accountId, String accountName, Long siteId, Date now)
        throws Exception {
        if (null == vo.getTrainingProjectId()) {
            throw new Exception("培训项目id不能为空");
        }
        TrainingProject tp = trainingProjectMapper.selectById(vo.getTrainingProjectId());
        if (null == tp) {
            throw new Exception("未查询到培训过项目--id：" + vo.getTrainingProjectId());
        }

        List<RelationIdVo> relationIdVos = vo.getList();
        tp.setUpdateById(accountId);
        tp.setUpdateByName(accountName);
        tp.setUpdateTime(now);

        // 删除以前的
        TpAuthorizationRange oldRange = new TpAuthorizationRange();
        oldRange.setSiteId(siteId);
        oldRange.setBizId(vo.getTrainingProjectId());
        tpAuthorizationRangeMapper.delete(new QueryWrapper<>(oldRange));

        if (CollectionUtils.isEmpty(relationIdVos)) {
            tp.setVisibleRange(ProjectConstant.PROJECT_VISIBLE_RANGE_SITE);
            tp.updateById();
        } else {
            tp.setVisibleRange(ProjectConstant.PROJECT_VISIBLE_RANGE_ACCOUNT);
            if (tp.updateById()) {
                List<TpAuthorizationRange> ranges = new ArrayList<>();

                for (RelationIdVo idVo : relationIdVos) {
                    TpAuthorizationRange range = new TpAuthorizationRange();
                    range.setBizId(vo.getTrainingProjectId());
                    range.setId(idGenerator.generate());
                    range.setRelationId(idVo.getRelationId());
                    range.setType(idVo.getType());
                    range.setName(idVo.getName());
                    ranges.add(range);
                }
                return tpAuthorizationRangeMapper.batchInsert(ranges);
            }
        }
        return null;
    }

    @Override
    public VisibleRangeExport vsibleRangeExport(Long trainingProjectId) {
        VisibleRangeExport visibleRangeExport = new VisibleRangeExport();
        List<Long> accountIds = new ArrayList<>();
        List<Long> orgIds = new ArrayList<>();

        TrainingProject trainingProject = trainingProjectMapper.selectById(trainingProjectId);
        if (trainingProject != null) {
            visibleRangeExport.setBizId(trainingProject.getId());
            visibleRangeExport.setBizName(trainingProject.getName());
        }

        List<TpAuthorizationRange> listStudent = tpAuthorizationRangeService.listByBizId(trainingProjectId);
        if (CollUtil.isNotEmpty(listStudent)) {
            TpAuthorizationRange tpAuthorizationRange;
            for (TpAuthorizationRange authorizationRange : listStudent) {
                tpAuthorizationRange = authorizationRange;
                if (tpAuthorizationRange != null && tpAuthorizationRange.getType() != null) {
                    if (tpAuthorizationRange.getType() == 2) {
                        accountIds.add(tpAuthorizationRange.getRelationId());
                    }
                    if (tpAuthorizationRange.getType() == 1) {
                        orgIds.add(tpAuthorizationRange.getRelationId());
                    }
                }
                visibleRangeExport.setAccountIds(accountIds);
                visibleRangeExport.setOrgIds(orgIds);
            }
        }
        return visibleRangeExport;
    }

    @Override
    public List<TrainingProjectVoPortalVo> getTrainingListByIds(List<Long> ids) {
        // TODO Auto-generated method stub
        List<TrainingProject> listTrainingProject = this.listByIds(ids);
        List<TrainingProjectVoPortalVo> listTrainingProjectPortalVo = null;
        TrainingProjectVoPortalVo tppv = null;
        if (!CollectionUtils.isEmpty(listTrainingProject)) {
            listTrainingProjectPortalVo = new ArrayList<>();
            for (int i = 0; i < listTrainingProject.size(); i++) {
                Long id = listTrainingProject.get(i).getId();
                // 项目启用的状态和未被删除的状态操作 add 2019-10-9 10:50
                if (listTrainingProject.get(i).getStatus() == 1 && listTrainingProject.get(i).getDeleted() == 0) {
                    List<Long> courseIds = tpPlanActivityService.getcourseIdsByTrainingProjectId(id);
                    tppv = new TrainingProjectVoPortalVo();
                    BeanUtils.copyProperties(listTrainingProject.get(i), tppv);
                    tppv.setListCourseIds(courseIds);//copy完bean，把课程的ids也放在目标对象里边
                    if (courseIds != null) {
                        tppv.setActivityNum(courseIds.size());
                    }
                    listTrainingProjectPortalVo.add(tppv);
                }
            }
        }
        return listTrainingProjectPortalVo;
    }

    @Override
    public Page<TrainingProject> listNotIds(List<Long> ids, String name, Long siteId, Integer pageNo,
        Integer pageSize) {
        // TODO Auto-generated method stub
        Page<TrainingProject> page = new Page<TrainingProject>(pageNo, pageSize);
        TrainingProject tp = new TrainingProject();
        tp.setSiteId(siteId);
        tp.setStatus(1);
        tp.setDeleted(0);
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<TrainingProject>(tp);
        wrapper.orderByDesc("create_time");
        if (!CollectionUtils.isEmpty(ids)) {
            wrapper.notIn("id", ids);
        }
        if (StringUtils.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        return this.page(page, wrapper);
    }

    @Override
    public List<TrainingProject> getTrainingListByRelationIds(List<Long> relationIds, Integer num, List<Long> listIds) {
        Long siteId = ContextHolder.get().getSiteId();
        return trainingProjectMapper.queryTrainingListByRelationIds(relationIds, num, siteId, listIds);

    }

    @Override
    public MyPageVO getTrainingCount(BaseModel<TrainingProjectMyParamVo> model) {

        MyPageVO pageVO = new MyPageVO();

        RequestContext context = ContextHolder.get();
        Long siteId = context.getSiteId();
        Long accountId = context.getAccountId();
        Date now = model.getDate();
        TrainingProjectMyParamVo vo = model.getObj();

        // 可见范围授权id
        List<Long> relationIds = model.getContext().getRelationIds();
        // 指定范围的可见
        List<Long> visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, siteId);
        // 报名通过的
        //List<Long> passEnrollTpIds = tpStudentEnrollPassedMapper.selectTpIds(model.getContext().getAccountId());
        // 已经完成的
        List<Long> finishedIds = tpStudentProjectRecordMapper.getByAccountId(accountId);

        // 未开始
        // 开始时间大于当前时间 且 可见范围内
        Integer unStartCount = trainingProjectMapper.selectMyCommingCount(now, siteId, vo.getKeyword(), visiableTpIds);
        pageVO.setUnStartRecords(unStartCount);
        // 已开始
        // 开始时间小于当前时间，结束时间大于当前时间
        Integer processCount =
            trainingProjectMapper.selectMyJoinedCount(now, siteId, vo.getKeyword(), visiableTpIds, finishedIds);
        pageVO.setProcessRecords(processCount);
        // 已结束
        Integer finishedCount = trainingProjectMapper.selectMyFinishedCount(accountId, siteId);
        LOGGER.info("已完成的数量是多少：{}", finishedCount);
        pageVO.setFinishRecords(finishedCount);

        return pageVO;
    }

    @Override
    public List<TrainingProject> getTrainingList(List<Long> ids) {

        RequestContext context = ContextHolder.get();
        List<Long> relationIds = context.getRelationIds();
        Long accountId = context.getAccountId();
        relationIds.add(accountId);

        List<Long> visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, context.getSiteId());

        List<Long> proIds = ids.parallelStream().map(id -> {
            Integer visibleRange = this.getById(id).getVisibleRange();
            if (visibleRange == 1) {
                return id;
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        visiableTpIds.addAll(proIds);

        List<TrainingProject> trainingProjectList;

        if (visiableTpIds.size() > 3) {
            trainingProjectList =
                trainingProjectMapper.getTrainingList(visiableTpIds.subList(0, 4), context.getSiteId(), new Date());
        } else {
            trainingProjectList = trainingProjectMapper.getTrainingList(visiableTpIds, context.getSiteId(), new Date());
        }

        LOGGER.info("返回的培训list：{}", trainingProjectList);
        return trainingProjectList;
    }

    @Override
    public List<TrainingProject> getCaseLibraryRangeProjects(RequestContext res) {

        Date date = new Date();

        // 可见范围
        List<Long> relationIds = res.getRelationIds();
        // 指定范围的可见
        List<Long> visiableTpIds = null;
        if (!CollectionUtils.isEmpty(relationIds)) {
            visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, res.getSiteId());
        }
        // 报名通过的
        List<Long> passEnrollTpIds = tpStudentEnrollPassedMapper.selectTpIds(res.getAccountId());

        return trainingProjectMapper.getCaseLibraryRangeProjects(visiableTpIds, passEnrollTpIds, date, res.getSiteId());
    }

    @Override
    public Page<com.yizhi.training.application.vo.domain.TrainingProjectVo> getPageToCalendar(Date date,
        Page<TrainingProject> page) {
        RequestContext context = ContextHolder.get();
        Page<com.yizhi.training.application.vo.domain.TrainingProjectVo> page1 = new Page<>();
        BeanUtils.copyProperties(page, page1);
        List<Long> ids = trainingProjectMapper.getIdsByDate(date, context.getSiteId());
        if (CollectionUtils.isEmpty(ids)) {
            return page1;
        }

        //根据可见范围获取培训ids
        List<Long> trIds = tpAuthorizationRangeMapper.getUsefulIds(ids, context.getRelationIds(), context.getSiteId());
        //获取已完成的培训ids
        List<Long> finishTrIds =
            tpStudentProjectRecordMapper.getFinshIdsByIds(ids, context.getAccountId(), context.getSiteId());
        List<TrainingProject> list =
            trainingProjectMapper.getPageToCalendar(finishTrIds, trIds, date, context.getSiteId(), page);
        List<com.yizhi.training.application.vo.domain.TrainingProjectVo> list1 = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (TrainingProject tr : list) {
                com.yizhi.training.application.vo.domain.TrainingProjectVo trainingProjectVo =
                    new com.yizhi.training.application.vo.domain.TrainingProjectVo();
                BeanUtils.copyProperties(tr, trainingProjectVo);
                //参加人数
                trainingProjectVo.setJoinNumber(getJoinNumberV2(trainingProjectVo));
                list1.add(trainingProjectVo);
            }
        }

        page1.setRecords(list1);
        page1.setTotal(trainingProjectMapper.getPageToCalendarNum(finishTrIds, trIds, date, context.getSiteId()));
        return page1;
    }

    @Override
    public Page<DroolsVo> getPageByDrools(String field, String value, Page<DroolsVo> page) {
        if (StringUtils.isBlank(field)) {
            LOGGER.info("列名不能为空！");
            return page;
        }
        if (field.equalsIgnoreCase(TaskParamsEnums.CLASSIFY.getCode())) {
            field = "name";
            page = tpClassificationService.getClassifyNameByDrools(field, value, page);
        } else {
            if (field.equalsIgnoreCase(TaskParamsEnums.NAME.getCode())) {
                return getPage(field, value, page);
            } else if (field.equalsIgnoreCase(TaskParamsEnums.KEYWORD.getCode())) {
                field = "key_words";
                return getPage(field, value, page);
            }
        }
        return page;
    }

    /**
     * 计划更新消息那边的业务状态或者可见范围
     *
     * @param tpPlans
     * @param trainingProject
     * @param context
     */
    @Override
    public void trPlanUpdateStatus(List<TpPlan> tpPlans, TrainingProject trainingProject, RequestContext context,
        Boolean updateVisibleRange) {
        if (!CollectionUtils.isEmpty(tpPlans)) {
            MessageRemindVo messageRemindVo = new MessageRemindVo();
            if (updateVisibleRange) {
                messageRemindVo.setVisibleRangeUpdate(true);
            } else {
                messageRemindVo.setTaskStatusUpdate(true);
            }
            for (TpPlan plan : tpPlans) {
                if (plan.getEnableRemindApp() == 1) {
                    try {
                        taskExecutor.asynExecute(new AbstractTaskHandler() {
                            @Override
                            public void handle() {
                                trainingEvenSendMessage.systemSendMessage(trainingProject, plan.getId(),
                                    messageRemindVo, context);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
    }

    /**
     * 获取精品内容付费项目
     *
     * @param qo
     * @return
     */
    @Override
    public List<PaidTrainingProjectVO> getPaidTrainingProject(PaidTrainingProjectQO qo) {
        RequestContext context = ContextHolder.get();
        // 可见范围
        List<Long> relationIds = context.getRelationIds();
        // 指定范围的可见
        List<Long> visiableTpIds = null;
        if (!CollectionUtils.isEmpty(relationIds)) {
            visiableTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, context.getSiteId());
        }
        return trainingProjectMapper.apiPaidPageList(visiableTpIds, new Date(), qo.getSiteId(), qo.getKeyword(),
            qo.getOrderField(), qo.getOrder());
    }

    @Override
    public com.yizhi.training.application.vo.domain.TrainingProjectVo getProjectDescription(Long projectId) {
        return trainingProjectMapper.getProjectDesc(projectId);
    }

    @Override
    public Page<GainPointProjectVo> getGainPointCourses(Integer pageNo, Integer pageSize, String title) {

        RequestContext context = ContextHolder.get();
        List<Long> relationIds = context.getRelationIds();
        if (null == relationIds) {
            relationIds = new ArrayList<>();
            relationIds.add(context.getOrgId());
            relationIds.add(context.getAccountId());
        }
        Page<GainPointProjectVo> page = new Page<>(pageNo, pageSize);
        //获取 ：可见范围内，项目时间内，未参加过的项目（配了积分）项目列表
        List<GainPointProjectVo> list =
            trainingProjectMapper.pageGainPointList(context.getCompanyId(), context.getSiteId(), context.getAccountId(),
                context.getOrgId(), relationIds, title, page);
        page.setRecords(list);
        return page;
    }

    @Override
    public List<TrainJoinNumVO> getTrainJoinNumber(List<Long> trainProjectIds) {

        List<TrainJoinNumVO> trainJoinNumVOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(trainProjectIds)) {
            log.error("trainProjectIds为空，查询结束");
            return trainJoinNumVOS;
        }
        List<TrainingProject> list = listByIds(trainProjectIds);
        if (CollectionUtils.isEmpty(list)) {
            return trainJoinNumVOS;
        }
        for (TrainingProject tr : list) {
            TrainJoinNumVO vo = new TrainJoinNumVO();
            vo.setTrainProjectId(tr.getId());
            //            int count;
            //            if (tr.getVisibleRange() == 1) {
            //                count = tpViewRecordMapper.getViewNum(tr);
            //            } else {
            //                count = tpViewRecordMapper.getViewNumRange(tr);
            //            }
            int count = tpViewRecordService.getViewNumRange(tr);
            vo.setJoinNum(count);
            trainJoinNumVOS.add(vo);
        }
        return trainJoinNumVOS;
    }

    @Override
    public Page<TrainDashboardResourceVO> dashboardLatestUpdateList(Integer pageNo, Integer pageSize) {

        Page<TrainDashboardResourceVO> page = new Page<>(pageNo, pageSize);
        RequestContext context = ContextHolder.get();
        Long companyId = context.getCompanyId();
        Long siteId = context.getSiteId();
        HQueryUtil.startHQ(TrainingProject.class);
        List<TrainDashboardResourceVO> records = trainingProjectMapper.selectPageList(companyId, siteId, page);
        if (CollectionUtils.isEmpty(records)) {
            return page;
        }
        // 查询参加人数和人次
        List<Long> ids = records.stream().map(TrainDashboardResourceVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return page;
        }
        //        List<TrainDashboardResourceVO> list = trainingProjectMapper.selectJoinCount(companyId, siteId, ids);
        List<TrainDashboardResourceVO> list = selectJoinCount(companyId, siteId, ids);
        if (!CollectionUtils.isEmpty(list)) {
            Map<Long, TrainDashboardResourceVO> map =
                list.stream().collect(Collectors.toMap(TrainDashboardResourceVO::getId, Function.identity()));

            records.forEach(resourceVo -> {

                Long projectId = resourceVo.getId();
                TrainDashboardResourceVO vo = map.get(projectId);
                if (null != vo) {
                    resourceVo.setJoinCount(vo.getJoinCount());
                    resourceVo.setJoinPersonTime(vo.getJoinPersonTime());
                }
            });
        }
        page.setRecords(records);
        return page;
    }

    @Override
    public TpStudentProjectRecordVo getProjectFinishRecords(Long accountId, Long tpId) {
        QueryWrapper<TpStudentProjectRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("finished", 1);
        wrapper.eq("account_id", accountId);
        List<TpStudentProjectRecord> tpStudentProjectRecords = tpStudentProjectRecordMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(tpStudentProjectRecords)) {
            TpStudentProjectRecordVo tpStudentProjectRecordVo = new TpStudentProjectRecordVo();
            TpStudentProjectRecord tpStudentProjectRecord = tpStudentProjectRecords.get(0);
            BeanUtils.copyProperties(tpStudentProjectRecord, tpStudentProjectRecordVo);
            return tpStudentProjectRecordVo;
        }
        return null;
    }

    @Override
    public TpStudentPlanRecordVo getPlanFinishRecords(Long accountId, Long planId) {
        QueryWrapper<TpStudentPlanRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_plan_id", planId);
        wrapper.eq("finished", 1);
        wrapper.eq("account_id", accountId);
        List<TpStudentPlanRecord> tpStudentPlanRecords = tpStudentPlanRecordMapper.selectList(wrapper);
        if (!CollectionUtils.isEmpty(tpStudentPlanRecords)) {
            TpStudentPlanRecordVo tpStudentPlanRecordVo = new TpStudentPlanRecordVo();
            TpStudentPlanRecord tpStudentPlanRecord = tpStudentPlanRecords.get(0);
            BeanUtils.copyProperties(tpStudentPlanRecord, tpStudentPlanRecordVo);
            return tpStudentPlanRecordVo;
        }
        return null;
    }

    @Override
    public boolean setWeight(Long bizId, Integer weight) {
        TrainingProject byId = this.getById(bizId);
        if (byId != null) {
            byId.setWeight(weight);
            return this.updateById(byId);
        }
        return false;
    }

    @Override
    public TrainingProject getByIdWithJoinNumber(Long id) {
        RequestContext context = ContextHolder.get();
        TrainingProject byId = this.getById(id);
        if (byId != null) {
            //            List<TrainDashboardResourceVO> list =
            //                trainingProjectMapper.selectJoinCount(context.getCompanyId(), context.getSiteId(),
            //                    Collections.singletonList(id));
            List<TrainDashboardResourceVO> list =
                selectJoinCount(context.getCompanyId(), context.getSiteId(), Collections.singletonList(id));
            if (!CollectionUtils.isEmpty(list)) {
                Map<Long, TrainDashboardResourceVO> map =
                    list.stream().collect(Collectors.toMap(TrainDashboardResourceVO::getId, Function.identity()));
                TrainDashboardResourceVO vo = map.get(id);
                if (null != vo) {
                    byId.setJoinNumber(Math.toIntExact(vo.getJoinCount()));
                } else {
                    byId.setJoinNumber(0);
                }
            }
            //支付类型
            if (byId.getEnableEnroll() == 1) {
                byId.setPayType(getPayType(byId.getId()));
            }
        }
        return byId;
    }

    @Override
    public List<TrainDashboardResourceVO> selectJoinCount(Long companyId, Long siteId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        String result = ids.stream().map(String::valueOf) // 将每个Long类型元素转换为String
            .collect(Collectors.joining(","));
        String redisKey = "training:join:count:" + companyId + ":" + siteId + ":" + result;
        Object o = redisCache.get(redisKey);
        if (Objects.nonNull(o)) {
            return JSON.parseArray(o.toString(), TrainDashboardResourceVO.class);
        }
        List<TrainDashboardResourceVO> trainDashboardResourceVOS =
            trainingProjectMapper.selectJoinCount(companyId, siteId, ids);
        if (CollectionUtils.isEmpty(trainDashboardResourceVOS)) {
            return new ArrayList<>();

        }
        redisCache.set(redisKey, JSON.toJSONString(trainDashboardResourceVOS), 600);
        return trainDashboardResourceVOS;
    }

    private Integer getPayType(Long tpId) {
        TpEnroll one = tpEnrollService.selectByTpId(tpId);
        if (Objects.isNull(one)) {
            return 0;
        }
        return one.getPayType();
    }

    public Page getPage(String field, String value, Page<DroolsVo> page) {
        RequestContext requestContext = ContextHolder.get();
        Long siteId = requestContext.getSiteId();
        Long companyId = requestContext.getCompanyId();

        TrainingProject trainingProject = new TrainingProject();
        trainingProject.setSiteId(siteId);
        trainingProject.setCompanyId(companyId);
        trainingProject.setDeleted(0);
        trainingProject.setStatus(1);
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>(trainingProject);
        wrapper.select("distinct(" + field + ")," + "id ").isNotNull(field).like(field, value)
            .orderByDesc("create_time");

        String upperField = ClassUtil.getFieldName(field);
        List<DroolsVo> voList;
        Page<TrainingProject> projectPage = new Page<>(page.getCurrent(), page.getSize());
        this.baseMapper.selectPage(projectPage, wrapper);
        List<TrainingProject> list = projectPage.getRecords();
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(list)) {
            voList = new ArrayList<>(list.size());
            for (TrainingProject a : list) {
                DroolsVo vo = new DroolsVo();
                vo.setTaskId(a.getId());
                vo.setTaskFieldValue(ClassUtil.invokeMethod(a, upperField));
                vo.setTaskParamsType(field);
                voList.add(vo);
            }
            page.setRecords(voList);
        }
        return page;
    }

    /**
     * 查询培训项目详情
     *
     * @param trainingProject
     * @param accountId
     * @return
     */
    private TrainingProjectIntroductionVo getTpIntroduction(TrainingProject trainingProject, Long accountId,
        Long siteId) {
        TrainingProjectIntroductionVo vo = null;
        vo = new TrainingProjectIntroductionVo();
        vo.setId(trainingProject.getId());
        vo.setLogoImg(trainingProject.getLogoImg());
        vo.setName(trainingProject.getName());
        vo.setDescription(trainingProject.getDescription());
        vo.setStartTime(trainingProject.getStartTime());
        vo.setEndTime(trainingProject.getEndTime());
        vo.setEnablePosition(trainingProject.getEnableSign().equals(1) ? trainingProject.getEnablePosition() : 0);
        boolean finished = false;
        vo.setEnablePay(0);
        vo.setPayType(0);
        // 如果需要报名，查看是否已经报名
        if (trainingProject.getEnableEnroll() == 1) {
            vo.setNeedEnroll(true);
            TpStudentEnrollPassed example = new TpStudentEnrollPassed();
            example.setAccountId(accountId);
            example.setTrainingProjectId(trainingProject.getId());
            ExchangeCode ec = new ExchangeCode();
            ec.setRefId(trainingProject.getId());
            ec.setAccountId(accountId);
            ec = exchangeCodeService.getOne(QueryUtil.condition(ec));
            // 如果已经报名
            if (null != tpStudentEnrollPassedService.getOne(
                QueryUtil.condition(example)) || (ec != null && ec.getAccountId().equals(accountId))) {
                vo.setHasEnrolled(true);
            }
            Enroll enroll = enrollFeignClient.selectByProjectId(trainingProject.getId());
            if (!ObjectUtils.isEmpty(enroll)) {
                vo.setPayType(enroll.getPayType());
                vo.setEnablePay(enroll.getEnablePay());
            }
        }
        // 判断项目是否已经完成
        Integer finishedIdsNum =
            tpStudentProjectRecordMapper.projectIsFinish(accountId, trainingProject.getId(), siteId);
        if (finishedIdsNum > 0) {
            finished = true;
        }
        vo.setHasFinished(finished);
        //判断是否显示积分数量
        Integer point = trainingProject.getPoint();
        vo.setPoint(point == null ? 0 : point);
        return vo;
    }

    /**
     * 查询学习统计
     *
     * @param project
     * @param accountId
     * @param now
     * @return
     */
    private TrainingProjectProgressVo getProgress(TrainingProject project, Long accountId, Date now,
        RequestContext context) throws ParseException {
        TrainingProjectProgressVo progressVo = new TrainingProjectProgressVo();
        //*********************************************************统计各类型活动的总数量*******************
        TpPlanActivity activity = new TpPlanActivity();
        activity.setTrainingProjectId(project.getId());
        activity.setDeleted(0);
        activity.setSiteId(context.getSiteId());
        activity.setCompanyId(context.getCompanyId());
        QueryWrapper wrapper = new QueryWrapper(activity);
        wrapper.orderByDesc("sort");
        List<TpPlanActivity> planActivities = tpPlanActivityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(planActivities)) {
            return progressVo;
        }
        List<Long> courseIds = new ArrayList<>();
        List<Long> activityRelationIds = new ArrayList<>(planActivities.size());
        for (TpPlanActivity a : planActivities) {
            activityRelationIds.add(a.getRelationId());
            switch (a.getType()) {
                case 0:
                    progressVo.setTotalCourse(progressVo.getTotalCourse() + 1);
                    courseIds.add(a.getRelationId());
                    break;
                case 1:
                    progressVo.setTotalExam(progressVo.getTotalExam() + 1);
                    break;
                case 2:
                    progressVo.setTotalResearch(progressVo.getTotalResearch() + 1);
                    break;
                case 4:
                    progressVo.setTotalVote(progressVo.getTotalVote() + 1);
                    break;
                case 5:
                    progressVo.setTotalAssignment(progressVo.getTotalAssignment() + 1);
                    break;
                default:
                    break;
            }
        }
        //查询签到的数量
        try {
            Integer finishedCount = signRecordApiClient.selectCountByTrainingProjectId(project.getId(), accountId);
            progressVo.setTotalSign(null == finishedCount ? 0 : finishedCount);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("培训统计查询签到数据异常");
        }

        //如果有课程就查询学习时间
        Long seconds = 0L;
        if (!CollectionUtils.isEmpty(courseIds)) {
            try {
                seconds = recordeClient.countStudyDurationByIds(accountId, context.getSiteId(), courseIds);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("培训统计查询课程学习数据异常");
            }
            seconds = seconds == null ? 0L : seconds;
        }
        progressVo.setTotalStudySeconds(seconds);
        progressVo.setTotalStudyHours(
            seconds / 3600 + "时" + (seconds % 3600) / 60 + "分" + (seconds % 3600) % 60 + "秒");
        //*****************************************************************************************************
        //        if (!CollectionUtils.isEmpty(activityRelationIds)) {
        //            TpStudentActivityRecord activityRecord = new TpStudentActivityRecord();
        //            activityRecord.setAccountId(accountId);
        //            activityRecord.setFinished(1);
        //            QueryWrapper<TpStudentActivityRecord> ew = new QueryWrapper<>(activityRecord);
        //            ew.in("relation_id", activityRelationIds);
        //            List<TpStudentActivityRecord> records = tpStudentActivityRecordMapper.selectList(ew);
        //            if (!CollectionUtils.isEmpty(records)) {
        //                Map<String, Long> dateCourseTime = new HashMap<>();
        //                String today = DateFormatUtils.format(now, "yyyyMMdd");
        //                List<TrainingProjectCourseRecentVo> recentVos = new ArrayList<>();
        //                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        //                Calendar c = Calendar.getInstance();
        //                for (int i = 0; i <= 6; i++) {
        //                    TrainingProjectCourseRecentVo recentVo = new TrainingProjectCourseRecentVo();
        //                    recentVo.setDate(sdf.parse(today));
        //                    recentVo.setSeconds(dateCourseTime.get(today) == null ? 0L : dateCourseTime.get(today));
        //                    recentVos.add(recentVo);
        //                    // 往前查一天
        //                    c.setTime(sdf.parse(today));
        //                    c.add(Calendar.DAY_OF_MONTH, -1);
        //                    today = DateFormatUtils.format(c.getTime(), "yyyyMMdd");
        //                }
        //                progressVo.setCourseRecentVos(recentVos);
        //            }
        //        }
        return progressVo;
    }

    /**
     * 记录一次浏览记录
     *
     * @param accountId
     * @param context
     * @param now
     */
    private void addViewRecord(long accountId, RequestContext context, TrainingProject project, Date now) {
        taskExecutor.asynExecute(new AbstractTaskHandler() {
            @Override
            public void handle() {
                TpViewRecord record = new TpViewRecord();
                record.setAccountId(accountId);
                record.setId(idGenerator.generate());
                record.setTime(now);
                record.setTrainingProjectId(project.getId());
                record.setCompanyId(context.getCompanyId());
                record.setOrgId(context.getOrgId());
                record.setSiteId(context.getSiteId());
                if (project.getVisibleRange() != 1) {
                    List<Long> relationList = new ArrayList<Long>();
                    List<TpAuthorizationRange> list = tpAuthorizationRangeService.listByBizId(project.getId());
                    list.forEach(e -> relationList.add(e.getRelationId()));
                    for (Long relationId : context.getRelationIds()) {
                        if (relationList.contains(relationId)) {
                            tpViewRecordMapper.insert(record);
                            break;
                        }
                    }
                } else {
                    tpViewRecordMapper.insert(record);
                }
            }
        });
    }

    /**
     * 获取参与人数
     *
     * @param tr
     * @return
     */
    private Integer getJoinNumber(TrainingProject tr) {
        RequestContext context = ContextHolder.get();
        String key = "tp:page:joinNumber:count:" + context.getCompanyId();
        String item = context.getSiteId() + tr.getId().toString();
        Object obj = redisCache.hget(key, item);
        int count = 0;
        if (obj != null) {
            return Integer.parseInt(String.valueOf(obj));
        }
        //        if (tr.getVisibleRange()==1) {
        //        	count = tpViewRecordMapper.getViewNum(tr);
        //		}else {
        //			count = tpViewRecordMapper.getViewNumRange(tr);
        //		}
        //
        //        redisCache.hset(key, item, count + "", 600);
        return count;
    }

    /**
     * 获取参与人数 版本2
     */
    private Integer getJoinNumberV2(com.yizhi.training.application.vo.domain.TrainingProjectVo tr) {
        RequestContext context = ContextHolder.get();
        String key = "tp:page:joinNumber:count:" + context.getCompanyId();
        String item = context.getSiteId() + tr.getId().toString();
        Object obj = redisCache.hget(key, item);
        int count = 0;
        if (obj != null) {
            return Integer.parseInt(String.valueOf(obj));
        }
        return count;
    }
}
