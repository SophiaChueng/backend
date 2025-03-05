package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpComment;

import java.util.List;

public interface TpCommentService extends IService<TpComment> {

    @Override
    default TpComment getOne(Wrapper<TpComment> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Integer getTpCommentCount(Long trainingProjectId, Integer state);

    List<TpComment> getTpComments(Long trainingProjectId, Integer state, Integer pageNo, Integer pageSize);
}
