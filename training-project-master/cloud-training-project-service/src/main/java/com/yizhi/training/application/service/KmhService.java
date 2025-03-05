package com.yizhi.training.application.service;

import com.yizhi.training.application.vo.domain.TpPlanActivityVo;

import java.util.List;

public interface KmhService {

    List<TpPlanActivityVo> getActivityByTpList(List<Long> aiaProjectKmhTpIdList);
}
