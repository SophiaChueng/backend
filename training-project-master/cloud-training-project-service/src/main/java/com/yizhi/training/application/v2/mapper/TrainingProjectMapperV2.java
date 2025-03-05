package com.yizhi.training.application.v2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.v2.model.SearchTpConditionBO;
import com.yizhi.training.application.v2.vo.HotTpVO;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import com.yizhi.training.application.v2.vo.RecentStudyTrainingVO;
import com.yizhi.training.application.v2.vo.request.ProjectJudgeAO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface TrainingProjectMapperV2 extends BaseMapper<TrainingProject> {

    Integer getTrainingProjectCount(SearchTpConditionBO searchTpConditionBO);

    List<TrainingProject> getTrainingProjectList(SearchTpConditionBO condition);

    Integer getMaxSort(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("tpClassificationId") Long tpClassificationId);

    /**
     * 免费报名，切没有添加到项目pro中
     *
     * @param siteId
     * @param companyId
     * @return
     */
    List<HotTpVO> getHotEnrollTp(@Param("siteId") Long siteId, @Param("companyId") Long companyId,
        @Param("terminalType") String terminalType, @Param("date") Date date);

    List<OnlineTpVO> getOnLineTp(@Param("terminalType") String terminalType, @Param("siteId") Long siteId,
        @Param("companyId") Long companyId);

    Boolean updateTpBaseInfo(TrainingProject updateProject);

    Integer judgeProjectDesc(ProjectJudgeAO ao);

    List<RecentStudyTrainingVO> getRecentStudyList(@Param("companyId") Long companyId, @Param("siteId") Long siteId,
        @Param("accountId") Long accountId, Page<RecentStudyTrainingVO> page);
}
