package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanConditionPre;

/**
 * <p>
 * 学习计化前置条件 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface ITpPlanConditionPreService extends IService<TpPlanConditionPre> {

    @Override
    default TpPlanConditionPre getOne(Wrapper<TpPlanConditionPre> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
