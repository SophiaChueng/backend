ALTER TABLE `cloud_forum`.`posts_relation`
    ADD COLUMN `sort` int NOT NULL DEFAULT 0 COMMENT '排序' AFTER `spare_field`;