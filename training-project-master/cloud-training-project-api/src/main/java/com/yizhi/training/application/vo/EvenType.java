package com.yizhi.training.application.vo;

import java.util.Arrays;
import java.util.List;

/**
 *
 */

public enum EvenType {

    COURSE_UP(1L, Arrays.asList(1, 2)),//"课程上架通知"
    COURSE_FINISH(2L, Arrays.asList(1, 2)),//课程完成通知
    ENROLL_START(3L, Arrays.asList(1, 3, 4, 5)),//报名开始
    TRAINING_AUDIT_PASS(4L, Arrays.asList(1, 3, 4, 5)),//项目审核通过通知
    TRAINING_AUDIT_FAIL(5L, Arrays.asList(1, 3, 4, 5)),//项目审核不通过通知
    SIGN_SUCCESS(6L, Arrays.asList(1, 3, 4, 5)),//签到成功通知
    TRAINING_FINISH(7L, Arrays.asList(1, 3, 4, 5)),//项目完成通知
    ASSIGNMENT_AUDIT_FINISH(8L, Arrays.asList(1, 6, 7, 8)),//作业已被批阅通知
    EXAM_AUDIT_FINISH(9L, Arrays.asList(1, 9, 10, 11)),//考试已被批阅通知
    POINT_CHANGE(10L, Arrays.asList(1, 12, 13, 14));//积分变动原因

    //数据库message表的id
    private Long key;

    //数据库message_parameter表的field_type
    private List<Integer> fieldType;

    private EvenType(Long key, List<Integer> fieldType) {
        this.key = key;
        this.fieldType = fieldType;
    }

    public Long getKey() {
        return key;
    }

    public List<Integer> getName() {
        return fieldType;
    }

}
