package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TrainingProjectPro;
import com.yizhi.training.application.v2.mapper.TrainingProjectProMapperV2;
import com.yizhi.training.application.v2.service.TrainingProjectProService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TrainingProjectProServiceImplV2 extends ServiceImpl<TrainingProjectProMapperV2, TrainingProjectPro>
    implements TrainingProjectProService {

    @Override
    public Page<TrainingProjectPro> getTpProList(Long companyId, Long siteId, String tpProName, List<Long> tpProIds,
        Integer pageNo, Integer pageSize) {
        QueryWrapper<TrainingProjectPro> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", companyId);
        wrapper.eq("site_id", siteId);
        if (StringUtils.isNotBlank(tpProName)) {
            wrapper.like("tp_pro_name", tpProName);
        }
        if (CollectionUtils.isNotEmpty(tpProIds)) {
            wrapper.in("id", tpProIds);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc(Arrays.asList("sort", "create_time"));

        return page(new Page<>(pageNo, pageSize), wrapper);
    }
}
