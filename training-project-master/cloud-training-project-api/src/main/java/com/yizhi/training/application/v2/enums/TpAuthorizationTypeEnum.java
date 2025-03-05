package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpAuthorizationTypeEnum {

    ORGANIZATION(1, "部门"), USER(2, "用户");

    private final Integer code;

    private final String description;
}
