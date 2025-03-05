package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpProcessStatusEnum {

    HAVE_NOT_START(1, "未开始"), IN_PROCESS(2, "进行中"), HAVE_END(3, "已结束");

    private final Integer code;

    private final String description;
}
