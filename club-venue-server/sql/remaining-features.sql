-- 补全剩余功能建表语句
-- 社团申请加入表
CREATE TABLE IF NOT EXISTS tb_club_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT,
    club_name VARCHAR(100),
    user_id BIGINT,
    user_name VARCHAR(100),
    reason VARCHAR(500),
    status VARCHAR(20) DEFAULT '待审核',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 社团相册/动态表
CREATE TABLE IF NOT EXISTS tb_club_album (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    club_id BIGINT,
    image_url VARCHAR(500),
    description VARCHAR(500),
    uploader_id BIGINT,
    uploader_name VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 活动总结/回顾表
CREATE TABLE IF NOT EXISTS tb_activity_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT,
    content TEXT,
    images VARCHAR(1000),
    participant_count INT DEFAULT 0,
    feedback VARCHAR(1000),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 活动评分表
CREATE TABLE IF NOT EXISTS tb_activity_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT,
    user_id BIGINT,
    user_name VARCHAR(100),
    score INT,
    comment VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
