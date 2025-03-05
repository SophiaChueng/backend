package com.yizhi.training.application.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.exception.CustomException;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.core.application.vo.DroolsVo;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.param.TrainingProjectParam;
import com.yizhi.training.application.vo.api.*;
import com.yizhi.training.application.vo.dashboard.TrainDashboardResourceVO;
import com.yizhi.training.application.vo.domain.TpAuthorizationRangeVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.training.application.vo.manage.TrainingProjectStepThreeVo;
import com.yizhi.training.application.vo.manage.VisibleRangeExport;
import com.yizhi.training.application.vo.manage.VisibleRangeVo;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/27 14:44
 */
@FeignClient(name = "trainingProject", contextId = "TrainingProjectClient")
public interface TrainingProjectClient {

    /**
     * 新增培训项目--基本信息
     *
     * @param trainingProject
     * @return
     */
    @PostMapping("/trainingProject/save")
    TrainingProjectVo save(@RequestBody TrainingProjectVo trainingProject);

    /**
     * 新增培训项目--第三步：可见范围、提醒
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/step/three/save")
    Integer saveStepThree(@RequestBody BaseModel<TrainingProjectStepThreeVo> model);

    /**
     * 修改培训项目--基本信息
     *
     * @param trainingProject
     * @return
     */
    @PostMapping("/trainingProject/update")
    TrainingProjectVo update(@RequestBody TrainingProjectVo trainingProject);

    /**
     * 根据id批量删除
     *
     * @param ids
     * @return
     */
    @PostMapping("/trainingProject/batch/delete")
    Integer batchDelete(@RequestBody BaseModel<List<Long>> ids);

    /**
     * 设置可见范围
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/visibleRange/set")
    Integer setVisibleRange(@RequestBody BaseModel<VisibleRangeVo> model);

    /**
     * 添加可见范围
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/visibleRange/insert")
    Boolean insertVisibleRange(@RequestBody List<TpAuthorizationRangeVo> tpAuthorizationRanges);

    /**
     * 查看可见范围
     *
     * @param trainingProjectId
     * @return
     */
    @GetMapping("/trainingProject/visibleRange/view")
    List<TpAuthorizationRangeVo> VisibleRange(@RequestParam("trainingProjectId") Long trainingProjectId);

    @PostMapping("/trainingProject/visibleRange/view/by/site")
    List<TpAuthorizationRangeVo> visibleRangeBySiteId(@RequestBody List<Long> siteId);

    @GetMapping("/trainingProject/visibleRange/getList")
    List<TpAuthorizationRangeVo> getVisibleRange(@RequestParam("trainingProjectId") Long trainingProjectId);

    /**
     * 新增培训项目--第三步：可见范围、提醒，查看
     *
     * @param id
     * @return
     */
    @GetMapping("/trainingProject/step/three/view")
    TrainingProjectStepThreeVo sepThreeView(@RequestParam("id") Long id);

    // *************************************************************

    /**
     * 学员端分页列表
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/api/page/list")
    Page<TrainingProjectListVo> apiPageList(@RequestBody BaseModel<TrainingProjectParamVo> model);

    @PostMapping("/trainingProject/api/page/noCondition/list")
    Page<TrainingProjectVo> apiPageListNoCondition(@RequestBody BaseModel<Page> model);

    /**
     * 分页列表 -- 我的培训项目
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/api/page/my/list")
    Page<TrainingProjectListVo> apiMyPageList(@RequestBody BaseModel<TrainingProjectMyParamVo> model);

    /**
     * 将以上两个接口合并 查询培训详情
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/api/detail/get")
    TrainingProjectDetailVo getTpDetail(@RequestBody BaseModel<Long> model);

    /**
     * 我的培训，添加上培训学习统计
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/api/detail/progress/get")
    TrainingProjectDetailVo getTpDetailWithProgress(@RequestBody BaseModel<Long> model);

    /**
     * 火热报名列表
     *
     * @param model
     * @return
     */
    @GetMapping("/trainingProject/api/hot/page/list")
    Page<HotEnrollListVo> apiHotPageList(@RequestBody BaseModel<HotEnrollParamVo> model);

    /**
     * 火热报名列表 v3 版本 修改点： 返回Vo内容新增付费类别
     */
    @ApiOperation(value = "火热报名列表 v3 版本 修改点： 返回Vo内容新增付费类别")
    @GetMapping("/trainingProject/api/v2/hot/page/list")
    public Page<HotEnrollListVo> apiHotPageListV2(@RequestBody BaseModel<HotEnrollParamVo> model);

    @GetMapping("/trainingProject/one/get")
    TrainingProjectVo getOne(@RequestParam("id") Long id);

    @GetMapping(value = "/trainingProject/one/get", produces = "application/json;charset=UTF-8")
    TrainingProjectVo getOneV2(@RequestParam("id") Long id);

    /**
     * 查询一个培训项目详情 包括项目和付费详情
     *
     * @param projectId 项目id
     */
    @GetMapping("/trainingProject/description/get")
    BizResponse<TrainingProjectVo> getProjectDescription(@RequestParam("projectId") Long projectId);

    /**
     * 根据查询参数查询
     *
     * @param param
     * @return
     */
    @GetMapping("/trainingProject/param/list")
    List<TrainingProjectVo> listByParam(@RequestBody TrainingProjectParam param);

    /**
     * 获取我的培训项目数目
     *
     * @return
     */
    @GetMapping("/trainingProject/my/countNum/get")
    Integer getMyTrainingProjectCountNum(@RequestBody BaseModel<TrainingProjectParamVo> model);

    /**
     * 可见范围导出
     *
     * @param assignmentId
     * @return
     */
    @GetMapping("/trainingProject/export/visiblRange")
    public VisibleRangeExport exportVisibleRange(
        @RequestParam(name = "trainingProjectId", required = true) Long trainingProjectId);

    /**
     * 根据培训项目IDS批量查询培训项目信息 王飞达
     *
     * @param ids
     * @return
     */
    @GetMapping("/trainingProject/training/list")
    public List<TrainingProjectVoPortalVo> getTrainingListByIds(@RequestParam(name = "ids") List<Long> ids);

    /**
     * PC端火热报名列表
     *
     * @param model
     * @return
     */
    @GetMapping("/trainingProject/pc/hot/page/list")
    Page<HotEnrollListVo> pcHotPageList(@RequestBody BaseModel<HotEnrollParamVo> model);

    /**
     * PC端培训项目-首页
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/pc/page/list")
    Page<TrainingProjectListVo> pcPageList(@RequestBody BaseModel<TrainingProjectParamVo> model);

    /**
     * PC端查询培训详情
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/pc/detail/get")
    TrainingProjectDetailVo getPcTpDetail(@RequestBody BaseModel<Long> model);

    /**
     * 分页列表 -- Pc端我的培训项目
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/trainingProject/pc/page/my/list")
    public Page<TrainingProjectListVo> pcMyPageList(@RequestBody BaseModel<TrainingProjectMyParamVo> model);

    @GetMapping("/trainingProject/pc/train/portal/train/relation")
    public List<TrainingProjectVo> pcTrainingProjectList(@RequestParam(name = "relationIds") List<Long> relationIds,
        @RequestParam(name = "num") Integer num, @RequestParam(name = "listIds", required = false) List<Long> listIds);

    /**
     * 获取培训条数
     *
     * @param model
     * @return
     * @throws Exception
     */
    @PostMapping("/trainingProject/api/page/my/list/count")
    public MyPageVO apiMyPageListCount(@RequestBody BaseModel<TrainingProjectMyParamVo> model);

    /*******************************************************  PC 端
     * 开始***************************************************/

    /**
     * 培训，添加上培训学习记录
     *
     * @param model
     * @return
     */
    @PostMapping("/trainingProject/pc/detail/progress/get")
    public TrainingProjectDetailVo getPcTpDetailWithProgress(@RequestBody BaseModel<Long> model);

    /*******************************************************  PC 端
     * 结束***************************************************/

    @GetMapping("/trainingProject/api/student/trainingList")
    public List<TrainingProjectVo> getTrainingList(@RequestParam(name = "ids") List<Long> ids);

    /**
     * 培训记录缓存
     *
     * @return
     */
    @GetMapping("/trainingProject/cache/init")
    boolean cacheInit(@RequestParam(name = "subDate", required = false) Date subDate);

    /**
     * 培训记录缓存
     *
     * @return
     */
    @GetMapping("/trainingProject/record/cache/init")
    boolean recordInit();

    @PostMapping("/trainingProject/list/ids")
    List<TrainingProjectVo> getByIds(@RequestBody Collection<Long> ids);

    @GetMapping("/trainingProject/list/siteId")
    List<TrainingProjectVo> getBySiteId(@RequestParam("siteId") Long siteId,
        @RequestParam(name = "name", required = false) String name);

    @PostMapping("/trainingProject/getPageToCalendar")
    public Page<TrainingProjectVo> getPageToCalendar(@ApiParam("paramVo") @RequestBody CalendarTaskParamVo paramVo);

    @GetMapping("/trainingProject/getPageByDrools")
    Page<DroolsVo> getPageByDrools(@RequestParam("field") String field,
        @RequestParam(value = "value", required = false) String value, @RequestParam("pageNo") Integer pageNo,
        @RequestParam("pageSize") Integer pageSize);

    @GetMapping("/trainingProject/getJoinNumber")
    public void getJoinNumber();

    @PostMapping("/trainingProject/shelfUp/get")
    boolean getShelfUp(@RequestParam("id") Long trainingProjectId);

    @GetMapping("/trainingProject/gainPoint/list")
    Page<GainPointProjectVo> pageGainPointCourses(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        @RequestParam(value = "title", required = false) String title);

    @GetMapping("/trainingProject/exchangeCode/get")
    CustomException<PaidTrainingProjectVO> getProjectForExchangCode(@RequestParam("exchangeCode") String exchangeCode);

    @GetMapping("/trainingProject/by/code/list")
    List<Long> getProjectForCodeExchange();

    @PostMapping("/trainingProject/realtime/joinNumber/get")
    List<TrainJoinNumVO> getTrainsJoinNumber(@RequestBody IdsQueryVO queryVO);

    @ApiOperation("查询最后更新的列表")
    @GetMapping(value = "/trainingProject/dashboard/latest/update/list")
    Page<TrainDashboardResourceVO> dashboardLatestUpdateList(
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize);

    @PostMapping("/trainingProject/stu/finished/records/list")
    List<BaseViewRecordVO> getTrainFinishedRecordsList(@RequestBody BaseParamVO baseParamVO);

    @GetMapping(value = "/trainingProject/weight/set")
    boolean setWeight(@RequestParam("bizId") Long bizId, @RequestParam("weight") Integer weight);
}
