package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpStudyClickLog;

import java.util.List;

public interface TpStudyClickLogService extends IService<TpStudyClickLog> {

    @Override
    default TpStudyClickLog getOne(Wrapper<TpStudyClickLog> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    public Long getLastStudyTpId(List<Long> tpId, Long accountId, Long companyId, Long siteId);

}
