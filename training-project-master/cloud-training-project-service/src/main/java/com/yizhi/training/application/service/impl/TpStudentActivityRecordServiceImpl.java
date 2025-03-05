package com.yizhi.training.application.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.training.application.domain.TpStudentActivityRecord;
import com.yizhi.training.application.mapper.TpStudentActivityRecordMapper;
import com.yizhi.training.application.service.ITpStudentActivityRecordService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 学员完成活动记录（这里无论有没有被设置成别的活动的开启条件，都记录） 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-29
 */
@Service
public class TpStudentActivityRecordServiceImpl
    extends ServiceImpl<TpStudentActivityRecordMapper, TpStudentActivityRecord>
    implements ITpStudentActivityRecordService {

}
