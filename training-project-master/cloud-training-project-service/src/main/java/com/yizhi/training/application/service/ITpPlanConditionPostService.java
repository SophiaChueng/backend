package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanConditionPost;

/**
 * <p>
 * 学习计划完成条件 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface ITpPlanConditionPostService extends IService<TpPlanConditionPost> {

    @Override
    default TpPlanConditionPost getOne(Wrapper<TpPlanConditionPost> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
