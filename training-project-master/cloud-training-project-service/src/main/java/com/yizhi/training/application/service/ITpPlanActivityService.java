package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.core.application.vo.BaseParamVO;
import com.yizhi.core.application.vo.BaseViewRecordVO;
import com.yizhi.training.application.constant.CertificateGrantStatus;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.vo.domain.CourseRelateProjectVO;
import com.yizhi.training.application.vo.domain.TpPlanActivityVo;
import com.yizhi.training.application.vo.manage.ConditionDeleteVo;
import com.yizhi.training.application.vo.manage.TpPlanActivityConditionUpdateVo;
import com.yizhi.training.application.vo.manage.TpPlanActivitySingleVo;
import com.yizhi.util.application.enums.i18n.Constants;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 学习计划中的活动 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpPlanActivityService extends IService<TpPlanActivity> {

    @Override
    default TpPlanActivity getOne(Wrapper<TpPlanActivity> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 更新排序
     *
     * @param list
     * @return
     */
    Integer updateSort(List<TpPlanActivity> list);

    /**
     * 查询培训计划所有活动（联合查询活动条件）
     *
     * @param tpPlanId 所属培训计划的id
     * @param name     模糊查询
     * @param type     活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7签到 8外部链接
     * @return
     */
    List<TpPlanActivity> allList(Long tpPlanId, String name, Integer type);

    /**
     * 查询培训计划下所有活动
     *
     * @param tpPlanId
     * @param type     指定活动类型
     * @return
     */
    List<TpPlanActivity> allListByTpPlanId(Long tpPlanId, Integer... type);

    /**
     * 查询培训项目所有活动
     *
     * @param tpId
     * @return
     */
    List<TpPlanActivity> allListByTp(Long tpId);

    /**
     * 根据培训活动id删除培训活动（联合删除相关条件）
     *
     * @param model
     * @return
     */
    Integer deleteByIds(BaseModel<List<Long>> model);

    /**
     * 新增、修改培训活动开启完成条件
     *
     * @param model
     * @return 入库的条件数目
     */
    Integer conditionEdit(@RequestBody BaseModel<TpPlanActivityConditionUpdateVo> model) throws Exception;

    /**
     * 修改培训计划时，添加学习活动用
     *
     * @param model
     * @return
     */
    Integer addActivity(@RequestBody BaseModel<List<TpPlanActivitySingleVo>> model);

    /**
     * 删除条件
     *
     * @param model
     * @return
     */
    Integer deleteConditions(BaseModel<ConditionDeleteVo> model);

    /**
     * 检查业务是否在培训项目中存在
     *
     * @param relationIds
     * @return 存在的业务id数组
     */
    List<Long> checkBizIsExistInTp(List<Long> relationIds);

    /**
     * 检查业务是否在培训项目中存在
     *
     * @param relationIds
     * @return 培训项目名称
     */
    Set<String> checkBizIsExistInTpNames(List<Long> relationIds);

    /**
     * 证书发放
     *
     * @param param
     * @return
     */
    CertificateGrantStatus certificateGrant(Map<String, Long> param);

    /************************************************************************  PC端 获取证书
     *  ****************************************************/

    /**
     * PC端获取证书
     *
     * @param param
     * @return
     */
    Constants certificatePcGrant(Map<String, Long> param);

    /**
     * 门户培训项目模块，只查询本培训项目下的课程的id
     *
     * @param id
     * @return
     */
    List<Long> getcourseIdsByTrainingProjectId(Long id);

    List<Long> getExcCertifercourseIdsByTrainingProjectId(Long id);

    List<Long> getAllCourseIdByTrainingProjectId(Long id);

    /**
     * 获取一个培训项目下所有的活动数量
     *
     * @param id
     * @return
     */
    Integer getactivityNumByTrainingProjectId(Long id);

    /**
     * 获取一个培训项目下不包含证书的活动数量
     *
     * @param id 项目id
     */
    Integer getExcCertificateActivityNumByTpId(Long id);

    /**
     * 是否完成培训计划下所有培训活动
     *
     * @param tpPlanId
     * @param accountId
     * @return
     */
    boolean hasFinisheAllActivity(Long tpPlanId, Long accountId);

    /**
     * 获取培训项目下边的所有活动，不带任何条件
     *
     * @param id
     * @return
     */
    List<TpPlanActivity> listTpPlanActivityByTpId(Long id);

    List<TpPlanActivityVo> getTpPlanActivitiesBySiteIds(List<Long> siteIds);

    /**
     * 获取该计划按顺序排列的列表
     *
     * @param tpPlanId
     * @return
     */
    List<TpPlanActivity> getByTpPlanId(Long tpPlanId);

    Boolean checkBizCanDown(Integer bizType, Long relationId);

    List<Long> allListByAssignmentIds(List<TpPlanActivityVo> tpPlanActivityVos);

    /**
     * 课程关联项目列表
     *
     * @param courseId
     * @param page
     * @return
     */
    List<CourseRelateProjectVO> courseRelateProjectList(Long courseId, Page<CourseRelateProjectVO> page);

    /**
     * 获取学员完成的活动记录
     *
     * @return
     */
    List<BaseViewRecordVO> getFinishedActivityList(BaseParamVO baseParamVO);

    Set<String> checkExistRelatedProject(Long id, Integer type);
}
