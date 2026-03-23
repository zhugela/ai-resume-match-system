-- AI简历分析 + 岗位匹配系统 - 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS resume_match DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE resume_match;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role ENUM('USER', 'HR', 'ADMIN') DEFAULT 'USER' COMMENT '角色',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 简历表
CREATE TABLE IF NOT EXISTS t_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_url VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(20) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    parsed_data JSON COMMENT 'AI解析后的数据',
    skills JSON COMMENT '提取的技能标签',
    work_experience TEXT COMMENT '工作经历',
    education TEXT COMMENT '教育经历',
    analysis_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING' COMMENT '分析状态',
    score INT COMMENT '简历评分',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_analysis_status (analysis_status),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 岗位表
CREATE TABLE IF NOT EXISTS t_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    hr_id BIGINT NOT NULL COMMENT 'HR用户ID',
    title VARCHAR(200) NOT NULL COMMENT '岗位标题',
    company VARCHAR(200) NOT NULL COMMENT '公司名称',
    location VARCHAR(100) COMMENT '工作地点',
    salary_range VARCHAR(50) COMMENT '薪资范围',
    job_type ENUM('FULL_TIME', 'PART_TIME', 'INTERNSHIP', 'CONTRACT') DEFAULT 'FULL_TIME' COMMENT '岗位类型',
    description TEXT COMMENT '岗位描述',
    requirements TEXT COMMENT '岗位要求',
    required_skills JSON COMMENT '必备技能',
    preferred_skills JSON COMMENT '加分技能',
    status ENUM('DRAFT', 'OPEN', 'CLOSED') DEFAULT 'DRAFT' COMMENT '状态',
    views INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_hr_id (hr_id),
    INDEX idx_status (status),
    INDEX idx_location (location),
    FULLTEXT INDEX ft_title_desc (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- 匹配记录表
CREATE TABLE IF NOT EXISTS t_match_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    job_id BIGINT NOT NULL COMMENT '岗位ID',
    match_score DECIMAL(5,2) COMMENT '匹配度 0-100',
    match_details JSON COMMENT '匹配详情',
    status ENUM('NEW', 'VIEWED', 'INTERESTED', 'IGNORED') DEFAULT 'NEW' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_resume_id (resume_id),
    INDEX idx_job_id (job_id),
    INDEX idx_match_score (match_score),
    UNIQUE KEY uk_resume_job (resume_id, job_id),
    FOREIGN KEY (resume_id) REFERENCES t_resume(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES t_job(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='匹配记录表';

-- 用户收藏岗位表
CREATE TABLE IF NOT EXISTS t_job_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_id BIGINT NOT NULL COMMENT '岗位ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    UNIQUE KEY uk_user_job (user_id, job_id),
    FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES t_job(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏岗位表';

-- 插入测试数据
INSERT INTO t_user (username, password, email, role) VALUES 
('test_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test@example.com', 'USER'),
('test_hr', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'hr@example.com', 'HR');

INSERT INTO t_job (hr_id, title, company, location, salary_range, job_type, description, requirements, required_skills, preferred_skills, status, views) VALUES
(2, 'Java开发工程师', '字节跳动', '北京', '20K-40K', 'FULL_TIME', '负责后端开发', '3年以上经验', '["Java", "Spring Boot", "MySQL"]', '["Redis", "Kafka", "Elasticsearch"]', 'OPEN', 100),
(2, '前端开发工程师', '阿里巴巴', '杭州', '15K-30K', 'FULL_TIME', '负责前端开发', '2年以上经验', '["Vue", "React", "JavaScript"]', '["TypeScript", "Node.js"]', 'OPEN', 80),
(2, '算法工程师', '百度', '北京', '30K-60K', 'FULL_TIME', '负责算法研发', '硕士及以上', '["Python", "TensorFlow", "PyTorch"]', '["C++", "分布式系统"]', 'OPEN', 150);

-- 查看表结构
SHOW TABLES;
