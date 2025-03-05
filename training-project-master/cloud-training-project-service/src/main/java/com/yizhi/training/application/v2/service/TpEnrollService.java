package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpEnroll;

public interface TpEnrollService extends IService<TpEnroll> {

    @Override
    default TpEnroll getOne(Wrapper<TpEnroll> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    TpEnroll selectByTpId(Long trainingProjectId);

    Integer getEnrollUserCount(Long tpId, Integer needAudit);

    Integer getEnrollStatus(Long tpId, Long accountId);
}
