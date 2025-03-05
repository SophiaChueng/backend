package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.StatisticsTrainingProjectLearn;

/**
 * <p>
 * 学员学习记录 服务类
 * </p>
 *
 * @author fulan123
 * @since 2018-10-19
 */
public interface IStatisticsTrainingProjectLearnService extends IService<StatisticsTrainingProjectLearn> {

    @Override
    default StatisticsTrainingProjectLearn getOne(Wrapper<StatisticsTrainingProjectLearn> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
