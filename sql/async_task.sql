-- 异步任务表
CREATE TABLE IF NOT EXISTS t_async_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    task_type VARCHAR(32) NOT NULL,
    resume_id BIGINT,
    job_description TEXT,
    target_job_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    result TEXT,
    error_message TEXT,
    progress INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at DATETIME,
    INDEX idx_task_id (task_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
