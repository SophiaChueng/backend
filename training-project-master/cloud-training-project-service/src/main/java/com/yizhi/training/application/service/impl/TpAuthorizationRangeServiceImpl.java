package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.TpAuthorizationRangeMapper;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.ITpAuthorizationRangeService;
import com.yizhi.training.application.vo.manage.RelationIdVo;
import com.yizhi.training.application.vo.manage.VisibleRangeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 授权范围 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-19
 */
@Service
@Transactional
public class TpAuthorizationRangeServiceImpl extends ServiceImpl<TpAuthorizationRangeMapper, TpAuthorizationRange>
    implements ITpAuthorizationRangeService {

    private Logger logger = LoggerFactory.getLogger(TpAuthorizationRangeServiceImpl.class);

    @Autowired
    private TpAuthorizationRangeMapper tpAuthorizationRangeMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public Integer batchInsert(BaseModel<VisibleRangeVo> model) {
        // 删除以前的
        TpAuthorizationRange oldRange = new TpAuthorizationRange();
        oldRange.setBizId(model.getObj().getTrainingProjectId());
        oldRange.setSiteId(model.getContext().getSiteId());
        tpAuthorizationRangeMapper.delete(new QueryWrapper<>(oldRange));

        VisibleRangeVo visibleRangeVo = model.getObj();
        if (!CollectionUtils.isEmpty(visibleRangeVo.getList())) {
            List<TpAuthorizationRange> ranges = new ArrayList<>();

            for (RelationIdVo vo : visibleRangeVo.getList()) {
                TpAuthorizationRange range = new TpAuthorizationRange();
                range.setBizId(visibleRangeVo.getTrainingProjectId());
                range.setId(idGenerator.generate());
                range.setRelationId(vo.getRelationId());
                range.setType(vo.getType());
                range.setName(vo.getName());
                range.setSiteId(model.getContext().getSiteId());
                ranges.add(range);
            }

            // 更新主体状态
            TrainingProject tp = new TrainingProject();
            tp.setId(model.getObj().getTrainingProjectId());
            tp.setVisibleRange(ProjectConstant.PROJECT_VISIBLE_RANGE_ACCOUNT);
            if (tp.updateById()) {
                return tpAuthorizationRangeMapper.batchInsert(ranges);
            }
        } else {
            // 更新主体状态
            TrainingProject tp = new TrainingProject();
            tp.setId(model.getObj().getTrainingProjectId());
            tp.setVisibleRange(ProjectConstant.PROJECT_VISIBLE_RANGE_SITE);
            tp.updateById();
        }
        return null;
    }

    @Override
    public Boolean insertVisibleRange(List<TpAuthorizationRange> tpAuthorizationRanges) {
        if (!CollectionUtils.isEmpty(tpAuthorizationRanges)) {
            Long projectId = tpAuthorizationRanges.get(0).getBizId();
            TpAuthorizationRange tpAuthorizationRange = new TpAuthorizationRange();
            tpAuthorizationRange.setBizId(projectId);
            QueryWrapper<TpAuthorizationRange> QueryWrapper = new QueryWrapper<>(tpAuthorizationRange);
            this.baseMapper.delete(QueryWrapper);
            for (TpAuthorizationRange tpAuthorizationRange1 : tpAuthorizationRanges) {
                tpAuthorizationRange1.setId(idGenerator.generate());
            }
            return this.saveBatch(tpAuthorizationRanges);
        } else {
            logger.error("列表为空");
            return Boolean.FALSE;
        }
    }

    @Override
    public List<TpAuthorizationRange> listByBizId(Long bizId) {
        // TODO Auto-generated method stub
        TpAuthorizationRange tpAuthorizationRange = new TpAuthorizationRange();
        tpAuthorizationRange.setBizId(bizId);
        tpAuthorizationRange.setDeleted(0);
        QueryWrapper<TpAuthorizationRange> wrapper = new QueryWrapper<TpAuthorizationRange>(tpAuthorizationRange);
        return this.list(wrapper);
    }

    @Override
    public List<TpAuthorizationRange> selectBySiteIds(List<Long> siteIds) {
        if (CollectionUtils.isEmpty(siteIds)) {
            return null;
        }
        return tpAuthorizationRangeMapper.selectBySiteIds(siteIds);
    }
}
