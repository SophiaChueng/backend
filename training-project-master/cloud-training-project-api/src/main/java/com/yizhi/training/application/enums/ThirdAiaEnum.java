package com.yizhi.training.application.enums;

public enum ThirdAiaEnum {
    train_value_y("Y", "通过"), train_value_n("N", "不通过"), CALLBACK_TYPE_TRAIN("20", "培训项目"),
    CALLBACK_TYPE_PRODUCT("30", "产品课程"),

    //错误信息
    err_project_ids_null("aia.sync.pid.not.null", "同步数据的项目ids为空"),
    err_account_null("aia.sync.account.not.find", "未查询到用户信息"),
    err_course_null("aia.sync.course.not.find", "未查询到项目中的课程"),
    ;

    private String key;

    private String value;

    ThirdAiaEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
