package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TrainingProjectPro;

import java.util.List;

public interface TrainingProjectProService extends IService<TrainingProjectPro> {

    @Override
    default TrainingProjectPro getOne(Wrapper<TrainingProjectPro> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Page<TrainingProjectPro> getTpProList(Long companyId, Long siteId, String tpProName, List<Long> tpProIds,
        Integer pageNo, Integer pageSize);

}
