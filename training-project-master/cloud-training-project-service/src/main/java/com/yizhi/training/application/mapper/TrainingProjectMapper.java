package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.vo.SwhyTrainingProject;
import com.yizhi.training.application.vo.SwhyTrainingProjectMem;
import com.yizhi.training.application.vo.api.GainPointProjectVo;
import com.yizhi.training.application.vo.api.HotEnrollListVo;
import com.yizhi.training.application.vo.api.PaidTrainingProjectVO;
import com.yizhi.training.application.vo.api.TrainingProjectListVo;
import com.yizhi.training.application.vo.dashboard.TrainDashboardResourceVO;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.training.application.vo.fo.TrainingProjectFoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表） Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TrainingProjectMapper extends BaseMapper<TrainingProject> {

    /* 删除不存在的项目 ：
    DELETE FROM tp_student_project_record WHERE training_project_id NOT IN (SELECT id FROM training_project)
    SELECT * FROM tp_student_project_record WHERE training_project_id NOT IN (SELECT id FROM training_project)*/
    @Select(
        "<script>" + " SELECT training_project_id AS trainingProjectId, " + " account_id AS accountId, finish_date " + "AS" + " finishDate" + " FROM tp_student_project_record" + " WHERE site_id = #{siteId} AND finished = 1 " +
            //"<if test='queryDate != null'>" +
            //"<if test=\"queryDate != null and queryDate != '' \"> " +
            //" AND finish_date > STR_TO_DATE(#{queryDate}, '%Y-%m-%d %H:%i:%s') " +
            //"</if>" +
            " AND finish_date BETWEEN STR_TO_DATE(#{startTime}, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(#{endTime}, " +
            "'%Y-%m-%d %H:%i:%s') " + " ORDER BY finish_date" +

            "</script>")
    List<SwhyTrainingProject> queryRecord(@Param("siteId") Long siteId, @Param("startTime") String startTime,
        @Param("endTime") String endTime, Page<SwhyTrainingProject> trPage);

    @Select("<script>" + " SELECT tpa.relation_id relationId, " +
        //" CAST(GROUP_CONCAT(tp.`name` SEPARATOR '##') AS CHAR) projectNames " +
        //" <![CDATA[ CONVERT(GROUP_CONCAT(tp.`name` SEPARATOR '##') USING utf8mb4) ]]> projectNames " +
        //" GROUP_CONCAT(tp.`name`) AS projectNames" +
        //" CAST(GROUP_CONCAT(tp.`name` SEPARATOR '##') AS CHAR)  AS projectNamesBlob " +
        //" CONCAT(GROUP_CONCAT(tp.`name` SEPARATOR '##'), '  ') AS projectNames" +
        " GROUP_CONCAT(tp.`name` SEPARATOR '##') AS projectNamesBlob " +

        " FROM tp_plan_activity tpa LEFT JOIN training_project tp ON tpa.training_project_id = tp.id" + " WHERE tpa" + ".company_id = #{companyId} AND tpa.site_id = #{siteId} AND tpa.deleted = 0 " + " AND tpa.relation_id IN " + "<foreach collection=\"relationIds\" open=\"(\" close=\")\" separator=\",\" item=\"rid\" index=\"index\">" + "#{rid} " + "</foreach>" + " AND tpa.type = #{type} " + " GROUP BY tpa.relation_id ORDER BY NULL" + "</script>")
    List<SwhyTrainingProjectMem> relevance(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("type") Integer type, @Param("relationIds") List<Long> relationIds);

    List<TrainingProject> searchPage(@Param("name") String name, @Param("tpClassificationId") Long tpClassificationId,
        @Param("status") Integer status, @Param("companyId") Long companyId, @Param("siteId") Long siteId,
        Page<TrainingProject> page);

    List<TrainingProjectFoVo> searchFoPage(@Param("siteId") Long siteId, Page<TrainingProjectFoVo> page);

    Integer searchFoPageCount(@Param("siteId") Long siteId);

    /**
     * 版本2 管理端查询项目列表 新增付费条件查询
     *
     * @param name               项目名称、自定义关键词
     * @param tpClassificationId 项目分类id
     * @param status             项目状态 上架、下架、草稿
     * @param enableEnroll       是否开启报名 0否；1是
     * @param enablePay          是否付费 0否；1是
     * @param companyId          公司id
     * @param siteId             站点id
     */
    List<TrainingProjectVo> searchPageV2(@Param("name") String name,
        @Param("tpClassificationId") Long tpClassificationId, @Param("status") Integer status,
        @Param("enableEnroll") Integer enableEnroll, @Param("enablePay") Integer enablePay,
        @Param("companyId") Long companyId, @Param("siteId") Long siteId, Page<TrainingProjectVo> page);

    Integer searchPageCount(@Param("name") String name, @Param("tpClassificationId") Long tpClassificationId,
        @Param("status") Integer status, @Param("companyId") Long companyId, @Param("siteId") Long siteId);

    /**
     * 培训首页分页列表
     *
     * @param visiableTpIds   指定范围可见 tp_authorization_range
     * @param passEnrollTpIds 需要报名且通过 tp_student_enroll_passed
     * @param now             当前时间
     * @param siteId          站点id
     * @param keyword         关键字（模糊查询）
     * @param enablePay       是否付费；0否 ；1是
     * @return
     */
    List<TrainingProjectVo> apiPageList(@Param("visiableTpIds") List<Long> visiableTpIds,
        @Param("passEnrollTpIds") List<Long> passEnrollTpIds, @Param("now") Date now, @Param("siteId") Long siteId,
        @Param("keyword") String keyword, @Param("enablePay") Integer enablePay, Page<TrainingProjectListVo> page);

    Integer apiPageListCount(@Param("visiableTpIds") List<Long> visiableTpIds,
        @Param("passEnrollTpIds") List<Long> passEnrollTpIds, @Param("now") Date now, @Param("siteId") Long siteId,
        @Param("keyword") String keyword);

    /**
     * 火热报名列表
     *
     * @param site
     * @param passIds       已经报名的培训项目id
     * @param visiableTpIds 该学员可见范围的培训项目id
     * @return
     */
    //    List<TrainingProjectVo> apiHotPageList(@Param("siteId") Long site, @Param("passIds") List<Long> passIds,
    //    @Param("visiableTpIds") List<Long> visiableTpIds, @Param("now") Date now, RowBounds rowBounds);
    List<HotEnrollListVo> apiHotPageList(@Param("siteId") Long site, @Param("passIds") List<Long> passIds,
        @Param("visiableTpIds") List<Long> visiableTpIds, @Param("now") Date now, @Param("enablePay") Integer enablePay,
        Page<HotEnrollListVo> page);

    Integer apiHotPageListNum(@Param("siteId") Long site, @Param("passIds") List<Long> passIds,
        @Param("visiableTpIds") List<Long> visiableTpIds, @Param("now") Date now);

    /**
     * 我的培训项目列表  未开始（已上架，开始时间大于当前时间） 1.平台可见 2.指定学员
     *
     * @param now
     * @param ids    可见范围的培训项目id集合
     * @param siteId
     * @return
     */
    List<TrainingProject> selectMyCommingPage(@Param("now") Date now, @Param("siteId") Long siteId,
        @Param("keyword") String keyword, @Param("ids") List<Long> ids, Page<TrainingProject> page);

    Integer selectMyCommingPageNum(@Param("now") Date now, @Param("siteId") Long siteId,
        @Param("keyword") String keyword, @Param("ids") List<Long> ids);

    /**
     * 我的培训项目列表  进行中 1. 已上架，开始时间大于当前时间，结束时间小于当前时间 2. 去除已结束
     *
     * @param now
     * @param siteId
     * @return
     */
    List<TrainingProject> selectMyJoinedPage(@Param("now") Date now, @Param("siteId") Long siteId,
        @Param("keyword") String keyword, @Param("visiableTpIds") List<Long> visiableTpIds,
        @Param("finishedTpIds") List<Long> finishedTpIds, Page<TrainingProject> page);

    Integer selectMyJoinedPageNum(@Param("now") Date now, @Param("siteId") Long siteId,
        @Param("keyword") String keyword, @Param("visiableTpIds") List<Long> visiableTpIds,
        @Param("finishedTpIds") List<Long> finishedTpIds);

    /**
     * 我的培训项目列表  已结束
     *
     * @param accountId
     * @return
     */
    List<TrainingProject> selectMyFinishedPage(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("now") Date now, @Param("visiableTpIds") List<Long> visiableTpIds, Page<TrainingProject> rowBounds);

    Integer selectMyFinishedPageNum(@Param("accountId") Long accountId, @Param("siteId") Long siteId,
        @Param("now") Date now, @Param("visiableTpIds") List<Long> visiableTpIds);

    /**
     * 我的培训项目列表 已过期
     *
     * @param siteId
     * @param now
     * @param visiableTpIds
     * @param finishedTpIds
     * @param rowBounds
     * @return
     */
    List<TrainingProject> selectMyExpiredPage(@Param("siteId") Long siteId, @Param("now") Date now,
        @Param("visiableTpIds") List<Long> visiableTpIds, @Param("finishedTpIds") List<Long> finishedTpIds,
        Page<TrainingProject> rowBounds);

    /**
     * 查询需要报名的培训项目已经报名的人数
     *
     * @param trainingProjectId
     * @return
     */
    Integer selectEnrollPassNum(@Param("projectId") Long trainingProjectId);

    /**
     * @param ids    指定范围可见 （由 tp_authorization_range查出）
     * @param siteId 站点id
     * @return
     */
    List<TrainingProject> apiPageListNoCondition(@Param("ids") List<Long> ids, @Param("siteId") Long siteId,
        @Param("now") Date now, Page rowBounds);

    /**
     * 根据主键批量删除
     *
     * @param ids
     * @param accountId
     * @param accountName
     * @param now
     * @return
     */
    Integer batchDelete(@Param("ids") List<Long> ids, @Param("accountId") Long accountId,
        @Param("accountName") String accountName, @Param("now") Date now);

    /**
     * 我的培训数目
     *
     * @param ids
     * @param finishedTpIds 已完成
     * @param siteId
     * @return
     */
    Integer getMyTrainingProjectCountNum(@Param("now") Date now, @Param("ids") List<Long> ids,
        @Param("finishedTpIds") List<Long> finishedTpIds, @Param("siteId") Long siteId);

    /**
     * 根据时间区间得到指定范围为全平台的项目
     *
     * @param startDate
     * @param endDate
     * @param siteId
     * @return
     */
    List<TrainingProject> getvisibileBySiteIdAndBetweenTime(@Param("startDate") Date startDate,
        @Param("endDate") Date endDate, @Param("siteId") Long siteId);

    /**
     * 按部门统计---完成人数
     * <p>
     * //     * @param startDate //     * @param endDate //     * @param accountIdList
     *
     * @return
     */
    //    Integer getTrainingProjectNumTrainingProjectOrgGroup(@Param("startDate") Date startDate, @Param("endDate")
    //    Date endDate, @Param("accountIdList") List<Long> accountIdList);

    List<TrainingProject> getList(@Param("ids") List<Long> ids, RowBounds rowBounds);

    List<TrainingProject> queryTrainingListByRelationIds(@Param("relationIds") List<Long> relationIds,
        @Param("num") Integer num, @Param("siteId") Long siteId, @Param("listIds") List<Long> listIds);

    /**
     * 未开始的培训总条数
     *
     * @param now
     * @param siteId
     * @param keyword
     * @param ids
     * @return
     */
    Integer selectMyCommingCount(@Param("now") Date now, @Param("siteId") Long siteId, @Param("keyword") String keyword,
        @Param("ids") List<Long> ids);

    /**
     * 进行中的总数
     *
     * @param now
     * @param siteId
     * @param keyword
     * @param visiableTpIds
     * @param finishedTpIds
     * @return
     */
    Integer selectMyJoinedCount(@Param("now") Date now, @Param("siteId") Long siteId, @Param("keyword") String keyword,
        @Param("visiableTpIds") List<Long> visiableTpIds, @Param("finishedTpIds") List<Long> finishedTpIds);

    /**
     * 已经完成总的条数
     *
     * @param accountId
     * @param siteId
     * @return
     */
    Integer selectMyFinishedCount(@Param("accountId") Long accountId, @Param("siteId") Long siteId);

    /**
     * 已过期总条数
     *
     * @param siteId
     * @param now
     * @param visiableTpIds
     * @param finishedTpIds
     * @return
     */
    Integer selectMyExpiredCount(@Param("siteId") Long siteId, @Param("now") Date now,
        @Param("visiableTpIds") List<Long> visiableTpIds, @Param("finishedTpIds") List<Long> finishedTpIds);

    List<TrainingProject> getTrainingList(@Param("ids") List<Long> ids, @Param("siteId") Long siteId,
        @Param("now") Date now);

    List<Long> getAllSiteIds();

    /**
     * 案例库获取培训项目信息
     *
     * @param idList
     * @return
     */
    List<TrainingProject> getCaseLibraryProject(@Param("idList") List<Long> idList);

    /**
     * 我的案例获取可见范围id
     *
     * @param visiableTpIds
     * @param passEnrollTpIds
     * @param date
     * @param siteId
     * @return
     */
    List<TrainingProject> getCaseLibraryRangeProjects(@Param("visiableTpIds") List<Long> visiableTpIds,
        @Param("passEnrollTpIds") List<Long> passEnrollTpIds, @Param("date") Date date, @Param("siteId") Long siteId);

    List<TrainingProject> allTpBySiteId(@Param("siteId") long siteId, @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    /**
     * 获取培训项目应参加人数
     *
     * @param ids
     * @return
     */
    Integer getTpShouldPeople(@Param("ids") Set<Long> ids);

    /**
     * 根据条件查询数据
     *
     * @param finishTrIds
     * @param
     * @param siteId
     * @param page
     * @return
     */

    List<TrainingProject> getPageToCalendar(@Param("finishTrIds") List<Long> finishTrIds,
        @Param("trIds") List<Long> trIds, @Param("currentDate") Date currentDate, @Param("siteId") Long siteId,
        Page page);

    Integer getPageToCalendarNum(@Param("finishTrIds") List<Long> finishTrIds, @Param("trIds") List<Long> trIds,
        @Param("currentDate") Date currentDate, @Param("siteId") Long siteId);

    List<Long> getIdsByDate(@Param("currentDate") Date currentDate, @Param("siteId") Long siteId);

    /**
     * 精品内容展示付费资源
     *
     * @param visiableTpIds 指定范围可见 tp_authorization_range
     * @param now           当前时间（在报名购买时间范围内的）
     * @param siteId        站点id
     * @param keyword       关键字（模糊查询）
     * @return
     */
    List<PaidTrainingProjectVO> apiPaidPageList(@Param("visiableTpIds") List<Long> visiableTpIds,
        @Param("now") Date now, @Param("siteId") Long siteId, @Param("keyword") String keyword,
        @Param("orderField") String orderField, @Param("order") String order);

    TrainingProjectVo getProjectDesc(@Param("projectId") Long projectId);

    /**
     * 获取 ：可见范围内，项目时间内，未参加过的项目（配了积分）上架的项目列表
     *
     * @param companyId 公司id
     * @param siteId    站点id
     * @param accountId 用户id
     * @param title     项目名称 支持模糊查询
     * @param page      分页
     * @return 列表
     */
    List<GainPointProjectVo> pageGainPointList(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("accountId") Long accountId, @Param("orgId") Long orgId, @Param("relationIds") List<Long> relationIds,
        @Param("title") String title, Page<GainPointProjectVo> page);

    List<TrainDashboardResourceVO> selectPageList(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        Page<TrainDashboardResourceVO> page);

    List<TrainDashboardResourceVO> selectJoinCount(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("ids") List<Long> ids);
}
