package com.yizhi.training.application.v2.service.biz;

import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpAnnouncement;
import com.yizhi.training.application.v2.service.TpAnnouncementService;
import com.yizhi.training.application.v2.vo.TpAnnouncementVO;
import com.yizhi.training.application.v2.vo.base.PageDataVO;
import com.yizhi.training.application.v2.vo.request.SearchAnnouncementVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TpAnnouncementBizService {

    @Autowired
    private TpAnnouncementService tpAnnouncementService;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 查询公告列表（分页）
     *
     * @param request
     * @return
     */
    public PageDataVO<TpAnnouncementVO> getTpAnnouncementList(SearchAnnouncementVO request) {
        PageDataVO<TpAnnouncementVO> pageData = new PageDataVO<>();
        pageData.setPageNo(request.getPageNo());
        pageData.setPageSize(request.getPageSize());

        Long trainingProjectId = request.getTrainingProjectId();

        Integer total = tpAnnouncementService.getAnnouncementCount(trainingProjectId);
        pageData.setTotal(total);
        if (total == 0) {
            return pageData;
        }
        List<TpAnnouncement> list =
            tpAnnouncementService.getAnnouncements(trainingProjectId, request.getPageNo(), request.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return pageData;
        }
        List<TpAnnouncementVO> records = new ArrayList<>();
        list.forEach(o -> {
            TpAnnouncementVO vo = new TpAnnouncementVO();
            BeanUtils.copyProperties(o, vo);
            records.add(vo);
        });
        pageData.setRecords(records);
        return pageData;
    }

    /**
     * 添加公告
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean addTpAnnouncement(TpAnnouncementVO request) {
        RequestContext context = ContextHolder.get();
        if (context == null || request.getTrainingProjectId() == null) {
            return false;
        }
        Long companyId = context.getCompanyId();
        Long siteId = context.getSiteId();

        TpAnnouncement announcement = new TpAnnouncement();
        announcement.setId(idGenerator.generate());
        announcement.setCompanyId(companyId);
        announcement.setSiteId(siteId);
        announcement.setTrainingProjectId(request.getTrainingProjectId());

        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setPublishTime(request.getPublishTime());

        Integer maxSort = tpAnnouncementService.getMaxSort(request.getTrainingProjectId());
        announcement.setSort(maxSort + 1);

        return tpAnnouncementService.save(announcement);
    }

    /**
     * 更新
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean editTpAnnouncement(TpAnnouncementVO request) {
        if (request.getId() == null) {
            return false;
        }
        TpAnnouncement announcement = new TpAnnouncement();
        announcement.setId(request.getId());
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setPublishTime(request.getPublishTime());

        return tpAnnouncementService.updateById(announcement);
    }

    /**
     * 查询单个公告内容
     *
     * @param announcementId
     * @return
     */
    public TpAnnouncementVO getTpAnnouncement(Long announcementId) {
        TpAnnouncement announcement = tpAnnouncementService.getById(announcementId);
        TpAnnouncementVO announcementVO = new TpAnnouncementVO();
        BeanUtils.copyProperties(announcement, announcementVO);
        return announcementVO;
    }

    /**
     * 拖动排序
     *
     * @param trainingProjectId
     * @param moveId
     * @param preId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTpAnnouncementSort(Long trainingProjectId, Long moveId, Long preId) {
        Integer preSort = 0;
        if (preId != null && preId > 0) {
            preSort = tpAnnouncementService.getSort(preId);
        }
        TpAnnouncement item = new TpAnnouncement();
        item.setId(moveId);
        item.setSort(preSort);
        tpAnnouncementService.addSortValue(trainingProjectId, preSort);
        return tpAnnouncementService.updateById(item);
    }

    /**
     * 删除公告
     *
     * @param announcementId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeTpAnnouncement(Long announcementId) {
        TpAnnouncement tpAnnouncement = new TpAnnouncement();
        tpAnnouncement.setId(announcementId);
        return tpAnnouncementService.removeById(tpAnnouncement);
    }

}
