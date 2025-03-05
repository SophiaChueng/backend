package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpEnrollStatusEnum {

    NO_ENROLL(10, "无需报名"), FREE_ENROLL(20, "免费报名"), PAY_ENROLL(30, "收费报名"),
    ;

    private final Integer code;

    private final String description;
}
