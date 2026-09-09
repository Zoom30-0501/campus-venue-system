-- ============================================================
-- 测试数据：用于移动端联调测试
-- ============================================================

USE club_venue;

-- 测试普通用户 (密码 MD5 加密: 123456 → e10adc3949ba59abbe56e057f20f883e)
INSERT IGNORE INTO sys_user (user_id, nickname, username, password, role, deleted)
VALUES (2, '测试用户', 'testuser', 'e10adc3949ba59abbe56e057f20f883e', 'user', 0);

-- 测试社团
INSERT IGNORE INTO tb_club (club_id, club_name, category, leader_name, phone, deleted)
VALUES
(1, '计算机协会', '学术科技', '测试用户', '13800001111', 0),
(2, '篮球社', '体育竞技', '测试用户', '13800002222', 0);

-- 测试场地
INSERT IGNORE INTO tb_venue (venue_id, venue_name, venue_no, status, deleted)
VALUES
(1, '多媒体教室A201', 'R001', '可用', 0),
(2, '大学生活动中心多功能厅', 'R002', '可用', 0),
(3, '室内体育馆', 'R003', '可用', 0),
(4, '学术报告厅', 'R004', '可用', 0);
