package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpPlanActivityConditionPost;
import com.yizhi.training.application.mapper.TpPlanActivityConditionPostMapper;
import com.yizhi.training.application.service.ITpPlanActivityConditionPostService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学习活动（考试、证书）完成条件 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-04-28
 */
@Service
public class TpPlanActivityConditionPostServiceImpl
    extends ServiceImpl<TpPlanActivityConditionPostMapper, TpPlanActivityConditionPost>
    implements ITpPlanActivityConditionPostService {

}
