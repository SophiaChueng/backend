package com.yizhi.training.application.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.enums.InternationalEnums;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.core.application.exception.CustomException;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.course.application.vo.CourseVo;
import com.yizhi.course.application.vo.domain.CourseEntityVo;
import com.yizhi.enroll.application.enums.EnrollCommonEnums;
import com.yizhi.enroll.application.feign.EnrollFeignClient;
import com.yizhi.enroll.application.vo.GetEnrollVO;
import com.yizhi.enroll.application.vo.domain.Enroll;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.system.application.vo.domain.Account;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.ExchangeCode;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.domain.factory.ExchangeCodeFactory;
import com.yizhi.training.application.enums.ExchangeCodeErrorEnum;
import com.yizhi.training.application.enums.TrainingCommonEnums;
import com.yizhi.training.application.mapper.ExchangeCodeMapper;
import com.yizhi.training.application.mapstruct.ExchangeCodeVOMapper;
import com.yizhi.training.application.qo.ExchangeCodeQO;
import com.yizhi.training.application.service.IExchangeCodeService;
import com.yizhi.training.application.service.ITpAuthorizationRangeService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.ExchangeCodeExportVO;
import com.yizhi.training.application.vo.ExchangeCodeVO;
import com.yizhi.training.application.vo.api.ExchangeTrainingProjectVO;
import com.yizhi.training.application.vo.api.PaidTrainingProjectVO;
import com.yizhi.util.application.page.PageCopyMapper;
import com.yizhi.util.application.page.PageInfo;
import com.yizhi.util.application.page.PageInfoFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 兑换码 服务实现类
 * </p>
 *
 * @author xiaoyu
 * @since 2021-05-14
 */
@Service
@Slf4j
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode>
    implements IExchangeCodeService {

    @Autowired
    private IdGenerator idGen;

    @Autowired
    private AccountClient accountFeign;

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private EnrollFeignClient enrollFeignClient;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ITpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private CourseClient courseClient;

    @Override
    public Integer codeCount(Long tpId, Integer type) {
        Long siteId = ContextHolder.get().getSiteId();
        ExchangeCode code = ExchangeCodeFactory.newInstance(tpId, type, siteId);
        return this.baseMapper.selectCount(QueryUtil.condition(code)).intValue();
    }

    @Override
    public void createCode(Long tpId, Integer type, Integer num) {
        if (num > ProjectConstant.CODE_CREATE_MAX_NUM) {
            throw new BizException(InternationalEnums.EXCHANGE_CODE_OVER_MAX_ERROR);
        }
        RequestContext context = ContextHolder.get();
        List<ExchangeCode> codes = new ArrayList<ExchangeCode>();
        while (num > 0) {
            String code = RandomStringUtils.randomAlphanumeric(16);
            codes.add(ExchangeCodeFactory.newInstance(idGen.generate(), tpId, type, code, context.getOrgId(),
                context.getSiteId(), context.getCompanyId(), context.getAccountId()));
            num--;
        }
        this.saveBatch(codes);
    }

    @Override
    public PageInfo<ExchangeCodeVO> listPage(ExchangeCodeQO qo) {
        Long siteId = ContextHolder.get().getSiteId();
        Page<ExchangeCode> page = new Page<>(qo.getPageNo(), qo.getPageSize());

        QueryWrapper<ExchangeCode> condition =
            QueryUtil.condition(ExchangeCodeFactory.newInstance(qo.getTpId(), qo.getType(), siteId, qo.getState()));

        if (StringUtils.isNotEmpty(qo.getCode())) {
            condition.like("code", qo.getCode());
        }

        Page<ExchangeCode> exchangeCodePage = this.baseMapper.selectPage(page, condition);
        Integer total = (int)exchangeCodePage.getTotal();
        List<ExchangeCode> list = exchangeCodePage.getRecords();
        List<ExchangeCodeVO> resultList = null;
        if (CollectionUtils.isNotEmpty(list)) {
            resultList = list.stream().map(s -> {
                ExchangeCodeVO vo = ExchangeCodeVOMapper.INSTANCE.do2vo(s);
                if (null != vo.getAccountId()) {
                    Account user = accountFeign.getAccountById(vo.getAccountId());
                    if (null != user) {
                        vo.setAccount(user.getName());
                        vo.setFullName(StrUtil.isBlank(user.getFullName()) ? user.getName() : user.getFullName());
                    }
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return PageInfoFactory.newInstance(qo.getPageNo(), qo.getPageSize(), total, resultList);
    }

    @Override
    public List<ExchangeCodeExportVO> listExport(ExchangeCodeQO qo, Long siteId) {
        QueryWrapper<ExchangeCode> condition =
            QueryUtil.condition(ExchangeCodeFactory.newInstance(qo.getTpId(), qo.getType(), siteId, qo.getState()));

        if (StringUtils.isNotEmpty(qo.getCode())) {
            condition.like("code", qo.getCode());
        }

        List<ExchangeCode> list = this.baseMapper.selectList(condition);
        List<ExchangeCodeExportVO> resultList = null;
        if (CollectionUtils.isNotEmpty(list)) {
            resultList = list.stream().map(s -> {
                ExchangeCodeExportVO vo = ExchangeCodeVOMapper.INSTANCE.do2export(s);
                if (null != s.getAccountId()) {
                    Account user = accountFeign.getAccountById(s.getAccountId());
                    if (null != user) {
                        vo.setAccount(user.getName());
                        vo.setFullName(user.getFullName());
                    }
                }
                if (TrainingCommonEnums.UN_USED.getCode().equals(s.getState())) {
                    vo.setStateString(TrainingCommonEnums.UN_USED.getMsg());
                } else if (TrainingCommonEnums.USED.getCode().equals(s.getState())) {
                    vo.setStateString(TrainingCommonEnums.USED.getMsg());
                }
                return vo;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    @Override
    public String batchDelete(List<Long> ids) {
        QueryWrapper<ExchangeCode> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids);
        List<ExchangeCode> exchangeCodes = this.list(wrapper);
        List<ExchangeCode> exchangeCodeDels = new ArrayList<>();
        exchangeCodeDels =
            exchangeCodes.stream().filter(t -> t.getState().equals(TrainingCommonEnums.UN_DELETED.getCode()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(exchangeCodeDels)) {
            log.info("兑换码为空或者选中的兑换码都是已经使用过的，无法删除");
            String error = "兑换码为空或者选中的兑换码都是已经使用过的，无法删除";
            return error;
        }
        List<Long> delIds = exchangeCodeDels.stream().map(key -> key.getId()).collect(Collectors.toList());
        wrapper = new QueryWrapper<>();
        wrapper.in("id", delIds);
        this.remove(wrapper);
        return null;
    }

    @Override
    public CustomException<String> codeExchangeTraining(String exchangeCode, Long tainingProjectId)
        throws BizException {
        RequestContext rc = ContextHolder.get();
        Long accountId = rc.getAccountId();
        try {
            //查询验证码是否存在
            ExchangeCode exchangeEntity =
                checkExchangeCode(exchangeCode, tainingProjectId, accountId, TrainingCommonEnums.PROJECT.getCode());

            if (ObjectUtil.isEmpty(exchangeEntity) || !exchangeCode.equals(exchangeEntity.getCode())) {
                //因为mysql默认不区分大小写，这里手动区分
                return CustomException.fail(
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_NOT_EXIST_OR_IS_BEING_USED.getCode(),
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_NOT_EXIST_OR_IS_BEING_USED.getMsg());
            }

            if (exchangeEntity.getAccountId() != null && !exchangeEntity.getAccountId().equals(accountId)) {
                log.info("兑换码已经被使用,请换一个兑换码重新兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getCode(),
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getMsg());
            } else if (exchangeEntity.getAccountId() != null) {
                log.info("您已参加该项目,无需重复兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getCode(),
                    ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getMsg());
            }
            ExchangeCode code = getExchangeCode(accountId, exchangeEntity);

            //查看项目是否已经被金币购买
            GetEnrollVO enrollByProjectId =
                enrollFeignClient.getEnrollByProjectId(exchangeEntity.getRefId(), accountId);
            log.info("{}用户购买的项目{}", accountId, enrollByProjectId);
            if (ObjectUtil.isNotEmpty(code) || (ObjectUtils.isNotEmpty(
                enrollByProjectId) && enrollByProjectId.getHasEnrolled())) {
                log.info("您已参加该项目,无需重复兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getCode(),
                    ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getMsg());
            }
            if (exchangeEntity.getState().equals(TrainingCommonEnums.USED.getCode())) {
                log.info("兑换码已经使用，无法再次兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getCode(),
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getMsg());
            }
            Long tpId = exchangeEntity.getRefId();
            TrainingProject trainingProject = trainingProjectService.getById(tpId);
            Long companyId = rc.getCompanyId();
            // 查询项目的可见范围
            if (checkTpRange(tainingProjectId, accountId, trainingProject, companyId)) {
                log.info("该项目您没有访问权限，无法兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.YOU_DO_NOT_HAVE_ACCESS_TO_THIS_ITEM.getCode(),
                    ExchangeCodeErrorEnum.YOU_DO_NOT_HAVE_ACCESS_TO_THIS_ITEM.getMsg());

            }
            if (ObjectUtil.isEmpty(trainingProject) || trainingProject.getDeleted()
                .equals(TrainingCommonEnums.DELETED.getCode()) || !trainingProject.getStatus()
                .equals(ProjectConstant.PROJECT_STATUS_ENABLE)) {
                log.info("项目不存在,或者项目已经过期");
                return CustomException.fail(ExchangeCodeErrorEnum.PROJECT_DOES_NOT_EXIST_OR_HAS_EXPIRED.getCode(),
                    ExchangeCodeErrorEnum.PROJECT_DOES_NOT_EXIST_OR_HAS_EXPIRED.getMsg());
            }
            Enroll enroll = enrollFeignClient.selectByProjectId(tpId);
            if (enroll.getPayType().equals(0) || enroll.getPayType().equals(1)) {
                log.info("兑换码无效");
                return CustomException.fail(ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getCode(),
                    ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getMsg());
            }
            Long nowTime = System.currentTimeMillis();
            if (enroll.getEndTime().getTime() < nowTime || enroll.getStartTime().getTime() > nowTime) {
                log.info("不在兑换时间范围内,无法兑换!");
                return CustomException.fail(ExchangeCodeErrorEnum.OUT_OF_THE_EXCHANGE_TIME_RANGE.getCode(),
                    ExchangeCodeErrorEnum.OUT_OF_THE_EXCHANGE_TIME_RANGE.getMsg());
            }

            // 开始兑换
            exchangeEntity.setAccountId(accountId);
            exchangeEntity.setState(TrainingCommonEnums.USED.getCode());
            exchangeEntity.setExchangeTime(new Date());
            if (updateById(exchangeEntity)) {
                // 兑换成功添加报名成功信息
                Long aLong = enrollFeignClient.insertTrEnrollRecord(trainingProject.getId(), exchangeCode);
                return CustomException.ok("succeed");
            }
            return CustomException.ok("fail");
        } finally {
            log.info("redis锁删除");
            String redisMallLock = String.format(ProjectConstant.ACCOUNT_TOKEN_LOCK, accountId);
            String format = String.format(ProjectConstant.TP_EXCHANGE_CODE, exchangeCode + "");
            String format1 = String.format(ProjectConstant.TP_EXCHANGE_CODE_USER, accountId.toString());
            //            // 不管成功还是失败 都会撤销 兑换码的正在使用状态
            redisCache.delete(redisMallLock);
            redisCache.delete(format);
            redisCache.delete(format1);
        }
    }

    @Override
    public CustomException<String> codeExchangeCourse(String exchangeCode, Long courseId) throws BizException {
        RequestContext rc = ContextHolder.get();
        Long accountId = rc.getAccountId();
        try {
            //查询验证码是否存在
            ExchangeCode exchangeEntity =
                checkExchangeCode(exchangeCode, courseId, accountId, TrainingCommonEnums.COURSE.getCode());

            if (ObjectUtil.isEmpty(exchangeEntity) || !exchangeCode.equals(exchangeEntity.getCode())) {
                //因为mysql默认不区分大小写，这里手动区分
                return CustomException.fail(
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_NOT_EXIST_OR_IS_BEING_USED.getCode(),
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_NOT_EXIST_OR_IS_BEING_USED.getMsg());
            }

            if (exchangeEntity.getAccountId() != null && !exchangeEntity.getAccountId().equals(accountId)) {
                log.info("兑换码已经被使用,请换一个兑换码重新兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getCode(),
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getMsg());
            } else if (exchangeEntity.getAccountId() != null) {
                log.info("您已参加该课程,无需重复兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getCode(),
                    ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getMsg());
            }
            ExchangeCode code = getExchangeCode(accountId, exchangeEntity);
            //查看项目是否已经被金币购买
            GetEnrollVO enrollByProjectId =
                enrollFeignClient.getEnrollByProjectId(exchangeEntity.getRefId(), accountId);
            log.info("{}用户购买的课程{}", accountId, enrollByProjectId);
            if (ObjectUtil.isNotEmpty(code) || (ObjectUtils.isNotEmpty(
                enrollByProjectId) && enrollByProjectId.getHasEnrolled())) {
                log.info("您已参加该课程,无需重复兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getCode(),
                    ExchangeCodeErrorEnum.NO_NEED_TO_REDEEMER.getMsg());
            }
            if (exchangeEntity.getState().equals(TrainingCommonEnums.USED.getCode())) {
                log.info("兑换码已经使用，无法再次兑换");
                return CustomException.fail(ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getCode(),
                    ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getMsg());
            }
            CourseVo courseVo = courseClient.getOne(courseId);
            if (!courseVo.getLearnPay().equals(2) && !courseVo.getLearnPay().equals(3)) {
                log.info("兑换码无效");
                return CustomException.fail(ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getCode(),
                    ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getMsg());
            }
            // 开始兑换
            exchangeEntity.setAccountId(accountId);
            exchangeEntity.setState(TrainingCommonEnums.USED.getCode());
            exchangeEntity.setExchangeTime(new Date());
            if (updateById(exchangeEntity)) {
                // 兑换成功添加报名成功信息
                Long aLong = enrollFeignClient.insertCourseEnrollRecord(courseId, exchangeCode, 0, 2);
                return CustomException.ok("succeed");
            }
            return CustomException.ok("fail");
        } finally {
            log.info("redis锁删除");
            String redisMallLock = String.format(ProjectConstant.ACCOUNT_TOKEN_LOCK, accountId);
            String format = String.format(ProjectConstant.TP_EXCHANGE_CODE, exchangeCode + "");
            String format1 = String.format(ProjectConstant.TP_EXCHANGE_CODE_USER, accountId.toString());
            //            // 不管成功还是失败 都会撤销 兑换码的正在使用状态
            redisCache.delete(redisMallLock);
            redisCache.delete(format);
            redisCache.delete(format1);
        }
    }

    @Override
    public CustomException<PageInfo<ExchangeTrainingProjectVO>> getExchangeTrainingList(Integer pageNo,
        Integer pageSize, Integer type) {
        RequestContext rc = ContextHolder.get();
        Long accountId = rc.getAccountId();
        try {
            Page<ExchangeCode> page = new Page<>(pageNo, pageSize);
            ExchangeCode exchangeCode = new ExchangeCode();
            exchangeCode.setAccountId(accountId);
            exchangeCode.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
            if(Objects.nonNull(type)){
                exchangeCode.setRefType(type);
            }
            QueryWrapper<ExchangeCode> wrapper = new QueryWrapper<>(exchangeCode);
            wrapper.orderByDesc(Arrays.asList("exchange_time", "create_time"));
            page = page(page, wrapper);
            if (CollectionUtils.isEmpty(page.getRecords())) {
                log.info("没有兑换记录");
                return CustomException.ok(PageCopyMapper.INSTANCE.mp2yizhi(page));
            }
            List<ExchangeCode> records = page.getRecords();
            List<TrainingProject> trainingProjects = getRefInfoMap(records);
            Map<Long, TrainingProject> projectMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(trainingProjects)) {
                projectMap = trainingProjects.stream().collect(Collectors.toMap(TrainingProject::getId, val -> val));
            }
            List<ExchangeTrainingProjectVO> etpV = new ArrayList<>();
            for (ExchangeCode code : records) {
                ExchangeTrainingProjectVO etpv = new ExchangeTrainingProjectVO();
                BeanUtils.copyProperties(code, etpv);
                TrainingProject tp = projectMap.get(code.getRefId());
                if (ObjectUtil.isNotEmpty(tp)) {
                    etpv.setLogoImg(tp.getLogoImg());
                    etpv.setRefType(code.getRefType());
                    etpv.setName(tp.getName());
                    etpv.setTrainingProjectId(tp.getId());
                    etpv.setStatus(tp.getStatus());
                }
                etpV.add(etpv);
            }
            PageInfo<ExchangeTrainingProjectVO> projectVOPageInfo = PageCopyMapper.INSTANCE.mp2yizhi(page);
            projectVOPageInfo.setRecords(etpV);
            return CustomException.ok(projectVOPageInfo);
        } catch (BeansException e) {
            log.error("查询兑换项目列表时出现异常:{}", e);
            return CustomException.fail("30006", "查询兑换项目列表时异常");
        }
    }

    //todo 当前仅支持课程项目
    private List<TrainingProject> getRefInfoMap(List<ExchangeCode> records) {
        Map<Integer, List<Long>> refIdMap = records.stream().collect(Collectors.groupingBy(ExchangeCode::getRefType,
            Collectors.mapping(ExchangeCode::getRefId, Collectors.toList())));
        List<TrainingProject> trainingProjects = new ArrayList<>();
        for (Integer refType : refIdMap.keySet()) {
            switch (refType) {
                case 0:
                    List<Long> courseIds = refIdMap.get(TrainingCommonEnums.COURSE.getCode());
                    if (CollectionUtils.isNotEmpty(courseIds)) {
                        List<CourseEntityVo> courseByIds = courseClient.getCourseByIds(courseIds);
                        List<TrainingProject> collect = courseByIds.stream().map(v -> {
                            TrainingProject trainingProject = new TrainingProject();
                            trainingProject.setId(v.getId());
                            trainingProject.setLogoImg(v.getImage());
                            trainingProject.setName(v.getName());
                            trainingProject.setStatus(v.getShelves());
                            return trainingProject;
                        }).collect(Collectors.toList());
                        trainingProjects.addAll(collect);
                    }
                    break;
                case 15:
                    List<Long> tpIds = refIdMap.get(TrainingCommonEnums.PROJECT.getCode());
                    trainingProjects.addAll(trainingProjectService.listByIds(tpIds));
                    break;
                default:
                    break;
            }
        }
        return trainingProjects;
    }

    @Override
    public Boolean userRelationExchange(Long relationId, Long accountId) {
        //根据用户查询兑换详情
        RequestContext rc = ContextHolder.get();
        Long siteId = rc.getSiteId();
        Long companyId = rc.getCompanyId();
        ExchangeCode ec = new ExchangeCode();
        ec.setAccountId(accountId);
        ec.setCompanyId(companyId);
        ec.setRefId(relationId);
        ec.setSiteId(siteId);
        ec.setDeleted(0);
        ExchangeCode exchangeCode = this.getOne(QueryUtil.condition(ec));
        if (Objects.isNull(exchangeCode)) {
            log.info("当前用户{}未使用兑换码兑换任何", accountId);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public List<Long> getEnrollRelationIdsByAccountId(Integer type) {
        RequestContext requestContext = ContextHolder.get();
        LambdaQueryWrapper<ExchangeCode> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(ExchangeCode::getId);
        lambdaQueryWrapper.eq(ExchangeCode::getRefType, type);
        lambdaQueryWrapper.eq(ExchangeCode::getAccountId, requestContext.getAccountId());
        List<ExchangeCode> exchangeCodes = this.baseMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(exchangeCodes)) {
            return Collections.emptyList();
        }
        return exchangeCodes.stream().map(ExchangeCode::getId).collect(Collectors.toList());
    }

    @Override
    public CustomException<ExchangeTrainingProjectVO> getRefInfoByExchangeCode(String exchangeCode) {
        ExchangeCode exchangeEntity = new ExchangeCode();
        exchangeEntity.setCode(exchangeCode);
        exchangeEntity.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
        QueryWrapper<ExchangeCode> wrapper = new QueryWrapper<>(exchangeEntity);
        exchangeEntity = this.getOne(wrapper);
        if (ObjectUtil.isEmpty(exchangeEntity)) {
            return CustomException.fail(ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getCode(),
                ExchangeCodeErrorEnum.INVALID_EXCHANGE_CODE.getMsg());
        }
        if (exchangeEntity.getState().equals(TrainingCommonEnums.USED.getCode())) {
            return CustomException.fail(ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getCode(),
                ExchangeCodeErrorEnum.THE_EXCHANGE_CODE_HAS_BEEN_USED.getMsg());
        }
        ExchangeTrainingProjectVO expTrainProjectVO = new ExchangeTrainingProjectVO();
        if (Objects.equals(TrainingCommonEnums.COURSE.getCode(), exchangeEntity.getRefType())) {
            List<CourseEntityVo> courseByIds =
                courseClient.getCourseByIds(Collections.singletonList(exchangeEntity.getRefId()));
            if (CollectionUtils.isNotEmpty(courseByIds) && courseByIds.get(0).getShelves() == 1) {
                CourseEntityVo courseEntityVo = courseByIds.get(0);
                expTrainProjectVO.setRefType(TrainingCommonEnums.COURSE.getCode());
                expTrainProjectVO.setId(exchangeEntity.getId());
                expTrainProjectVO.setTrainingProjectId(courseEntityVo.getId());
                expTrainProjectVO.setName(courseEntityVo.getName());
                expTrainProjectVO.setLogoImg(courseEntityVo.getImage());
                expTrainProjectVO.setStatus(courseEntityVo.getShelves());
            }
        }
        if (Objects.equals(TrainingCommonEnums.PROJECT.getCode(), exchangeEntity.getRefType())) {
            Long tpId = exchangeEntity.getRefId();
            TrainingProject trainingProject = trainingProjectService.getById(tpId);
            if (ObjectUtil.isEmpty(trainingProject) || trainingProject.getDeleted()
                .equals(TrainingCommonEnums.DELETED.getCode()) || !trainingProject.getStatus()
                .equals(ProjectConstant.PROJECT_STATUS_ENABLE)) {
                return CustomException.fail(
                    ExchangeCodeErrorEnum.THE_PROJECT_NOT_EXIST_OR_THE_PROJECT_HAS_EXPIRED.getCode(),
                    ExchangeCodeErrorEnum.THE_PROJECT_NOT_EXIST_OR_THE_PROJECT_HAS_EXPIRED.getMsg());
            }
            BeanUtils.copyProperties(trainingProject, expTrainProjectVO);
        }
        return CustomException.ok(expTrainProjectVO);
    }

    private ExchangeCode checkExchangeCode(String exchangeCode, Long tainingProjectId, Long accountId,
        Integer refType) {
        Long timeout = 30L;
        String redisMallLock = String.format(ProjectConstant.ACCOUNT_TOKEN_LOCK, accountId);
        String format = String.format(ProjectConstant.TP_EXCHANGE_CODE, exchangeCode + "");
        String format1 = String.format(ProjectConstant.TP_EXCHANGE_CODE_USER, accountId.toString());
        // 读取redis 查看当前兑换码是否已经有人使用
        boolean o = redisCache.setIfAbsent(format, exchangeCode);
        // 读取redis  查看当前用户是否在使用别的兑换码
        boolean o1 = redisCache.setIfAbsent(format1, accountId + "");
        // 读取redis 查看当前用户是否在使用金币兑换
        boolean b = redisCache.setIfAbsent(redisMallLock, "1");
        // 预防删除失败导致的分布式锁一直存在
        redisCache.expire(redisMallLock, timeout);
        redisCache.expire(format, timeout);
        redisCache.expire(format1, timeout);
        if (!o || !o1 || !b) {
            return null;
        }
        ExchangeCode exchangeEntity = new ExchangeCode();
        exchangeEntity.setCode(exchangeCode);
        if (tainingProjectId != null) {
            exchangeEntity.setRefId(tainingProjectId);
            exchangeEntity.setRefType(refType);
        }
        exchangeEntity.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
        QueryWrapper<ExchangeCode> wrapper = new QueryWrapper<>(exchangeEntity);
        exchangeEntity = getOne(wrapper);
        return exchangeEntity;
    }

    private ExchangeCode getExchangeCode(Long accountId, ExchangeCode exchangeEntity) {
        ExchangeCode code = new ExchangeCode();
        code.setAccountId(accountId);
        code.setDeleted(TrainingCommonEnums.UN_DELETED.getCode());
        code.setRefId(exchangeEntity.getRefId());
        code.setRefType(exchangeEntity.getRefType());
        QueryWrapper<ExchangeCode> wrapper1 = new QueryWrapper<>(code);
        code = getOne(wrapper1);
        return code;
    }

    private boolean checkTpRange(Long tainingProjectId, Long accountId, TrainingProject trainingProject,
        Long companyId) {
        List<Long> accountIds = new ArrayList<>();
        if (ObjectUtils.isEmpty(tainingProjectId)) {
            // 项目id 为空 证明用户是使用兑换码直接兑换的 不是进入到项目详情页兑换的 所以需要验证这个项目对用户是否可见
            int range = trainingProject.getVisibleRange();
            if (range == 1) {
                return false;
            } else if (range == 0) {
                TpAuthorizationRange tpAuth = new TpAuthorizationRange();
                tpAuth.setBizId(trainingProject.getId());
                List<TpAuthorizationRange> list = tpAuthorizationRangeService.list(new QueryWrapper<>(tpAuth));
                if (CollectionUtils.isEmpty(list)) {
                    return false;
                }
                for (TpAuthorizationRange a : list) {
                    if (2 == a.getType()) {
                        accountIds.add(a.getRelationId());
                    } else if (1 == a.getType()) {
                        List<AccountVO> accountVOS = accountFeign.findByOrgId(a.getRelationId(), companyId);
                        List<Long> ids = new ArrayList<Long>();
                        if (CollectionUtils.isNotEmpty(list)) {
                            log.info("可见范围关联部门：" + list.toString());
                            for (AccountVO vo : accountVOS) {
                                ids.add(vo.getId());
                            }
                        }
                        if (CollectionUtils.isNotEmpty(ids)) {
                            accountIds.addAll(ids);
                        }
                        if (accountIds == null) {
                            continue;
                        }
                    }
                }
                if (!accountIds.contains(accountId)) {
                    return true;
                }
            }
        }
        return false;
    }

}
