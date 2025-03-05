package com.yizhi.training.application.service.using;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.yizhi.application.orm.id.IdGenerator;
import com.yizhi.system.application.model.SiteOrgIdModel;
import com.yizhi.system.application.system.remote.AccountClient;
import com.yizhi.system.application.system.remote.RemoteDaIndicatorSystemClient;
import com.yizhi.system.application.vo.AccountVO;
import com.yizhi.system.application.vo.domain.Account;
import com.yizhi.system.application.vo.domain.DaIndicatorTpFinish;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.mapper.TpStudentProjectRecordMapper;
import com.yizhi.training.application.mapper.TrainingProjectMapper;
import com.yizhi.training.application.service.ITpAuthorizationRangeService;
import com.yizhi.util.application.num.NumUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName RemoteDaIndicatorServiceUsing
 * @Description TODO
 * @Author shengchenglong
 * @DATE 2019-10-10 15:01
 * @Version 1.0
 */
@Service
public class RemoteDaIndicatorServiceUsing {

    @Autowired
    private TrainingProjectMapper tpMapper;

    @Autowired
    private TpStudentProjectRecordMapper projectRecordMapper;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private RemoteDaIndicatorSystemClient systemClient;

    @Autowired
    private ITpAuthorizationRangeService tpAuthorizationRangeService;

    @Autowired
    private AccountClient accountClient;

    private Logger logger = LoggerFactory.getLogger(RemoteDaIndicatorServiceUsing.class);

    /**
     * dashboard 课程完成数
     *
     * @param siteId
     * @param startDate
     * @param endDate
     * @param processTime
     * @return
     */
    public boolean tpFinish(long siteId, Date startDate, Date endDate, Date processTime) {

        Set<Long> tpIds = null;
        //应参加人数
        Integer shouldPeople = 0;
        //完成人数
        Integer finishPeople = 0;
        //项目通过率
        String percentage = "0.00%";
        //项目环比
        String weekChain = "-";

        //上上周项目通过率
        String prePrePercentage = "0.00%";
        //上上周的完成人数
        Integer prePreFinishPeople = 0;

        //统计上周的项目完成情况
        List<TrainingProject> preTrainingProjects = tpMapper.allTpBySiteId(siteId, startDate, endDate);
        if (CollectionUtils.isNotEmpty(preTrainingProjects)) {
            tpIds = new HashSet<>(preTrainingProjects.size());

            for (TrainingProject a : preTrainingProjects) {
                tpIds.add(a.getId());
            }
            shouldPeople = getShouldJoinNum(preTrainingProjects, siteId);
            finishPeople = projectRecordMapper.getFinisheNum(tpIds, startDate, endDate);

            if (shouldPeople != null && finishPeople != null && shouldPeople > 0) {
                percentage = NumUtil.getPercentage(finishPeople, shouldPeople, 2);
            } else {
                logger.warn("站点:" + siteId + "获取的应参加人数有误！！！");
            }
        }

        //统计上上周的项目
        Date dateOfPreWeek = DateUtil.offset(startDate, DateField.DAY_OF_YEAR, -7);
        Date preStartDate = DateUtil.beginOfWeek(dateOfPreWeek);
        Date preEndDate = DateUtil.endOfWeek(dateOfPreWeek);
        //获取上上周的项目
        List<TrainingProject> prePreTrainingProjects = tpMapper.allTpBySiteId(siteId, preStartDate, preEndDate);
        if (CollectionUtils.isNotEmpty(prePreTrainingProjects)) {

            tpIds = new HashSet<>(prePreTrainingProjects.size());
            for (TrainingProject a : prePreTrainingProjects) {
                tpIds.add(a.getId());
            }
            shouldPeople = getShouldJoinNum(prePreTrainingProjects, siteId);
            prePreFinishPeople = projectRecordMapper.getFinisheNum(tpIds, preStartDate, preEndDate);

            if (shouldPeople != null && prePreFinishPeople != null && shouldPeople > 0) {
                prePrePercentage = NumUtil.getPercentage(prePreFinishPeople, shouldPeople, 2);
            } else {
                logger.warn("站点:" + siteId + "获取的应参加人数有误！！！");
            }
        }
        if (!prePrePercentage.equals("0.00%")) {
            BigDecimal nowPercentageN = new BigDecimal(percentage.replace("%", ""));
            BigDecimal prePercentageN = new BigDecimal(prePrePercentage.replace("%", ""));
            BigDecimal weekChainN = nowPercentageN.subtract(prePercentageN);
            weekChain = NumUtil.getPercentage(weekChainN, prePercentageN, 0);
        } else {
            weekChain = "-";
        }
        DaIndicatorTpFinish record = new DaIndicatorTpFinish();
        record.setEndDay(endDate);
        record.setFinishCount(finishPeople);
        record.setTpFinishPercentage(percentage);
        record.setId(idGenerator.generate());
        record.setProcessTime(processTime);
        record.setSiteId(siteId);
        record.setStartDay(startDate);
        record.setWeekChain(weekChain);
        return systemClient.insertTpFinish(record);
    }

    /**
     * 获取该站点下符合条件的应参加人数
     *
     * @param trainingProjects
     * @return
     */
    public Integer getShouldJoinNum(List<TrainingProject> trainingProjects, Long siteId) {
        Integer shouldJoinNum = 0;
        if (CollectionUtils.isNotEmpty(trainingProjects)) {
            for (TrainingProject project : trainingProjects) {

                if (project.getVisibleRange() == null) {
                    logger.info("visibleRange为null，不合理");
                    continue;
                }
                if (project.getVisibleRange() == 0) {

                    List<TpAuthorizationRange> rangeList = tpAuthorizationRangeService.listByBizId(project.getId());

                    if (CollectionUtils.isNotEmpty(rangeList)) {
                        //存放用户id
                        Set<Long> accountSet = new HashSet<>();
                        //获取部门list
                        List<Long> orgIdList = new ArrayList<>();

                        for (TpAuthorizationRange a : rangeList) {
                            //如果是指定范围为部门
                            if (a.getType() == 1) {
                                orgIdList.add(a.getRelationId());
                            }
                            if (a.getType() == 2) {
                                accountSet.add(a.getRelationId());
                            }
                        }
                        //通过部门id获取用户
                        SiteOrgIdModel model = new SiteOrgIdModel();
                        model.setSiteId(siteId);
                        model.setOrgIds(orgIdList);
                        List<Account> accounts = accountClient.getSiteOrgWithChildAccountNums(model);

                        if (CollectionUtils.isNotEmpty(accounts)) {
                            for (Account a : accounts) {
                                accountSet.add(a.getId());
                            }
                        }
                        shouldJoinNum += accountSet.size();
                    }
                } else if (project.getVisibleRange() == 1) {
                    List<AccountVO> list = accountClient.findBySiteId(siteId);
                    if (CollectionUtils.isNotEmpty(list)) {
                        shouldJoinNum += list.size();
                    }
                }
            }
        }
        return shouldJoinNum;
    }

}
