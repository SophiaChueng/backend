package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.v2.MyTpFinishedVO;
import com.yizhi.training.application.v2.MyTpUnFinishedVO;
import com.yizhi.training.application.vo.api.TpStudentProjectRecordVoVO;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 培训项目完成情况，由学习计划完成记录计算得出 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-09
 */
public interface TpStudentProjectRecordMapper extends BaseMapper<TpStudentProjectRecord> {

    /**
     * 获取account已经完成的培训项目id集合
     *
     * @param accountId
     * @return
     */
    List<Long> getByAccountId(@Param("accountId") Long accountId);

    /**
     * 批量插入
     *
     * @param list
     * @return
     */
    Integer batchInsert(@Param("list") List<TpStudentProjectRecord> list);

    /**
     * 按部门统计-实际参加人数
     * @param startDate
     * @param endDate
     * @param accountIdList
     * @return
     */
    // Integer selectCountFactJoinNumTrainingProjectOrgGroup(@Param("startDate")Date startDate,@Param("endDate")Date
    // endDate,@Param("accountIdList") List<Long> accountIdList);

    /**
     * 按部门统计----完成人数
     *
     * @param startDate
     * @param endDate
     * @param accountIdList
     * @return
     */
    Integer getTrainingProjectNumTrainingProjectOrgGroup(@Param("startDate") Date startDate,
        @Param("endDate") Date endDate, @Param("accountIdList") List<Long> accountIdList);

    /**
     * 判断项目是否完成了
     *
     * @param accountId
     * @param projectId
     * @param siteId
     * @return
     */
    Integer projectIsFinish(@Param("accountId") Long accountId, @Param("projectId") Long projectId,
        @Param("siteId") Long siteId);

    /*
     * 根据id时间段查看项目完成记录
     */
    List<TpStudentProjectRecord> getTpStudentProjectRecordByTpIdAndTime(@Param("tpId") Long tpId,
        @Param("startTime") String startTime, @Param("endTime") String endTime);

    Integer getFinisheNum(@Param("ids") Set<Long> ids, @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    /**
     * 复旦mini mba项目获取项目完成情况
     *
     * @param accountId
     * @param projectIds
     * @return
     */
    List<TpStudentProjectRecordVoVO> getProjectsStatus(@Param("accountId") Long accountId,
        @Param("projectIds") List<Long> projectIds);

    /**
     * 复旦mini mba项目获取正在学习人数
     *
     * @param projectIds
     * @param companyId
     * @param siteId
     * @return
     */
    Integer getProjectsStudyingNum(@Param("projectIds") List<Long> projectIds, @Param("companyId") Long companyId,
        @Param("siteId") Long siteId);

    /**
     * 复旦mini mba项目获取学习记录
     *
     * @param projectIds
     * @param accountId
     * @return
     */
    List<TpStudentProjectRecordVoVO> getProjectsStudyingRecords(@Param("projectIds") List<Long> projectIds,
        @Param("accountId") Long accountId);

    List<Long> getFinshIdsByIds(@Param("ids") List<Long> ids, @Param("accountId") Long accountId,
        @Param("siteId") Long siteId);

    //根据时间 站点获取完成记录
    List<TpStudentProjectRecord> getFinishRecords(@Param("siteId") Long siteId, @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    /**
     * 查询培训项目通过的记录
     *
     * @param projectIds      需要同步的培训项目的ids
     * @param accountIds      需要同步的用户ids
     * @param passedStartTime 查询通过的开始时间
     * @param passedEndTime   查询通过的结束时间
     * @return 通过记录
     */
    List<TpStudentProjectRecord> getPageList(@Param("projectIds") List<Long> projectIds,
        @Param("accountIds") List<Long> accountIds, @Param("passedStartTime") Date passedStartTime,
        @Param("passedEndTime") Date passedEndTime, @Param("siteId") Long siteId, Page<TpStudentProjectRecord> page);

    List<TpStudentProjectRecordVo> getTpStudentProjectRecordBySiteIdsAndTime(@Param("siteIds") List<Long> siteIds,
        @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<MyTpFinishedVO> getFinishedTpV2(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds, @Param("page") Page<MyTpFinishedVO> page);

    List<Long> selectFinishedTpAndProId(@Param("accountId") Long accountId, @Param("siteId") Long siteId);

    List<MyTpUnFinishedVO> getStartViewAnUnFinished(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds);

    List<MyTpUnFinishedVO> getEnrollAnUnFinished(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds);

    List<MyTpUnFinishedVO> getProAnUnFinished(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds);

    List<Long> getFinishedAccountIds(@Param("tpId") Long tpId, @Param("companyId") Long companyId,
        @Param("siteId") Long siteId);

    Integer getFinishedAccountCount(@Param("tpId") Long tpId, @Param("companyId") Long companyId,
        @Param("siteId") Long siteId);

    List<BaseViewRecordVO> getFinishedRecordsListGroupByAccountId(@Param("trainProjectId") Long trainProjectId,
        @Param("companyId") Long companyId, @Param("siteId") Long siteId);
}
