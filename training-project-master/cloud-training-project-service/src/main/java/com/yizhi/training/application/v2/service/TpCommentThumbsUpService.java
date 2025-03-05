package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpCommentThumbsUp;

import java.util.List;
import java.util.Map;

public interface TpCommentThumbsUpService extends IService<TpCommentThumbsUp> {

    @Override
    default TpCommentThumbsUp getOne(Wrapper<TpCommentThumbsUp> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Map<Long, Integer> getThumbsUpCountMap(List<Long> commentIds);
}
