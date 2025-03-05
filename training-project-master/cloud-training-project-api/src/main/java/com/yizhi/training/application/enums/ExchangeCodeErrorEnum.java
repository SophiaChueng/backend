package com.yizhi.training.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Gyg
 */

@AllArgsConstructor
@Getter
public enum ExchangeCodeErrorEnum {
    THE_EXCHANGE_CODE_NOT_EXIST_OR_IS_BEING_USED("30000",
        "The exchange code does not exist or is being used. Please check and reenter",
        "兑换码不存在或正在被使用，请检查后重新输入"), THE_EXCHANGE_CODE_HAS_BEEN_USED("30001",
        "The exchange code has been used. Please change it to another exchange code",
        "兑换码已经被使用,请换一个兑换码重新兑换"),
    NO_NEED_TO_REDEEMER("30002", "You have already participated in this program, so there is no need to redeemer",
        "您已参加该资源,无需重复兑换"),
    YOU_DO_NOT_HAVE_ACCESS_TO_THIS_ITEM("30003", "You do not have access to this item and cannot redeem it",
        "该资源您没有访问权限，无法兑换"),
    PROJECT_DOES_NOT_EXIST_OR_HAS_EXPIRED("30004", "The resource does not exist, or the resource has expired",
        "资源不存在,或者资源已经过期"), INVALID_EXCHANGE_CODE("30005", "Invalid exchange code", "兑换码无效"),
    OUT_OF_THE_EXCHANGE_TIME_RANGE("30006", "Out of the exchange time range, cannot be redeemed",
        "不在兑换时间范围内,无法兑换!"),
    THE_PROJECT_NOT_EXIST_OR_THE_PROJECT_HAS_EXPIRED("30006", "The resource does not exist, or the resource has " +
        "expired",
        "资源不存在,或者资源已经过期"),

    ;

    private String code;

    private String msg;

    private String remark;
}
