package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.domain.TrainingProjectPro;
import com.yizhi.training.application.mapper.TpStudentProjectRecordMapper;
import com.yizhi.training.application.v2.enums.TpExceptionCodeEnum;
import com.yizhi.training.application.v2.enums.TpStatusEnum;
import com.yizhi.training.application.v2.mapper.TrainingProjectMapperV2;
import com.yizhi.training.application.v2.mapper.TrainingProjectProMapperV2;
import com.yizhi.training.application.v2.model.SearchTpConditionBO;
import com.yizhi.training.application.v2.service.TrainingProjectService;
import com.yizhi.training.application.v2.vo.HotTpVO;
import com.yizhi.training.application.v2.vo.OnlineTpVO;
import com.yizhi.training.application.v2.vo.RecentStudyTrainingVO;
import com.yizhi.training.application.v2.vo.request.ProjectJudgeAO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class TrainingProjectServiceImplV2 extends ServiceImpl<TrainingProjectMapperV2, TrainingProject>
    implements TrainingProjectService {

    @Autowired
    private TrainingProjectMapperV2 trainingProjectMapper;

    @Autowired
    private TrainingProjectProMapperV2 projectProMapperV2;

    @Autowired
    private TpStudentProjectRecordMapper studentProjectRecordMapper;


    @Override
    public Integer getTrainingProjectCount(SearchTpConditionBO searchTpConditionBO) {
        return trainingProjectMapper.getTrainingProjectCount(searchTpConditionBO);
    }

    @Override
    public List<TrainingProject> getTrainingProjectList(SearchTpConditionBO condition) {
        return trainingProjectMapper.getTrainingProjectList(condition);
    }

    @Override
    public Boolean updateSort(Long tpId, Integer sort) {
        TrainingProject project = new TrainingProject();
        project.setId(tpId);
        project.setSort(sort);
        return this.updateById(project);
    }

    @Override
    public Boolean putOnShelf(Long tpId) {
        TrainingProject project = new TrainingProject();
        project.setId(tpId);
        project.setStatus(TpStatusEnum.IN_USE.getCode());
        project.setReleaseTime(new Date());
        return this.updateById(project);
    }

    @Override
    public Boolean putOffShelf(Long tpId) {
        TrainingProject project = new TrainingProject();
        project.setId(tpId);
        project.setStatus(TpStatusEnum.DISABLE.getCode());
        return this.updateById(project);
    }

    @Override
    public String getDescription(Long tpId) {
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
        wrapper.eq("id", tpId);
        TrainingProject trainingProject = getOne(wrapper);
        return trainingProject == null ? "" : trainingProject.getDescription();
    }

    @Override
    public Integer getMaxSort(Long companyId, Long siteId, Long tpClassificationId) {
        Integer sort = trainingProjectMapper.getMaxSort(companyId, siteId, tpClassificationId);
        return sort == null ? 0 : sort;
    }

    @Override
    public Page<TrainingProject> getTpPage(String searchTpName, List<Long> excludeTpIds, Integer pageNo,
        Integer pageSize) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        if (CollectionUtils.isNotEmpty(excludeTpIds)) {
            wrapper.notIn("id", excludeTpIds);
        }
        if (StringUtils.isNotBlank(searchTpName)) {
            wrapper.like("name", searchTpName);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc(Arrays.asList("sort", "create_time"));

        return page(new Page<>(pageNo, pageSize), wrapper);
    }

    @Override
    public List<HotTpVO> getHotEnrollTp(Long siteId, Long companyId, String terminalType) {

        return trainingProjectMapper.getHotEnrollTp(siteId, companyId, terminalType, new Date());
    }

    @Override
    public List<OnlineTpVO> getOnLineTpPage(String terminalType, Long siteId, Long companyId) {
        return trainingProjectMapper.getOnLineTp(terminalType, siteId, companyId);
    }

    @Override
    public Boolean updateTpBaseInfo(TrainingProject updateProject) {
        return trainingProjectMapper.updateTpBaseInfo(updateProject);
    }

    @Override
    public List<TrainingProject> getOnShelves(List<Long> tpIds) {
        if (CollectionUtils.isEmpty(tpIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
        wrapper.in("id", tpIds);
        wrapper.eq("status", TpStatusEnum.IN_USE.getCode());
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }

    @Override
    public List<TrainingProject> getAllTpList(Long companyId, Long siteId, Integer pageNo, Integer pageSize) {
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
        if (companyId != null && companyId > 0) {
            wrapper.eq("company_id", companyId);
        }
        if (siteId != null && siteId > 0) {
            wrapper.eq("site_id", siteId);
        }
        wrapper.eq("deleted", 0);
        wrapper.last("LIMIT " + (pageNo - 1) * pageSize + "," + pageSize);
        return list(wrapper);
    }

    @Override
    public String judgeProjectDesc(ProjectJudgeAO ao) {
        //如果是pro不需要
        TrainingProjectPro trainingProjectPro = projectProMapperV2.selectById(ao.getTrainingProjectId());
        if (!ObjectUtils.isEmpty(trainingProjectPro)) {
            return "1";
        }
        //不显示介绍页的项目：不需要报名&已报名成功的项目直接进入学习页，返回也要相应调整
        TrainingProject trainingProject = trainingProjectMapper.selectById(ao.getTrainingProjectId());
        if (ObjectUtils.isEmpty(trainingProject)) {
            throw new BizException(TpExceptionCodeEnum.NOT_EXISTS_PROJECT.getCode(),
                TpExceptionCodeEnum.NOT_EXISTS_PROJECT.getDescription());
        }
        if (trainingProject.getProjectDescriptionFlag().equals(1)) {
            return "1";
        } else {
            if (trainingProject.getEnableEnroll().equals(0)) {
                return "0";
            } else {
                Integer count = trainingProjectMapper.judgeProjectDesc(ao);
                if (count > 0) {
                    return "0";
                } else {
                    return "1";
                }
            }
        }
    }

    @Override
    public Page<RecentStudyTrainingVO> getRecentStudyList(Long companyId, Long siteId, Long accountId,
        Page<RecentStudyTrainingVO> page) {
        List<RecentStudyTrainingVO> records =
            trainingProjectMapper.getRecentStudyList(companyId, siteId, accountId, page);
//        if (!CollectionUtils.isEmpty(records)) {
//            for (RecentStudyTrainingVO record : records) {
//                record.setJoinNumber(studentProjectRecordMapper.getFinishedAccountCount(record.getTrainingProjectId(), companyId, siteId));
//            }
//        }
        page.setRecords(records);
        return page;
    }
}
