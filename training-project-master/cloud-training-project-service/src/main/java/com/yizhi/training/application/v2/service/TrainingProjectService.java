package com.yizhi.training.application.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.v2.model.SearchTpConditionBO;
import com.yizhi.training.application.v2.vo.HotTpVO;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import com.yizhi.training.application.v2.vo.RecentStudyTrainingVO;
import com.yizhi.training.application.v2.vo.request.ProjectJudgeAO;

import java.util.List;

public interface TrainingProjectService extends IService<TrainingProject> {

    @Override
    default TrainingProject getOne(Wrapper<TrainingProject> queryWrapper) {
        return getOne(queryWrapper, false);
    }

    Integer getTrainingProjectCount(SearchTpConditionBO searchTpConditionBO);

    List<TrainingProject> getTrainingProjectList(SearchTpConditionBO condition);

    Boolean updateSort(Long tpId, Integer sort);

    Boolean putOnShelf(Long tpId);

    Boolean putOffShelf(Long tpId);

    String getDescription(Long tpId);

    Integer getMaxSort(Long companyId, Long siteId, Long tpClassificationId);

    Page<TrainingProject> getTpPage(String searchTpName, List<Long> excludeTpIds, Integer pageNo, Integer pageSize);

    List<HotTpVO> getHotEnrollTp(Long siteId, Long companyId, String terminalType);

    List<OnlineTpVO> getOnLineTpPage(String terminalType, Long siteId, Long companyId);

    Boolean updateTpBaseInfo(TrainingProject updateProject);

    List<TrainingProject> getOnShelves(List<Long> tpIds);

    List<TrainingProject> getAllTpList(Long companyId, Long siteId, Integer pageNo, Integer pageSize);

    String judgeProjectDesc(ProjectJudgeAO ao);

    Page<RecentStudyTrainingVO> getRecentStudyList(Long companyId, Long siteId, Long accountId,
        Page<RecentStudyTrainingVO> page);
}
