package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpCommentThumbsUp;
import com.yizhi.training.application.v2.vo.TpCommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpCommentThumbsUpMapperV2 extends BaseMapper<TpCommentThumbsUp> {

    List<TpCommentVO> getThumbsUpCounts(@Param("tpCommentIds") List<Long> tpCommentIds);
}
