package com.yizhi.training.application.v2.service.biz;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpProMapping;
import com.yizhi.training.application.domain.TpViewRecord;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.TpAuthorizationRangeMapper;
import com.yizhi.training.application.mapper.TpViewRecordMapper;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.TpViewRecordService;
import com.yizhi.training.application.v2.TpProEnum;
import com.yizhi.training.application.v2.mapper.TpProMappingMapperV2;
import com.yizhi.training.application.v2.service.TrainingProjectService;
import com.yizhi.training.application.v2.vo.HotTpVO;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.OnLineTpVO;
import com.yizhi.training.application.v2.vo.request.SearchTpAndProVO;
import com.yizhi.training.application.vo.StuMemberResourceTpVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TpHomeBizService {

    @Autowired
    private TrainingProjectService trainingProjectService;

    @Autowired
    private TpAuthorizationRangeMapper tpAuthorizationRangeMapper;

    @Autowired
    private TpProMappingMapperV2 tpProMappingMapperV2;

    @Autowired
    private ITpStudentProjectRecordService tpStudentProjectRecordService;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private TpIntroduceBizServices tpIntroduceBizServices;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Autowired
    private TpViewRecordService tpViewRecordService;

    public List<HotTpVO> hotEnrollTp(String terminalType) {

        RequestContext requestContext = ContextHolder.get();
        Long siteId = requestContext.getSiteId();
        Long companyId = requestContext.getCompanyId();
        List<Long> relationIds = requestContext.getRelationIds();
        HttpServletRequest request = requestContext.getRequest();

        // 免费报名且没有在项目PRO中
        List<HotTpVO> hotEnrollTpList = trainingProjectService.getHotEnrollTp(siteId, companyId, terminalType);

        if (CollectionUtils.isEmpty(hotEnrollTpList)) {
            return new ArrayList<>();
        }

        // 可见的项目
        List<Long> authTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, siteId);
        if (CollectionUtils.isEmpty(authTpIds)) {
            authTpIds = new ArrayList<>();
        }
        List<Long> finalAuthTpIds = authTpIds;

        // 筛选出全员可见的项目，已经自己在可见范围中的项目
        List<HotTpVO> visiableTpList = hotEnrollTpList.stream()
            .filter(it -> it.getVisibleRange().equals(1) || finalAuthTpIds.contains(it.getTpId()))
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(visiableTpList)) {
            visiableTpList.forEach(it -> {
                it.setActivityCount(tpPlanActivityService.getExcCertificateActivityNumByTpId(it.getTpId()));
            });
        }
        return visiableTpList;
    }

    public PageDataVO<OnlineTpVO> getOnLineTpPage(OnLineTpVO tpVO) {
        RequestContext requestContext = ContextHolder.get();
        Long siteId = requestContext.getSiteId();
        Long companyId = requestContext.getCompanyId();
        Long accountId = requestContext.getAccountId();
        List<Long> relationIds = requestContext.getRelationIds();

        PageDataVO<OnlineTpVO> pageDataVO = new PageDataVO<>();
        pageDataVO.setPageNo(tpVO.getPageNo());
        pageDataVO.setPageSize(tpVO.getPageSize());
        pageDataVO.setTotal(0);

        // 查询所有能看到的项目及PRO
        List<OnlineTpVO> tpHomeListAll = getTpHomeListAll(tpVO.getTerminalType(), siteId, companyId, relationIds);
        if (CollectionUtils.isEmpty(tpHomeListAll)) {
            log.info("项目列表-查询所有能看到的项目及PRO结果size为空");
            return pageDataVO;
        }
        //        log.info("查询所有能看到的项目及PRO结果size={}",tpHomeListAll.size());

        // 根据状态筛选
        if (tpVO.getStatus() != null) {
            tpHomeListAll = getTpVoByStatus(tpHomeListAll, tpVO.getStatus(), siteId, accountId);
        }
        if (CollectionUtils.isEmpty(tpHomeListAll)) {
            //            log.info("根据状态筛选后结果size为空");
            return pageDataVO;
        }
        log.info("项目列表-根据状态筛选后结果size={}", tpHomeListAll.size());

        pageDataVO.setTotal(tpHomeListAll.size());
        // 手动分页
        List<OnlineTpVO> pageRecords = CollUtil.page(tpVO.getPageNo() - 1, tpVO.getPageSize(), tpHomeListAll);
        if (!CollectionUtils.isEmpty(pageRecords)) {
            for (OnlineTpVO pageRecord : pageRecords) {
                TrainingProject byId = trainingProjectService.getById(pageRecord.getTpId());
                if (byId != null) {
                    //                    if (byId.getVisibleRange() == 1) {
                    //                        pageRecord.setJoinNumber(tpViewRecordMapper.getViewNum(byId));
                    //                    } else {
                    //                        pageRecord.setJoinNumber(tpViewRecordMapper.getViewNumRange(byId));
                    //                    }
                    Integer viewNumRange = tpViewRecordService.getViewNumRange(byId);
                    pageRecord.setJoinNumber(viewNumRange);
                } else {
                    //是否为项目pro  取第一个项目的学习人数
                    QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
                    wrapper.eq("tp_pro_id", pageRecord.getTpId());
                    wrapper.eq("deleted", 0);
                    wrapper.eq("site_id", siteId);
                    wrapper.eq("company_id", companyId);
                    List<TpProMapping> tpProMappings = tpProMappingMapperV2.selectList(wrapper);
                    if (!CollectionUtils.isEmpty(tpProMappings)) {
                        //所有项目id
                        List<Long> tpIds =
                            tpProMappings.stream().map(TpProMapping::getTrainingProjectId).collect(Collectors.toList());
                        QueryWrapper<TpViewRecord> wrapper1 = new QueryWrapper<>();
                        wrapper1.in("training_project_id", tpIds);
                        wrapper1.select("DISTINCT account_id");
                        Integer joinNumber = Math.toIntExact(tpViewRecordMapper.selectCount(wrapper1));
                        pageRecord.setJoinNumber(joinNumber);
                    }
                }
            }
        }
        pageDataVO.setRecords(pageRecords);
        log.info("手动分页后结果pageRecordsSize={}", pageRecords.size());
        // 设置活动数及期数
        pageDataVO.getRecords().forEach(tp -> {
            setActivitycount(tp);
        });
        //        log.info("设置活动数及期数后结果pageDataVO={}", JSON.toJSONString(pageDataVO));
        return pageDataVO;
    }

    public List<StuMemberResourceTpVo> memberOnLineTpList(OnLineTpVO tpVO) {
        RequestContext requestContext = ContextHolder.get();
        List<StuMemberResourceTpVo> tpHomeListAll = new ArrayList<>();
        LambdaQueryWrapper<TrainingProject> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TrainingProject::getId, tpVO.getTpIds());
        List<TrainingProject> list = trainingProjectService.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return tpHomeListAll;
        }
        List<Long> finishedTpId = tpStudentProjectRecordService.accountFinishedTpId(requestContext.getAccountId(),
            requestContext.getSiteId());

        return list.stream().map(v -> {
            StuMemberResourceTpVo stuMemberResourceTpVo = new StuMemberResourceTpVo();
            stuMemberResourceTpVo.setRelationId(v.getId());
            stuMemberResourceTpVo.setRelationName(v.getName());
            stuMemberResourceTpVo.setImageUrl(v.getLogoImg());
            stuMemberResourceTpVo.setStartTime(v.getStartTime());
            stuMemberResourceTpVo.setEndTime(v.getEndTime());
            stuMemberResourceTpVo.setPayType(v.getPayType());
            stuMemberResourceTpVo.setRelationType(2);

            //            if (v.getVisibleRange() == 1) {
            //                stuMemberResourceTpVo.setJoinNumber(tpViewRecordMapper.getViewNum(v));
            //            } else {
            //                stuMemberResourceTpVo.setJoinNumber(tpViewRecordMapper.getViewNumRange(v));
            //            }
            Integer viewNumRange = tpViewRecordService.getViewNumRange(v);
            stuMemberResourceTpVo.setJoinNumber(viewNumRange);
            if (finishedTpId.contains(v.getId())) {
                stuMemberResourceTpVo.setStudentStatus(1);
            } else {
                stuMemberResourceTpVo.setStudentStatus(0);
            }
            return stuMemberResourceTpVo;

        }).collect(Collectors.toList());

    }

    /**
     * 可以出现在项目首页列表中的项目PRO及项目
     *
     * @param terminalType
     * @param siteId
     * @param companyId
     * @return
     */
    public List<OnlineTpVO> getTpHomeListAll(String terminalType, Long siteId, Long companyId, List<Long> relationIds) {

        List<OnlineTpVO> vos = new ArrayList<>();
        getAllAuthTpIds(terminalType, siteId, companyId, relationIds, vos);

        // 制定端可以出现在列表中的pro
        List<OnlineTpVO> tpProList = tpProMappingMapperV2.getTpHomeListPro(terminalType, siteId, companyId,
            ContextHolder.get().getRelationIds());
        if (!CollectionUtils.isEmpty(tpProList)) {
            vos.addAll(tpProList);
        }
        // 排序
        List<OnlineTpVO> collect = vos.stream().sorted(Comparator.comparing(OnlineTpVO::getCreatedAt).reversed())
            .sorted(Comparator.comparing(OnlineTpVO::getTpType).reversed())
            .sorted(Comparator.comparing(OnlineTpVO::getSort).reversed()).collect(Collectors.toList());
        return collect;
    }

    public PageDataVO<OnlineTpVO> searchTp(SearchTpAndProVO searchVo) {
        RequestContext requestContext = ContextHolder.get();
        List<OnlineTpVO> tpHomeListAll =
            getTpHomeListAll(searchVo.getTerminalType(), requestContext.getSiteId(), requestContext.getCompanyId(),
                requestContext.getRelationIds());
        PageDataVO<OnlineTpVO> page = new PageDataVO<>(searchVo.getPageNo(), searchVo.getPageSize());
        List<OnlineTpVO> collect =
            tpHomeListAll.stream().filter(it -> it.getName().contains(searchVo.getName())).collect(Collectors.toList());
        if (CollUtil.isEmpty(collect)) {
            return page;
        }
        List<OnlineTpVO> recordes = CollUtil.page(searchVo.getPageNo() - 1, searchVo.getPageSize(), collect);

        // 设置活动数及期数
        recordes.forEach(tp -> {
            // 设置活动期数
            setActivitycount(tp);
        });

        page.setTotal(collect.size());
        page.setRecords(recordes);
        return page;
    }

    private List<OnlineTpVO> getTpVoByStatus(List<OnlineTpVO> tpHomeListAll, Integer status, Long siteId,
        Long accountId) {

        Boolean finishedEmpty = true;
        List<Long> finishedTpId = tpStudentProjectRecordService.accountFinishedTpId(accountId, siteId);
        //        List<Long> finishedTpId = null;
        if (!CollectionUtils.isEmpty(finishedTpId)) {
            finishedEmpty = false;
            //            finishedTpId = tpStudentProjectRecords.stream()
            //                    .map(TpStudentProjectRecord::getTrainingProjectId).collect(Collectors.toList());
        }

        List<Long> finalFinishedTpId = finishedTpId;
        if (status == 1 && finishedEmpty) {
            // 需要完成的，但是没有完成的。
            return null;
        } else if (status == 1 && !finishedEmpty) {
            // 查询已完成
            // 查询完成ID,然后查询制定端的，在列表中展示的项目及pro
            // 筛选出已完成的ID
            return tpHomeListAll.stream().filter(it -> finalFinishedTpId.contains(it.getTpId()))
                .collect(Collectors.toList());
        } else if (status == 0 && finishedEmpty) {
            // 需要未完成的，没有已完成的，返回全部
            return tpHomeListAll;
        } else {
            // 未完成
            // 过滤掉已完成的ID,剩余为完成的ID
            return tpHomeListAll.stream().filter(it -> !finalFinishedTpId.contains(it.getTpId()))
                .collect(Collectors.toList());
        }

    }

    private void setActivitycount(OnlineTpVO tp) {
        if (tp.getTpType() == TpProEnum.TP_DEFAULT.getCode()) {
            tp.setActivityCount(tpPlanActivityService.getExcCertificateActivityNumByTpId(tp.getTpId()));
        } else {
            List<OnlineTpVO> tpListByProId = tpIntroduceBizServices.getTpListByProId(tp.getTpId());
            if (CollectionUtils.isEmpty(tpListByProId)) {
                tp.setActivityCount(0);
                return;
            }
            tp.setActivityCount(tpListByProId.size());
        }
    }

    private List<Long> getAllAuthTpIds(String terminalType, Long siteId, Long companyId, List<Long> relationIds,
        List<OnlineTpVO> vos) {
        // 指定端不在项目PRO中的项目
        List<OnlineTpVO> tpList = trainingProjectService.getOnLineTpPage(terminalType, siteId, companyId);
        if (CollectionUtils.isEmpty(tpList)) {
            tpList = new ArrayList<>();
        }

        List<Long> authTpIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, siteId);
        if (CollectionUtils.isEmpty(authTpIds)) {
            authTpIds = new ArrayList<>();
        }

        // 可见的项目
        List<Long> finalAuthTpIds = authTpIds;
        // 筛选出全员可见的项目、已经自己在可见范围中的项目
        List<OnlineTpVO> visiableTpList =
            tpList.stream().filter(it -> it.getVisibleRange().equals(1) || finalAuthTpIds.contains(it.getTpId()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(visiableTpList)) {
            vos.addAll(visiableTpList);
        }
        return authTpIds;
    }

}
