package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpStudentPlanRecord;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 学习计划完成记录，由两张条件表和活动记录表计算得出 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface TpStudentPlanRecordMapper extends BaseMapper<TpStudentPlanRecord> {

    /**
     * 获取已经完成的计划
     *
     * @param accountId
     * @param siteId
     * @param trainingProjectId 可不传
     * @return
     */
    List<Long> getIdsByAccountId(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("trainingProjectId") Long trainingProjectId);

    /**
     * 批量插入记录
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpStudentPlanRecord> list);

    /**
     * 查询计划完成数
     *
     * @param planIds
     * @param accountId
     * @return
     */
    Integer getFinishedCountByPlanIds(@Param("list") List<Long> planIds, @Param("accountId") Long accountId);

    /**
     * 根据id时间段查看项目计划完成记录
     *
     * @param tpId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TpStudentPlanRecord> getTpStudentPlanRecordByTpIdAndTime(@Param("id") Long id,
        @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取学员完成的计划完成记录
     *
     * @param accountId 学员id
     * @param projectId 项目id
     * @return
     */
    List<TpStudentPlanRecord> getTpStudentPlanFinishRecords(@Param("accountId") Long accountId,
        @Param("projectId") Long projectId);

    List<TpStudentPlanRecordVo> getTpStudentPlanRecordBySiteIdsAndTime(@Param("siteIds") List<Long> siteIds,
        @Param("startTime") String startTime, @Param("endTime") String endTime);

    Integer getFinishedCountAllByPlanId(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("planIds") List<Long> planIds);

    List<Long> getFinishedIdByPlanId(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("planIds") List<Long> planIds);

    Date getPlanMinFinishedTime(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("prePlanIds") List<Long> prePlanIds, @Param("finishedCount") Integer finishedCount);

    List<Long> getFinishedAccountIds(@Param("planId") Long planId, @Param("companyId") Long companyId,
        @Param("siteId") Long siteId);
}
