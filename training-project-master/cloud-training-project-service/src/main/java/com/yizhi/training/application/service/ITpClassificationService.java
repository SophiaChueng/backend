package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.core.application.classify.vo.ClassifyDetailSimpleVO;
import com.yizhi.training.application.domain.TpClassification;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;

import java.util.List;

/**
 * <p>
 * 培训项目分类表 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpClassificationService extends IService<TpClassification> {

    @Override
    default TpClassification getOne(Wrapper<TpClassification> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    /**
     * 分类删除
     *
     * @param param
     * @return
     */
    Boolean delete(TpClassification param);

    Page getClassifyNameByDrools(String field, String value, Page page);

    /**
     * 根据分类id获取分类名称
     *
     * @param ids 分类id列表
     * @return List<ClassifyDetailSimpleVO>
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    List<ClassifyDetailSimpleVO> listClassifyByIds(List<Long> ids);

    /**
     * 根据分类id列表查询个数（判断id列表中是否含有不存在的分类id）
     *
     * @param ids 分类id列表
     * @return 存在数量
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    Integer cntClassifyByIds(List<Long> ids);

    boolean changeSort(Long id, Integer type, Integer sort);

    Page<TrainingProjectVo> projectsListByClassifyId(Long id, String keyword, Integer pageNo, Integer pageSize);

    boolean projectsRemoveRelation(Long classifyId, Long projectId);
}
