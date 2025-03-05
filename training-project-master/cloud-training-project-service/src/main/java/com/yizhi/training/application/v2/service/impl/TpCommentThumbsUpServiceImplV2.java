package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpCommentThumbsUp;
import com.yizhi.training.application.v2.mapper.TpCommentThumbsUpMapperV2;
import com.yizhi.training.application.v2.service.TpCommentThumbsUpService;
import com.yizhi.training.application.v2.vo.TpCommentVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TpCommentThumbsUpServiceImplV2 extends ServiceImpl<TpCommentThumbsUpMapperV2, TpCommentThumbsUp>
    implements TpCommentThumbsUpService {

    @Override
    public Map<Long, Integer> getThumbsUpCountMap(List<Long> commentIds) {
        if (CollectionUtils.isEmpty(commentIds)) {
            return Collections.emptyMap();
        }
        List<TpCommentVO> thumbsUpCounts = this.baseMapper.getThumbsUpCounts(commentIds);
        if (CollectionUtils.isEmpty(thumbsUpCounts)) {
            return Collections.emptyMap();
        }
        return thumbsUpCounts.stream()
            .collect(Collectors.toMap(TpCommentVO::getId, TpCommentVO::getThumbsUpCount, (k1, k2) -> k2));
    }
}
