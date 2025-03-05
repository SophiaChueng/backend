package com.yizhi.training.application.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpHeadTeacher;
import com.yizhi.training.application.v2.mapper.TpHeadTeacherMapperV2;
import com.yizhi.training.application.v2.service.TpHeadTeacherService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TpHeadTeacherServiceImplV2 extends ServiceImpl<TpHeadTeacherMapperV2, TpHeadTeacher>
    implements TpHeadTeacherService {

    @Autowired
    private IdGenerator idGenerator;

    @Override
    public List<Long> getTeacherIds(Long trainingProjectId) {
        List<TpHeadTeacher> list = getTeachers(trainingProjectId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(TpHeadTeacher::getAccountId).collect(Collectors.toList());
    }

    @Override
    public Boolean checkTeacher(Long trainingProjectId, Long accountId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpHeadTeacher> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("account_id", accountId);
        wrapper.eq("deleted", 0);
        List<TpHeadTeacher> tpHeadTeachers = list(wrapper);
        return !CollectionUtils.isEmpty(tpHeadTeachers);
    }

    /**
     * 保存班主任信息
     *
     * @param trainingProjectId
     * @param accountIds
     * @return
     */
    @Override
    public Boolean saveHeadTeachers(Long trainingProjectId, List<Long> accountIds) {
        boolean delRes = deleteByTpId(trainingProjectId);
        if (CollectionUtils.isEmpty(accountIds)) {
            return delRes;
        }

        RequestContext context = ContextHolder.get();
        List<TpHeadTeacher> headTeachers = new ArrayList<>();
        for (Long accountId : accountIds) {
            TpHeadTeacher headTeacher = new TpHeadTeacher();
            headTeacher.setId(idGenerator.generate());
            headTeacher.setCompanyId(context.getCompanyId());
            headTeacher.setSiteId(context.getSiteId());
            headTeacher.setTrainingProjectId(trainingProjectId);
            headTeacher.setAccountId(accountId);

            headTeachers.add(headTeacher);
        }
        return saveBatch(headTeachers);
    }

    /**
     * 删除项目的班主任关联信息
     *
     * @param trainingProjectId
     * @return
     */
    @Override
    public Boolean deleteByTpId(Long trainingProjectId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpHeadTeacher> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("training_project_id", trainingProjectId);
        return remove(wrapper);
    }

    public List<TpHeadTeacher> getTeachers(Long trainingProjectId) {
        RequestContext context = ContextHolder.get();
        QueryWrapper<TpHeadTeacher> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", context.getCompanyId());
        wrapper.eq("site_id", context.getSiteId());
        wrapper.eq("training_project_id", trainingProjectId);
        wrapper.eq("deleted", 0);
        return list(wrapper);
    }
}
