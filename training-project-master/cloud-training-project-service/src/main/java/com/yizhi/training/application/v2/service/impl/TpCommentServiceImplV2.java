package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpComment;
import com.yizhi.training.application.v2.mapper.TpCommentMapperV2;
import com.yizhi.training.application.v2.service.TpCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TpCommentServiceImplV2 extends ServiceImpl<TpCommentMapperV2, TpComment> implements TpCommentService {

    @Override
    public Integer getTpCommentCount(Long trainingProjectId, Integer state) {
        QueryWrapper<TpComment> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        if (state != null) {
            wrapper.eq("state", state);
        }
        return (int)count(wrapper);
    }

    @Override
    public List<TpComment> getTpComments(Long trainingProjectId, Integer state, Integer pageNo, Integer pageSize) {
        QueryWrapper<TpComment> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        if (state != null) {
            wrapper.eq("state", state);
        }
        wrapper.orderByDesc("create_time");
        wrapper.last("LIMIT " + (pageNo - 1) * pageSize + "," + pageSize);
        return list(wrapper);
    }
}
