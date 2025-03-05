package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpPlanConditionPostTypeEnum {

    ALL_ACTIVITY(-1, "完成全部学习活动"), SPECIFIC_COUNT(0, "完成指定数量"), SPECIFIC_ACTIVITIES(1, "完成指定学习活动"),

    COUNT_AND_SPECIFIC(2, "完成指定活动数并且完成指定学习活动");

    private final Integer code;

    private final String description;
}
