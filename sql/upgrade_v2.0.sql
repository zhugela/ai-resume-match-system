-- =============================================
-- AI简历匹配系统 v2.0 数据库升级脚本
-- 执行方式：在MySQL中运行此脚本
-- =============================================

USE resume_match;

-- =============================================
-- 1. 收藏表 (t_favorite)
-- =============================================
CREATE TABLE IF NOT EXISTS t_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_id BIGINT NOT NULL COMMENT '职位ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    UNIQUE KEY uk_user_job (user_id, job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- =============================================
-- 2. 通知表 (t_notification)
-- =============================================
CREATE TABLE IF NOT EXISTS t_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(50) DEFAULT 'SYSTEM' COMMENT '通知类型：SYSTEM/匹配结果/系统公告',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- =============================================
-- 3. 反馈表 (t_feedback)
-- =============================================
CREATE TABLE IF NOT EXISTS t_feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content TEXT NOT NULL COMMENT '反馈内容',
    type VARCHAR(50) DEFAULT 'SUGGESTION' COMMENT '反馈类型：BUG/建议/投诉',
    status VARCHAR(50) DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，RESOLVED-已处理，REJECTED-已拒绝',
    reply TEXT COMMENT '管理员回复',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- =============================================
-- 4. 操作日志表 (t_operation_log)
-- =============================================
CREATE TABLE IF NOT EXISTS t_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(100) COMMENT '用户名',
    operation VARCHAR(100) NOT NULL COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    url VARCHAR(500) COMMENT '请求URL',
    ip VARCHAR(50) COMMENT 'IP地址',
    params TEXT COMMENT '请求参数',
    result VARCHAR(50) COMMENT '操作结果：SUCCESS/FAIL',
    error_msg TEXT COMMENT '错误信息',
    duration BIGINT COMMENT '耗时（毫秒）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 5. 简历评分表 (t_resume_score)
-- =============================================
CREATE TABLE IF NOT EXISTS t_resume_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    skill_score DECIMAL(5,2) DEFAULT 0 COMMENT '技能匹配分（0-100）',
    experience_score DECIMAL(5,2) DEFAULT 0 COMMENT '经验匹配分（0-100）',
    education_score DECIMAL(5,2) DEFAULT 0 COMMENT '学历匹配分（0-100）',
    overall_score DECIMAL(5,2) DEFAULT 0 COMMENT '综合评分（0-100）',
    skill_details JSON COMMENT '技能详细分析',
    experience_details JSON COMMENT '经验详细分析',
    education_details JSON COMMENT '学历详细分析',
    radar_data JSON COMMENT '雷达图数据',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评分时间',
    updated_at DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历评分表';

-- =============================================
-- 插入测试数据（可选）
-- =============================================

-- 插入测试通知
INSERT INTO t_notification (user_id, type, title, content) VALUES
(1, 'SYSTEM', '欢迎使用AI简历匹配系统', '感谢您使用我们的系统，祝您求职顺利！'),
(1, 'MATCH', '新的匹配结果', '您有一份新的岗位匹配结果，请点击查看。');

-- 插入测试反馈
INSERT INTO t_feedback (user_id, content, type, status) VALUES
(1, '系统很好用，希望能增加更多的岗位类型', 'SUGGESTION', 'PENDING'),
(1, '希望增加简历导出功能', 'SUGGESTION', 'RESOLVED');

SELECT 'v2.0数据库升级完成！' AS message;
