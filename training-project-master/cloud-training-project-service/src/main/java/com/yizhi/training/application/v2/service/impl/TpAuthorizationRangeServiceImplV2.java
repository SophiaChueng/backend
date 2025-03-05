package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.util.CacheUtil;
import com.yizhi.training.application.v2.mapper.TpAuthorizationRangeMapperV2;
import com.yizhi.training.application.v2.service.TpAuthorizationRangeService;
import com.yizhi.training.application.v2.service.biz.TrainingProjectBizService;
import com.yizhi.training.application.v2.vo.TpVisibleRangeVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TpAuthorizationRangeServiceImplV2 extends ServiceImpl<TpAuthorizationRangeMapperV2, TpAuthorizationRange>
    implements TpAuthorizationRangeService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private CacheUtil cacheUtil;

    @Autowired
    private TrainingProjectBizService trainingProjectBizService;

    @Override
    public List<TpAuthorizationRange> getAuthorizationRanges(Long bizId) {
        // 查询缓存
        List<TpAuthorizationRange> rangeList = cacheUtil.getAuthorizationList(bizId);
        if (CollectionUtils.isNotEmpty(rangeList)) {
            return rangeList;
        }

        QueryWrapper<TpAuthorizationRange> wrapper = new QueryWrapper<>();
        wrapper.eq("biz_id", bizId);
        wrapper.eq("deleted", 0);
        rangeList = list(wrapper);

        // 添加到缓存
        cacheUtil.addAuthorizationList(rangeList, bizId);
        return rangeList;
    }

    /**
     * 保存可见范围
     *
     * @param trainingProjectId
     * @param list
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveVisibleRange(Long trainingProjectId, List<TpVisibleRangeVO> list) {
        // 先物理删除已有的数据
        QueryWrapper<TpAuthorizationRange> wrapper = new QueryWrapper<>();
        wrapper.eq("biz_id", trainingProjectId);
        remove(wrapper);

        RequestContext context = ContextHolder.get();

        List<TpAuthorizationRange> rangeList = new ArrayList<>();
        list.forEach(vo -> {
            TpAuthorizationRange range = new TpAuthorizationRange();
            range.setId(idGenerator.generate());
            range.setCompanyId(context.getCompanyId());
            range.setSiteId(context.getSiteId());
            range.setBizId(trainingProjectId);
            range.setType(vo.getType());
            range.setRelationId(vo.getRelationId());
            range.setName(vo.getName());

            rangeList.add(range);
        });
        if (CollectionUtils.isEmpty(rangeList)) {
            return true;
        }
        boolean res = saveBatch(rangeList);
        cacheUtil.delAuthorizationList(trainingProjectId);
        trainingProjectBizService.generateVisibleAccountIdsCache(trainingProjectId, context.getCompanyId(), list);
        return res;
    }
}
