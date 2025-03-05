package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpEnrollPayTypeEnum {

    FREE(0, "非付费"), VIRTUAL_COIN(1, "虚拟币"), EXCHANGE_CODE(2, "兑换码"),
    VIRTUAL_COIN_OR_EXCHANGE_CODE(3, "虚拟币/兑换码"), MEMBER(4, "会员");

    private final Integer code;

    private final String description;
}
