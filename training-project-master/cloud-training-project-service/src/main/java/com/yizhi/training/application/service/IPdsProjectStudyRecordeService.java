package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.PdsProjectStudyRecorde;
import com.yizhi.training.application.vo.PdsProjectStudyRecordeVo;

import java.util.List;

/**
 * <p>
 * pds 自定义项目 学习记录 服务类
 * </p>
 *
 * @author fulan123
 * @since 2022-05-18
 */
public interface IPdsProjectStudyRecordeService extends IService<PdsProjectStudyRecorde> {

    @Override
    default PdsProjectStudyRecorde getOne(Wrapper<PdsProjectStudyRecorde> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 分页查询 排行榜列表
     *
     * @param pageSize
     * @param pageNum
     * @param companyId
     * @param siteId
     * @param pid
     * @return
     */
    Page<PdsProjectStudyRecordeVo> studyPeriodRankingPage(Integer pageSize, Integer pageNum, Long companyId,
        Long siteId, Long pid, Long uid);

    /**
     * 查询自己的课程学习名次和情况
     *
     * @param companyId
     * @param siteId
     * @param pid
     * @param uid
     * @return
     */
    List<PdsProjectStudyRecordeVo> studyPeriodRankingList(Long companyId, Long siteId, Long pid, Long uid);

}
