package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpPlanTimeConditionTypeEnum {

    NO_CONDITION(0, "无时间限制"), SPECIFIC_TIME(1, "指定时间段"), AFTER_START(2, "开始学习后n天"),
    AFTER_PRE_PLAN(3, "完成前置单元后n天");

    private final Integer code;

    private final String description;
}
