package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.TpComment;
import com.yizhi.training.application.vo.manage.PageCommentVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 培训项目 - 评论 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TpCommentMapper extends BaseMapper<TpComment> {

    /**
     * 培训项目列表分页查询 RowBounds
     *
     * @param trainingProjectId
     * @param rowBounds
     * @return
     */
    Page<PageCommentVo> searchPage(@Param("trainingProjectId") Long trainingProjectId,
        @Param("accountId") Long accountId, Page<PageCommentVo> rowBounds, @Param("type") Integer type);

    Integer searchPageCount(@Param("trainingProjectId") Long trainingProjectId, @Param("accountId") Long accountId,
        @Param("type") Integer type);

    List<PageCommentVo> getList(@Param("trainingProjectId") Long trainingProjectId, @Param("accountId") Long accountId);
}
