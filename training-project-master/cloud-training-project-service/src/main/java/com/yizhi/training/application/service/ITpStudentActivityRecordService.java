package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpStudentActivityRecord;

/**
 * <p>
 * 学员完成活动记录（这里无论有没有被设置成别的活动的开启条件，都记录） 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface ITpStudentActivityRecordService extends IService<TpStudentActivityRecord> {

    @Override
    default TpStudentActivityRecord getOne(Wrapper<TpStudentActivityRecord> queryWrapper) {
        return getOne(queryWrapper, false);
    }

}
