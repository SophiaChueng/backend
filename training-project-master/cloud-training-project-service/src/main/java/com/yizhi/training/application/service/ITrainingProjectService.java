package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.vo.DroolsVo;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.param.PaidTrainingProjectQO;
import com.yizhi.training.application.vo.SwhyTrainingProject;
import com.yizhi.training.application.vo.SwhyTrainingProjectMem;
import com.yizhi.training.application.vo.api.*;
import com.yizhi.training.application.vo.dashboard.TrainDashboardResourceVO;
import com.yizhi.training.application.vo.domain.TpStudentPlanRecordVo;
import com.yizhi.training.application.vo.domain.TpStudentProjectRecordVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.training.application.vo.fo.TrainingProjectFoVo;
import com.yizhi.training.application.vo.manage.SearchProjectVo;
import com.yizhi.training.application.vo.manage.TrainingProjectStepThreeVo;
import com.yizhi.training.application.vo.manage.VisibleRangeExport;
import com.yizhi.training.application.vo.manage.VisibleRangeVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 培训项目主体表（报名、签到 是在报名签到表中记录项目id，论坛是单独的关系表） 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITrainingProjectService extends IService<TrainingProject> {

    @Override
    default TrainingProject getOne(Wrapper<TrainingProject> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    List<SwhyTrainingProjectMem> relevance(Long companyId, Long siteId, Integer type, List<Long> relationIds);

    Page<SwhyTrainingProject> queryRecord(Long companyId, Long siteId, Integer pageNo, Integer pageSize,
        String queryDate);

    Page<TrainingProject> searchPage(String name, Long tpClassificationId, Integer status, Long companyId, Long siteId,
        List<Long> orgId, int pageNo, int pageSize);

    Page<TrainingProjectFoVo> searchFoPage(SearchProjectVo searchProjectVo);

    /**
     * 版本2 查询项目列表 新增是否报名的筛选条件；
     *
     * @param name
     * @param tpClassificationId
     * @param status
     * @param companyId
     * @param siteId
     * @param orgId
     * @param pageNo
     * @param pageSize
     * @return
     */
    Page<TrainingProjectVo> searchPageV2(String name, Long tpClassificationId, Integer status, Integer enrollStatus,
        Long companyId, Long siteId, List<Long> orgId, int pageNo, int pageSize);

    /**
     * 新增培训项目--基本信息
     *
     * @param trainingProject
     * @return
     */
    TrainingProject saveTrPro(TrainingProject trainingProject);

    /**
     * 修改培训项目--基本信息
     *
     * @param trainingProject
     * @return
     */
    TrainingProject update(TrainingProject trainingProject) throws Exception;

    /**
     * 新增培训项目--第三步：可见范围、提醒
     *
     * @param model
     * @return
     */
    Integer saveStepThree(BaseModel<TrainingProjectStepThreeVo> model) throws Exception;

    List<Long> getProjectForCodeExchange(Boolean isMall);

    /**
     * 培训项目编辑第三部回显
     *
     * @param id
     * @return
     */
    TrainingProjectStepThreeVo stepThreeView(Long id);

    /**
     * 根据id批量删除
     *
     * @param model
     * @return
     */
    Integer batchDelete(BaseModel<List<Long>> model);

    // *********************************************************

    /**
     * 分页查培训项目列表 -- 培训首页
     *
     * @return
     */
    Page<TrainingProjectListVo> apiPageList(BaseModel<TrainingProjectParamVo> model) throws IOException;

    /**
     * 火热报名列表 需要报名，且未报名
     *
     * @param model 包含火热报名参数vo
     * @return
     */
    Page<HotEnrollListVo> apiHotPageList(BaseModel<HotEnrollParamVo> model);

    /**
     * 分页列表 -- 我的培训项目
     *
     * @param model
     * @return
     */
    Page<TrainingProjectListVo> apiMyPageList(BaseModel<TrainingProjectMyParamVo> model) throws Exception;

    /**
     * 根据培训项目id查找 培训详情--培训简介
     *
     * @param model 培训项目id
     * @return
     */
    //    TrainingProjectIntroductionVo getTpIntroduction(BaseModel<Long> model);

    Page<TrainingProject> apiPageListNoCondition(BaseModel<Page> model);

    /**
     * 培训项目
     * 培训项目 计划集合
     * 计划活动集合
     *
     * @param model
     * @return
     */
    //    TrainingProjectContentVo getTpContent(BaseModel<Long> model);

    /**
     * 培训详情
     *
     * @param trainingProjectId
     * @param context
     * @param containStatistics 是否包含学习统计
     * @return
     */
    TrainingProjectDetailVo getTpDetail(Long trainingProjectId, RequestContext context, Date now,
        Boolean containStatistics) throws ParseException;

    /**
     * 获取我的培训项目
     *
     * @return
     */
    Integer getMyTrainingProjectCountNum(BaseModel<TrainingProjectParamVo> model);

    /**
     * 设置可见范围
     *
     * @param vo
     * @param accountId
     * @param now
     * @return
     */
    Integer setVisibleRange(VisibleRangeVo vo, Long accountId, String accountName, Long siteId, Date now)
        throws Exception;

    /**
     * 范围人员导出             管理端-------2018/09/19
     *
     * @param assignmentId
     * @return
     */
    public VisibleRangeExport vsibleRangeExport(Long assignmentId);

    /**
     * 批量查询
     *
     * @param ids
     * @return
     */
    List<TrainingProjectVoPortalVo> getTrainingListByIds(@RequestParam(name = "ids") List<Long> ids);

    Page<TrainingProject> listNotIds(List<Long> ids, String name, Long siteId, Integer pageNo, Integer pageSize);

    List<TrainingProject> getTrainingListByRelationIds(List<Long> relationIds, Integer num, List<Long> listIds);

    MyPageVO getTrainingCount(BaseModel<TrainingProjectMyParamVo> model);

    /**
     * 微信端首页培训列表
     *
     * @param ids 培训list
     * @return
     */
    List<TrainingProject> getTrainingList(List<Long> ids);

    /**
     * 我的案例获取可见范围id
     *
     * @param res
     * @return
     */
    List<TrainingProject> getCaseLibraryRangeProjects(RequestContext res);

    Page<TrainingProjectVo> getPageToCalendar(Date date, Page<TrainingProject> page);

    Page<DroolsVo> getPageByDrools(String field, String value, Page<DroolsVo> page);

    void trPlanUpdateStatus(List<TpPlan> tpPlans, TrainingProject trainingProject, RequestContext context,
        Boolean updateVisibleRange);

    /**
     * 获取付费课程
     *
     * @param qo
     * @return
     */
    List<PaidTrainingProjectVO> getPaidTrainingProject(PaidTrainingProjectQO qo);

    /**
     * 获取项目详情
     *
     * @param projectId 项目id
     */
    TrainingProjectVo getProjectDescription(Long projectId);

    /**
     * 获取可见范围内，项目时间内，未参加过的项目（配了积分）的项目列表
     *
     * @param title 项目名称 支持模糊查询
     */
    Page<GainPointProjectVo> getGainPointCourses(Integer pageNo, Integer pageSize, String title);

    List<TrainJoinNumVO> getTrainJoinNumber(List<Long> trainProjectIds);

    Page<TrainDashboardResourceVO> dashboardLatestUpdateList(Integer pageNo, Integer pageSize);

    TpStudentProjectRecordVo getProjectFinishRecords(Long accountId, Long tpId);

    TpStudentPlanRecordVo getPlanFinishRecords(Long accountId, Long planId);

    boolean setWeight(Long bizId, Integer weight);

    TrainingProject getByIdWithJoinNumber(Long id);

    List<TrainDashboardResourceVO> selectJoinCount(Long companyId, Long siteId, List<Long> ids);
}
