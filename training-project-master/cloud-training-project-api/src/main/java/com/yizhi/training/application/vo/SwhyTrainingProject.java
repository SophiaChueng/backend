package com.yizhi.training.application.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SwhyTrainingProject {

    private Long id;

    private String workNum;

    private String userName;

    private String dept;

    private String projectName;

    private String projectStartTime;

    private String projectEndTime;

    private Long courseStudyDuration;

    private Float classHour;

    private Integer points;

    private Long trainingProjectId;

    private Long accountId;

    private Date finishDate;
}
