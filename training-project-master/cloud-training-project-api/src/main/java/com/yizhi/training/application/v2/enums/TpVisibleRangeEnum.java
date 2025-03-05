package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpVisibleRangeEnum {

    SPECIFIC_USER(0, "指定用户可见"), PLATFORM_USER(1, "平台用户可见");

    private final Integer code;

    private final String description;
}
