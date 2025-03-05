package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpDirectoryTypeEnum {

    STUDY_PAGE(0, "学习页"), INTRODUCE_PAGE(1, "介绍页");

    private final Integer code;

    private final String description;
}
