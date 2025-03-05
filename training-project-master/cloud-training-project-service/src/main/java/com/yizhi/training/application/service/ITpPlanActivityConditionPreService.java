package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanActivityConditionPre;

/**
 * <p>
 * 学习活动前置条件 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface ITpPlanActivityConditionPreService extends IService<TpPlanActivityConditionPre> {

    @Override
    default TpPlanActivityConditionPre getOne(Wrapper<TpPlanActivityConditionPre> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
