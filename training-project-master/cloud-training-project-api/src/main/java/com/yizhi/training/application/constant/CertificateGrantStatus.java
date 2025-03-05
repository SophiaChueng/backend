package com.yizhi.training.application.constant;

/**
 * 证书获取状态
 *
 * @Author: shengchenglong
 * @Date: 2018/10/23 18:52
 */
public enum CertificateGrantStatus {

    UN_FINISHED("计划未完成，无法获得证书"),

    HAS_GOT("已经获取过该证书"),

    SUCCESS_GET("恭喜您！成功获得证书");

    private String value;

    CertificateGrantStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
