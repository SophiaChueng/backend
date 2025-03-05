package com.yizhi.training.application.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.PdsProjectStudyRecorde;
import com.yizhi.training.application.vo.PdsProjectStudyRecordeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * pds 自定义项目 学习记录 Mapper 接口
 * </p>
 *
 * @author fulan123
 * @since 2022-05-18
 */
public interface PdsProjectStudyRecordeMapper extends BaseMapper<PdsProjectStudyRecorde> {

    List<PdsProjectStudyRecordeVo> studyPeriodRankingList(@Param("companyId") Long companyId,
        @Param("siteId") Long siteId, @Param("pid") Long pid, @Param("uid") Long uid);
}
