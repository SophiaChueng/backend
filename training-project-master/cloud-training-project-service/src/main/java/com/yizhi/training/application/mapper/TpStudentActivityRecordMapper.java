package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.training.application.domain.TpStudentActivityRecord;
import com.yizhi.training.application.vo.domain.TpStudentActivityRecordVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 学员完成活动记录（这里无论有没有被设置成别的活动的开启条件，都记录） Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
public interface TpStudentActivityRecordMapper extends BaseMapper<TpStudentActivityRecord> {

    /**
     * 查询学员已经完成的
     *
     * @param accountId
     * @param relationIds
     * @param finished    1:完成，0:未完成
     * @return 返回已经完成的活动relationId（该id指课程、考试等的id）
     */
    List<Long> selectFinished(@Param("accountId") Long accountId, @Param("relationIds") List<Long> relationIds,
        @Param("finished") Integer finished,@Param("companyId") Long companyId, @Param("siteId") Long siteId);

    /**
     * 活动id
     *
     * @param relationIds
     * @return
     */
    List<Long> getAccountIdByRelationIds(@Param("relationIds") List<Long> relationIds);

    /**
     * 查询活动完成数
     *
     * @param relationIds
     * @param accountId
     * @param siteId
     * @return
     */
    Integer getFinishedCountByRelationIds(@Param("relationIds") List<Long> relationIds,
        @Param("accountId") Long accountId, @Param("siteId") Long siteId);

    /**
     * 按部门统计-实际参加人数
     *
     * @param startDate
     * @param endDate
     * @param accountIdList
     * @return
     */
    Integer selectCountFactJoinNumTrainingProjectOrgGroup(@Param("startDate") Date startDate,
        @Param("endDate") Date endDate, @Param("accountIdList") List<Long> accountIdList);

    List<Long> getRecordeAllWorkId();

    List<TpStudentActivityRecord> getAllRecordeByTimeLimit(@Param(value = "startDate") String startDate,
        @Param(value = "endDate") String endDate, @Param("relationId") Long relationId,@Param("companyId") Long companyId,@Param("siteId") Long siteId);

    @Select(
        "<script>" +
                "SELECT relation_id AS relationId,MAX(finished) finished" +
                " FROM tp_student_activity_record " +
                " WHERE account_id=#{accountId} and company_id = #{companyId} and site_id = #{siteId} AND relation_id IN " +
                " <foreach collection='relationIds' item='id'  open='(' close=')' separator=','> " +
                "    #{id}" + " </foreach> " +
                "  GROUP BY account_id, relation_id ORDER BY account_id, relation_id" +
                "</script>")
    List<TpStudentActivityRecord> getPlanActivityRecord(@Param(value = "accountId") Long accountId,
        @Param("relationIds") List<Long> relationIds,@Param("companyId") Long companyId,@Param("siteId") Long siteId);

    /**
     * 获取活动的完成明细
     *
     * @param siteId      站点id
     * @param relationIds 关联的活动ids
     * @param type        活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7外部链接 8报名 9签到
     * @param page        分页
     * @return
     */
    List<TpStudentActivityRecord> selectPageListByRelationIds(@Param("siteId") Long siteId,
        @Param("relationIds") List<Long> relationIds, @Param("type") Integer type, @Param("startTime") Date startTime,
        @Param("endTime") Date endTime, @Param("accountIds") List<Long> accountIds, Page<TpStudentActivityRecord> page);

    List<TpStudentActivityRecordVo> getAllRecordeBySiteIdsTimeLimit(@Param("startDate") String startDate,
        @Param("endDate") String endDate, @Param("siteIds") List<Long> siteIds);

    List<TpStudentActivityRecord> getActivityFinishedDate(@Param("accountId") Long accountId,
        @Param("relationIds") List<Long> relationIds, @Param("limitEndTime") Date limitEndTime,@Param("companyId") Long companyId,@Param("siteId") Long siteId);

    List<BaseViewRecordVO> selectStuActivityFinishedList(@Param("companyId") Long companyId,
        @Param("siteId") Long siteId, @Param("relationId") Long relationId,
        @Param("relationType") Integer relationType);
}
