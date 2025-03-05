package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.vo.manage.VisibleRangeVo;

import java.util.List;

/**
 * <p>
 * 授权范围 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-19
 */
public interface ITpAuthorizationRangeService extends IService<TpAuthorizationRange> {

    @Override
    default TpAuthorizationRange getOne(Wrapper<TpAuthorizationRange> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 批量插入可见范围
     *
     * @param model
     * @return
     */
    Integer batchInsert(BaseModel<VisibleRangeVo> model);

    /**
     * 批量新增可见范围，不删除之前记录
     *
     * @param tpAuthorizationRanges
     * @return
     */
    Boolean insertVisibleRange(List<TpAuthorizationRange> tpAuthorizationRanges);

    /**
     * 根据关联id找到所有的指定人
     *
     * @param bizId
     * @return
     */
    List<TpAuthorizationRange> listByBizId(Long bizId);

    List<TpAuthorizationRange> selectBySiteIds(List<Long> siteIds);
}
