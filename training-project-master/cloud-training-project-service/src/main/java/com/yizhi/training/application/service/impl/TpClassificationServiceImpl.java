package com.yizhi.training.application.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yizhi.application.orm.hierarchicalauthorization.HQueryUtil;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.classify.vo.ClassifyDetailSimpleVO;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.vo.DroolsVo;
import com.yizhi.course.application.vo.MaterialVo;
import com.yizhi.training.application.domain.TpClassification;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.TpClassificationMapper;
import com.yizhi.training.application.mapstruct.TpClassifyConvert;
import com.yizhi.training.application.service.ITpClassificationService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 培训项目分类表 服务实现类
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Service
@Transactional
public class TpClassificationServiceImpl extends ServiceImpl<TpClassificationMapper, TpClassification>
    implements ITpClassificationService {

    @Autowired
    private ITpClassificationService tpClassificationService;

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private RedisCache redisCache;

    private Logger logger = LoggerFactory.getLogger(TpClassificationServiceImpl.class);

    @Override
    public Boolean delete(TpClassification param) {

        TpClassification tpClassification = tpClassificationService.getById(param.getId());

        if (null != tpClassification) {

            if (tpClassification.deleteById()) {
                // 查询关联项目
                TrainingProject trainingProject = new TrainingProject();
                trainingProject.setTpClassificationId(param.getId());
                QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>(trainingProject);
                List<TrainingProject> trainingProjects = trainingProjectService.list(wrapper);

                if (!CollectionUtils.isEmpty(trainingProjects)) {
                    List<Long> ids = new ArrayList<>();
                    for (TrainingProject t : trainingProjects) {
                        ids.add(t.getId());
                    }

                    QueryWrapper<TrainingProject> ew = new QueryWrapper<>();
                    ew.in("id", ids);
                    TrainingProject tp = new TrainingProject();
                    tp.setTpClassificationId(0L);
                    trainingProjectService.update(tp, ew);
                }
            }
        }

        return Boolean.valueOf(true);
    }

    @Override
    public Page getClassifyNameByDrools(String field, String value, Page page) {
        if (StrUtil.isBlank(field)) {
            logger.info("列名不能为空！");
            return page;
        }
        RequestContext requestContext = ContextHolder.get();
        Long siteId = requestContext.getSiteId();
        Long companyId = requestContext.getCompanyId();
        List<DroolsVo> voList = null;

        TpClassification classify = new TpClassification();
        classify.setSiteId(siteId);
        classify.setCompanyId(companyId);
        classify.setDeleted(0);
        QueryWrapper<TpClassification> wrapper = new QueryWrapper<>(classify);
        wrapper.select("distinct(" + field + ")," + "id ").isNotNull(field).like(field, value).ne(field, "")
            .orderByDesc("create_time");

        Page<TpClassification> tpClassificationPage = this.baseMapper.selectPage(page, wrapper);
        List<TpClassification> list = tpClassificationPage.getRecords();
        if (!CollectionUtils.isEmpty(list)) {
            voList = new ArrayList<>(list.size());
            for (TpClassification a : list) {
                DroolsVo vo = new DroolsVo();
                vo.setTaskId(a.getId());
                vo.setTaskFieldValue(a.getName());
                vo.setTaskParamsType(field);
                voList.add(vo);
            }
        }
        page.setRecords(voList);
        return page;
    }

    /**
     * 根据分类id获取分类名称
     *
     * @param ids 分类id列表
     * @return List<ClassifyDetailSimpleVO>
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    @Override
    public List<ClassifyDetailSimpleVO> listClassifyByIds(List<Long> ids) {
        return TpClassifyConvert.INSTANCE.do2vo(
            lambdaQuery().select(TpClassification::getId, TpClassification::getName).in(TpClassification::getId, ids)
                .list());
    }

    /**
     * 根据分类id列表查询个数（判断id列表中是否含有不存在的分类id）
     *
     * @param ids 分类id列表
     * @return 存在数量
     * @author Gxr
     * @since 2024/4/22 14:52
     */
    @Override
    public Integer cntClassifyByIds(List<Long> ids) {
        return lambdaQuery().in(TpClassification::getId, ids).count().intValue();
    }

    @Override
    public boolean changeSort(Long id, Integer type, Integer sort) {
        RequestContext requestContext = ContextHolder.get();
        Long siteId = requestContext.getSiteId();
        Long companyId = requestContext.getCompanyId();

        String lockKey = "project_sort_" + siteId;
        try {
            if (redisCache.setIfAbsent(lockKey, "1",10, TimeUnit.SECONDS)) {
                TpClassification byId = this.getById(id);
                if (byId == null || !Objects.equals(byId.getSort(), sort)) {
                    return false;
                }
                HQueryUtil.startHQ(TpClassification.class);
                if (type == 0) {
                    //上移  获取上一个未删除的分类sort两者对换
                    QueryWrapper<TpClassification> wrapper = new QueryWrapper<>();
                    wrapper.eq("company_id", companyId);
                    wrapper.eq("site_id", siteId);
                    wrapper.eq("deleted", 0);
                    wrapper.lt("sort", sort);
                    wrapper.orderByDesc("sort");
                    wrapper.last("limit 1");
                    TpClassification previous = this.getOne(wrapper);
                    if (previous != null) {
                        byId.setSort(previous.getSort());
                        previous.setSort(sort);
                        return updateById(previous) && updateById(byId);
                    }

                } else if (type == 1) {
                    //下移
                    QueryWrapper<TpClassification> wrapper = new QueryWrapper<>();
                    wrapper.eq("company_id", companyId);
                    wrapper.eq("site_id", siteId);
                    wrapper.eq("deleted", 0);
                    wrapper.gt("sort", sort);
                    wrapper.orderByAsc("sort");
                    wrapper.last("limit 1");
                    TpClassification next = this.getOne(wrapper);
                    if (next != null) {
                        byId.setSort(next.getSort());
                        next.setSort(sort);
                        return updateById(next) && updateById(byId);
                    }
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            logger.info("修改项目分类排序失败");
            e.printStackTrace();
        }finally {
            redisCache.delete(lockKey);
        }

        return false;

    }

    @Override
    public Page<TrainingProjectVo> projectsListByClassifyId(Long id, String keyword, Integer pageNo, Integer pageSize) {
        RequestContext requestContext = ContextHolder.get();
        Page<TrainingProjectVo> voPage = new Page<>();
        Page<TrainingProject> page = new Page<>(pageNo, pageSize);
        QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
        wrapper.eq("company_id", requestContext.getCompanyId());
        wrapper.eq("site_id", requestContext.getSiteId());
        if (keyword != null) {
            wrapper.and(e -> e.like("name", keyword).or().like("key_words", keyword));
        }
        wrapper.eq("tp_classification_id", id);
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        HQueryUtil.startHQ(TpClassification.class);
        trainingProjectService.page(page, wrapper);
        List<TrainingProject> records = page.getRecords();
        List<TrainingProjectVo> vos = new ArrayList<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(records)) {
            for (TrainingProject record : records) {
                TrainingProjectVo vo = new TrainingProjectVo();
                BeanUtils.copyProperties(record, vo);
                vos.add(vo);
            }
        }
        voPage.setRecords(vos);
        voPage.setTotal(page.getTotal());
        voPage.setPages(page.getPages());

        return voPage;
    }

    @Override
    public boolean projectsRemoveRelation(Long classifyId, Long projectId) {
        TrainingProject trainingProject = trainingProjectService.getById(projectId);
        if (trainingProject == null) {
            return false;
        }
        if (!Objects.equals(trainingProject.getTpClassificationId(), classifyId)) {
            return false;
        }
        trainingProject.setTpClassificationId(0L);
        return trainingProjectService.updateById(trainingProject);
    }
}
