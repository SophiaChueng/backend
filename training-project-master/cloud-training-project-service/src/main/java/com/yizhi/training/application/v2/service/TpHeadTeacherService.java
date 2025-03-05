package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpHeadTeacher;

import java.util.List;

public interface TpHeadTeacherService extends IService<TpHeadTeacher> {

    @Override
    default TpHeadTeacher getOne(Wrapper<TpHeadTeacher> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<Long> getTeacherIds(Long trainingProjectId);

    Boolean checkTeacher(Long trainingProjectId, Long accountId);

    Boolean saveHeadTeachers(Long trainingProjectId, List<Long> accountIds);

    Boolean deleteByTpId(Long trainingProjectId);
}
