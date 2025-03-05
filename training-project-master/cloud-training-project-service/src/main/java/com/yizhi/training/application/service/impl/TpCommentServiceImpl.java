package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpComment;
import com.yizhi.training.application.mapper.TpCommentMapper;
import com.yizhi.training.application.service.ITpCommentService;
import com.yizhi.training.application.vo.manage.PageCommentVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 培训项目 - 评论 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Service
public class TpCommentServiceImpl extends ServiceImpl<TpCommentMapper, TpComment> implements ITpCommentService {

    @Override
    public Page<PageCommentVo> getCommentPage(Long trainingProjectId, Long accountId, int pageNo, int pageSize,
        int type) {
        Page<PageCommentVo> page = new Page<>(pageNo, pageSize);
        page = baseMapper.searchPage(trainingProjectId, accountId, page, type);
        return page;
    }

    @Override
    public List<PageCommentVo> getList(Long trainingProjectId, Long accountId) {
        return this.baseMapper.getList(trainingProjectId, accountId);
    }
}
