package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpStudentPlanRecord;
import com.yizhi.training.application.mapper.TpStudentPlanRecordMapper;
import com.yizhi.training.application.service.ITpStudentPlanRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 学员完成活动记录（这里无论有没有被设置成别的活动的开启条件，都记录） 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
@Service
public class TpStudentPlanRecordServiceImpl extends ServiceImpl<TpStudentPlanRecordMapper, TpStudentPlanRecord>
    implements ITpStudentPlanRecordService {

    @Autowired
    TpStudentPlanRecordMapper mapper;

    @Override
    public Integer getFinishedCountAllByPlanId(Long accountId, Long siteId, List<Long> planId) {
        return mapper.getFinishedCountAllByPlanId(accountId, siteId, planId);
    }

    @Override
    public List<Long> getFinishedIdByTpId(Long accountId, Long siteId, List<Long> planIds) {
        return mapper.getFinishedIdByPlanId(accountId, siteId, planIds);
    }

    @Override
    public Date getPlanMinFinishedTime(Long accountId, Long siteId, List<Long> prePlanIds, Integer finishedCount) {
        return mapper.getPlanMinFinishedTime(accountId, siteId, prePlanIds, finishedCount);
    }

    @Override
    public List<Long> getFinishedAccountIds(Long planId, Long companyId, Long siteId) {
        return mapper.getFinishedAccountIds(planId, companyId, siteId);
    }
}
