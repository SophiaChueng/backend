package com.yizhi.training.application.service.impl;

import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.mapper.TpPlanActivityMapper;
import com.yizhi.training.application.service.KmhService;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KmgServiceImpl implements KmhService {

    @Autowired
    private TpPlanActivityMapper tpPlanActivityMapper;

    @Override
    public List<TpPlanActivityVo> getActivityByTpList(List<Long> aiaProjectKmhTpIdList) {
        RequestContext requestContext = ContextHolder.get();
        return tpPlanActivityMapper.getAllAiaActivity(aiaProjectKmhTpIdList, requestContext.getSiteId());
    }
}
