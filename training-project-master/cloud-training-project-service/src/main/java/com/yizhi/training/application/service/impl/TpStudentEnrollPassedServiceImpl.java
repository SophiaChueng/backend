package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpStudentEnrollPassed;
import com.yizhi.training.application.mapper.TpStudentEnrollPassedMapper;
import com.yizhi.training.application.service.ITpStudentEnrollPassedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学生参与培训项目记录（只针对需要报名的培训项目，不需要报名的该表不记录） 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-28
 */
@Service
public class TpStudentEnrollPassedServiceImpl extends ServiceImpl<TpStudentEnrollPassedMapper, TpStudentEnrollPassed>
    implements ITpStudentEnrollPassedService {

    @Autowired
    TpStudentEnrollPassedMapper studentEnrollPassedMapper;

    @Override
    public Long selectTpIdByCondition(Long tpProjrctId, Long accountId, Long siteId) {
        return studentEnrollPassedMapper.selectTpIdByCondition(tpProjrctId, accountId, siteId);
    }
}
