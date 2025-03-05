package com.yizhi.training.application.v2.service;

import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.v2.vo.TpIntroduceBaseVO;
import com.yizhi.training.application.v2.vo.TpStudyDetailsVO;
import com.yizhi.training.application.v2.vo.request.ProjectJudgeAO;
import com.yizhi.training.application.v2.vo.study.*;

import java.util.List;

public interface ITpStudyBizService {

    public void startStudySaveLog(Long tpId);

    public List<TpIntroduceBaseVO> getStudyDirect(Long tpId);

    TpStudyDetailsVO getTpDetails(Long tpId);

    TpStudyIntroduceVO getIntroduceDetails(Long tpId);

    String getHtmlDetails(Long tpId, Long itemId);

    List<TpStudyPlanVO<TpStudyExamVO>> getExamAndAssignmentDetails(Long tpId);

    List<TpStudyForumVO> getTpForumDetails(Long tpId, Integer forumType);

    List<TpStudyPlanVO<TpStudyActivityVO>> getContentDetails(Long tpId, Long itemId);

    void applyCeitificate(Long relationId, Integer relationType);

    public String getStudyTimeStr(TpPlan plan, TpStudyPlanVO planVO);

    String judgeProjectDesc(ProjectJudgeAO ao);
}

