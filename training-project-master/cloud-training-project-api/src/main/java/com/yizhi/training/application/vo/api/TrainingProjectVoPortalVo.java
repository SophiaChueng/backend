package com.yizhi.training.application.vo.api;

import com.yizhi.training.application.vo.domain.TrainingProjectVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TrainingProjectVoPortalVo extends TrainingProjectVo {

    /**
     * 活动数量  只要课程的
     */
    private Integer activityNum;

    /**
     * 课程学时
     */
    private BigDecimal studyTime;

    /**
     * 课程id
     */
    private List<Long> listCourseIds;

}
