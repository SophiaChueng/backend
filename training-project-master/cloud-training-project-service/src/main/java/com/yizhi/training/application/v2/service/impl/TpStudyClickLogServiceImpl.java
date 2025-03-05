package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpStudyClickLog;
import com.yizhi.training.application.v2.mapper.TpStudyClickLogMapperV2;
import com.yizhi.training.application.v2.service.TpStudyClickLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class TpStudyClickLogServiceImpl extends ServiceImpl<TpStudyClickLogMapperV2, TpStudyClickLog>
    implements TpStudyClickLogService {

    @Autowired
    private TpStudyClickLogMapperV2 tpStudyClickLogMapperV2;

    @Override
    public Long getLastStudyTpId(List<Long> tpId, Long accountId, Long companyId, Long siteId) {
        QueryWrapper<TpStudyClickLog> wrapper = new QueryWrapper();
        wrapper.eq("account_id", accountId);
        wrapper.eq("site_id", siteId);
        wrapper.eq("company_id", companyId);
        wrapper.in("training_project_id", tpId);
        wrapper.select("training_project_id");
        wrapper.orderByDesc("created_at");
        wrapper.last("limit 1");
        List<TpStudyClickLog> tpStudyClickLogs = tpStudyClickLogMapperV2.selectList(wrapper);
        if (CollectionUtils.isEmpty(tpStudyClickLogs)) {
            return null;
        }
        return tpStudyClickLogs.get(0).getTrainingProjectId();
    }
}
