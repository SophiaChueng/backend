package com.yizhi.training.application.v2.service.biz;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.application.orm.hierarchicalauthorization.HQueryUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.training.application.domain.TpProMapping;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.domain.TrainingProjectPro;
import com.yizhi.training.application.v2.constant.TrainingProjectConstant;
import com.yizhi.training.application.v2.enums.TpExceptionCodeEnum;
import com.yizhi.training.application.v2.service.TpProMappingService;
import com.yizhi.training.application.v2.service.TrainingProjectProService;
import com.yizhi.training.application.v2.service.TrainingProjectService;
import com.yizhi.training.application.v2.vo.TpBaseInfoVO;
import com.yizhi.training.application.v2.vo.TrainingProjectProVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.*;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainingProjectProBizService {

    @Autowired
    private TpProMappingService tpProMappingService;

    @Autowired
    private TrainingProjectProService trainingProjectProService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TrainingProjectService trainingProjectService;

    /**
     * 将项目往项目Pro中添加
     *
     * @param requestVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean addProjectToPros(AddProjectToProsRequestVO requestVO) {
        Boolean res = tpProMappingService.deleteBatchByTpId(requestVO.getTrainingProjectId());
        if (CollectionUtils.isEmpty(requestVO.getProjectProIds())) {
            return res;
        }
        Map<Long, Integer> maxSortMap =
            tpProMappingService.getMaxSortOfTp(requestVO.getTrainingProjectId(), requestVO.getProjectProIds());
        RequestContext context = ContextHolder.get();
        List<TpProMapping> list = new ArrayList<>();
        requestVO.getProjectProIds().forEach(tpProId -> {
            TpProMapping mapping = new TpProMapping();
            mapping.setCompanyId(context.getCompanyId());
            mapping.setSiteId(context.getSiteId());
            mapping.setTrainingProjectId(requestVO.getTrainingProjectId());
            mapping.setTpProId(tpProId);
            mapping.setSort(maxSortMap.getOrDefault(tpProId, 0) + 1);
            list.add(mapping);
        });

        return tpProMappingService.saveBatch(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean addProjectsToPro(AddProjectsToProRequestVO requestVO) {
        // 增量添加，不删除已有数据
        if (CollectionUtils.isEmpty(requestVO.getTrainingProjectIds())) {
            return false;
        }
        List<TpProMapping> tpProMappings = tpProMappingService.getMappingListByTpPro(requestVO.getTpProId());
        List<Long> oldTpIds =
            tpProMappings.stream().map(TpProMapping::getTrainingProjectId).collect(Collectors.toList());
        int maxSort =
            CollectionUtils.isEmpty(tpProMappings) ? 0 : tpProMappings.get(tpProMappings.size() - 1).getSort();
        RequestContext context = ContextHolder.get();
        List<TpProMapping> list = new ArrayList<>();
        for (Long tpId : requestVO.getTrainingProjectIds()) {
            if (oldTpIds.contains(tpId)) {
                // 去重
                continue;
            }
            TpProMapping mapping = new TpProMapping();
            mapping.setCompanyId(context.getCompanyId());
            mapping.setSiteId(context.getSiteId());
            mapping.setTrainingProjectId(tpId);
            mapping.setTpProId(requestVO.getTpProId());
            mapping.setSort(++maxSort);
            list.add(mapping);
        }
        if (CollectionUtils.isEmpty(list)) {
            return true;
        }
        return tpProMappingService.saveBatch(list);
    }

    /**
     * 查询项目pro列表
     *
     * @param request
     * @return
     */
    public PageDataVO<TrainingProjectProVO> getTpProList(SearchTpProVO request) {
        PageDataVO<TrainingProjectProVO> pageDataVO = new PageDataVO<>();
        pageDataVO.setPageNo(request.getPageNo());
        pageDataVO.setPageSize(request.getPageSize());

        RequestContext context = ContextHolder.get();
        List<Long> tpProIds = null;

        if (request.getTrainingProjectId() != null && request.getTrainingProjectId() > 0) {
            List<TpProMapping> mappings = tpProMappingService.getMappingListByTpId(request.getTrainingProjectId());
            if (CollectionUtils.isEmpty(mappings)) {
                pageDataVO.setTotal(0);
                return pageDataVO;
            }
            tpProIds = mappings.stream().map(TpProMapping::getTpProId).collect(Collectors.toList());
        }
        HQueryUtil.startHQ(TrainingProjectPro.class);
        Page<TrainingProjectPro> tpProPage =
            trainingProjectProService.getTpProList(context.getCompanyId(), context.getSiteId(), request.getTpProName(),
                tpProIds, request.getPageNo(), request.getPageSize());
        if (tpProPage == null || CollectionUtils.isEmpty(tpProPage.getRecords())) {
            pageDataVO.setTotal(0);
            return pageDataVO;
        }
        pageDataVO.setTotal((int)tpProPage.getTotal());

        List<TrainingProjectProVO> tpProVOS = new ArrayList<>();
        tpProPage.getRecords().forEach(o -> {
            TrainingProjectProVO vo = new TrainingProjectProVO();
            BeanUtils.copyProperties(o, vo);
            vo.setProjectCount(tpProMappingService.getTpCountOfTpPro(o.getId()));

            tpProVOS.add(vo);
        });
        pageDataVO.setRecords(tpProVOS);

        return pageDataVO;
    }

    /**
     * 新增项目pro
     *
     * @param tpProVo
     * @return
     */
    public Boolean addTrainingProjectPro(TrainingProjectProVO tpProVo) {
        TrainingProjectPro tpPro = new TrainingProjectPro();
        BeanUtils.copyProperties(tpProVo, tpPro);
        RequestContext context = ContextHolder.get();
        tpPro.setCompanyId(context.getCompanyId());
        tpPro.setSiteId(context.getSiteId());
        tpPro.setId(idGenerator.generate());
        if (tpProVo.getSort() == null) {
            tpPro.setSort(TrainingProjectConstant.DEFAULT_SORT);
        }

        return trainingProjectProService.save(tpPro);
    }

    /**
     * 更新项目pro(更新名称，logo，排序)
     *
     * @param tpProVo
     * @return
     */
    public Boolean updateTrainingProjectPro(TrainingProjectProVO tpProVo) {
        TrainingProjectPro tpPro = new TrainingProjectPro();
        tpPro.setId(tpProVo.getId());
        tpPro.setTpProName(tpProVo.getTpProName());
        tpPro.setTpProLogo(tpProVo.getTpProLogo());
        tpPro.setSort(tpProVo.getSort());

        return trainingProjectProService.updateById(tpPro);
    }

    /**
     * 删除项目pro
     *
     * @param tpProId
     * @return
     */
    public Boolean deleteTrainingProjectPro(Long tpProId) {
        if (tpProMappingService.getTpCountOfTpPro(tpProId) > 0) {
            throw new BizException(TpExceptionCodeEnum.TP_PRO_CONTAIN_TP.getCode(),
                TpExceptionCodeEnum.TP_PRO_CONTAIN_TP.getDescription());
        }
        return trainingProjectProService.removeById(tpProId);
    }

    /**
     * 查询项目pro下的项目列表
     *
     * @param requestVO
     * @return
     */
    public PageDataVO<TpBaseInfoVO> getTpListOfPro(SearchProjectOfProVO requestVO) {
        PageDataVO<TpBaseInfoVO> pageDataVO = new PageDataVO<>();
        pageDataVO.setPageNo(requestVO.getPageNo());
        pageDataVO.setPageSize(requestVO.getPageSize());
        HQueryUtil.startHQ(TrainingProject.class);
        Integer total = tpProMappingService.getTpCountOfPro(requestVO.getTpProId(), requestVO.getSearchTpName());
        pageDataVO.setTotal(total);
        if (total == 0) {
            return pageDataVO;
        }
        List<TrainingProject> projects =
            tpProMappingService.getTpListOfPro(requestVO.getTpProId(), requestVO.getSearchTpName(),
                requestVO.getPageNo(), requestVO.getPageSize(), null);
        List<TpBaseInfoVO> projectVos = BeanCopyListUtil.copyListProperties(projects, TpBaseInfoVO::new);
        pageDataVO.setRecords(projectVos);
        return pageDataVO;
    }

    /**
     * 删除项目pro中的项目
     *
     * @param requestVO
     * @return
     */
    public Boolean deleteProjectsFromPro(DeleteProjectsFromProRequestVO requestVO) {
        return tpProMappingService.deleteByProIdAndTpIds(requestVO.getTpProId(), requestVO.getTrainingProjectIds());
    }

    /**
     * 查询项目pro可以添加的项目列表
     *
     * @param requestVO
     * @return
     */
    public PageDataVO<TpBaseInfoVO> getTpListCanBeAdd(SearchProjectToProVO requestVO) {
        List<Long> excludeTpIds = tpProMappingService.getTpIds(requestVO.getTpProId());
        Page<TrainingProject> tpPage =
            trainingProjectService.getTpPage(requestVO.getSearchTpName(), excludeTpIds, requestVO.getPageNo(),
                requestVO.getPageSize());

        PageDataVO<TpBaseInfoVO> pageDataVO = new PageDataVO<>();
        pageDataVO.setPageNo(requestVO.getPageNo());
        pageDataVO.setPageSize(requestVO.getPageSize());

        if (tpPage == null || CollectionUtils.isEmpty(tpPage.getRecords())) {
            pageDataVO.setTotal(0);
            return pageDataVO;
        }
        pageDataVO.setTotal((int)tpPage.getTotal());
        List<TpBaseInfoVO> records = BeanCopyListUtil.copyListProperties(tpPage.getRecords(), TpBaseInfoVO::new);
        pageDataVO.setRecords(records);
        return pageDataVO;
    }

    /**
     * 更新排序
     *
     * @param tpProId
     * @param moveTpId
     * @param preTpId
     * @return
     */
    public Boolean updateProjectSortOfPro(Long tpProId, Long moveTpId, Long preTpId) {
        Integer preSort = 0;

        if (preTpId != null && preTpId > 0) {
            preSort = tpProMappingService.getOneByTpId(tpProId, preTpId);
        }
        tpProMappingService.addSortValue(tpProId, preSort + 1);
        TpProMapping mapping = new TpProMapping();
        mapping.setSort(preSort + 1);
        return tpProMappingService.updateBy(mapping, tpProId, moveTpId);
    }
}
