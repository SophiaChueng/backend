package com.yizhi.training.application.v2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum TpDirectoryItemTypeEnum {

    STUDY_UNIT(0, "学习单元"), BRIEF_INTRODUCE(1, "简介"), DOCUMENT(2, "资料"), COMMENT(3, "评论"),
    EXAM_AND_HOMEWORK(4, "考试与作业"), ANNOUNCEMENT(5, "公告"), FORUM(6, "讨论"), RICH_TEXT(7, "富文本"),
    CONSULT_ENTRANCE(8, "咨询"), INTRODUCE_RICH_TEXT(9, "介绍页富文本");

    public static final List<Integer> canBeAddRepeatedlyTypes =
        Arrays.asList(STUDY_UNIT.getCode(), RICH_TEXT.getCode());

    private final Integer code;

    private final String description;

    /**
     * 校验是否可重复添加
     *
     * @param code
     * @return
     */
    public static Boolean canBeAddRepeatedly(Integer code) {
        return canBeAddRepeatedlyTypes.contains(code);
    }

    /**
     * 判断是否是学习页的目录项
     *
     * @param code
     * @return
     */
    public static Boolean isStudyDirectory(Integer code) {
        // 判断是否是来自学习页的目录项
        TpDirectoryItemTypeEnum typeEnum = getDirectoryItemTypeEnum(code);
        if (typeEnum == null) {
            return false;
        }
        switch (typeEnum) {
            case STUDY_UNIT:
            case BRIEF_INTRODUCE:
            case DOCUMENT:
            case COMMENT:
            case EXAM_AND_HOMEWORK:
            case RICH_TEXT:
            case FORUM:
            case ANNOUNCEMENT:
                return true;
            default:
                return false;
        }

    }

    /**
     * @param code
     * @return
     */
    public static TpDirectoryItemTypeEnum getDirectoryItemTypeEnum(Integer code) {
        for (TpDirectoryItemTypeEnum typeEnum : TpDirectoryItemTypeEnum.values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
