package com.yizhi.training.application.v2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpProEnum {

    TP_DEFAULT(0, "常规项目"), TP_PRO(1, "项目PRO");

    private Integer code;

    private String msg;

}
