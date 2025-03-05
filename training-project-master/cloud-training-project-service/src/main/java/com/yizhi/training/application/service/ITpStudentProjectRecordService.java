package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.vo.api.TpStudentProjectRecordVoVO;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;

import java.util.List;

/**
 * <p>
 * 培训项目完成情况，由学习计划完成记录计算得出 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-09
 */
public interface ITpStudentProjectRecordService extends IService<TpStudentProjectRecord> {

    @Override
    default TpStudentProjectRecord getOne(Wrapper<TpStudentProjectRecord> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 实际完成数量
     *
     * @param accountId
     * @return
     */
    Integer getFactNumByAccountId(Long accountId);

    TpStudentProjectRecord getTpStudentProjectRecord(Long accountId, Long projectId);

    /**
     * 根据培训项目id和完成时间筛选项目完成情况
     *
     * @param tpId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TpStudentProjectRecord> getTpStudentProjectRecordByTpIdAndTime(Long tpId, String startTime, String endTime);

    /**
     * 复旦mini mba项目获取项目完成情况
     *
     * @param projectIds
     * @return
     */
    List<TpStudentProjectRecordVoVO> getProjectsStatus(List<Long> projectIds);

    /**
     * 复旦mini mba项目获取正在学习人数
     *
     * @param projectIds
     * @return
     */
    Integer getProjectsStudyingNum(List<Long> projectIds);

    /**
     * 复旦mini mba项目获取学习记录
     *
     * @param projectIds
     * @return
     */
    List<TpStudentProjectRecordVoVO> getProjectsStudyingRecords(List<Long> projectIds);

    List<TpStudentProjectRecordVo> getTpStudentProjectRecordBySiteIdsAndTime(List<Long> siteIds, String startDate,
        String endDate);

    List<Long> accountFinishedTpId(Long accountId, Long siteId);

    List<Long> getFinishedAccountIds(Long tpId, Long companyId, Long siteId);

    Integer getFinishedAccountCount(Long tpId, Long companyId, Long siteId);

    List<BaseViewRecordVO> getFinishedRecordsListGroupByAccountId(BaseParamVO baseParamVO);
}
