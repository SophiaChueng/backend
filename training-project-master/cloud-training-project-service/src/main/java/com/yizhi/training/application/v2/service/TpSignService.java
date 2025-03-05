package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpSign;

public interface TpSignService extends IService<TpSign> {

    @Override
    default TpSign getOne(Wrapper<TpSign> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    TpSign selectByTpId(Long trainingProjectId);
}
