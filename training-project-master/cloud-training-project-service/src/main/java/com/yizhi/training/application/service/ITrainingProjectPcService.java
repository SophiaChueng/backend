package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.vo.api.TrainingProjectDetailVo;

import java.text.ParseException;
import java.util.Date;

/**
 * <p>
 * 培训项目主体表（ PC端 ） 服务类
 * </p>
 */
public interface ITrainingProjectPcService extends IService<TrainingProject> {

    @Override
    default TrainingProject getOne(Wrapper<TrainingProject> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /***************************************************  PC端  ****************************************/

    /**
     * 培训详情
     *
     * @param trainingProjectId
     * @param context
     * @param containStatistics 包含学习记录
     * @return
     */
    TrainingProjectDetailVo getPcTpDetail(Long trainingProjectId, RequestContext context, Date now)
        throws ParseException;

}
