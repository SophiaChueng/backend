package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.v2.vo.TpVisibleRangeVO;

import java.util.List;

public interface TpAuthorizationRangeService extends IService<TpAuthorizationRange> {

    @Override
    default TpAuthorizationRange getOne(Wrapper<TpAuthorizationRange> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<TpAuthorizationRange> getAuthorizationRanges(Long bizId);

    Boolean saveVisibleRange(Long trainingProjectId, List<TpVisibleRangeVO> list);
}
