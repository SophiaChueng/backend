package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpStudentPlanRecord;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 学员完成计划记录 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface ITpStudentPlanRecordService extends IService<TpStudentPlanRecord> {

    @Override
    default TpStudentPlanRecord getOne(Wrapper<TpStudentPlanRecord> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Integer getFinishedCountAllByPlanId(Long accountId, Long siteId, List<Long> planId);

    List<Long> getFinishedIdByTpId(Long accountId, Long siteId, List<Long> planIds);

    Date getPlanMinFinishedTime(Long accountId, Long siteId, List<Long> prePlanIds, Integer finishedCount);

    List<Long> getFinishedAccountIds(Long planId, Long companyId, Long siteId);
}
