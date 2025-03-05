ALTER TABLE `cloud_certificate`.`relation_activities`
    MODIFY COLUMN `bizd_type` tinyint(2) NULL DEFAULT NULL COMMENT '关联活动类型0:培训项目 1:课程，3：学习单元' AFTER `certificate_id`,
    ADD COLUMN `issue_strategy` tinyint(1) NOT NULL DEFAULT 0 COMMENT '证书发放策略（0：完成后自动获取，1：学员申请）' AFTER `bizd_id`;


CREATE TABLE `certificate_apply`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `certificate_id`      bigint(20) NOT NULL DEFAULT '0' COMMENT '证书id',
    `biz_id`              bigint(20) NOT NULL DEFAULT '0' COMMENT '业务id',
    `biz_type`            tinyint(1) NOT NULL DEFAULT '0' COMMENT '业务类型（0：培训项目，1：课程，3：学习单元）',
    `biz_name`            varchar(100) DEFAULT NULL COMMENT '业务名称',
    `audit_state`         tinyint(1) DEFAULT NULL COMMENT '审核状态(0:待审批，1：通过，2：不通过)',
    `create_by_id`        bigint(20) NOT NULL DEFAULT '0' COMMENT '创建者id（申请人id）',
    `create_by_name`      varchar(100) DEFAULT NULL COMMENT '创建者名称（申请人名称）',
    `create_by_full_name` varchar(100) DEFAULT NULL COMMENT '创建者全名（申请人全名）',
    `create_time`         timestamp NULL DEFAULT NULL COMMENT '创建时间',
    `update_by_id`        bigint(20) NOT NULL DEFAULT '0' COMMENT '更新者id',
    `update_by_name`      varchar(100) DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='证书申请表';

CREATE TABLE `certificate_audit_record`
(
    `id`                   bigint(20) NOT NULL COMMENT '主键',
    `company_id`           bigint(20) NOT NULL COMMENT '企业id',
    `site_id`              bigint(20) NOT NULL COMMENT '站点id',
    `certificate_apply_id` bigint(20) NOT NULL COMMENT '证书申请记录id',
    `audit_state`          tinyint(1) NOT NULL COMMENT '审核状态(0:待审批，1：通过，2：不通过)',
    `create_by_id`         bigint(20) NOT NULL COMMENT '创建者id（审核人）',
    `create_by_name`       varchar(100) DEFAULT NULL COMMENT '创建者名称（审核人）',
    `create_time`          timestamp NULL DEFAULT NULL COMMENT '创建时间（审核时间）',
    `update_by_id`         bigint(20) NOT NULL COMMENT '更新者id',
    `update_by_name`       varchar(100) DEFAULT NULL COMMENT '更新者名称',
    `update_time`          timestamp NULL DEFAULT NULL COMMENT '更新时间',
    `deleted`              tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='证书审核记录表';