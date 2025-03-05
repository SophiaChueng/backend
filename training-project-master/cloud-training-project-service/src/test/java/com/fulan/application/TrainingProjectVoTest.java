package com.fulan.application;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yizhi.core.application.cache.RedisCache;
import com.yizhi.core.application.context.RequestContext;
import com.yizhi.core.application.event.EventWrapper;
import com.yizhi.training.application.domain.TpAuthorizationRange;
import com.yizhi.training.application.domain.TrainingProject;
import com.yizhi.training.application.event.TpEventHandler;
import com.yizhi.training.application.job.Process2PlatTrainingProject;
import com.yizhi.training.application.job.ProcessFinishedRecordCacheJob;
import com.yizhi.training.application.mapper.TpAuthorizationRangeMapper;
import com.yizhi.training.application.mapper.TpCommentMapper;
import com.yizhi.training.application.mapper.TpStudentEnrollPassedMapper;
import com.yizhi.training.application.mapper.TrainingProjectMapper;
import com.yizhi.training.application.model.BaseModel;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.service.ITrainingProjectService;
import com.yizhi.training.application.vo.api.TrainingProjectParamVo;
import com.yizhi.training.application.vo.manage.TrainingProjectStepThreeVo;
import com.yizhi.util.application.event.TrainingProjectEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.clients.jedis.util.SafeEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @Author: shengchenglong
 * @Date: 2018/3/28 16:01
 */
@SpringBootTest

public class TrainingProjectVoTest {

    @Autowired
    private TrainingProjectMapper trainingProjectMapper;

    @Autowired
    private ITrainingProjectService trainingProjectService;

    @Autowired
    private TpAuthorizationRangeMapper tpAuthorizationRangeMapper;

    @Autowired
    private TpStudentEnrollPassedMapper tpStudentEnrollPassedMapper;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private TpCommentMapper tpCommentMapper;

    @Autowired
    private Process2PlatTrainingProject process2PlatTrainingProject;

    @Autowired
    private ProcessFinishedRecordCacheJob processFinishedRecordCacheJob;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private TpEventHandler tpEventHandler;

    @Autowired
    private RedisCache redisCache;

    @Test
    public void joinSchema() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(
            "/Users/shengchenglong/Documents/IdeaProjects/wmy_workspace/training-project/cloud-training-project/src" + "/test/java/com/fulan/application/relationIds")));
        List<Long> relationIds = new ArrayList<>();
        String s = null;
        while ((s = br.readLine()) != null) {
            relationIds.add(Long.valueOf(s));
        }

        Long start = System.currentTimeMillis();

        //        List<TrainingProjectVo> list = trainingProjectMapper.apiPageList(1111111111111111111L,
        //        551893122703726319L, relationIds, new Date(), new RowBounds(0, 50));



        //        for (TrainingProjectVo tp : list) {
        //
        //        }
    }

    @Test
    public void sepratorQuery() {

    }

    @Test
    public void test1() throws IOException {
        //        trainingProjectMapper.apiPageList(0L, 0L, new Date());
        BaseModel<TrainingProjectParamVo> model = new BaseModel<>();
        TrainingProjectParamVo vo = new TrainingProjectParamVo();
        vo.setNow(new Date());
        vo.setPageNo(1);
        vo.setPageSize(10);
        model.setObj(vo);

        RequestContext context = new RequestContext();
        context.setAccountId(0L);
        context.setOrgId(0L);
        context.setSiteId(0L);
        //        model.setContext(context);
        trainingProjectService.apiPageList(model);
    }

    @Test
    public void test2() {
        BaseModel model = new BaseModel();

        RequestContext context = new RequestContext();
        context.setAccountId(0L);
        context.setOrgId(0L);
        context.setSiteId(0L);

        Page<TrainingProject> page = new Page(1, 10);

        //        model.setContext(context);
        model.setObj(page);

        page = trainingProjectService.apiPageListNoCondition(model);

    }

    @Test
    public void test3() throws Exception {
        Set<Long> tpIds = new HashSet<>();

        BufferedReader br = new BufferedReader(new FileReader(new File(
            "/Users/shengchenglong/Documents/IdeaProjects/wmy_workspace/training-project/cloud-training-project/src" + "/test/java/com/fulan/application/relationIds")));
        List<Long> relationIds = new ArrayList<>();
        String s = null;
        while ((s = br.readLine()) != null) {
            relationIds.add(Long.valueOf(s));
        }

        Long start = System.currentTimeMillis();

        // 可见范围内
        List<Long> visibleRangeIds = tpAuthorizationRangeMapper.selectBizIdByRelationId(relationIds, 0L);



        // 报名通过
        List<Long> passEnrollIds = tpStudentEnrollPassedMapper.selectTpIds(551893122703726319L);



        visibleRangeIds.addAll(passEnrollIds);

        //        List<TrainingProjectVo> result = trainingProjectMapper.apiPageList2(visibleRangeIds, new Date(),
        //        1111111111111111111L, new RowBounds(0, 50));



    }

    @Test
    public void test4() throws ParseException {

        List<TpAuthorizationRange> list = new ArrayList<>();
        TpAuthorizationRange range = new TpAuthorizationRange();
        range.setBizId(111L);
        range.setType(1);
        range.setRelationId(111L);
        range.setId(111L);

        list.add(range);
        tpAuthorizationRangeMapper.batchInsert(list);
    }

    @Test
    public void testtt() {
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        byte[] bytes = redisConnection.hGet(SafeEncoder.encode("COMPANY_STRUCTURE"), SafeEncoder.encode("91"));

    }

    @Test
    public void saveStepThreeTest() {
        BaseModel<TrainingProjectStepThreeVo> model = new BaseModel<>();
    }

    @Test
    public void getTpDetailTest() throws Exception {

        //        trainingProjectService.getTpDetail(1012177669667659776L, 1314L, new Date(), false);
    }

    @Test
    public void eventTest() throws Exception {
        //        id=1026397573038694400, type=0, accountId=1314, now=Tue Aug 07 10:36:04 CST 2018,
        //                examScore=null, trainingProjectId=null, courseSeconds=0, courseFinished=true, siteId=1314

        EventWrapper ew = new EventWrapper(1026397573038694400L,
            TrainingProjectEvent.getInstanceCourse(1026397573038694400L, 1314L, new Date(), Long.valueOf(0), true,
                1314L));
        tpEventHandler.handle(ew);
    }

    @Test
    public void getTpDetail() throws ParseException {
        //        trainingProjectService.getTpDetail(1045878214633119744L, 1314L, new Date(), false);
        //        tpPlanService.viewDetail(1044426182201925632L);
    }

    @Test
    public void testt() {
        //        process2PlatTrainingProject.startProcess(null);
        processFinishedRecordCacheJob.processActivityFinished(null, null);
        processFinishedRecordCacheJob.processActivityUnfinished(null);
        processFinishedRecordCacheJob.processClicked(null);
        processFinishedRecordCacheJob.processPlan(null);
        processFinishedRecordCacheJob.processTrainingProject(null);
    }

/*    @Test
    public void redisTest() {
//        redisCache.getRedisTemplate().opsForZSet().add("queue:task:scheduler:trainingProject", "method1", System
.nanoTime());
//        Long score = redisCache.getRedisTemplate().opsForZSet().rank("queue:task:scheduler:trainingProject",
"method1");
        Long size = null;
        while ((size = redisCache.getRedisTemplate().opsForZSet().size("queue:task:scheduler:trainingProject")) > 0) {

            Set set = redisCache.getRedisTemplate().opsForZSet().range("queue:task:scheduler:trainingProject", 0L, 0L);

            redisCache.getRedisTemplate().opsForZSet().removeRange("queue:task:scheduler:trainingProject", 0L, 0L);

        }


    }*/

    @Test
    public void logicDelTest() {
        TpAuthorizationRange range = new TpAuthorizationRange();
        range.setName("hahahahahahah");
        range.delete(new QueryWrapper(range));
    }

}
