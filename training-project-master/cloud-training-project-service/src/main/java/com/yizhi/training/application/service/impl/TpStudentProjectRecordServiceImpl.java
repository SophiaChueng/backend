package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.mapper.TpStudentProjectRecordMapper;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.vo.api.TpStudentProjectRecordVoVO;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 培训项目完成情况，由学习计划完成记录计算得出 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-09
 */
@Service
@Slf4j
public class TpStudentProjectRecordServiceImpl extends ServiceImpl<TpStudentProjectRecordMapper, TpStudentProjectRecord>
    implements ITpStudentProjectRecordService {

    @Autowired
    private TpStudentProjectRecordMapper tpStudentProjectRecordMapper;

    @Override
    public Integer getFactNumByAccountId(Long accountId) {
        // TODO Auto-generated method stub
        TpStudentProjectRecord tpStudentProjectRecord = new TpStudentProjectRecord();
        tpStudentProjectRecord.setAccountId(accountId);
        QueryWrapper<TpStudentProjectRecord> wrapper =
            new QueryWrapper<TpStudentProjectRecord>(tpStudentProjectRecord);
        return (int)this.count(wrapper);
    }

    @Override
    public TpStudentProjectRecord getTpStudentProjectRecord(Long accountId, Long projectId) {
        // TODO Auto-generated method stub
        TpStudentProjectRecord tpr = new TpStudentProjectRecord();
        tpr.setAccountId(accountId);
        tpr.setTrainingProjectId(projectId);
        QueryWrapper<TpStudentProjectRecord> wrapper = new QueryWrapper<TpStudentProjectRecord>(tpr);
        return this.getOne(wrapper);
    }

    @Override
    public List<TpStudentProjectRecord> getTpStudentProjectRecordByTpIdAndTime(Long tpId, String startTime,
        String endTime) {
        // TODO Auto-generated method stub
        return tpStudentProjectRecordMapper.getTpStudentProjectRecordByTpIdAndTime(tpId, startTime, endTime);
    }

    @Override
    public List<TpStudentProjectRecordVoVO> getProjectsStatus(List<Long> projectIds) {

        RequestContext res = ContextHolder.get();

        List<TpStudentProjectRecordVoVO> list =
            tpStudentProjectRecordMapper.getProjectsStatus(res.getAccountId(), projectIds);
        if (CollectionUtils.isNotEmpty(list)) {
            for (TpStudentProjectRecordVoVO studentProjectRecordVO : list) {
                //未开始
                if (studentProjectRecordVO.getStartTime().getTime() > System.currentTimeMillis()) {
                    studentProjectRecordVO.setState(0);
                }
            }
        }
        return list;
    }

    @Override
    public Integer getProjectsStudyingNum(List<Long> projectIds) {
        RequestContext res = ContextHolder.get();

        return tpStudentProjectRecordMapper.getProjectsStudyingNum(projectIds, res.getCompanyId(), res.getSiteId());
    }

    @Override
    public List<TpStudentProjectRecordVoVO> getProjectsStudyingRecords(List<Long> projectIds) {

        RequestContext res = ContextHolder.get();

        List<TpStudentProjectRecordVoVO> tpStudentProjectRecords =
            tpStudentProjectRecordMapper.getProjectsStudyingRecords(projectIds, res.getAccountId());

        if (CollectionUtils.isNotEmpty(tpStudentProjectRecords)) {
            for (TpStudentProjectRecordVoVO tpStudentProjectRecordVO : tpStudentProjectRecords) {
                if (tpStudentProjectRecordVO.getStartTime().getTime() > System.currentTimeMillis()) {
                    tpStudentProjectRecordVO.setState(0);
                }
            }
        }
        return tpStudentProjectRecords;
    }

    @Override
    public List<TpStudentProjectRecordVo> getTpStudentProjectRecordBySiteIdsAndTime(List<Long> siteIds,
        String startDate, String endDate) {
        return tpStudentProjectRecordMapper.getTpStudentProjectRecordBySiteIdsAndTime(siteIds, startDate, endDate);
    }

    @Override
    public List<Long> accountFinishedTpId(Long accountId, Long siteId) {
        return tpStudentProjectRecordMapper.selectFinishedTpAndProId(accountId, siteId);
    }

    @Override
    public List<Long> getFinishedAccountIds(Long tpId, Long companyId, Long siteId) {
        return tpStudentProjectRecordMapper.getFinishedAccountIds(tpId, companyId, siteId);
    }

    @Override
    public Integer getFinishedAccountCount(Long tpId, Long companyId, Long siteId) {
        return tpStudentProjectRecordMapper.getFinishedAccountCount(tpId, companyId, siteId);
    }

    @Override
    public List<BaseViewRecordVO> getFinishedRecordsListGroupByAccountId(BaseParamVO baseParamVO) {

        List<BaseViewRecordVO> voList = new ArrayList<>();
        Long companyId = baseParamVO.getCompanyId();
        Long siteId = baseParamVO.getSiteId();
        Long trainProjectId = baseParamVO.getRelationId();
        if (null == companyId || null == siteId) {
            return voList;
        }
        try {

            return tpStudentProjectRecordMapper.getFinishedRecordsListGroupByAccountId(trainProjectId, companyId,
                siteId);
        } catch (Exception e) {
            log.error("获取学员完成项目记录异常", e);
        }
        return voList;
    }

}
