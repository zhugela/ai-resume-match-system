-- ===============================================
-- AI简历匹配系统 v2.0 数据库升级脚本
-- ===============================================

-- 1. 收藏表
CREATE TABLE IF NOT EXISTS `t_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `job_id` BIGINT NOT NULL COMMENT '职位ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_job` (`user_id`, `job_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_job_id` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 2. 消息通知表
CREATE TABLE IF NOT EXISTS `t_notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `type` VARCHAR(50) NOT NULL COMMENT '通知类型: SYSTEM-系统通知, MATCH_RESULT-匹配结果, JOB_UPDATE-岗位更新',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_is_read` (`is_read`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 3. 用户反馈表
CREATE TABLE IF NOT EXISTS `t_feedback` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT NOT NULL COMMENT '反馈内容',
    `type` VARCHAR(50) NOT NULL COMMENT '反馈类型: BUG-问题反馈, FEATURE-功能建议, OTHER-其他',
    `status` VARCHAR(50) DEFAULT 'PENDING' COMMENT '处理状态: PENDING-待处理, PROCESSING-处理中, RESOLVED-已解决, REJECTED-已拒绝',
    `reply` TEXT COMMENT '管理员回复',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表';

-- 4. 操作日志表
CREATE TABLE IF NOT EXISTS `t_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `user_id` BIGINT COMMENT '用户ID',
    `username` VARCHAR(100) COMMENT '用户名',
    `module` VARCHAR(50) COMMENT '操作模块',
    `operation` VARCHAR(50) COMMENT '操作类型',
    `method` VARCHAR(20) COMMENT 'HTTP方法',
    `url` VARCHAR(500) COMMENT '请求URL',
    `params` TEXT COMMENT '请求参数',
    `result` TEXT COMMENT '返回结果',
    `status` VARCHAR(20) COMMENT '操作状态: SUCCESS, FAIL',
    `error_message` TEXT COMMENT '错误信息',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT 'User-Agent',
    `duration` BIGINT COMMENT '耗时(毫秒)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_module` (`module`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 5. 简历评分表
CREATE TABLE IF NOT EXISTS `t_resume_score` (
    `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `resume_id` BIGINT NOT NULL COMMENT '简历ID',
    `overall_score` DECIMAL(5,2) COMMENT '综合评分(0-100)',
    `skill_score` DECIMAL(5,2) COMMENT '技能匹配度(0-100)',
    `experience_score` DECIMAL(5,2) COMMENT '经验匹配度(0-100)',
    `education_score` DECIMAL(5,2) COMMENT '学历匹配度(0-100)',
    `radar_data` JSON COMMENT '雷达图数据',
    `analysis_detail` TEXT COMMENT '详细分析结果',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_resume_id` (`resume_id`),
    UNIQUE KEY `uk_resume_id` (`resume_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历评分表';

-- ===============================================
-- 示例数据
-- ===============================================

-- 插入测试通知
INSERT INTO `t_notification` (`user_id`, `type`, `title`, `content`, `is_read`) VALUES
(1, 'SYSTEM', '欢迎使用AI简历匹配系统', '感谢您注册我们的系统，祝您求职顺利！', 0),
(1, 'MATCH_RESULT', '您有新的匹配结果', '您上传的简历与3个岗位高度匹配，快去看看吧！', 0);
