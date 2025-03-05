package com.yizhi.training.application.v2.service.biz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.vo.member.MemberResourceVO;
import com.yizhi.training.application.domain.TpEnroll;
import com.yizhi.training.application.mapper.TpStudentProjectRecordMapper;
import com.yizhi.training.application.mapper.TpViewRecordMapper;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.v2.MyTpFinishedVO;
import com.yizhi.training.application.v2.MyTpUnFinishedVO;
import com.yizhi.training.application.v2.TpProEnum;
import com.yizhi.training.application.v2.TpProEnum;
import com.yizhi.training.application.v2.service.TpEnrollService;
import com.yizhi.training.application.v2.service.TrainingProjectProService;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MyTpService {

    @Autowired
    private TpStudentProjectRecordMapper tpStudentProjectRecordMapper;

    @Autowired
    private TrainingProjectProService projectProService;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Autowired
    private TpEnrollService tpEnrollService;

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private TpIntroduceBizServices tpIntroduceBizServices;

    public Integer getCount() {

        Page<MyTpFinishedVO> finishedTp = getFinishedTp(1, 1);
        Page<MyTpUnFinishedVO> unFinishedTp = getunFinishedTp(1, 1, null);

        Integer totalFinishedCount = (int)finishedTp.getTotal();
        Integer totalUnFinishedCoiunt = (int)unFinishedTp.getTotal();
        return totalFinishedCount + totalUnFinishedCoiunt;
    }

    public Page<MyTpFinishedVO> getFinishedTp(Integer pageIndex, Integer pageSize) {

        Page<MyTpFinishedVO> page = new Page<>(pageIndex, pageSize);
        RequestContext context = ContextHolder.get();
        List<Long> relationIds = context.getRelationIds();
        // 查询所有可以参与的项目ID
        List<MyTpFinishedVO> records =
            tpStudentProjectRecordMapper.getFinishedTpV2(context.getAccountId(), context.getSiteId(), relationIds,
                page);
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(tp -> {
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
            });
        }
        page.setRecords(records);
        return page;
    }

    public Page<MyTpUnFinishedVO> getunFinishedTp(Integer pageIndex, Integer pageSize, Integer status) {
        RequestContext context = ContextHolder.get();

        List<MyTpUnFinishedVO> unFinishedList = new ArrayList<>();

        // 查询所有 已开始未完成不在项目PRO项目
        List<MyTpUnFinishedVO> startStudyList =
            tpStudentProjectRecordMapper.getStartViewAnUnFinished(context.getAccountId(), context.getSiteId(),
                context.getRelationIds());
        if (!CollectionUtils.isEmpty(startStudyList)) {
            unFinishedList.addAll(startStudyList);
        }

        // 查询所有 已报名未完成不在项目PRO项目
        List<MyTpUnFinishedVO> ebnrollList =
            tpStudentProjectRecordMapper.getEnrollAnUnFinished(context.getAccountId(), context.getSiteId(),
                context.getRelationIds());
        if (!CollectionUtils.isEmpty(ebnrollList)) {
            // 如果存在已开始的，已报名的丢弃
            List<Long> startStudyTpId =
                unFinishedList.stream().map(MyTpUnFinishedVO::getTpId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(startStudyTpId)) {
                ebnrollList = ebnrollList.stream().filter(it -> !startStudyTpId.contains(it.getTpId()))
                    .collect(Collectors.toList());
            }
            unFinishedList.addAll(ebnrollList);
        }
        // 获取未完成（未开始或）的项目PRO
        List<MyTpUnFinishedVO> proList =
            tpStudentProjectRecordMapper.getProAnUnFinished(context.getAccountId(), context.getSiteId(),
                context.getRelationIds());
        if (!CollectionUtils.isEmpty(proList)) {
            // 查询项目PRO的报名时间或学习时间
            List<MyTpUnFinishedVO> startAndUnFinished = filterUnFinishedTpPro(proList);
            // 过滤未开始PRO
            unFinishedList.addAll(startAndUnFinished);
        }
        CollUtil.sortByProperty(unFinishedList, "studyAndEnorll");
        CollUtil.reverse(unFinishedList);

        unFinishedList = getMyTpUnFinishedVOS(status, unFinishedList);

        Page<MyTpUnFinishedVO> page = new Page<>(pageIndex, pageSize);
        if (CollectionUtils.isEmpty(unFinishedList)) {
            return page;
        }
        List<MyTpUnFinishedVO> currentPage = CollUtil.page(pageIndex - 1, pageSize, unFinishedList);

        currentPage.stream().filter(it -> it.getStartAt() != null && it.getStartAt().getTime() >= new Date().getTime())
            .forEach(tp -> {
                // 当项目未开始时，设为未参加过学习或报名
                tp.setStartType(null);
                tp.setStudyAndEnorll(null);
            });
        if (!CollectionUtils.isEmpty(currentPage)) {
            currentPage.forEach(tp -> {
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
            });
        }
        List<MyTpUnFinishedVO> tpInfo =
            currentPage.stream().filter(v -> Objects.equals(v.getTpType(), TpProEnum.TP_DEFAULT.getCode()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(tpInfo)) {
            List<Long> collect = unFinishedList.stream().map(MyTpUnFinishedVO::getTpId).collect(Collectors.toList());
            LambdaQueryWrapper<TpEnroll> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.in(TpEnroll::getTrainingProjectId, collect);
            List<TpEnroll> tpEnrolls = tpEnrollService.list(lambdaQueryWrapper);
            if (!CollectionUtils.isEmpty(tpEnrolls)) {
                Map<Long, Integer> payTypes =
                    tpEnrolls.stream().collect(Collectors.toMap(TpEnroll::getTrainingProjectId, TpEnroll::getPayType));
                currentPage.forEach(it -> {
                    it.setPayType(payTypes.get(it.getTpId()));
                });
            }
        }
        page.setRecords(currentPage);
        page.setTotal(unFinishedList.size());
        // 查询未完成的项目PRO
        return page;
    }

    private List<MyTpUnFinishedVO> filterUnFinishedTpPro(List<MyTpUnFinishedVO> proList) {
        List<Long> tpProIdList = proList.stream().map(MyTpUnFinishedVO::getTpId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tpProIdList)) {
            return proList;
        }
        RequestContext context = ContextHolder.get();
        List<MyTpUnFinishedVO> allUnFinished = new ArrayList<>();

        // 查询项目PRO的开始学习时间
        List<MyTpUnFinishedVO> viewList =
            tpViewRecordMapper.getTpProMaxViewTime(context.getAccountId(), context.getSiteId(),
                context.getRelationIds(), tpProIdList);
        if (!CollectionUtils.isEmpty(viewList)) {
            allUnFinished.addAll(viewList);
        }
        List<MyTpUnFinishedVO> enrollList =
            tpViewRecordMapper.getTpProMaxEnrollTime(context.getAccountId(), context.getSiteId(),
                context.getRelationIds(), tpProIdList);
        if (!CollectionUtils.isEmpty(enrollList)) {
            allUnFinished.addAll(enrollList);
        }

        CollUtil.sortByProperty(allUnFinished, "studyAndEnorll");
        List<MyTpUnFinishedVO> startAndUnFinished = new ArrayList<>();

        Map<Long, MyTpUnFinishedVO> map = allUnFinished.stream()
            .collect(Collectors.toMap(MyTpUnFinishedVO::getTpId, Function.identity(), (value1, value2) -> value2));
        if (MapUtil.isEmpty(map)) {
            return startAndUnFinished;
        }
        proList.forEach(it -> {
            MyTpUnFinishedVO unFinishedVO = map.get(it.getTpId());
            if (unFinishedVO != null) {
                it.setStudyAndEnorll(unFinishedVO.getStudyAndEnorll());
                it.setStartType(unFinishedVO.getStartType());
                startAndUnFinished.add(it);
            }
        });
        return startAndUnFinished;
    }

    private List<MyTpUnFinishedVO> getMyTpUnFinishedVOS(Integer status, List<MyTpUnFinishedVO> unFinishedList) {
        if (status == null) {
            return unFinishedList;
        }
        // 0:未开始，1：进行中，2：已结束
        if (status == 0) {
            return unFinishedList.stream()
                .filter(it -> it.getStartAt() != null && it.getStartAt().getTime() > System.currentTimeMillis())
                .collect(Collectors.toList());
        }
        if (status == 1) {
            return unFinishedList.stream().filter(
                it -> (it.getStartAt() == null && it.getEndAt() == null) || (it.getStartAt() != null && it.getStartAt()
                    .getTime() <= System.currentTimeMillis() && (it.getEndAt() == null || it.getEndAt()
                    .getTime() >= System.currentTimeMillis())) || (it.getEndAt() != null && it.getEndAt()
                    .getTime() > System.currentTimeMillis() && (it.getStartAt() == null || it.getStartAt()
                    .getTime() <= System.currentTimeMillis()))).collect(Collectors.toList());
        }
        if (status == 2) {
            return unFinishedList.stream()
                .filter(it -> it.getEndAt() != null && it.getEndAt().getTime() < System.currentTimeMillis())
                .collect(Collectors.toList());
        }
        return unFinishedList;
    }

}
