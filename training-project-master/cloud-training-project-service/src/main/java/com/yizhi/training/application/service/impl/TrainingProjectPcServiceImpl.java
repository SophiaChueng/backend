package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.task.AbstractTaskHandler;
import com.yizhi.core.application.task.TaskExecutor;
import com.yizhi.course.application.feign.RecordeClient;
import com.yizhi.enroll.application.feign.EnrollFeignClient;
import com.yizhi.enroll.application.vo.domain.Enroll;
import com.yizhi.sign.application.feign.SignApiClient;
import com.yizhi.sign.application.feign.SignRecordApiClient;
import com.yizhi.system.application.model.SiteOrgIdModel;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.domain.Account;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.*;
import com.yizhi.training.application.mapper.*;
import com.yizhi.training.application.service.IExchangeCodeService;
import com.yizhi.training.application.service.ITpAuthorizationRangeService;
import com.yizhi.training.application.service.ITpStudentEnrollPassedService;
import com.yizhi.training.application.service.ITrainingProjectPcService;
import com.yizhi.training.application.vo.api.TrainingProjectContentVo;
import com.yizhi.training.application.vo.api.TrainingProjectDetailVo;
import com.yizhi.training.application.vo.api.TrainingProjectIntroductionVo;
import com.yizhi.training.application.vo.api.TrainingProjectProgressVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表） 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Service
@Transactional
public class TrainingProjectPcServiceImpl extends ServiceImpl<TrainingProjectMapper, TrainingProject>
    implements ITrainingProjectPcService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingProjectServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private ITpStudentEnrollPassedService tpStudentEnrollPassedService;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    @Autowired
    private TpStudentProjectRecordMapper tpStudentProjectRecordMapper;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Autowired
    private ITpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private SignApiClient signApiClient;

    @Autowired
    private SignRecordApiClient signRecordApiClient;

    @Autowired
    private RecordeClient recordeClient;

    @Autowired
    private TpContentStudentPcStatusServiceUsing tpContentStudentPcStatusServiceUsing;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private EnrollFeignClient enrollFeignClient;

    @Autowired
    private IExchangeCodeService exchangeCodeService;

    @Override
    public TrainingProjectDetailVo getPcTpDetail(Long trainingProjectId, RequestContext context, Date now)
        throws ParseException {
        Long accountId = context.getAccountId();
        Long siteId = context.getSiteId();
        Boolean finished = false;
        TrainingProjectDetailVo vo = new TrainingProjectDetailVo();
        TrainingProject project = trainingProjectMapper.selectById(trainingProjectId);
        if (null == project) {
            LOGGER.error("获取项目详情异常siteId= {}；根据projectId = {} 没有查询到培训项目", siteId, trainingProjectId);
            return vo;
        }
        TrainingProjectIntroductionVo introductionVo = getTpIntroduction(project, accountId);
        TrainingProjectContentVo contentVo = tpContentStudentPcStatusServiceUsing.getTpContent(project, accountId);
        vo.setContentVo(contentVo);
        vo.setIntroductionVo(introductionVo);
        // 判断项目是否已经完成
        Integer finishedIdsNum = tpStudentProjectRecordMapper.projectIsFinish(accountId, trainingProjectId, siteId);
        if (finishedIdsNum > 0) {
            finished = true;
        }
        introductionVo.setHasFinished(finished);
        //填充培训统计数据
        if (introductionVo.getNeedEnroll().equals(false) || introductionVo.getHasEnrolled().equals(true)) {
            TrainingProjectProgressVo progressVo = getPcProgress(project, accountId, now);
            vo.setProgressVo(progressVo);
        }

        //计算应参加人数
        if (project.getVisibleRange() == 0) {
            vo.getIntroductionVo().setType(ProjectConstant.PROJECT_VISIBLE_RANGE_ACCOUNT);
            List<TpAuthorizationRange> rangeList = tpAuthorizationRangeService.listByBizId(trainingProjectId);
            List<Long> orgIdList = null;
            List<Long> accountList = null;
            //应参加人数
            Set<Long> set = new HashSet<>();

            if (!CollectionUtils.isEmpty(rangeList)) {
                orgIdList = new ArrayList<>(rangeList.size());
                accountList = new ArrayList<>(rangeList.size());
                for (TpAuthorizationRange a : rangeList) {
                    //如果是指定范围为部门
                    if (a.getType() == 1) {
                        orgIdList.add(a.getRelationId());
                    } else {
                        accountList.add(a.getRelationId());
                    }
                }
                if (!CollectionUtils.isEmpty(orgIdList)) {

                    SiteOrgIdModel model = new SiteOrgIdModel();
                    model.setSiteId(siteId);
                    model.setOrgIds(orgIdList);
                    //获取部门下（包含子部门）的所有人
                    List<Account> accounts = accountClient.getSiteOrgWithChildAccountNums(model);
                    if (!CollectionUtils.isEmpty(accounts)) {
                        List<Long> accountIds = accounts.stream().map(a -> a.getId()).collect(Collectors.toList());
                        set.addAll(accountIds);
                    }
                }
                if (!CollectionUtils.isEmpty(accountList)) {
                    set.addAll(accountList);
                }
                vo.getIntroductionVo().setTrMembers(set.size());
            }
        } else {
            vo.getIntroductionVo().setType(ProjectConstant.PROJECT_VISIBLE_RANGE_SITE);
        }
        addViewRecord(accountId, context, project, now);
        return vo;
    }

    /**
     * 查询培训项目详情
     *
     * @param trainingProject
     * @param accountId
     * @return
     */
    private TrainingProjectIntroductionVo getTpIntroduction(TrainingProject trainingProject, Long accountId) {
        TrainingProjectIntroductionVo vo = null;
        vo = new TrainingProjectIntroductionVo();
        vo.setId(trainingProject.getId());
        vo.setLogoImg(trainingProject.getLogoImg());
        vo.setName(trainingProject.getName());
        vo.setDescription(trainingProject.getDescription());
        vo.setStartTime(trainingProject.getStartTime());
        vo.setEndTime(trainingProject.getEndTime());
        //判断是否显示积分数量
        Integer point = trainingProject.getPoint();
        vo.setPoint(point == null ? 0 : point);
        vo.setPayType(0);//默认付费方式未不付费
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
        vo.setJoinNumber(getJoinNumber(trainingProject));
        return vo;
    }

    /**
     * 查询学习记录
     *
     * @param project
     * @param accountId
     * @param now
     * @return
     */
    private TrainingProjectProgressVo getPcProgress(TrainingProject project, Long accountId, Date now)
        throws ParseException {
        TrainingProjectProgressVo progressVo = new TrainingProjectProgressVo();
        RequestContext context = ContextHolder.get();
        //*********************************************************统计各类型活动的总数量*******************
        TpPlanActivity activity = new TpPlanActivity();
        activity.setTrainingProjectId(project.getId());
        activity.setDeleted(0);
        activity.setSiteId(context.getSiteId());
        activity.setCompanyId(context.getCompanyId());
        QueryWrapper wrapper = new QueryWrapper(activity);
        List<TpPlanActivity> planActivities = tpPlanActivityMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(planActivities)) {
            return progressVo;
        }
        List<Long> courseIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(planActivities)) {
            for (TpPlanActivity a : planActivities) {
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
            // 签到的总数量
            Integer totalCount = null;
            try {
                totalCount = signApiClient.getPcSignTimeCount(project.getId());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("培训统计查询签到数据异常");
            }
            progressVo.setTotalSign(totalCount);
            LOGGER.info("trainingProjectId为：" + project.getId());

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

            //给完成数据赋值
            setFinishData(project, accountId, progressVo);
        }
        //*******************************************************************************************************
        return progressVo;
    }

    /**
     * 记录一次浏览记录
     *
     * @param accountId
     * @param context
     * @param project
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
        //        redisCache.hset(key, item, count + "", 600);
        return count;
    }

    /**
     * 给项目完成进度赋值
     *
     * @param project
     * @param accountId
     * @param progressVo
     */
    private void setFinishData(TrainingProject project, Long accountId, TrainingProjectProgressVo progressVo) {
        List<Long> activityRelationIds = tpPlanActivityMapper.getRelationIdsByTpId(project.getId());
        if (!CollectionUtils.isEmpty(activityRelationIds)) {
            TpStudentActivityRecord activityRecord = new TpStudentActivityRecord();
            activityRecord.setAccountId(accountId);
            activityRecord.setFinished(1);
            activityRecord.setCompanyId(project.getCompanyId());
            activityRecord.setSiteId(project.getSiteId());
            QueryWrapper<TpStudentActivityRecord> ew = new QueryWrapper<>(activityRecord);
            ew.in("relation_id", activityRelationIds);
            List<TpStudentActivityRecord> records = tpStudentActivityRecordMapper.selectList(ew);

            if (!CollectionUtils.isEmpty(records)) {
                Set<Long> finishedCourseIdSet = new HashSet<>();
                Set<Long> finishedExamIdSet = new HashSet<>();
                Set<Long> finishedResearchIdSet = new HashSet<>();
                Set<Long> finishedVoteIdSet = new HashSet<>();
                Set<Long> finishedAssignmentIdSet = new HashSet<>();
                for (TpStudentActivityRecord record : records) {
                    if (record.getFinished().equals(1)) {
                        switch (record.getType()) {
                            case 0:
                                finishedCourseIdSet.add(record.getRelationId());
                                break;
                            case 1:
                                finishedExamIdSet.add(record.getRelationId());
                                break;
                            case 2:
                                finishedResearchIdSet.add(record.getRelationId());
                                break;
                            case 4:
                                finishedVoteIdSet.add(record.getRelationId());
                                break;
                            case 5:
                                finishedAssignmentIdSet.add(record.getRelationId());
                                break;
                            default:
                                break;
                        }
                    }
                }
                // 通过获取的id集合的大小来保存完成的次数
                progressVo.setFinishedCourseNum(finishedCourseIdSet.size());
                progressVo.setFinishedExamNum(finishedExamIdSet.size());
                progressVo.setFinishedResearchNum(finishedResearchIdSet.size());
                progressVo.setFinishedVoteNum(finishedVoteIdSet.size());
                progressVo.setFinishedAssignmentNum(finishedAssignmentIdSet.size());
            }
            try {
                // 签到的完成数量
                Integer finishedCount = signRecordApiClient.selectCountByTrainingProjectId(project.getId(), accountId);
                LOGGER.info("已完成的签到数量" + finishedCount);
                progressVo.setFinishedSignNum(finishedCount);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("签到服务查询异常");
            }
        }
    }
}
