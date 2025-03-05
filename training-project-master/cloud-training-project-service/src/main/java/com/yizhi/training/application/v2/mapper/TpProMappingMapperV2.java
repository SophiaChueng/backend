package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yizhi.training.application.domain.TpProMapping;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TpProMappingMapperV2 extends BaseMapper<TpProMapping> {

    Integer getTpCountOfPro(@Param("tpProId") Long tpProId, @Param("searchTpName") String searchTpName);

    List<TrainingProject> getTpListOfPro(@Param("tpProId") Long tpProId, @Param("searchTpName") String searchTpName,
        @Param("offset") Integer offset, @Param("pageSize") Integer pageSize, @Param("status") Integer status);

    Boolean updateSortValue(@Param("tpProId") Long tpProId, @Param("sort") Integer sort);

    Integer getMaxSort(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("tpProId") Long tpProId);

    List<TpProMapping> getMaxSortsBy(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("trainingProjectId") Long trainingProjectId, @Param("tpProIds") List<Long> tpProIds);

    List<OnlineTpVO> getTpHomeListPro(@Param("terminalType") String terminalType, @Param("siteId") Long siteId,
        @Param("companyId") Long companyId, @Param("relationIds") List<Long> relationIds);
}
