package com.yizhi.training.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TrainingCommonEnums {

    USED(1, "已使用"), UN_USED(0, "未使用"), DELETED(1, "已删除"), UN_DELETED(0, "未删除"),

    COURSE(0, "课程"), EXAM(1, "考试"), RESEARCH(2, "调研"), LIVE(3, "直播"), HOMEWORK(5, "作业"),
    OFFLINE_COURSES(10, "线下课程"), ACTIVITY(11, "原创活动"), WORKS(12, "精选作品"), POST(14, "帖子"),
    PROJECT(15, "项目"), INTELLIGENT_SPARRING(16, "智能陪练"), ALBUM(17, "专辑"),

    ;

    private Integer code;

    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
