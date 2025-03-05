package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpProMapping;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.v2.mapper.TpProMappingMapperV2;
import com.yizhi.training.application.v2.service.TpProMappingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TpProMappingServiceImplV2 extends ServiceImpl<TpProMappingMapperV2, TpProMapping>
    implements TpProMappingService {

    @Override
    public Boolean deleteBatchByTpId(Long trainingProjectId) {
        RequestContext context = ContextHolder.get();

        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("training_project_id", trainingProjectId);
        return remove(wrapper);
    }

    @Override
    public Integer getTpCountOfTpPro(Long tpProId) {
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("tp_pro_id", tpProId);
        wrapper.eq("deleted", 0);
        return (int)count(wrapper);
    }

    @Override
    public Boolean deleteBatchByProId(Long tpProId) {
        RequestContext context = ContextHolder.get();

        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("tp_pro_id", tpProId);
        return remove(wrapper);
    }

    @Override
    public Integer getTpCountOfPro(Long tpProId, String searchTpName) {
        return this.baseMapper.getTpCountOfPro(tpProId, searchTpName);
    }

    @Override
    public List<TrainingProject> getTpListOfPro(Long tpProId, String searchTpName, Integer pageNo, Integer pageSize,
        Integer status) {
        return this.baseMapper.getTpListOfPro(tpProId, searchTpName, (pageNo - 1) * pageSize, pageSize, status);
    }

    @Override
    public Boolean deleteByProIdAndTpIds(Long tpProId, List<Long> trainingProjectIds) {
        if (tpProId == null || tpProId <= 0 || CollectionUtils.isEmpty(trainingProjectIds)) {
            return false;
        }
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("tp_pro_id", tpProId);
        wrapper.in("training_project_id", trainingProjectIds);
        return remove(wrapper);
    }

    @Override
    public List<Long> getTpIds(Long tpProId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.select("training_project_id");
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("tp_pro_id", tpProId);
        wrapper.eq("deleted", 0);
        List<TpProMapping> list = list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(TpProMapping::getTrainingProjectId).collect(Collectors.toList());
    }

    @Override
    public Integer getOneByTpId(Long tpProId, Long trainingProjectId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("tp_pro_id", tpProId);
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.last("LIMIT 1");
        TpProMapping mapping = getOne(wrapper);
        return mapping == null ? 0 : mapping.getSort();
    }

    @Override
    public Boolean addSortValue(Long tpProId, Integer sort) {
        return this.baseMapper.updateSortValue(tpProId, sort);
    }

    @Override
    public Boolean updateBy(TpProMapping mapping, Long tpProId, Long moveTpId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("tp_pro_id", tpProId);
        wrapper.eq("training_project_id", moveTpId);
        wrapper.eq("deleted", 0);
        return update(mapping, wrapper);
    }

    @Override
    public Integer getMaxSort(Long tpProId) {
        RequestContext context = ContextHolder.get();
        Integer maxSort = this.baseMapper.getMaxSort(context.getCompanyId(), context.getSiteId(), tpProId);
        return maxSort == null ? 0 : maxSort;
    }

    @Override
    public Map<Long, Integer> getMaxSortOfTp(Long trainingProjectId, List<Long> tpProIds) {
        if (CollectionUtils.isEmpty(tpProIds)) {
            return Collections.emptyMap();
        }
        RequestContext context = ContextHolder.get();
        List<TpProMapping> maxSorts =
            this.baseMapper.getMaxSortsBy(context.getCompanyId(), context.getSiteId(), trainingProjectId, tpProIds);
        if (CollectionUtils.isEmpty(maxSorts)) {
            return Collections.emptyMap();
        }
        return maxSorts.stream()
            .collect(Collectors.toMap(TpProMapping::getTpProId, TpProMapping::getSort, (o1, o2) -> o2));
    }

    @Override
    public List<TpProMapping> getMappingListByTpId(Long trainingProjectId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc(Arrays.asList("sort", "create_time"));
        return list(wrapper);
    }

    @Override
    public List<TpProMapping> getMappingListByTpPro(Long tpProId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("tp_pro_id", tpProId);
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("sort");
        return list(wrapper);
    }

    @Override
    public List<Long> getInTpProIds(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return tpIds;
        }
        QueryWrapper<TpProMapping> wrapper = new QueryWrapper<>();
        wrapper.in("training_project_id", tpIds);
        wrapper.eq("deleted", 0);

        List<TpProMapping> list = list(wrapper);
        return list.stream().map(TpProMapping::getTrainingProjectId).collect(Collectors.toList());
    }
}
