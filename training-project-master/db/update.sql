-- 1.tp_authorization_range,tp_student_activity_record,tp_student_plan_record,tp_student_project_record表添加字段company_id,需要刷数据
-- 2.tr_sign_time,tr_sign_record表添加字段company_id,site_id,需要刷数据
UPDATE `tr_sign_time` a
    INNER JOIN `training_project` b
ON a.`training_project_id` = b.`id`
    SET a.`company_id` = b.`company_id`, a.`site_id` = b.`site_id`;

UPDATE `tr_sign_record` a
    INNER JOIN `training_project` b
ON a.`training_project_id` = b.`id`
    SET a.`company_id` = b.`company_id`, a.`site_id` = b.`site_id`;


-- 3.以前的项目的sort字段默认设为999
UPDATE `training_project`
SET `sort` = 999
WHERE `sort` = 0

-- 4.刷数据接口:添加学习目录
-- wget http://127.0.0.1:80/v2/manage/api/initStudyDirectory

-- 5.刷数据接口:学习活动的排序值逆转
-- wget http://127.0.0.1:80/v2/manage/api/reverseActivitySort

-- 6.sql刷数据:历史数据的学习活动的别名需要补充上
UPDATE `tp_plan_activity`
SET `customize_name` = `name`
WHERE `customize_name` IS NULL
-- 7.证书的nacos配置需要加上自动填充的配置
-- 8.项目的配置需要注意是否有配置自动填充和logic删除
-- 9.所有查询活动的地方需要排除证书类型（枚举：6）
-- 10.先把证书历史数据由挂靠项目改为挂靠单元，然后查出历史数据中的只有证书的单元id查出来，再到证书库去利用这些单元id将绑定关系改为和项目绑定
UPDATE relation_activities
SET bizd_type = 3
WHERE bizd_type = 0
  AND bizd_id != tp_plan_id