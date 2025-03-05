package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpCommentThumbsUp;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 评论点赞记录 Mapper 接口
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
public interface TpCommentThumbsUpMapper extends BaseMapper<TpCommentThumbsUp> {

    Integer countThumbsUp(@Param("commentId") Long commentId);

}
