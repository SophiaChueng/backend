package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanRemind;

/**
 * <p>
 * 培训计划提醒 物理删除 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpPlanRemindService extends IService<TpPlanRemind> {

    @Override
    default TpPlanRemind getOne(Wrapper<TpPlanRemind> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
