package com.yizhi.training.application.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.hierarchicalauthorization.HQueryUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.enums.InternationalEnums;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.training.application.constant.ProjectConstant;
import com.yizhi.training.application.domain.TpClassification;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.service.ITpClassificationService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.domain.TpClassificationVo;
import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import com.yizhi.util.application.domain.BizResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 培训项目分类表 前端控制器
 * </p>
 *
 * @author shengchenglong
 * @since 2018-03-27
 */
@Api(tags = "项目分类")
@RestController
@RequestMapping("/tpClassification")
@Slf4j
public class TpClassificationController {

    @Autowired
    ITpClassificationService iTpClassificationService;

    @Autowired
    ITrainingProjectService iTrainingProjectService;

    @Autowired
    IdGenerator idGenerator;

    @ApiOperation("分类列表查询")
    @GetMapping(value = "/list")
    public List<TpClassification> list(@RequestBody RequestContext context) {

        TpClassification classification = new TpClassification();
        classification.setSiteId(context.getSiteId());
        classification.setCompanyId(context.getCompanyId());
        classification.setDeleted(ProjectConstant.DELETED_NO);
        QueryWrapper<TpClassification> QueryWrapper = new QueryWrapper<>(classification);
        QueryWrapper.orderByAsc("sort");

        //        if (!context.isAdmin() && !CollectionUtils.isEmpty(context.getOrgIds())) {
        //            QueryWrapper.in("org_id", context.getOrgIds());
        //        }
        HQueryUtil.startHQ(TpClassification.class);
        return iTpClassificationService.list(QueryWrapper);
    }

    @GetMapping(value = "/page/list")
    Page<TpClassificationVo> pageList(@RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        RequestContext context = ContextHolder.get();
        Page<TpClassificationVo> voPage = new Page<>(pageNo, pageSize);
        Page<TpClassification> page = new Page<>(pageNo, pageSize);
        TpClassification classification = new TpClassification();
        classification.setSiteId(context.getSiteId());
        classification.setCompanyId(context.getCompanyId());
        classification.setDeleted(ProjectConstant.DELETED_NO);
        QueryWrapper<TpClassification> QueryWrapper = new QueryWrapper<>(classification);
        if (StringUtils.isNotBlank(keyword)) {
            QueryWrapper.like("name", keyword);
        }
        QueryWrapper.orderByAsc("sort");

        //        if (!context.isAdmin() && !CollectionUtils.isEmpty(context.getOrgIds())) {
        //            QueryWrapper.in("org_id", context.getOrgIds());
        //        }
        HQueryUtil.startHQ(TpClassification.class);
        iTpClassificationService.page(page, QueryWrapper);
        List<TpClassification> records = page.getRecords();
        List<TpClassificationVo> voList = new ArrayList<>();
        if (pageNo == 1) {
            TpClassificationVo tpClassificationVo = new TpClassificationVo();
            tpClassificationVo.setId(0L);
            tpClassificationVo.setName("未分组");
            //获取关联项目数量
            QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
            wrapper.eq("site_id", context.getSiteId());
            wrapper.eq("company_id", context.getCompanyId());
            wrapper.eq("tp_classification_id", 0L);
            wrapper.eq("deleted", ProjectConstant.DELETED_NO);
            HQueryUtil.startHQ(TpClassification.class);
            tpClassificationVo.setRelationNum(Math.toIntExact(iTrainingProjectService.count(wrapper)));
            voList.add(tpClassificationVo);
        }
        if (records != null && !records.isEmpty()) {
            for (TpClassification record : records) {
                TpClassificationVo tpClassificationVo = new TpClassificationVo();
                BeanUtils.copyProperties(record, tpClassificationVo);
                //获取关联项目数量
                QueryWrapper<TrainingProject> wrapper = new QueryWrapper<>();
                wrapper.eq("site_id", context.getSiteId());
                wrapper.eq("company_id", context.getCompanyId());
                wrapper.eq("tp_classification_id", record.getId());
                wrapper.eq("deleted", ProjectConstant.DELETED_NO);
                HQueryUtil.startHQ(TpClassification.class);
                tpClassificationVo.setRelationNum(Math.toIntExact(iTrainingProjectService.count(wrapper)));
                voList.add(tpClassificationVo);
            }
        }
        voPage.setPages(page.getPages());
        voPage.setRecords(voList);
        voPage.setTotal(page.getTotal());
        return voPage;
    }

    @GetMapping(value = "/change/sort")
    boolean changeSort(@RequestParam(value = "id") Long id, @RequestParam(value = "type") Integer type,
        @RequestParam(value = "sort") Integer sort) {
        return iTpClassificationService.changeSort(id, type, sort);
    }

    @GetMapping(value = "/projects/list")
    Page<TrainingProjectVo> projectsListByClassifyId(@RequestParam(value = "id") Long id,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return iTpClassificationService.projectsListByClassifyId(id, keyword, pageNo, pageSize);
    }

    @GetMapping(value = "/projects/remove")
    boolean projectsRemoveRelation(@RequestParam(value = "classifyId") Long classifyId,
        @RequestParam(value = "projectId") Long projectId) {
        return iTpClassificationService.projectsRemoveRelation(classifyId, projectId);
    }

    @ApiOperation("分类保存")
    @PostMapping(value = "/save")
    public BizResponse<Boolean> save(@RequestBody TpClassificationVo tpClassification) {
        //同名校验
        LambdaQueryWrapper<TpClassification> lamQueryWrapper = new LambdaQueryWrapper<>();
        lamQueryWrapper.eq(TpClassification::getName, tpClassification.getName());
        if (iTpClassificationService.count(lamQueryWrapper) > 0) {
            log.error("项目目录已经存在！");
            return BizResponse.fail(InternationalEnums.PROJECT_CLASSIFICATION_EXIST.getCode());
        }
        tpClassification.setId(idGenerator.generate());
        TpClassification tc = new TpClassification();
        BeanUtils.copyProperties(tpClassification, tc);
        //获取最大sort
        QueryWrapper<TpClassification> wrapper = new QueryWrapper<>();
        wrapper.eq("site_id", tc.getSiteId());
        wrapper.eq("company_id", tc.getCompanyId());
        wrapper.eq("deleted", ProjectConstant.DELETED_NO);
        wrapper.orderByDesc("sort");
        wrapper.last("limit 1");
        TpClassification one = iTpClassificationService.getOne(wrapper);
        if (one != null) {
            tc.setSort(one.getSort() + 1);
        }
        boolean f = iTpClassificationService.save(tc);
        if (f) {
            return BizResponse.ok();
        }
        return BizResponse.fail(InternationalEnums.INSERT_ERROR.getCode());

    }

    @ApiOperation("分类修改")
    @PostMapping(value = "/update")
    public BizResponse<Boolean> update(@RequestBody TpClassificationVo tpClassification) {
        TpClassification tc = new TpClassification();
        BeanUtils.copyProperties(tpClassification, tc);
        //同名校验
        LambdaQueryWrapper<TpClassification> lamQueryWrapper = new LambdaQueryWrapper<>();
        lamQueryWrapper.eq(TpClassification::getName, tpClassification.getName());
        lamQueryWrapper.ne(TpClassification::getId, tc.getId());
        if (iTpClassificationService.count(lamQueryWrapper) > 0) {
            log.error("项目目录已经存在！");
            return BizResponse.fail(InternationalEnums.PROJECT_CLASSIFICATION_EXIST.getCode());
        }
        boolean f = iTpClassificationService.updateById(tc);
        if (f) {
            return BizResponse.ok();
        }
        return BizResponse.fail(InternationalEnums.UPDATE_ERROR.getCode());

    }

    @ApiOperation("分类根据id查询")
    @GetMapping(value = "/view")
    public TpClassification view(@RequestParam("id") Long id) {
        TpClassification classification = iTpClassificationService.getById(id);
        return classification;
    }

    /**
     * 分类逻辑删除  删除之前将关联的项目改为未分类
     *
     * @param param 接参实体 只取id
     * @return 布尔值  true删除成功 false 删除失败
     */
    @ApiOperation("删除项目分类")
    @PostMapping(value = "delete")
    public Boolean tpClassificationDelete(@RequestBody TpClassificationVo param) {
        return iTpClassificationService.removeById(param.getId());
    }

}

