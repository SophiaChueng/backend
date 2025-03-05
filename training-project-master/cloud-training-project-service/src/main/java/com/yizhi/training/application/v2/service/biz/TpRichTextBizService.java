package com.yizhi.training.application.v2.service.biz;

import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.training.application.domain.TpIntroduceDirectory;
import com.yizhi.training.application.domain.TpRichText;
import com.yizhi.training.application.domain.TpStudyDirectory;
import com.yizhi.training.application.v2.enums.TpDirectoryTypeEnum;
import com.yizhi.training.application.v2.service.TpIntroduceDirectoryService;
import com.yizhi.training.application.v2.service.TpRichTextService;
import com.yizhi.training.application.v2.service.TpStudyDirectoryService;
import com.yizhi.training.application.v2.vo.TpRichTextVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TpRichTextBizService {

    @Autowired
    private TpRichTextService tpRichTextService;

    @Autowired
    private TpStudyDirectoryService tpStudyDirectoryService;

    @Autowired
    private TpIntroduceDirectoryService tpIntroduceDirectoryService;

    @Autowired
    private IdGenerator idGenerator;

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateStudyPageRichText(TpRichTextVO request) {
        if (StringUtils.isNotBlank(request.getTitle())) {
            TpStudyDirectory richTextItem = new TpStudyDirectory();
            richTextItem.setId(request.getDirectoryItemId());
            richTextItem.setItemName(request.getTitle());
            tpStudyDirectoryService.updateById(richTextItem);
        }

        RequestContext context = ContextHolder.get();

        TpRichText text = new TpRichText();
        BeanUtils.copyProperties(request, text);
        text.setDirectoryType(TpDirectoryTypeEnum.STUDY_PAGE.getCode());
        if (tpRichTextService.existRichText(request.getTrainingProjectId(), TpDirectoryTypeEnum.STUDY_PAGE.getCode(),
            request.getDirectoryItemId())) {
            return tpRichTextService.updateRichText(text);
        } else {
            text.setId(idGenerator.generate());
            text.setCompanyId(context.getCompanyId());
            text.setSiteId(context.getSiteId());
            return tpRichTextService.save(text);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateIntroducePageRichText(TpRichTextVO request) {
        if (StringUtils.isNotBlank(request.getTitle())) {
            TpIntroduceDirectory richTextItem = new TpIntroduceDirectory();
            richTextItem.setId(request.getDirectoryItemId());
            richTextItem.setItemName(request.getTitle());
            tpIntroduceDirectoryService.updateById(richTextItem);
        }

        RequestContext context = ContextHolder.get();
        TpRichText text = new TpRichText();
        BeanUtils.copyProperties(request, text);
        text.setDirectoryType(TpDirectoryTypeEnum.INTRODUCE_PAGE.getCode());
        if (tpRichTextService.existRichText(request.getTrainingProjectId(),
            TpDirectoryTypeEnum.INTRODUCE_PAGE.getCode(), request.getDirectoryItemId())) {
            return tpRichTextService.updateRichText(text);
        } else {
            text.setId(idGenerator.generate());
            text.setCompanyId(context.getCompanyId());
            text.setSiteId(context.getSiteId());
            return tpRichTextService.save(text);
        }
    }

    public TpRichTextVO getRichText(Long trainingProjectId, Integer directoryType, Long directoryItemId) {
        TpRichText text = tpRichTextService.getRichText(trainingProjectId, directoryType, directoryItemId);
        if (text == null) {
            return null;
        }
        TpRichTextVO textVO = new TpRichTextVO();
        BeanUtils.copyProperties(text, textVO);
        return textVO;
    }
}
