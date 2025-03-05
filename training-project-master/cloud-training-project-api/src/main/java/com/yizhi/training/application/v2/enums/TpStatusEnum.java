package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpStatusEnum {

    DRAFT(0, "草稿"), IN_USE(1, "启用"), DISABLE(2, "停用");

    private final Integer code;

    private final String description;
}
