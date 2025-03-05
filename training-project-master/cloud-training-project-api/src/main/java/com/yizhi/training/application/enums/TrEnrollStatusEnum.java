package com.yizhi.training.application.enums;

public enum TrEnrollStatusEnum {
    NO_ENROLL(10, "无需报名"), FREE_ENROLL(20, "免费报名"), PAY_ENROLL(30, "收费报名"),
    ;

    private Integer code;

    private String value;

    TrEnrollStatusEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
