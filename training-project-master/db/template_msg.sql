INSERT INTO `cloud_template_message`.`tmp_scene_mapping` (`id`, `app_scene`, `app_scene_desc`, `remind_scene`,
                                                          `remind_scene_desc`, `send_type`, `send_type_desc`,
                                                          `send_rule`, `send_rule_desc`, `send_range`, `send_time_rule`,
                                                          `send_time_rule_offset`, `variable_param_inner`,
                                                          `variable_param_enterprise`, `create_at`, `deleted`)
VALUES (6, 50, '项目', 51, '学习项目提醒', 1, '定时发送', 200, '根据设置的提醒时间发送',
        '[{\"sendRangeCode\":51,\"sendRangeMsg\":\"可见范围未完成用户\"}]',
        '[{\"timeRule\":3,\"timeRuleDesc\":\"自定义时间\"}]', '[{\"timeOffset\":40,\"timeOffsetDesc\":\"自定义时间\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\"},{\"paramKey\":\"【TpTime】\",\"paramDesc\":\"项目时间\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\",\"paramCode\":\"11\"},{\"paramKey\":\"【TpTime】\",\"paramDesc\":\"项目时间\",\"paramCode\":\"12\"}]',
        '2022-12-16 16:22:28', 0);
INSERT INTO `cloud_template_message`.`tmp_scene_mapping` (`id`, `app_scene`, `app_scene_desc`, `remind_scene`,
                                                          `remind_scene_desc`, `send_type`, `send_type_desc`,
                                                          `send_rule`, `send_rule_desc`, `send_range`, `send_time_rule`,
                                                          `send_time_rule_offset`, `variable_param_inner`,
                                                          `variable_param_enterprise`, `create_at`, `deleted`)
VALUES (7, 50, '项目', 52, '报名提醒', 1, '定时发送', 200, '根据设置的提醒时间发送',
        '[{\"sendRangeCode\":100,\"sendRangeMsg\":\"可见范围用户\"}]',
        '[{\"timeRule\":3,\"timeRuleDesc\":\"自定义时间\"}]', '[{\"timeOffset\":40,\"timeOffsetDesc\":\"自定义时间\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\"},{\"paramKey\":\"【TpEnrollTime】\",\"paramDesc\":\"报名时间\"},{\"paramKey\":\"【TpTime】\",\"paramDesc\":\"项目时间\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\",\"paramCode\":\"11\"},{\"paramKey\":\"【TpEnrollTime】\",\"paramDesc\":\"报名时间\",\"paramCode\":\"13\"},{\"paramKey\":\"【TpTime】\",\"paramDesc\":\"项目时间\",\"paramCode\":\"12\"}]',
        '2022-12-16 16:22:28', 0);
INSERT INTO `cloud_template_message`.`tmp_scene_mapping` (`id`, `app_scene`, `app_scene_desc`, `remind_scene`,
                                                          `remind_scene_desc`, `send_type`, `send_type_desc`,
                                                          `send_rule`, `send_rule_desc`, `send_range`, `send_time_rule`,
                                                          `send_time_rule_offset`, `variable_param_inner`,
                                                          `variable_param_enterprise`, `create_at`, `deleted`)
VALUES (8, 50, '项目', 53, '报名审核通知', 0, '即时发送', 11, '每次审核完成发送',
        '[{\"sendRangeCode\":13,\"sendRangeMsg\":\"审核过的用户\"}]', NULL, NULL,
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\"},{\"paramKey\":\"【TpEnrollAuditStatus】\",\"paramDesc\":\"审核状态\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\",\"paramCode\":\"11\"},{\"paramKey\":\"【TpEnrollAuditStatus】\",\"paramDesc\":\"审核状态\",\"paramCode\":\"14\"}]',
        '2022-12-16 16:22:28', 0);
INSERT INTO `cloud_template_message`.`tmp_scene_mapping` (`id`, `app_scene`, `app_scene_desc`, `remind_scene`,
                                                          `remind_scene_desc`, `send_type`, `send_type_desc`,
                                                          `send_rule`, `send_rule_desc`, `send_range`, `send_time_rule`,
                                                          `send_time_rule_offset`, `variable_param_inner`,
                                                          `variable_param_enterprise`, `create_at`, `deleted`)
VALUES (9, 50, '项目', 54, '学习单元提醒', 1, '定时发送', 200, '根据设置的提醒时间发送',
        '[{\"sendRangeCode\":51,\"sendRangeMsg\":\"可见范围未完成用户\"}]',
        '[{\"timeRule\":3,\"timeRuleDesc\":\"自定义时间\"}]', '[{\"timeOffset\":40,\"timeOffsetDesc\":\"自定义时间\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\"},{\"paramKey\":\"【TpPlanName】\",\"paramDesc\":\"单元名称\"},{\"paramKey\":\"【TpPlanTime】\",\"paramDesc\":\"单元时间\"}]',
        '[{\"paramKey\":\"【TpName】\",\"paramDesc\":\"项目名称\",\"paramCode\":\"11\"},{\"paramKey\":\"【TpPlanName】\",\"paramDesc\":\"单元名称\",\"paramCode\":\"15\"},{\"paramKey\":\"【TpPlanTime】\",\"paramDesc\":\"单元时间\",\"paramCode\":\"16\"}]',
        '2022-12-16 16:22:28', 0);
