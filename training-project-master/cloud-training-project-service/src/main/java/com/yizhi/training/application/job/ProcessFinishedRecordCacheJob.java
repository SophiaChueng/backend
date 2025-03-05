package com.yizhi.training.application.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.cache.CacheNamespace;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.system.application.request.SiteUnselectedResq;
import com.yizhi.system.application.system.remote.SiteClient;
import com.yizhi.system.application.vo.SiteVO;
import com.yizhi.training.application.domain.TpPlanActivityViewRecord;
import com.yizhi.training.application.domain.TpStudentActivityRecord;
import com.yizhi.training.application.domain.TpStudentPlanRecord;
import com.yizhi.training.application.domain.TpStudentProjectRecord;
import com.yizhi.training.application.service.ITpStudentActivityRecordService;
import com.yizhi.training.application.service.ITpStudentPlanRecordService;
import com.yizhi.training.application.service.ITpStudentProjectRecordService;
import com.yizhi.training.application.service.TpPlanActivityViewRecordService;
import com.yizhi.util.application.domain.BizResponse;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @ClassName ProcessFinishedRecordCacheJob
 * @Author shengchenglong
 * @Date 2019-04-01 21:15
 * @Version 1.0
 **/
@Service
public class ProcessFinishedRecordCacheJob {

    public static final String finished = "1";

    public static final String unFinishe = "0";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessFinishedRecordCacheJob.class);

    public int pageSize = 20000;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ITpStudentActivityRecordService tpStudentActivityRecordService;

    @Autowired
    private ITpStudentPlanRecordService tpStudentPlanRecordService;

    @Autowired
    private ITpStudentProjectRecordService tpStudentProjectRecordService;

    @Autowired
    private TpPlanActivityViewRecordService tpPlanActivityViewRecordService;
    @Autowired
    private SiteClient siteClient;


    /**
     * 处理已完成
     *
     * @param subDate
     */
    public void processActivityFinished(Date subDate, Long siteId) {
        processActivity(subDate, true, siteId);
    }

    /**
     * 处理未完成
     *
     * @param subDate
     */
    public void processActivityUnfinished(Date subDate) {
        processActivity(subDate, false, null);
    }

    /**
     * 处理计划 cache
     *
     * @param subDate
     */
    public void processPlan(Date subDate) {
        QueryWrapper<TpStudentPlanRecord> ew = new QueryWrapper<>();
        if (subDate != null) {
            subDate = DateUtils.truncate(subDate, Calendar.DATE);
            ew.ge("finish_date", subDate);
        }

        List<TpStudentPlanRecord> list;
        int current = 0;
        Page<TpStudentPlanRecord> page = new Page<>(current, pageSize);
        // 如果是第一次 或者 当前页数<总共页数
        while (page.getCurrent() <= page.getPages() || current == 0) {
            page.setCurrent(++current);
            page = tpStudentPlanRecordService.page(page, ew);
            list = page.getRecords();
            if (!CollectionUtils.isEmpty(list)) {
                LOGGER.info("----------------开始 处理 已完成 计划 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                    subDate, page.getPages(), page.getCurrent());
                list.forEach(item -> redisCache.hset(CacheNamespace.TP_PLAN_FINISHED.concat(String.valueOf(item.getAccountId())),
                    String.valueOf(item.getTpPlanId()), finished));
                LOGGER.info("----------------成功 处理 已完成 计划 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                    subDate, page.getPages(), page.getCurrent());
            }
        }
    }

    /**
     * 处理项目 cache
     */
    public void processTrainingProject(Date subDate) {
        QueryWrapper<TpStudentProjectRecord> ew = new QueryWrapper<>();
        if (subDate != null) {
            subDate = DateUtils.truncate(subDate, Calendar.DATE);
            ew.ge("finish_date", subDate);
        }

        List<TpStudentProjectRecord> list;
        int current = 0;
        Page<TpStudentProjectRecord> page = new Page<>(current, pageSize);
        // 如果是第一次 或者 当前页数<总共页数
        while (page.getCurrent() <= page.getPages() || current == 0) {
            page.setCurrent(++current);
            page = tpStudentProjectRecordService.page(page, ew);
            list = page.getRecords();
            if (!CollectionUtils.isEmpty(list)) {
                LOGGER.info("----------------开始 处理 已完成 项目 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                    subDate, page.getPages(), page.getCurrent());
                list.forEach(item -> redisCache.hset(
                    CacheNamespace.TP_TRAININGPROJECT_FINISHED.concat(String.valueOf(item.getAccountId())),
                    String.valueOf(item.getTrainingProjectId()), finished));
                LOGGER.info("----------------成功 处理 已完成 项目 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                    subDate, page.getPages(), page.getCurrent());
            }
        }
    }

    /**
     * 处理活动点击 cache
     *
     * @param subDate
     */
    public void processClicked(Date subDate) {
        BizResponse<List<SiteVO>> response = siteClient.getAllSiteList(new SiteUnselectedResq());
        List<SiteVO> siteVOList = response.getData();
        if (!CollectionUtils.isEmpty(siteVOList)){
            for (SiteVO siteVO : siteVOList) {
                Long companyId = siteVO.getCompanyId();
                Long siteVOId = siteVO.getId();
                if (null == companyId){
                    continue;
                }
                try {
                    TpPlanActivityViewRecord query = new TpPlanActivityViewRecord();
                    query.setCompanyId(companyId);
                    query.setSiteId(siteVOId);
                    QueryWrapper<TpPlanActivityViewRecord> ew = new QueryWrapper<>(query);
                    if (subDate != null) {
                        subDate = DateUtils.truncate(subDate, Calendar.DATE);
                        ew.ge("time", subDate);
                    }
                    List<TpPlanActivityViewRecord> list;
                    int current = 0;
                    Page<TpPlanActivityViewRecord> page = new Page<>(current, pageSize);
                    // 如果是第一次 或者 当前页数<总共页数
                    while (page.getCurrent() <= page.getPages() || current == 0) {
                        page.setCurrent(++current);
                        page = tpPlanActivityViewRecordService.page(page, ew);
                        list = page.getRecords();
                        if (!CollectionUtils.isEmpty(list)) {
                            LOGGER.info(
                                    "----------------开始 处理 已完成 活动点击 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                                    subDate, page.getPages(), page.getCurrent());
                            list.forEach(item -> redisCache.hset(CacheNamespace.TP_ACTIVITY_CLICKED.concat(String.valueOf(item.getAccountId())),
                                    String.valueOf(item.getTpPlanActivityId()).concat("_")
                                            .concat(String.valueOf(item.getTrainingProjectId())), finished));
                            LOGGER.info(
                                    "----------------成功 处理 已完成 活动点击 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                                    subDate, page.getPages(), page.getCurrent());
                        }
                    }
                }catch (Exception e){
                    LOGGER.error("处理活动TpPlanActivityViewRecord点击 cache异常;companyId={},siteId={}",companyId,siteVOId,e);
                }

            }
        }

    }

    /**
     * 处理活动 cache
     *
     * @param subDate
     * @param isFinished
     */
    private void processActivity(Date subDate, boolean isFinished, Long siteId) {
        //  如果siteId为空，则查询所有的公司和站点信息，循环处理
        if (null == siteId){
            BizResponse<List<SiteVO>> response = siteClient.getAllSiteList(new SiteUnselectedResq());
            List<SiteVO> siteVOList = response.getData();
            if (!CollectionUtils.isEmpty(siteVOList)){

                for (SiteVO siteVO : siteVOList) {
                    Long siteVOId = siteVO.getId();
                    Long companyId = siteVO.getCompanyId();
                    try {
                        QueryWrapper<TpStudentActivityRecord> ew = new QueryWrapper<>();
                        ew.eq("finished", isFinished ? 1 : 0);
                        String keyPrefix = isFinished ? CacheNamespace.TP_ACTIVITY_FINISHED : CacheNamespace.TP_ACTIVITY_UNFINISHED;
                        if (subDate != null) {
                            subDate = DateUtils.truncate(subDate, Calendar.DATE);
                            ew.ge("finish_date", subDate);
                        }
                        ew.eq("site_id", siteVOId);
                        ew.eq("company_id", companyId);

                        List<TpStudentActivityRecord> list;
                        int current = 0;
                        Page<TpStudentActivityRecord> page = new Page<>(current, pageSize);
                        // 如果是第一次 或者 当前页数<总共页数
                        while (page.getCurrent() <= page.getPages() || current == 0) {
                            page.setCurrent(++current);
                            page = tpStudentActivityRecordService.page(page, ew);
                            list = page.getRecords();
                            if (!CollectionUtils.isEmpty(list)) {
                                LOGGER.info("----------------开始 处理 {} 活动 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                                        isFinished ? "已完成" : "未通过", subDate, page.getPages(), page.getCurrent());
                                list.forEach(item -> redisCache.hset(keyPrefix.concat(String.valueOf(item.getAccountId())),
                                        String.valueOf(item.getRelationId()), finished));
                                LOGGER.info("----------------成功 处理 {} 活动 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                                        isFinished ? "已完成" : "未通过", subDate, page.getPages(), page.getCurrent());
                            }
                        }

                    }catch (Exception e){
                        LOGGER.error("处理活动记录异常siteId={}",siteVOId,e);
                    }

                }

            }
        }else {

            QueryWrapper<TpStudentActivityRecord> ew = new QueryWrapper<>();
            ew.eq("finished", isFinished ? 1 : 0);
            String keyPrefix = isFinished ? CacheNamespace.TP_ACTIVITY_FINISHED : CacheNamespace.TP_ACTIVITY_UNFINISHED;
            if (subDate != null) {
                subDate = DateUtils.truncate(subDate, Calendar.DATE);
                ew.ge("finish_date", subDate);
            }
            if (siteId != null) {
                ew.eq("site_id", siteId);
            }

            List<TpStudentActivityRecord> list;
            int current = 0;
            Page<TpStudentActivityRecord> page = new Page<>(current, pageSize);
            // 如果是第一次 或者 当前页数<总共页数
            while (page.getCurrent() <= page.getPages() || current == 0) {
                page.setCurrent(++current);
                page = tpStudentActivityRecordService.page(page, ew);
                list = page.getRecords();
                if (!CollectionUtils.isEmpty(list)) {
                    LOGGER.info("----------------开始 处理 {} 活动 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                            isFinished ? "已完成" : "未通过", subDate, page.getPages(), page.getCurrent());
                    list.forEach(item -> redisCache.hset(keyPrefix.concat(String.valueOf(item.getAccountId())),
                            String.valueOf(item.getRelationId()), finished));
                    LOGGER.info("----------------成功 处理 {} 活动 cache，截止日期：{}，总页数：{}，当前页：{}--------------",
                            isFinished ? "已完成" : "未通过", subDate, page.getPages(), page.getCurrent());
                }
            }
        }
    }

}
