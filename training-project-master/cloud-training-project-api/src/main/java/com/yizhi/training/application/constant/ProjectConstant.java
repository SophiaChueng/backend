package com.yizhi.training.application.constant;

/**
 * 培训项目常量
 *
 * @Author: shengchenglong
 * @Date: 2018/3/26 14:47
 */
public interface ProjectConstant {

    /**
     * 删除状态：未删
     */
    Integer DELETED_NO = 0;

    /**
     * 删除状态：已删
     */
    Integer DELETED_YES = 1;

    /**
     * 是否启用报名 0：否
     */
    Integer PROJECT_ENABLE_ENROLL_NO = 0;

    /**
     * 是否启用报名 1：是
     */
    Integer PROJECT_ENABLE_ENROLL_YES = 1;

    /**
     * 是否启用签到 0：否
     */
    Integer PROJECT_ENABLE_SIGN_NO = 0;

    /**
     * 是否启用签到 1：是
     */
    Integer PROJECT_ENABLE_SIGN_YES = 1;

    /**
     * 是否启用提醒 0：否
     */
    Integer PROJECT_ENABLE_REMIND_NO = 0;

    /**
     * 是否启用提醒 1：是
     */
    Integer PROJECT_ENABLE_REMIND_YES = 1;

    // ****************************************

    /**
     * 项目状态 0：草稿
     */
    Integer PROJECT_STATUS_DRAFT = 0;

    /**
     * 项目状态 1：启用
     */
    Integer PROJECT_STATUS_ENABLE = 1;

    /**
     * 项目状态 2：停用
     */
    Integer PROJECT_STATUS_DISABLE = 2;

    // **************************************

    /**
     * 项目指定学员可见:0
     */
    Integer PROJECT_VISIBLE_RANGE_ACCOUNT = 0;

    /**
     * 项目平台用户可见（创建人管理权限范围）
     */
    Integer PROJECT_VISIBLE_RANGE_SITE = 1;

    // **************************************

    /**
     * 审核状态：0通过
     */
    Integer COMMENT_AUDIT_STATUS_UP = 0;

    /**
     * 审核状态：1下架
     */
    Integer COMMENT_AUDIT_STATUS_DOWN = 1;

    // *****************************************

    /**
     * 完成条件：0属于学习计划
     */
    Integer CONDITION_BELONG_PLAN = 0;

    /**
     * 完成条件：1属于学习活动
     */
    Integer CONDITION_BELONG_ACTIVITY = 1;

    // ***********************************************
    //    // 活动类型： 0课程 1考试 2调研 3直播 4投票 5作业 6证书 7签到 8外部链接
    //
    //    Integer PROJECT_ACTIVITY_TYPE_COURSE = 0;
    //    Integer PROJECT_ACTIVITY_TYPE_EXAM = 1;
    //    Integer PROJECT_ACTIVITY_TYPE_RESEARCH = 2;
    //    Integer PROJECT_ACTIVITY_TYPE_LIVE = 3;
    //    Integer PROJECT_ACTIVITY_TYPE_VOTE = 4;
    //    Integer PROJECT_ACTIVITY_TYPE_ASSIGNMENT = 5;
    //    Integer PROJECT_ACTIVITY_TYPE_CERTIFICATE = 6;
    //    Integer PROJECT_ACTIVITY_TYPE_SIGN = 7;
    //    Integer PROJECT_ACTIVITY_TYPE_LINK = 8;

    // *************************************************

    /**
     * 计划条件类型，0：计划前置条件
     */
    Integer TP_PLAN_CONDITION_VO_TYPE_PRE = 0;

    /**
     * 计划条件类型，1：计划完成条件
     */
    Integer TP_PLAN_CONDITION_VO_TYPE_POST = 1;

    /**
     * 活动条件类型，0：活动开启条件
     */
    Integer TP_PLAN_ACTIVITY_CONDITION_VO_PRE = 0;

    /**
     * 活动条件类型，1：活动完成条件（目前只有考试有完成条件）
     */
    Integer TP_PLAN_ACTIVITY_CONDITION_VO_POST = 1;

    /**
     * 学习计划完成条件的类型，0：设置完成活动数
     */
    Integer TP_PLAN_CONDITION_POST_ACTIVITY_NUM = 0;

    /**
     * 学习计划完成条件的类型，1：指定完成活动
     */
    Integer TP_PLAN_CONDITION_POST_ACTIVITY_ID = 1;

    /**
     * 学习活动开启条件的类型，0：设置完成活动数
     */
    Integer TP_PLAN_ACTIVITY_CONDITION_PRE_NUM = 0;

    /**
     * 学习活动开启条件的类型，1：指定完成活动
     */
    Integer TP_PLAN_ACTIVITY_CONDITION_PRE_ID = 1;

    /**
     * 学习活动（考试）完成条件，0：不设置及格分
     */
    Integer TP_PLAN_ACTIVITY_EXAM_CONDITION_POST_NONE = 0;

    /**
     * 学习活动（考试）完成条件，1：指定分数及格
     */
    Integer TP_PLAN_ACTIVITY_EXAM_CONDITION_POST_SCORE = 1;

    /**
     * 自定义项目h5接口权限验证通过 1
     */
    Integer TP_PLAN_CUSTOMPROJECT_PASS = 1;

    /**
     * 自定义项目h5接口权限验证通过 0
     */
    Integer TP_PLAN_CUSTOMPROJECT_FAIL = 0;

    /**
     * 一次最多生成的激活码数量
     */
    public static final Integer CODE_CREATE_MAX_NUM = 500;

    /**
     * 兑换码和金币分布式锁
     */
    public static final String TP_EXCHANGE_CODE = "tp:exchange:code_%s";

    public static final String TP_EXCHANGE_CODE_USER = "tp:exchange:usercode_%s";

    public static final String ACCOUNT_TOKEN_LOCK = "ACCOUNT_TOKEN_LOCK_%s";
}
