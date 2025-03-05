package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TpActivityTypeEnum {

    /**
     * code值来源于
     *
     * @see com.yizhi.util.application.constant.TpActivityType 整理到training-project中，便于管理
     */

    COURSE(0, "在线课程"), EXAM(1, "考试"), RESEARCH(2, "调研"), LIVE(3, "直播"), VOTE(4, "投票"),
    ASSIGNMENT(5, "作业"),
    /**
     * 证书不再作为活动
     */
    CERTIFICATE(6, "证书"), LINK(7, "外部链接"), ENROLL(8, "报名"), SIGN(9, "签到"), OFFLINE_COURSE(10, "线下课程"),
    CASE_ACTIVITY(11, "原创活动"), SELECT_CASE(12, "精选作品"), DOCUMENT(13, "资料"), FORUM_POST(14, "帖子"),
    PRACTICE(18, "智能陪练"), ANSWER_ACTIVITY(19, "答题活动"), COMMITMENT_LETTER(20, "承诺书");

    private final Integer code;

    private final String description;

    public static String getDescription(Integer code) {
        for (TpActivityTypeEnum typeEnum : TpActivityTypeEnum.values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum.description;
            }
        }
        return "";
    }

    public static TpActivityTypeEnum getTypeEnum(Integer code) {
        for (TpActivityTypeEnum typeEnum : TpActivityTypeEnum.values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
