-- AI 调用日志表
CREATE TABLE IF NOT EXISTS t_ai_call_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    resume_id BIGINT,
    call_type VARCHAR(32) NOT NULL,
    prompt TEXT,
    response TEXT,
    duration INT COMMENT '调用耗时(毫秒)',
    tokens INT COMMENT '消耗token数',
    status VARCHAR(20) DEFAULT 'SUCCESS',
    error_message TEXT,
    model VARCHAR(64),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_resume_id (resume_id),
    INDEX idx_call_type (call_type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE favorite (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          job_id BIGINT NOT NULL,
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              type VARCHAR(50),
                              title VARCHAR(200),
                              content TEXT,
                              is_read TINYINT DEFAULT 0,
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE feedback (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          content TEXT NOT NULL,
                          type VARCHAR(50),
                          status VARCHAR(50) DEFAULT 'PENDING',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);