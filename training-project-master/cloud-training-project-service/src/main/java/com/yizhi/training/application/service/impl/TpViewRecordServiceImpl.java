package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.training.application.domain.TpViewRecord;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.TpViewRecordMapper;
import com.yizhi.training.application.service.TpViewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
@Service
public class TpViewRecordServiceImpl extends ServiceImpl<TpViewRecordMapper, TpViewRecord>
    implements TpViewRecordService {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TpViewRecordMapper tpViewRecordMapper;

    @Override
    public Long getLastStudyTpId(List<Long> tpIdList, Long accountId, Long companyId, Long siteId) {
        QueryWrapper<TpViewRecord> wrapper = new QueryWrapper();
        wrapper.in("training_project_id", tpIdList);
        wrapper.eq("company_id", companyId);
        wrapper.eq("site_id", siteId);
        wrapper.eq("account_id", accountId);
        wrapper.select("training_project_id");
        wrapper.orderByDesc("time");
        wrapper.last("limit 1");
        List<TpViewRecord> viewRecords = this.baseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(viewRecords)) {
            return null;
        }
        return viewRecords.get(0).getTrainingProjectId();
    }

    @Override
    public Date getstartTimeByTpId(Long tpId, Long accountId, Long companyId, Long siteId) {
        QueryWrapper<TpViewRecord> wrapper = new QueryWrapper();
        wrapper.eq("training_project_id", tpId);
        wrapper.eq("company_id", companyId);
        wrapper.eq("site_id", siteId);
        wrapper.eq("account_id", accountId);
        wrapper.select("time");
        wrapper.orderByAsc("time");
        wrapper.last("limit 1");
        List<TpViewRecord> viewRecords = this.baseMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(viewRecords)) {
            return null;
        }
        return viewRecords.get(0).getTime();
    }

    @Override
    public Integer getLearningCount(Long tpId, Long companyId, Long siteId) {
        return this.baseMapper.getLearningCount(tpId, companyId, siteId);
    }

    @Override
    public Integer getViewNumRange(TrainingProject trainingProject) {
        String redisKey = "training:view:range:" + trainingProject.getId();
        Object o = redisCache.get(redisKey);
        if (Objects.nonNull(o)) {
            return Integer.parseInt(o.toString());
        }
        Integer count = 0;
        if (trainingProject.getVisibleRange() == 1) {
            count = tpViewRecordMapper.getViewNum(trainingProject);
        } else {
            count = tpViewRecordMapper.getViewNumRange(trainingProject);
        }
        redisCache.set(redisKey, count + "", 600);
        return count;
    }
}
