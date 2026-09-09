-- ============================================
-- 高校社团活动场地管理系统 - 数据库建表脚本
-- 所有主键均采用 AUTO_INCREMENT 自增
-- ============================================

CREATE DATABASE IF NOT EXISTS club_venue DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE club_venue;

-- 1. 菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID（自增）',
  `parent_id` BIGINT DEFAULT 0 COMMENT '上级菜单ID，0为顶级',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `menu_type` CHAR(1) NOT NULL DEFAULT 'M' COMMENT '菜单类型：M=目录 C=菜单 F=按钮',
  `permission` VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `path` VARCHAR(200) DEFAULT NULL COMMENT '访问路径',
  `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态：0=启用 1=禁用',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除：0=未删除 1=已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统菜单表';

-- 2. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID（自增）',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_key` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `permissions` VARCHAR(500) DEFAULT NULL COMMENT '权限列表',
  `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除：0=未删除 1=已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 3. 普通用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID（自增）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `username` VARCHAR(50) NOT NULL COMMENT '账号',
  `password` VARCHAR(64) NOT NULL COMMENT '密码（MD5加密）',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '头像URL',
  `club_id` BIGINT DEFAULT NULL COMMENT '所属社团ID',
  `club_name` VARCHAR(50) DEFAULT NULL COMMENT '社团名称',
  `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色：admin=管理员 club_leader=社团负责人 user=普通用户',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除 1=已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 4. 社团表
DROP TABLE IF EXISTS `tb_club`;
CREATE TABLE `tb_club` (
  `club_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '社团ID（自增）',
  `club_name` VARCHAR(50) NOT NULL COMMENT '社团名称',
  `avatar` VARCHAR(200) DEFAULT NULL COMMENT '社团头像',
  `leader_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
  `leader_name` VARCHAR(50) DEFAULT NULL COMMENT '负责人名称',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '社团类别',
  `phone` VARCHAR(30) DEFAULT NULL COMMENT '联系电话',
  `intro_id` BIGINT DEFAULT NULL COMMENT '简介ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除 1=已删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`club_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团表';

-- 5. 社团成员表
DROP TABLE IF EXISTS `tb_club_member`;
CREATE TABLE `tb_club_member` (
  `member_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '成员ID（自增）',
  `club_id` BIGINT NOT NULL COMMENT '社团ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role` VARCHAR(20) DEFAULT '成员' COMMENT '角色：负责人 副负责人 成员',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团成员表';

-- 6. 社团相册表
DROP TABLE IF EXISTS `tb_club_album`;
CREATE TABLE `tb_club_album` (
  `album_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '相册ID（自增）',
  `club_id` BIGINT NOT NULL COMMENT '社团ID',
  `url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`album_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社团相册表';

-- 7. 入团申请表
DROP TABLE IF EXISTS `tb_club_application`;
CREATE TABLE `tb_club_application` (
  `application_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID（自增）',
  `club_id` BIGINT NOT NULL COMMENT '社团ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `reason` TEXT DEFAULT NULL COMMENT '申请理由',
  `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核 已通过 已驳回',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入团申请表';

-- 8. 场地表
DROP TABLE IF EXISTS `tb_venue`;
CREATE TABLE `tb_venue` (
  `venue_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '场地ID（自增）',
  `venue_name` VARCHAR(50) NOT NULL COMMENT '场地名称',
  `venue_no` VARCHAR(20) DEFAULT NULL COMMENT '场地编号',
  `status` VARCHAR(20) DEFAULT '可用' COMMENT '状态：可用 使用中 维护中',
  `venue_group_id` BIGINT DEFAULT NULL COMMENT '场地组ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`venue_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地表';

-- 9. 场地设施表
DROP TABLE IF EXISTS `tb_facility`;
CREATE TABLE `tb_facility` (
  `facility_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设施ID（自增）',
  `facility_name` VARCHAR(50) NOT NULL COMMENT '设施名称',
  `status` VARCHAR(20) DEFAULT '正常' COMMENT '状态：正常 损坏 维修中',
  `venue_id` BIGINT NOT NULL COMMENT '所属场地ID',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`facility_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地设施表';

-- 10. 场地排期表
DROP TABLE IF EXISTS `tb_venue_schedule`;
CREATE TABLE `tb_venue_schedule` (
  `schedule_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '排期ID（自增）',
  `venue_id` BIGINT NOT NULL COMMENT '场地ID',
  `activity_id` BIGINT DEFAULT NULL COMMENT '关联活动ID',
  `title` VARCHAR(100) NOT NULL COMMENT '排期标题',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `status` VARCHAR(20) DEFAULT '已锁定' COMMENT '状态：已锁定 已取消',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地排期表';

-- 11. 活动表
DROP TABLE IF EXISTS `tb_activity`;
CREATE TABLE `tb_activity` (
  `activity_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID（自增）',
  `activity_name` VARCHAR(100) NOT NULL COMMENT '活动名称',
  `reason` TEXT DEFAULT NULL COMMENT '申请理由',
  `start_time` DATETIME NOT NULL COMMENT '活动开始时间',
  `end_time` DATETIME NOT NULL COMMENT '活动结束时间',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `club_id` BIGINT DEFAULT NULL COMMENT '申请社团ID',
  `venue_id` BIGINT DEFAULT NULL COMMENT '场地ID',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人ID',
  `poster` VARCHAR(200) DEFAULT NULL COMMENT '宣传图URL',
  `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核 已通过 已驳回 进行中 已结束',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 12. 活动评分表
DROP TABLE IF EXISTS `tb_activity_rating`;
CREATE TABLE `tb_activity_rating` (
  `rating_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评分ID（自增）',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `score` INT NOT NULL COMMENT '评分（1-5）',
  `comment` TEXT DEFAULT NULL COMMENT '评语',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`rating_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动评分表';

-- 13. 活动总结表
DROP TABLE IF EXISTS `tb_activity_summary`;
CREATE TABLE `tb_activity_summary` (
  `summary_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '总结ID（自增）',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `participant_count` INT DEFAULT 0 COMMENT '参与人数',
  `feedback` TEXT DEFAULT NULL COMMENT '活动反馈',
  `summary_content` TEXT DEFAULT NULL COMMENT '总结内容',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`summary_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动总结表';

-- 14. 签到表
DROP TABLE IF EXISTS `tb_check_in`;
CREATE TABLE `tb_check_in` (
  `check_in_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '签到ID（自增）',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `check_in_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `verified` TINYINT(1) DEFAULT 0 COMMENT '是否核验：0=未核验 1=已核验',
  `verify_time` DATETIME DEFAULT NULL COMMENT '核验时间',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`check_in_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';

-- 15. 用户建议表
DROP TABLE IF EXISTS `tb_suggestion`;
CREATE TABLE `tb_suggestion` (
  `suggestion_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '建议ID（自增）',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content` TEXT NOT NULL COMMENT '建议内容',
  `status` VARCHAR(20) DEFAULT '待处理' COMMENT '状态：待处理 已采纳 已关闭',
  `reply` TEXT DEFAULT NULL COMMENT '回复内容',
  `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`suggestion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户建议表';

-- 16. 活动报名表
DROP TABLE IF EXISTS `tb_activity_registration`;
CREATE TABLE `tb_activity_registration` (
  `registration_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID（自增）',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `status` VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核 已通过 已驳回',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';
