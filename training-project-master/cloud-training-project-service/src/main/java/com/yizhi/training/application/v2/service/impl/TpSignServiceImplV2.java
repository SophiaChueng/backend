package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpSign;
import com.yizhi.training.application.v2.mapper.TpSignMapperV2;
import com.yizhi.training.application.v2.service.TpSignService;
import org.springframework.stereotype.Service;

@Service
public class TpSignServiceImplV2 extends ServiceImpl<TpSignMapperV2, TpSign> implements TpSignService {

    /**
     * 查询项目的签到设置
     *
     * @param trainingProjectId
     * @return
     */
    @Override
    public TpSign selectByTpId(Long trainingProjectId) {
        QueryWrapper<TpSign> wrapper = new QueryWrapper<>();
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("state", 0);
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }
}
