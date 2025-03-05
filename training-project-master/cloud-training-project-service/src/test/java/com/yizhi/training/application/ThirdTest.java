package com.yizhi.training.application;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.context.ContextHolder;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.system.application.vo.third.ThirdCallbackConfigVO;
import com.yizhi.training.application.service.ThirdAiaSyncTrainDataService;
import com.yizhi.training.application.vo.third.AiaProductVO;
import com.yizhi.training.application.vo.third.AiaTrainingVO;
import com.yizhi.training.application.vo.third.SyncTrainingReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest

public class ThirdTest {

    @Autowired
    ThirdAiaSyncTrainDataService thirdAiaSyncDataService;

    @Test
    public void thirdTproductList() {
        RequestContext context = new RequestContext();
        context.setAccountId(1314L);
        context.setCompanyId(1314L);
        context.setSiteId(1472815901869916160L);
        context.setAdmin(true);
        ContextHolder.set(context);

        String time = "2022-01-04 00:00:00";
        String time2 = "2022-01-08 00:00:00";
        DateTime startTime = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");
        DateTime endTime = DateUtil.parse(time2, "yyyy-MM-dd HH:mm:ss");
        DateTime start = DateUtil.beginOfDay(startTime);
        DateTime end = DateUtil.beginOfDay(endTime);
        SyncTrainingReq syncTrainingReq = new SyncTrainingReq();
        syncTrainingReq.setCompanyId(1314L);
        syncTrainingReq.setSiteId(1472815901869916160L);
        syncTrainingReq.setPageNo(1);
        syncTrainingReq.setPageSize(10);
        syncTrainingReq.setStartTime(start);
        syncTrainingReq.setEndTime(end);
        syncTrainingReq.setConfigVOList(getList());
        syncTrainingReq.setRootOrgId(1472815407273394176L);

        List<ThirdCallbackConfigVO> configVOList = new ArrayList<>();
        ThirdCallbackConfigVO vo1 = new ThirdCallbackConfigVO();
        vo1.setName("CHO-CPCZY-product_jj-12-20210305");
        vo1.setRelationId(1473224171507953664L);
        ThirdCallbackConfigVO vo2 = new ThirdCallbackConfigVO();
        vo2.setName("CHO-CPCZY-product_jj-31-20211110");
        vo2.setRelationId(1473139393551151104L);
        ThirdCallbackConfigVO vo3 = new ThirdCallbackConfigVO();
        vo3.setName("");
        vo3.setRelationBizContent("[{\"relation_id\":1472831419758686208,\"name\":\"CHO-1234567\"}]");
        vo3.setRelationId(1472831356454055936L);
        configVOList.add(vo1);
        configVOList.add(vo2);
        configVOList.add(vo3);
        syncTrainingReq.setConfigVOList(configVOList);
        Page<AiaProductVO> trainingStudyPassedRecords =
            thirdAiaSyncDataService.getProductStudyPassedRecords(syncTrainingReq);


    }

    @Test
    public void thirdTpList() {
        //        RequestContext context = new RequestContext();
        //        context.setAccountId(1314L);
        //        context.setCompanyId(1314L);
        //        context.setSiteId(1050678553944940544L);
        //        context.setAdmin(true);
        //        ContextHolder.set(context);

        String time = "2022-01-04 00:00:00";
        String time2 = "2022-01-08 00:00:00";
        DateTime startTime = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");
        DateTime endTime = DateUtil.parse(time2, "yyyy-MM-dd HH:mm:ss");
        DateTime start = DateUtil.beginOfDay(startTime);
        DateTime end = DateUtil.beginOfDay(endTime);
        SyncTrainingReq syncTrainingReq = new SyncTrainingReq();
        syncTrainingReq.setCompanyId(1314L);
        syncTrainingReq.setSiteId(1472815901869916160L);
        syncTrainingReq.setPageNo(1);
        syncTrainingReq.setPageSize(10);
        syncTrainingReq.setProjectIds(Arrays.asList(1472842653459591168L));
        syncTrainingReq.setStartTime(start);
        syncTrainingReq.setEndTime(end);
        syncTrainingReq.setConfigVOList(getList());
        syncTrainingReq.setRootOrgId(1472815407273394176L);
        Page<AiaTrainingVO> trainingStudyPassedRecords =
            thirdAiaSyncDataService.getTrainingStudyPassedRecords(syncTrainingReq);


    }

    private List<ThirdCallbackConfigVO> getList() {

        List<ThirdCallbackConfigVO> list = new ArrayList<>();
        ThirdCallbackConfigVO vo = new ThirdCallbackConfigVO();
        vo.setProjectId(1472842653459591168L);
        vo.setName("1BASETRAIN");
        vo.setId(10L);
        vo.setCallbackType(20);
        list.add(vo);

        return list;
    }

}
