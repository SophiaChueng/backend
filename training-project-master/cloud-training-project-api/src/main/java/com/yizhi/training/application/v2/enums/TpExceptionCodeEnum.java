package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpExceptionCodeEnum {

    /**
     * TODO 暂定
     *
     * 第1，2位，表示业务模块 10：公共 12:项目 （其他可添加）
     * 第3，4位，表示业务场景，00：公共， 01：项目，02：学习目录，03：学习计划/单元，04：学习活动，05：签到，06：报名, 07：证书 08:项目pro
     * 第5，6位，表示业务异常
     */

    PARAM_ERROR("100001", "参数错误"),

    /**
     * 01:项目
     */
    PROJECT_STATUS_IS_ON("120101", "项目状态已经是上架，无需上架"),
    PROJECT_STATUS_IS_NOT_ON("120102", "项目状态不是上架，无需下架"),
    PROJECT_NEED_PLAN("120103", "项目上架需要添加学习单元"),
    PROJECT_NEED_ACTIVITY("120104", "项目上架需要添加学习活动"),
    JUST_NOT_ON_SHELF_CAN_BE_EDIT("120105", "非上架状态的项目才能被修改"),

    CAN_NOT_DELETE_ON_SHELF("120106", "不可删除已上架项目"),

    CAN_NOT_DELETE_IN_PRO("120107", "不可删除关联了项目pro的项目"),

    NOT_EXISTS_PROJECT("120108", "项目不存在"),
    /**
     * 02：学习目录
     */
    STUDY_UNIT_COUNT_LIMIT("120201", "必须有至少一个学习单元"),

    /**
     * 04：学习活动
     */
    ACTIVITY_CAN_BE_EMPTY("120401", "学习活动不能为空"),

    /**
     * 07：证书
     */
    CERTIFICATE_IN_PROJECT_LIMIT("120701", "添加的证书中有证书在项目中已经被关联过了"),

    /**
     * 项目pro
     */
    TP_PRO_CONTAIN_TP("120801", "项目pro中有关联的项目"),

    /**
     * 学员端
     */

    TP_PRO_NOT_EXISTR_VISIABLE_TP("200001", "没有可参加的项目，请联系管理员");

    private final String code;

    private final String description;
}
