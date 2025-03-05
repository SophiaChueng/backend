package com.yizhi.training.application.v2.service.biz;

import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.certificate.application.feign.CertificateClient;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.exception.BizException;
import com.yizhi.documents.application.feign.DocumentRelationClient;
import com.yizhi.documents.application.vo.documents.DocumentCountVo;
import com.yizhi.training.application.domain.TpConsultEntrance;
import com.yizhi.training.application.domain.TpIntroduceDirectory;
import com.yizhi.training.application.domain.TpStudyDirectory;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.v2.enums.TpDirectoryItemTypeEnum;
import com.yizhi.training.application.v2.enums.TpDirectoryTypeEnum;
import com.yizhi.training.application.v2.enums.TpExceptionCodeEnum;
import com.yizhi.training.application.v2.service.*;
import com.yizhi.training.application.v2.vo.TpConsultEntranceVO;
import com.yizhi.training.application.v2.vo.TpIntroduceDirectoryVO;
import com.yizhi.training.application.v2.vo.TpStudyDirectoryVO;
import com.yizhi.util.application.beanutil.BeanCopyListUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TpStudyDirectoryBizService {

    @Autowired
    private TpStudyDirectoryService tpStudyDirectoryService;

    @Autowired
    private TpIntroduceDirectoryService tpIntroduceDirectoryService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TpPlanActivityService tpPlanActivityService;

    @Autowired
    private TpPlanService tpPlanService;

    @Autowired
    private TpConsultEntranceService tpConsultEntranceService;

    @Autowired
    private TpPlanConditionPreService tpPlanConditionPreService;

    @Autowired
    private TpPlanConditionPostService tpPlanConditionPostService;

    @Autowired
    private TpPlanStudyTimeConditionService tpPlanStudyTimeConditionService;

    @Autowired
    private TpRichTextService tpRichTextService;

    @Autowired
    private DocumentRelationClient documentRelationClient;

    @Autowired
    private CertificateClient certificateClient;

    @Autowired
    private TpCommentService tpCommentService;

    @Autowired
    private TrainingProjectService trainingProjectService;

    public Boolean addBatchStudyDirectoryItem(List<TpStudyDirectoryVO> items) {
        RequestContext context = ContextHolder.get();
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        Long trainingProjectId = items.get(0).getTrainingProjectId();
        if (trainingProjectId == null || trainingProjectId < 1) {
            return false;
        }
        List<Integer> canNotAddTypes = tpStudyDirectoryService.getCanNotAddType(trainingProjectId);
        Integer maxSort = tpStudyDirectoryService.getMaxSort(trainingProjectId);
        List<TpStudyDirectory> addList = new ArrayList<>();
        for (TpStudyDirectoryVO item : items) {
            if (canNotAddTypes.contains(item.getItemType())) {
                continue;
            }
            TpStudyDirectory directory = new TpStudyDirectory();
            BeanUtils.copyProperties(item, directory);
            directory.setCompanyId(context.getCompanyId());
            directory.setSiteId(context.getSiteId());
            directory.setSort(++maxSort);
            directory.setId(idGenerator.generate());

            addList.add(directory);
        }
        if (CollectionUtils.isNotEmpty(addList)) {
            return tpStudyDirectoryService.saveBatch(addList);
        }
        return false;
    }

    /**
     * 更新目录项信息 PS：目前可更新的信息仅名称
     *
     * @param item
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStudyDirectoryItem(TpStudyDirectoryVO item) {
        TpStudyDirectory studyDir = tpStudyDirectoryService.getById(item.getId());
        if (studyDir == null) {
            return false;
        }
        TpStudyDirectory directory = new TpStudyDirectory();
        directory.setId(item.getId());
        directory.setItemName(item.getItemName());
        if (tpStudyDirectoryService.updateById(directory)) {
            tpIntroduceDirectoryService.updateItemName(studyDir.getTrainingProjectId(), studyDir.getId(),
                item.getItemName());
            return true;
        }
        return false;
    }

    /**
     * 查询项目学习页目录
     *
     * @param tpId
     * @return
     */
    public List<TpStudyDirectoryVO> getTpStudyDirectory(Long tpId) {
        // 如果没有默认的学习单元目录项，需要新增
        if (tpStudyDirectoryService.getStudyUnitCount(tpId) == 0) {
            RequestContext context = ContextHolder.get();
            TpStudyDirectory defaultStudyUnit = new TpStudyDirectory();
            defaultStudyUnit.setId(idGenerator.generate());
            defaultStudyUnit.setCompanyId(context.getCompanyId());
            defaultStudyUnit.setSiteId(context.getSiteId());
            defaultStudyUnit.setTrainingProjectId(tpId);
            defaultStudyUnit.setItemType(TpDirectoryItemTypeEnum.STUDY_UNIT.getCode());
            defaultStudyUnit.setItemName("学习目录");
            defaultStudyUnit.setSort(1);

            if (tpStudyDirectoryService.save(defaultStudyUnit)) {
                // 兼容旧逻辑
                // 若新增成功，则将tpPlan中directoryItemId为0的变更为该目录项id
                tpPlanService.updateDirectoryItemId(tpId, 0L, defaultStudyUnit.getId());
            }
        }

        List<TpStudyDirectory> records = tpStudyDirectoryService.getStudyDirectory(tpId);
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<TpStudyDirectoryVO> resultList = new ArrayList<>();
        records.forEach(o -> {
            TpStudyDirectoryVO vo = new TpStudyDirectoryVO();
            BeanUtils.copyProperties(o, vo);
            resultList.add(vo);
        });
        return resultList;
    }

    /**
     * 更新排序
     *
     * @param trainingProjectId
     * @param moveId
     * @param preId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStudyDirectorySort(Long trainingProjectId, Long moveId, Long preId) {
        Integer preSort = 0;
        if (preId != null && preId > 0) {
            preSort = tpStudyDirectoryService.getSort(preId);
        }
        TpStudyDirectory item = new TpStudyDirectory();
        item.setId(moveId);
        item.setSort(preSort + 1);
        tpStudyDirectoryService.addSortValue(trainingProjectId, preSort + 1);
        return tpStudyDirectoryService.updateById(item);
    }

    /**
     * 查询介绍页目录
     *
     * @param tpId
     * @return
     */
    public List<TpIntroduceDirectoryVO> getTpIntroduceDirectory(Long tpId) {
        List<TpIntroduceDirectory> records = tpIntroduceDirectoryService.getIntroduceDirectory(tpId);
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        return BeanCopyListUtil.copyListProperties(records, TpIntroduceDirectoryVO::new);
    }

    public Boolean addBatchIntroduceDirectory(List<TpIntroduceDirectoryVO> items) {
        if (CollectionUtils.isEmpty(items)) {
            return false;
        }
        Long trainingProjectId = items.get(0).getTrainingProjectId();
        if (trainingProjectId == null || trainingProjectId < 1) {
            return false;
        }
        List<TpIntroduceDirectory> introduceList = tpIntroduceDirectoryService.getIntroduceDirectory(trainingProjectId);

        RequestContext context = ContextHolder.get();

        Map<Long, TpIntroduceDirectory> itemIdMap =
            introduceList.stream().collect(Collectors.toMap(TpIntroduceDirectory::getItemId, o -> o, (o1, o2) -> o1));
        Integer maxSort = tpIntroduceDirectoryService.getMaxSort(trainingProjectId);
        List<TpIntroduceDirectory> addList = new ArrayList<>();
        for (TpIntroduceDirectoryVO item : items) {
            if (TpDirectoryItemTypeEnum.isStudyDirectory(item.getItemType())) {
                if (itemIdMap.containsKey(item.getItemId())) {
                    continue;
                }
            }
            if (TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode().equals(item.getItemType())) {
                TpIntroduceDirectory consultEntrance = introduceList.stream()
                    .filter(o -> TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode().equals(o.getItemType())).findFirst()
                    .orElse(null);
                if (consultEntrance != null) {
                    continue;
                }
            }
            TpIntroduceDirectory introduceDirectory = new TpIntroduceDirectory();
            BeanUtils.copyProperties(item, introduceDirectory);
            introduceDirectory.setId(idGenerator.generate());
            introduceDirectory.setCompanyId(context.getCompanyId());
            introduceDirectory.setSiteId(context.getSiteId());

            if (TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode().equals(item.getItemType())) {
                introduceDirectory.setSort(Integer.MAX_VALUE);
            } else if (TpDirectoryItemTypeEnum.COMMENT.getCode().equals(item.getItemType())) {
                introduceDirectory.setSort(Integer.MAX_VALUE - 1);
            } else {
                introduceDirectory.setSort(++maxSort);
            }
            addList.add(introduceDirectory);
        }
        if (CollectionUtils.isNotEmpty(addList)) {
            return tpIntroduceDirectoryService.saveBatch(addList);
        }
        return false;
    }

    /**
     * 更新咨询信息
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConsultEntrance(TpConsultEntranceVO request) {
        TpConsultEntrance consultEntrance = new TpConsultEntrance();
        consultEntrance.setEntranceImg(request.getEntranceImg());
        consultEntrance.setEntranceName(request.getEntranceName());

        // 同时更新目录项名称
        if (StringUtils.isNotBlank(request.getEntranceName())) {
            TpIntroduceDirectory consultItem = new TpIntroduceDirectory();
            consultItem.setId(request.getDirectoryItemId());
            consultItem.setItemName(request.getEntranceName());
            tpIntroduceDirectoryService.updateById(consultItem);
        }

        RequestContext context = ContextHolder.get();

        TpConsultEntrance oldValue =
            tpConsultEntranceService.getOne(request.getTrainingProjectId(), request.getDirectoryItemId());
        if (oldValue == null) {
            consultEntrance.setId(idGenerator.generate());
            consultEntrance.setCompanyId(context.getCompanyId());
            consultEntrance.setSiteId(context.getSiteId());
            consultEntrance.setTrainingProjectId(request.getTrainingProjectId());
            consultEntrance.setDirectoryItemId(request.getDirectoryItemId());
            return tpConsultEntranceService.save(consultEntrance);
        }
        consultEntrance.setId(oldValue.getId());
        return tpConsultEntranceService.updateById(consultEntrance);
    }

    public TpConsultEntranceVO getConsultEntrance(Long trainingProjectId, Long directoryItemId) {
        TpConsultEntrance entrance = tpConsultEntranceService.getOne(trainingProjectId, directoryItemId);
        if (entrance == null) {
            return null;
        }
        TpConsultEntranceVO entranceVO = new TpConsultEntranceVO();
        BeanUtils.copyProperties(entrance, entranceVO);
        return entranceVO;
    }

    /**
     * 更新排序
     *
     * @param moveId
     * @param preId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIntroduceDirectorySort(Long trainingProjectId, Long moveId, Long preId) {
        Integer preSort = 0;
        if (preId != null && preId > 0) {
            preSort = tpIntroduceDirectoryService.getSort(preId);
        }
        TpIntroduceDirectory item = new TpIntroduceDirectory();
        item.setId(moveId);
        item.setSort(preSort + 1);
        tpIntroduceDirectoryService.addSortValue(trainingProjectId, preSort + 1);
        return tpIntroduceDirectoryService.updateById(item);
    }

    /**
     * 删除学习页目录项
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteStudyDirectoryItem(Long trainingProjectId, Long directoryItemId) {
        TpStudyDirectory item = tpStudyDirectoryService.getById(directoryItemId);
        if (item == null || BooleanUtils.isTrue(item.getDeleted())) {
            return false;
        }
        // 学习单元需要判断是否只剩最后一条，需要保证至少有一条
        if (TpDirectoryItemTypeEnum.STUDY_UNIT.getCode()
            .equals(item.getItemType()) && tpStudyDirectoryService.getStudyUnitCount(trainingProjectId) <= 1) {
            throw new BizException(TpExceptionCodeEnum.STUDY_UNIT_COUNT_LIMIT.getCode(),
                TpExceptionCodeEnum.STUDY_UNIT_COUNT_LIMIT.getDescription());
        }
        if (!tpStudyDirectoryService.deleteByItemId(trainingProjectId, directoryItemId)) {
            return false;
        }
        // 同时删除对应的介绍页目录
        tpIntroduceDirectoryService.deleteByStudyItem(trainingProjectId, item.getId());

        // 删除目录项时，只能添加一项的目录项（例如：资料），删除时，不删除关联关系
        // 若删除的是学习单元和富文本，因为可添加多项，所以删除关联关系
        if (TpDirectoryItemTypeEnum.STUDY_UNIT.getCode().equals(item.getItemType())) {
            deleteStudyUnitItem(trainingProjectId, directoryItemId);
        } else if (TpDirectoryItemTypeEnum.RICH_TEXT.getCode().equals(item.getItemType())) {
            tpRichTextService.deleteByItemId(trainingProjectId, TpDirectoryTypeEnum.STUDY_PAGE.getCode(),
                directoryItemId);
        }
        return true;
    }

    /**
     * 删除介绍页目录项
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteIntroduceDirectoryItem(Long trainingProjectId, Long directoryItemId) {
        TpIntroduceDirectory item = tpIntroduceDirectoryService.getById(directoryItemId);
        if (item == null || BooleanUtils.isTrue(item.getDeleted())) {
            return false;
        }
        if (tpIntroduceDirectoryService.deleteByItemId(trainingProjectId, directoryItemId)) {
            return false;
        }
        if (TpDirectoryItemTypeEnum.INTRODUCE_RICH_TEXT.getCode().equals(item.getItemType())) {
            // 删除富文本
            tpRichTextService.deleteByItemId(trainingProjectId, TpDirectoryTypeEnum.INTRODUCE_PAGE.getCode(),
                directoryItemId);
        } else if (TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode().equals(item.getItemType())) {
            // 删除咨询
            tpConsultEntranceService.deleteByItemId(trainingProjectId, directoryItemId);
        }
        return true;
    }

    /**
     * 更新介绍页目录项
     *
     * @param item
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIntroduceDirectory(TpIntroduceDirectoryVO item) {
        TpIntroduceDirectory directory = new TpIntroduceDirectory();
        directory.setId(item.getId());
        directory.setItemName(item.getItemName());
        return tpIntroduceDirectoryService.updateById(directory);
    }

    public Boolean initStudyDirectory(Long companyId, Long siteId) {
        int pageNo = 1;
        int pageSize = 1000;
        while (true) {
            List<TrainingProject> tpList = trainingProjectService.getAllTpList(companyId, siteId, pageNo++, pageSize);
            if (CollectionUtils.isEmpty(tpList)) {
                break;
            }
            for (TrainingProject tp : tpList) {
                initTpDirectory(tp);
            }
        }
        return true;
    }

    public Boolean initTpDirectory(TrainingProject tp) {
        if (tp == null || Objects.equals(tp.getDeleted(), 1)) {
            return false;
        }

        // 如果没有默认的学习单元目录项，需要新增
        if (tpStudyDirectoryService.getStudyUnitCount(tp.getId()) == 0) {
            TpStudyDirectoryVO unit = new TpStudyDirectoryVO();
            unit.setTrainingProjectId(tp.getId());
            unit.setItemType(TpDirectoryItemTypeEnum.STUDY_UNIT.getCode());
            unit.setItemName("学习目录");
            TpStudyDirectoryVO res = addStudyDirectoryItem(tp.getCompanyId(), tp.getSiteId(), unit);
            if (res != null) {
                tpPlanService.updateDirectoryItemId(tp.getId(), 0L, res.getId());
                TpIntroduceDirectoryVO introUnit = new TpIntroduceDirectoryVO();
                introUnit.setTrainingProjectId(tp.getId());
                introUnit.setItemType(res.getItemType());
                introUnit.setItemName(res.getItemName());
                introUnit.setItemId(res.getId());
                addIntroduceDirectory(tp.getCompanyId(), tp.getSiteId(), introUnit);
            }
        }

        if (StringUtils.isNotBlank(tp.getDescription())) {
            TpStudyDirectoryVO intro = new TpStudyDirectoryVO();
            intro.setTrainingProjectId(tp.getId());
            intro.setItemType(TpDirectoryItemTypeEnum.BRIEF_INTRODUCE.getCode());
            intro.setItemName(TpDirectoryItemTypeEnum.BRIEF_INTRODUCE.getDescription());
            addStudyDirectoryItem(tp.getCompanyId(), tp.getSiteId(), intro);
        }
        List<Long> tpPlanIds = tpPlanService.getTpPlanIds(Collections.singletonList(tp.getId()));
        List<Long> relationIds = new ArrayList<>(tpPlanIds);
        relationIds.add(tp.getId());
        List<DocumentCountVo> documentCountVoList = documentRelationClient.getRelationCount(relationIds);
        if (CollectionUtils.isNotEmpty(documentCountVoList)) {
            TpStudyDirectoryVO doc = new TpStudyDirectoryVO();
            doc.setTrainingProjectId(tp.getId());
            doc.setItemType(TpDirectoryItemTypeEnum.DOCUMENT.getCode());
            doc.setItemName(TpDirectoryItemTypeEnum.DOCUMENT.getDescription());
            addStudyDirectoryItem(tp.getCompanyId(), tp.getSiteId(), doc);
        }
        if (tpCommentService.getTpCommentCount(tp.getId(), 0) > 0) {
            TpStudyDirectoryVO comment = new TpStudyDirectoryVO();
            comment.setTrainingProjectId(tp.getId());
            comment.setItemType(TpDirectoryItemTypeEnum.COMMENT.getCode());
            comment.setItemName(TpDirectoryItemTypeEnum.COMMENT.getDescription());
            addStudyDirectoryItem(tp.getCompanyId(), tp.getSiteId(), comment);
        }
        return true;
    }

    /**
     * 添加学习页目录项
     *
     * @param item
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TpStudyDirectoryVO addStudyDirectoryItem(Long companyId, Long siteId, TpStudyDirectoryVO item) {
        // 1.校验是否可重复添加
        if (!TpDirectoryItemTypeEnum.canBeAddRepeatedly(item.getItemType())) {
            if (tpStudyDirectoryService.existSameType(item.getTrainingProjectId(), item.getItemType())) {
                return null;
            }
        }
        // 2.获取最大排序值（默认添加到最后）
        TpStudyDirectory directory = new TpStudyDirectory();
        BeanUtils.copyProperties(item, directory);
        directory.setCompanyId(companyId);
        directory.setSiteId(siteId);
        Integer maxSort = tpStudyDirectoryService.getMaxSort(item.getTrainingProjectId());
        directory.setSort(maxSort + 1);
        directory.setId(idGenerator.generate());
        // 3.新增
        if (tpStudyDirectoryService.save(directory)) {
            TpStudyDirectoryVO response = new TpStudyDirectoryVO();
            BeanUtils.copyProperties(directory, response);
            return response;
        }
        return null;
    }

    /**
     * 添加介绍页目录
     *
     * @param item
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public TpIntroduceDirectoryVO addIntroduceDirectory(Long companyId, Long siteId, TpIntroduceDirectoryVO item) {
        TpIntroduceDirectory introduceDirectory = new TpIntroduceDirectory();
        introduceDirectory.setId(idGenerator.generate());
        introduceDirectory.setCompanyId(companyId);
        introduceDirectory.setSiteId(siteId);
        introduceDirectory.setTrainingProjectId(item.getTrainingProjectId());
        introduceDirectory.setItemType(item.getItemType());
        introduceDirectory.setItemName(item.getItemName());
        // 1.如果是来自学习目录，则需要校验是否已存在该项，或者学习目录是否存在
        if (TpDirectoryItemTypeEnum.isStudyDirectory(item.getItemType())) {
            if (item.getItemId() == null) {
                return null;
            }
            // 不存在学习目录
            TpStudyDirectory studyDirectory = tpStudyDirectoryService.getById(item.getItemId());
            if (studyDirectory == null) {
                return null;
            }
            // 介绍页目录中已存在同一个学习目录
            if (tpIntroduceDirectoryService.existSameItem(item.getTrainingProjectId(), item.getItemId())) {
                return null;
            }
            introduceDirectory.setItemId(item.getItemId());
            introduceDirectory.setItemName(studyDirectory.getItemName());
        }

        // 2.如果是咨询，校验是否存在重复
        if (TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode().equals(item.getItemType())) {
            if (tpIntroduceDirectoryService.existSameItemType(item.getTrainingProjectId(), item.getItemType())) {
                return null;
            }
        }

        if (TpDirectoryItemTypeEnum.CONSULT_ENTRANCE.getCode().equals(item.getItemType())) {
            introduceDirectory.setSort(Integer.MAX_VALUE);
        } else if (TpDirectoryItemTypeEnum.COMMENT.getCode().equals(item.getItemType())) {
            introduceDirectory.setSort(Integer.MAX_VALUE - 1);
        } else {
            Integer maxSort = tpIntroduceDirectoryService.getMaxSort(item.getTrainingProjectId());
            introduceDirectory.setSort(maxSort + 1);
        }
        // 新增
        if (tpIntroduceDirectoryService.save(introduceDirectory)) {
            TpIntroduceDirectoryVO response = new TpIntroduceDirectoryVO();
            BeanUtils.copyProperties(introduceDirectory, response);
            return response;
        }
        return null;
    }

    /**
     * 删除学习单元目录项
     *
     * @param trainingProjectId
     * @param directoryItemId
     * @return
     */
    private Boolean deleteStudyUnitItem(Long trainingProjectId, Long directoryItemId) {
        List<Long> tpPlanIds = tpPlanService.getTpPlanIdsByItem(trainingProjectId, directoryItemId);
        tpPlanService.deleteBatchById(tpPlanIds);
        // 删除时间条件
        tpPlanStudyTimeConditionService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除学习计划前置条件
        tpPlanConditionPreService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除学习计划完成条件
        tpPlanConditionPostService.deleteBatchByTpPlanId(tpPlanIds);
        // 删除学习计划和证书的绑定关系
        certificateClient.deleteBatchRelation(tpPlanIds);
        // 删除学习计划和资料的绑定关系
        documentRelationClient.deleteByRelationId(tpPlanIds);
        // 删除学习活动
        tpPlanActivityService.deleteBatchByTpPlan(tpPlanIds);
        return true;
    }
}
