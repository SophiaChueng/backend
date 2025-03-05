package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpEnroll;
import com.yizhi.training.application.v2.mapper.TpEnrollMapperV2;
import com.yizhi.training.application.v2.service.TpEnrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TpEnrollServiceImplV2 extends ServiceImpl<TpEnrollMapperV2, TpEnroll> implements TpEnrollService {

    @Autowired
    private TpEnrollMapperV2 mapperV2;

    @Override
    public TpEnroll selectByTpId(Long trainingProjectId) {
        QueryWrapper<TpEnroll> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        // 由于该表没有deleted字段，且没有唯一索引，故加limit 1
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Integer getEnrollUserCount(Long tpId, Integer needAudit) {
        return mapperV2.getEnrollUserCount(tpId, needAudit);
    }

    @Override
    public Integer getEnrollStatus(Long tpId, Long accountId) {
        return mapperV2.getEnrollStatus(tpId, accountId);
    }
}


