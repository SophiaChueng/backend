package com.yizhi.training.application.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.course.application.feign.CourseClient;
import com.yizhi.course.application.vo.CourseRecordReq;
import com.yizhi.course.application.vo.RecordeVO;
import com.yizhi.system.application.feign.AiaThirdClient;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.system.application.vo.third.ThirdCallbackConfigVO;
import com.yizhi.training.application.domain.TpStudentActivityRecord;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.enums.ThirdAiaEnum;
import com.yizhi.training.application.mapper.TpPlanActivityMapper;
import com.yizhi.training.application.mapper.TpStudentActivityRecordMapper;
import com.yizhi.training.application.mapper.TpStudentProjectRecordMapper;
import com.yizhi.training.application.mapper.TrainingProjectMapper;
import com.yizhi.training.application.service.ThirdAiaSyncTrainDataService;
import com.yizhi.training.application.vo.third.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ThirdAiaSyncTrainDataServiceImpl implements ThirdAiaSyncTrainDataService {

    @Autowired
    private TpStudentProjectRecordMapper studentProjectRecordMapper;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Autowired
    private CourseClient courseClient;

    @Autowired
    private TpStudentActivityRecordMapper tpStudentActivityRecordMapper;

    @Autowired
    private AiaThirdClient aiaThirdClient;

    @Override
    public Page<AiaTrainingVO> getTrainingStudyPassedRecords(SyncTrainingReq syncTrainingReq) {

        Integer pageSize = syncTrainingReq.getPageSize();
        Integer pageNo = syncTrainingReq.getPageNo();
        Long siteId = syncTrainingReq.getSiteId();
        Page<AiaTrainingVO> pageVo = new Page<>(pageNo, pageSize);
        List<Long> projectIds = syncTrainingReq.getProjectIds();
        if (CollectionUtils.isEmpty(projectIds)) {
            log.warn("同步培训项目到友邦，项目ids为空!不做操作!参数={}", JSON.toJSONString(syncTrainingReq));
            throw new BizException(ThirdAiaEnum.err_project_ids_null.getKey(),
                ThirdAiaEnum.err_project_ids_null.getValue());
        }
        List<TrainingProject> projectList =
            trainingProjectMapper.getList(projectIds, new RowBounds(0, Integer.MAX_VALUE));
        if (CollectionUtils.isEmpty(projectList)) {
            log.error("同步培训项目到友邦，查询项目为空!projectIds = {}", projectIds);
            throw new BizException(ThirdAiaEnum.err_project_ids_null.getKey(),
                ThirdAiaEnum.err_project_ids_null.getValue());
        }
        //获取项目map
        Page<TpStudentProjectRecord> page = new Page<>(pageNo, pageSize);
        List<TpStudentProjectRecord> list =
            studentProjectRecordMapper.getPageList(projectIds, syncTrainingReq.getAccountIds(),
                syncTrainingReq.getStartTime(), syncTrainingReq.getEndTime(), siteId, page);
        if (CollectionUtils.isEmpty(list)) {
            log.info("获取友邦培训项目记录数据为空!不做操作!参数={};", JSON.toJSONString(syncTrainingReq));
            return pageVo;
        }
        Set<Long> accountIdSet = list.stream().map(TpStudentProjectRecord::getAccountId).collect(Collectors.toSet());
        List<Long> accountIdList = new ArrayList<>(accountIdSet);
        List<AccountVO> accountVOList = accountClient.findByIds(accountIdList);
        if (CollectionUtils.isEmpty(accountVOList)) {
            log.error("同步培训项目到友邦,用户查询用户信息为空!参数={}", JSON.toJSONString(syncTrainingReq));
            throw new BizException(ThirdAiaEnum.err_account_null.getKey(), ThirdAiaEnum.err_account_null.getValue());
        }
        //获取用户map
        Map<Long, AccountVO> accountIdMap =
            accountVOList.stream().collect(Collectors.toMap(AccountVO::getId, Function.identity()));
        List<ThirdCallbackConfigVO> projectConfigs = syncTrainingReq.getConfigVOList();
        //项目id-回传newdams系统的枚举值
        Map<Long, String> pIdAndCallbackNameMap = projectConfigs.stream()
            .collect(Collectors.toMap(ThirdCallbackConfigVO::getProjectId, ThirdCallbackConfigVO::getName));

        //key= 回传友邦的机构码co, value= 学员list
        Map<String, List<AiaTrainingDetailVO>> aiaOrgMap = new HashMap<>();
        Map<Long, String> orgIdCoCodeMap = new HashMap<>();
        list.forEach(studentTpRecord -> {
            Long accountId = studentTpRecord.getAccountId();
            AccountVO accountVO = accountIdMap.get(accountId);
            if (null == accountVO) {
                log.warn(
                    "同步友邦培训项目完成数据,完成记录表存在但根据accountId获取到用户信息为空!accountId ={},项目id={}",
                    accountId, studentTpRecord.getTrainingProjectId());
                return;
            }
            String agtCode = accountVO.getName();
            if (StrUtil.isBlank(agtCode)) {
                log.error("同步友邦培训项目完成数据-获取到用户名为空!accountId={}", accountId);
                return;
            }

            Long accountVOOrgId = accountVO.getOrgId();
            //获取用户的机构码 co;
            String accountCoCode = orgIdCoCodeMap.get(accountVOOrgId);
            if (StrUtil.isBlank(accountCoCode)) {
                accountCoCode = getAccountCoCode(accountVOOrgId, syncTrainingReq.getRootOrgId());
                if (StrUtil.isBlank(accountCoCode)) {
                    log.error(
                        "同步友邦培训项目完成数据用户无法获取友邦机构码,改用户不同步到友邦系统;accountId={},accountName={},accountOrgId ={}",
                        accountId, agtCode, accountVOOrgId);
                    return;
                }
            }
            orgIdCoCodeMap.put(accountVOOrgId, accountCoCode);
            //获取部门下的明细数据
            List<AiaTrainingDetailVO> detailVOList = aiaOrgMap.get(accountCoCode);
            if (null == detailVOList) {
                detailVOList = new ArrayList<>();
            }
            Long tpId = studentTpRecord.getTrainingProjectId();
            String projectCallbackName = pIdAndCallbackNameMap.get(tpId);
            if (StrUtil.isBlank(projectCallbackName)) {
                log.error("获取培训项目回传code为空!tpId ={},accountId ={}", tpId, accountId);
                return;
            }
            AiaTrainingDetailVO detailVO = new AiaTrainingDetailVO();
            //设置项目枚举值
            detailVO.setTrainType(projectCallbackName);
            detailVO.setAgtCode(agtCode);
            Date finishDate = studentTpRecord.getFinishDate();
            if (null != finishDate) {
                String finishTime = DateUtil.format(finishDate, "yyyy-MM-dd");
                detailVO.setEffDate(finishTime);
            }
            Integer finished = studentTpRecord.getFinished();
            if (null != finished && finished.equals(1)) {
                detailVO.setTrainValue(ThirdAiaEnum.train_value_y.getKey());
            } else {
                detailVO.setTrainValue(ThirdAiaEnum.train_value_n.getKey());
            }
            detailVOList.add(detailVO);
            aiaOrgMap.put(accountCoCode, detailVOList);
        });
        List<AiaTrainingVO> aiaTrainingVOList = new ArrayList<>();
        aiaOrgMap.forEach((co, hgList) -> {
            AiaTrainingVO trainingVO = new AiaTrainingVO();
            trainingVO.setCo(co);
            trainingVO.setHgTrainList(hgList);
            aiaTrainingVOList.add(trainingVO);

        });
        pageVo.setRecords(aiaTrainingVOList);
        pageVo.setCurrent(page.getCurrent());
        pageVo.setTotal(page.getTotal());
        pageVo.setSize(page.getSize());
        return pageVo;
    }

    @Override
    public Page<AiaProductVO> getProductStudyPassedRecords(SyncTrainingReq req) {

        Long companyId = req.getCompanyId();
        Long siteId = req.getSiteId();
        List<TpStudentActivityRecord> totalRecordList = new ArrayList<>();
        //获取配置的所有课程
        List<ThirdCallbackConfigVO> courseConfigList = req.getConfigVOList();
        List<Long> courseIds =
            courseConfigList.stream().map(ThirdCallbackConfigVO::getRelationId).collect(Collectors.toList());
        // 设置一个课程/章节id  -->> 需要回传友邦的课程code  map
        Map<Long, String> idAndAiaProCodeMap = courseConfigList.stream()
            .collect(Collectors.toMap(ThirdCallbackConfigVO::getRelationId, ThirdCallbackConfigVO::getName));
        //找到需要特殊处理课程章节的配置信息；
        List<ThirdCallbackConfigVO> needHandlerChapterStateList =
            courseConfigList.stream().filter(item -> !StrUtil.isBlank(item.getRelationBizContent()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(needHandlerChapterStateList) && req.getPageNo() != null && req.getPageNo()
            .equals(1)) {
            log.info("开始处理特殊课程章节完成状态");
            //获取需要特殊处理的课程ids
            List<Long> needHandlerChapterStateCourseIds =
                needHandlerChapterStateList.stream().map(ThirdCallbackConfigVO::getRelationId)
                    .collect(Collectors.toList());
            //从所有课程ids中去掉需要特殊处理的课程id
            courseIds.removeAll(needHandlerChapterStateCourseIds);
            //将特殊处理的课程从总code对应map中移除
            needHandlerChapterStateCourseIds.forEach(idAndAiaProCodeMap::remove);
            //将特殊处理的章节和code设置到idAndAiaProCodeMap中
            needHandlerChapterStateList.forEach(config -> {
                String relationBizContent = config.getRelationBizContent();
                List<ThirdCallbackConfigVO> chapterConfigList =
                    JSON.parseArray(relationBizContent, ThirdCallbackConfigVO.class);
                chapterConfigList.forEach(chapterCofig -> {
                    Long chapterId = chapterCofig.getRelationId();
                    String pCode = chapterCofig.getName();
                    idAndAiaProCodeMap.put(chapterId, pCode);
                });
            });

            // 获取特殊处理的课程章节完成记录
            CourseRecordReq recordReq = new CourseRecordReq();
            recordReq.setPageNo(1);
            recordReq.setPageSize(Integer.MAX_VALUE);
            recordReq.setCompanyId(companyId);
            recordReq.setSiteId(siteId);
            recordReq.setCourseIds(needHandlerChapterStateCourseIds);
            recordReq.setStartTime(req.getStartTime());
            recordReq.setEndTime(req.getEndTime());
            recordReq.setAccountIds(req.getAccountIds());
            Page<RecordeVO> courseChapterPage = courseClient.getCourseChapterFinishList(recordReq);
            List<RecordeVO> recordeVOList = courseChapterPage.getRecords();
            if (!CollectionUtils.isEmpty(recordeVOList)) {
                //将章节完成记录设置到总记录中
                recordeVOList.forEach(recordeVO -> {
                    TpStudentActivityRecord record = new TpStudentActivityRecord();
                    record.setAccountId(recordeVO.getAccountId());
                    record.setRelationId(recordeVO.getChapterId());
                    record.setFinishDate(recordeVO.getEndTime());
                    record.setFinished(recordeVO.getOvered());
                    totalRecordList.add(record);
                });
            }
        }

        // 获取课程的完成记录
        Date startTime = req.getStartTime();
        Date endTime = req.getEndTime();
        Page<AiaProductVO> voPage = new Page<>(req.getPageNo(), req.getPageSize());
        Page<TpStudentActivityRecord> page = new Page<>(req.getPageNo(), req.getPageSize());
        List<TpStudentActivityRecord> activityRecords =
            tpStudentActivityRecordMapper.selectPageListByRelationIds(siteId, courseIds, 0, startTime, endTime,
                req.getAccountIds(), page);
        totalRecordList.addAll(activityRecords);
        log.info("友邦同步课程状态: 总记录数={},pageNo={}", totalRecordList.size(), req.getPageNo());
        if (CollectionUtils.isEmpty(totalRecordList)) {
            log.info("友邦总学习记录为空;不做操作");
            return voPage;
        }
        Set<Long> accountIdSet =
            totalRecordList.stream().map(TpStudentActivityRecord::getAccountId).collect(Collectors.toSet());
        List<Long> accountIdList = new ArrayList<>(accountIdSet);
        List<AccountVO> accountVOList = accountClient.findByIds(accountIdList);
        if (CollectionUtils.isEmpty(accountVOList)) {
            log.error("同步产品完成记录到友邦,用户查询用户信息为空!参数={}", JSON.toJSONString(req));
            return voPage;
        }

        //获取需要添加的多余的code
        List<ThirdCallbackConfigVO> exCodeList = aiaThirdClient.getCallbackConfs(80);

        //获取用户map
        Map<Long, AccountVO> accountIdMap =
            accountVOList.stream().collect(Collectors.toMap(AccountVO::getId, Function.identity()));
        //  组装数据
        Map<String, List<AiaProductDetailVO>> aiaOrgMap = new HashMap<>();
        Map<Long, String> orgIdCoCodeMap = new HashMap<>();
        totalRecordList.forEach(record -> {
            //relationId有可能是courseId or chapterId
            Long relationId = record.getRelationId();

            //从配置中获取对应回传的课程code
            String prodCode = idAndAiaProCodeMap.get(relationId);
            //            String prodCode = courseConfig.getName();
            if (StrUtil.isBlank(prodCode)) {
                //                log.error("同步友邦产品课程完成数据用户无法获取友邦机构码,用户记录不同步到友邦系统;record ={}"
                //                        ,JSON.toJSONString(record));
                return;
            }
            Long accountId = record.getAccountId();
            AccountVO accountVO = accountIdMap.get(accountId);
            if (null == accountVO) {
                accountVO = accountClient.findById(accountId);
            }
            String agtCode = accountVO.getName();
            if (StrUtil.isBlank(agtCode)) {
                log.error("同步友邦产品课程完成数据:获取用户名为空!accountId ={},relationId={}", accountId, relationId);
                return;
            }
            Long accountVOOrgId = accountVO.getOrgId();
            String accountCoCode = orgIdCoCodeMap.get(accountVOOrgId);
            if (StrUtil.isBlank(accountCoCode)) {
                accountCoCode = aiaThirdClient.getAiaCoCode(accountVOOrgId, req.getRootOrgId());
                if (StrUtil.isBlank(accountCoCode)) {
                    log.warn(
                        "同步友邦产品课程完成数据用户无法获取友邦机构码,该用户不同步到友邦系统;accountId={},accountName={},accountOrgId ={}",
                        accountId, agtCode, accountVOOrgId);
                    return;
                }
            }
            orgIdCoCodeMap.put(accountVOOrgId, accountCoCode);
            List<AiaProductDetailVO> aiaProductDetailVOList = aiaOrgMap.get(accountCoCode);
            if (null == aiaProductDetailVOList) {
                aiaProductDetailVOList = new ArrayList<>();
            }
            AiaProductDetailVO productDetailVO = new AiaProductDetailVO();

            productDetailVO.setProductCode(prodCode);
            productDetailVO.setAgtCode(agtCode);
            Date finishDate = record.getFinishDate();
            String passedTime = DateUtil.format(finishDate, "yyyy-MM-dd");
            productDetailVO.setEffDate(passedTime);
            Integer finished = record.getFinished();
            if (null != finished && finished.equals(1)) {
                productDetailVO.setTrainValue(ThirdAiaEnum.train_value_y.getKey());
            } else {
                productDetailVO.setTrainValue(ThirdAiaEnum.train_value_n.getKey());
            }
            aiaProductDetailVOList.add(productDetailVO);
            //添加多余的code
            if (!CollectionUtils.isEmpty(exCodeList)) {
                for (ThirdCallbackConfigVO callbackConfigVO : exCodeList) {
                    Long needExtraCodeRelationId = callbackConfigVO.getRelationId();
                    if (null == needExtraCodeRelationId) {
                        continue;
                    }
                    if (!needExtraCodeRelationId.equals(relationId)) {
                        continue;
                    }
                    //需要添加回传多余code的产品
                    AiaProductDetailVO detailVO = new AiaProductDetailVO();
                    detailVO.setEffDate(productDetailVO.getEffDate());
                    detailVO.setProductCode(callbackConfigVO.getName());
                    detailVO.setTrainValue(productDetailVO.getTrainValue());
                    detailVO.setAgtCode(productDetailVO.getAgtCode());
                    aiaProductDetailVOList.add(detailVO);
                }
            }

            aiaOrgMap.put(accountCoCode, aiaProductDetailVOList);
        });
        List<AiaProductVO> aiaProductVOS = new ArrayList<>();
        aiaOrgMap.forEach((co, list) -> {
            AiaProductVO vo = new AiaProductVO();
            vo.setCo(co);
            vo.setProductTrainResultList(list);
            aiaProductVOS.add(vo);
            log.info("友邦同步课程状态：机构码co={},size={}", co, list.size());
        });
        voPage.setRecords(aiaProductVOS);
        voPage.setCurrent(page.getCurrent());
        voPage.setTotal(page.getTotal());
        voPage.setSize(page.getSize());
        return voPage;
    }

    /**
     * 获取用户所在部门机构code
     *
     * @param accountVOOrgId
     * @return
     */
    private String getAccountCoCode(Long accountVOOrgId, Long rootOrgId) {

        if (null == accountVOOrgId) {
            return null;
        }
        //获取用户部门
        try {
            return aiaThirdClient.getAiaCoCode(accountVOOrgId, rootOrgId);
        } catch (Exception e) {
            log.error("同步培训数据到友邦，获取用户的部门异常：accountOrgId ={}", accountVOOrgId);
        }
        return null;
    }
}
