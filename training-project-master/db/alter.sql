ALTER TABLE `cloud_trainning_project`.`tp_plan`
    ADD COLUMN `enable_study_in_sequence` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否按顺序学习（0：否，1：是）' AFTER `remind_content`,
ADD COLUMN `enable_continue_study` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启到期继续学习（0：否，1：是）' AFTER `enable_study_in_sequence`,
ADD COLUMN `directory_item_id` bigint NOT NULL DEFAULT 0 COMMENT '学习页目录id' AFTER `enable_continue_study`;

ALTER TABLE `cloud_trainning_project`.`tp_plan`
    MODIFY COLUMN `name` varchar (200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学习计划名称' AFTER `training_project_id`;

ALTER TABLE `cloud_trainning_project`.`tp_plan_activity`
    ADD COLUMN `logo_url` varchar(500) NULL COMMENT '活动logo' AFTER `old_id`,
ADD COLUMN `customize_name` varchar(255) NULL COMMENT '自定义学习活动名称' AFTER `logo_url`;


ALTER TABLE `cloud_trainning_project`.`training_project`
    ADD COLUMN `sort` int NOT NULL DEFAULT 0 COMMENT '排序' AFTER `enable_queue`,
ADD COLUMN `publish_terminal` tinyint(1) NOT NULL DEFAULT 0 COMMENT '发布终端（0：所有终端，1：PC端，2：移动端）' AFTER `sort`,
ADD COLUMN `enable_statistics` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否显示数据统计（0：不显示，1：显示）' AFTER `publish_terminal`,
ADD COLUMN `enable_head_teacher` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启班主任（0：关闭，1：开启）' AFTER `enable_statistics`,
ADD COLUMN `enable_msg_remind` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否开启消息提醒（0：关闭，1：开启）' AFTER `enable_head_teacher`;

ALTER TABLE `cloud_trainning_project`.`training_project`
    MODIFY COLUMN `name` varchar (200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '培训项目名称' AFTER `tp_classification_id`;

ALTER TABLE `tp_student_project_record`
    ADD `type` INT NULL  DEFAULT '0'  COMMENT '默认为0:项目  1:项目PRO'  AFTER `account_id`;

ALTER TABLE `cloud_trainning_project`.`tp_plan_condition_pre`
    ADD COLUMN `finish_count` int(11) NULL DEFAULT NULL COMMENT '完成前置单元中的n个' AFTER `pre_plan_id`;

ALTER TABLE `cloud_trainning_project`.`tp_student_project_record`
    ADD COLUMN `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业id' AFTER `id`;

ALTER TABLE `cloud_trainning_project`.`tp_student_plan_record`
    ADD COLUMN `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业id' AFTER `id`;

ALTER TABLE `cloud_trainning_project`.`tp_student_activity_record`
    ADD COLUMN `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业id' AFTER `id`;

ALTER TABLE `cloud_trainning_project`.`tr_sign_time`
    ADD COLUMN `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业id' AFTER `id`;

ALTER TABLE `cloud_trainning_project`.`tr_sign_record`
    ADD COLUMN `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业id' AFTER `id`;

ALTER TABLE `cloud_trainning_project`.`tp_authorization_range`
    ADD COLUMN `company_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '企业id' AFTER `id`;

ALTER TABLE `cloud_trainning_project`.`tr_sign_time`
    ADD COLUMN `site_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '站点id' AFTER `company_id`;

ALTER TABLE `cloud_trainning_project`.`tr_sign_record`
    ADD COLUMN `site_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '站点id' AFTER `company_id`;

