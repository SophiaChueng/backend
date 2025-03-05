package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanActivityConditionPost;

/**
 * <p>
 * 学习活动（考试、证书）完成条件 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
public interface ITpPlanActivityConditionPostService extends IService<TpPlanActivityConditionPost> {

    @Override
    default TpPlanActivityConditionPost getOne(Wrapper<TpPlanActivityConditionPost> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
