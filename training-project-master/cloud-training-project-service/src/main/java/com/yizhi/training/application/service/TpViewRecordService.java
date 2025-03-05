package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpViewRecord;
import com.yizhi.training.application.domain.TrainingProject;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-09-11
 */
public interface TpViewRecordService extends IService<TpViewRecord> {

    @Override
    default TpViewRecord getOne(Wrapper<TpViewRecord> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Long getLastStudyTpId(List<Long> tpIdList, Long accountId, Long companyId, Long siteId);

    Date getstartTimeByTpId(Long id, Long accountId, Long companyId, Long siteId);

    Integer getLearningCount(Long tpId, Long companyId, Long siteId);
    Integer getViewNumRange(TrainingProject trainingProject);
}
