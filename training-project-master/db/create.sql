CREATE TABLE `tp_announcement`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `title`               varchar(100) NOT NULL DEFAULT '' COMMENT '公告标题',
    `content`             varchar(500) NOT NULL DEFAULT '' COMMENT '公告内容',
    `publish_time`        timestamp NULL DEFAULT NULL COMMENT '发布时间',
    `sort`                int(11) NOT NULL DEFAULT '0' COMMENT '排序',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)           DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30)           DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_id` (`training_project_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目公告表';


CREATE TABLE `tp_condition_post`
(
    `id`                  bigint(20) NOT NULL COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `condition_type`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '条件类型（0：完成学习单元数，1：完成指定学习单元）',
    `complete_count`      int(11) NOT NULL DEFAULT '0' COMMENT '当condition_type为0，表示完成学习单元数',
    `tp_plan_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '当condition_type为1，表示完成指定学习单元id',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)        DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30)        DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_id` (`training_project_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目完成条件';

CREATE TABLE `tp_consult_entrance`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `directory_item_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT '介绍页目录项id',
    `entrance_name`       varchar(200) NOT NULL DEFAULT '' COMMENT '咨询名称',
    `entrance_img`        varchar(500) NOT NULL DEFAULT '' COMMENT '咨询入口二维码',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(255)          DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(255)          DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_dir_id` (`training_project_id`,`directory_item_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目咨询入口表';

CREATE TABLE `tp_introduce_directory`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `item_type`           tinyint(1) NOT NULL DEFAULT '0' COMMENT '目录类型（0：学习单元，1：简介，2：资料，3：评论，4：考试与作业，5：公告，6：讨论，7：富文本（学习页），8：咨询，9：富文本（介绍页））',
    `item_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '关联学习页目录项id',
    `item_name`           varchar(100) NOT NULL DEFAULT '' COMMENT '目录项名称',
    `sort`                int(11) NOT NULL DEFAULT '0' COMMENT '排序',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)           DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(255)          DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_id` (`training_project_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='介绍页目录表';

CREATE TABLE `tp_plan_study_time_condition`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `tp_plan_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '学习计划id',
    `condition_type`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '条件类型（0：无时间限制，1：指定时间段，2：开始学习后n天，3：完成前置单元后n天）',
    `start_time`          timestamp NULL DEFAULT NULL COMMENT '指定时间段：开始时间',
    `end_time`            timestamp NULL DEFAULT NULL COMMENT '指定时间段：结束时间',
    `after_start_date`    int(11) DEFAULT NULL COMMENT '开始学习后n天',
    `after_pre_plan_date` int(11) DEFAULT NULL COMMENT '完成前置单元后n天',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)        DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30)        DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_id_plan_id` (`training_project_id`,`tp_plan_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学习计划学习时间条件';

CREATE TABLE `tp_pro_mapping`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `tp_pro_id`           bigint(20) NOT NULL DEFAULT '0' COMMENT '项目proid',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `sort`                int(11) NOT NULL DEFAULT '0' COMMENT '排序',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)        DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30)        DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目pro与项目关联关系';

CREATE TABLE `tp_rich_text`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `directory_type`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '目录类型（0：学习页目录，1：介绍页目录）',
    `directory_item_id`   bigint(20) NOT NULL DEFAULT '0' COMMENT '目录项id',
    `title`               varchar(100) NOT NULL DEFAULT '' COMMENT '标题',
    `content`             text COMMENT '文本内容',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)           DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30)           DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_dir_type_item_id` (`training_project_id`,`directory_type`,`directory_item_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='富文本';

CREATE TABLE `tp_study_directory`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `item_type`           tinyint(1) NOT NULL DEFAULT '0' COMMENT '学习页目录项类型（0：学习单元，1：简介，2：资料，3：评论，4：考试与作业，5：公告，6：讨论，7：富文本）',
    `item_name`           varchar(100) NOT NULL DEFAULT '' COMMENT '目录项名称',
    `sort`                int(11) NOT NULL DEFAULT '0' COMMENT '排序',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)           DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30)           DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`),
    KEY                   `idx_tp_id` (`training_project_id`) USING BTREE COMMENT '查询索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学习页目录';

CREATE TABLE `training_project_pro`
(
    `id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '主键id',
    `company_id`     bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`        bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `tp_pro_name`    varchar(100) NOT NULL DEFAULT '' COMMENT '项目pro名称',
    `tp_pro_logo`    varchar(255) NOT NULL DEFAULT '' COMMENT '项目pro的logo',
    `sort`           int(11) NOT NULL DEFAULT '0' COMMENT '排序',
    `create_by_id`   bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name` varchar(30)           DEFAULT NULL COMMENT '创建者名称',
    `create_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`   bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name` varchar(30)           DEFAULT NULL COMMENT '更新者名称',
    `update_time`    timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除（0：否，1：是）',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目pro';


CREATE TABLE `tp_head_teacher`
(
    `id`                  bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
    `company_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '企业id',
    `site_id`             bigint(20) NOT NULL DEFAULT '0' COMMENT '站点id',
    `training_project_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '项目id',
    `account_id`          bigint(20) NOT NULL DEFAULT '0' COMMENT '班主任id',
    `create_by_id`        bigint(20) DEFAULT NULL COMMENT '创建者id',
    `create_by_name`      varchar(30)                      DEFAULT NULL COMMENT '创建者名称',
    `create_time`         timestamp NOT NULL               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by_id`        bigint(20) DEFAULT NULL COMMENT '更新者id',
    `update_by_name`      varchar(30) CHARACTER SET latin1 DEFAULT NULL COMMENT '更新者名称',
    `update_time`         timestamp NOT NULL               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已被删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目-班主任关联表';