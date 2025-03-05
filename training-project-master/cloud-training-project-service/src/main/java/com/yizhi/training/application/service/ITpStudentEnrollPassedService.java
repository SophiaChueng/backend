package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpStudentEnrollPassed;

/**
 * <p>
 * 学生参与培训项目记录（只针对需要报名的培训项目，不需要报名的该表不记录） 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-28
 */
public interface ITpStudentEnrollPassedService extends IService<TpStudentEnrollPassed> {

    @Override
    default TpStudentEnrollPassed getOne(Wrapper<TpStudentEnrollPassed> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 查询培训项目是否报名通过
     *
     * @param accountId
     * @param siteId
     * @param tpProjrctId
     * @return
     */
    Long selectTpIdByCondition(Long tpProjrctId, Long accountId, Long siteId);

}
