package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpCompleteConditionTypeEnum {

    ALL_PLAN(-1, "完成全部学习单元"), SPECIFIC_COUNT(0, "完成指定学习计划数量"), SPECIFIC_PLANS(1, "完成指定学习计划"),

    COUNT_AND_SPECIFIC(2, "指定数量及指定学习单元");

    private final Integer code;

    private final String description;
}
