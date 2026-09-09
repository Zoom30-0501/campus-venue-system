-- 初始化管理员账号
USE club_venue;

INSERT INTO sys_user (user_id, nickname, username, password, role) VALUES
(1, '管理员', 'admin', MD5('123456'), 'admin');
