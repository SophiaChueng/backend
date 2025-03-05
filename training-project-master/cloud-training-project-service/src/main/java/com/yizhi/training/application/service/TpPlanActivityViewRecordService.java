package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpPlanActivityViewRecord;
import com.yizhi.training.application.model.BaseModel;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
public interface TpPlanActivityViewRecordService extends IService<TpPlanActivityViewRecord> {

    @Override
    default TpPlanActivityViewRecord getOne(Wrapper<TpPlanActivityViewRecord> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 增加一条活动浏览记录
     *
     * @param model
     * @return
     */
    Integer addViewRecord(BaseModel<Long> model);

}
