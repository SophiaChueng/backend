package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpSign;
import com.yizhi.training.application.domain.TpSignTime;
import com.yizhi.training.application.v2.vo.TpSignTimeVO;

import java.util.List;

public interface TpSignTimeService extends IService<TpSignTime> {

    @Override
    default TpSignTime getOne(Wrapper<TpSignTime> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<TpSignTime> selectBySignId(Long trainingProjectId, Long signId);

    void updateSignTime(Long companyId, Long siteId, Long trainingProjectId, TpSign sign,
        List<TpSignTimeVO> tpSignTimes);
}
