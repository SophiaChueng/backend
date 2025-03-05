package com.yizhi.training.application.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yizhi.application.orm.util.QueryUtil;
import com.yizhi.course.application.feign.AiaEbookClient;
import com.yizhi.course.application.vo.AiaEbookSearchVo;
import com.yizhi.course.application.vo.CourseVo;
import com.yizhi.training.application.domain.TpPlan;
import com.yizhi.training.application.domain.TpPlanActivity;
import com.yizhi.training.application.service.ITpPlanActivityService;
import com.yizhi.training.application.service.ITpPlanService;
import com.yizhi.training.application.vo.AiaEbookSearchRsp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aia/ebook")
public class AiaEbookController {

    @Autowired
    private ITpPlanActivityService tpPlanActivityService;

    @Autowired
    private ITpPlanService tpPlanService;

    @Autowired
    private AiaEbookClient aiaEbookClient;

    @GetMapping("/search")
    public List<AiaEbookSearchRsp> searchKey(@RequestParam("tid") Long tid,
        @RequestParam(value = "key", defaultValue = "未搜索") String key) {
        QueryWrapper<TpPlanActivity> ew = QueryUtil.condition(new TpPlanActivity());
        ew.eq("training_project_id", tid).eq("type", 0).eq("deleted", 0);
        List<TpPlanActivity> activities = tpPlanActivityService.list(ew);

        List<AiaEbookSearchRsp> aiaEbookSearchRspList = null;

        if (!CollectionUtils.isEmpty(activities)) {
            List<Long> courseIds = activities.stream().map(item -> item.getRelationId()).collect(Collectors.toList());
            AiaEbookSearchVo aiaEbookSearchVo = new AiaEbookSearchVo();
            aiaEbookSearchVo.setKey(key);
            aiaEbookSearchVo.setIds(courseIds);
            List<CourseVo> data = aiaEbookClient.getCoursesByIdsAndKey(aiaEbookSearchVo);

            List<TpPlan> tpPlans = tpPlanService.listByIds(
                activities.stream().map(planId -> planId.getTpPlanId()).collect(Collectors.toList()));

            Map<Long, CourseVo> dataMap =
                data.stream().collect(Collectors.toMap(courseVoKey -> courseVoKey.getId(), courseVoVal -> courseVoVal));

            Map<Long, String> tpPlanMap =
                tpPlans.stream().collect(Collectors.toMap(planKey -> planKey.getId(), planName -> planName.getName()));

            Map<Long, List<TpPlanActivity>> activitiesPlanMap =
                activities.stream().collect(Collectors.groupingBy(activitiesKey -> activitiesKey.getRelationId()));

            aiaEbookSearchRspList = new ArrayList<>();
            for (Map.Entry<Long, CourseVo> item : dataMap.entrySet()) {
                AiaEbookSearchRsp aiaEbookSearchRsp = new AiaEbookSearchRsp();
                Long courseId = item.getValue().getId();
                aiaEbookSearchRsp.setCourseId(courseId);
                aiaEbookSearchRsp.setCourseName(item.getValue().getName());
                aiaEbookSearchRsp.setAuthorUnit(item.getValue().getAuthorUnit());

                List<TpPlanActivity> activitiesObjs = activitiesPlanMap.get(item.getValue().getId());
                TpPlanActivity activitiesObj =
                    activitiesObjs.parallelStream().filter(flItem -> flItem.getRelationId().equals(courseId)).findAny()
                        .get();
                aiaEbookSearchRsp.setProjectId(activitiesObj.getTrainingProjectId());

                Long planId = activitiesObj.getTpPlanId();
                aiaEbookSearchRsp.setPlanId(planId);
                aiaEbookSearchRsp.setPlanName(tpPlanMap.get(planId));

                aiaEbookSearchRspList.add(aiaEbookSearchRsp);
            }

        }
        return aiaEbookSearchRspList;
    }
}
