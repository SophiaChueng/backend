package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpRemind;

/**
 * <p>
 * 培训项目提醒 物理删除 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpRemindService extends IService<TpRemind> {

    @Override
    default TpRemind getOne(Wrapper<TpRemind> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
