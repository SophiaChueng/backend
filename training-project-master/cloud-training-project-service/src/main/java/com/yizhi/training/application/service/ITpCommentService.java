package com.yizhi.training.application.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TpComment;
import com.yizhi.training.application.vo.manage.PageCommentVo;

import java.util.List;

/**
 * <p>
 * 培训项目 - 评论 服务类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface ITpCommentService extends IService<TpComment> {

    @Override
    default TpComment getOne(Wrapper<TpComment> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Page<PageCommentVo> getCommentPage(Long trainingProjectId, Long accountId, int pageNo, int pageSize, int type);

    List<PageCommentVo> getList(Long trainingProjectId, Long accountId);
}
