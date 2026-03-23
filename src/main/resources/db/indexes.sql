-- ===============================================
-- AI简历匹配系统 - 数据库索引脚本
-- 适用于 MySQL 8.0+
-- ===============================================

-- 简历表索引
CREATE INDEX idx_resume_user_id ON t_resume(user_id);
CREATE INDEX idx_resume_status ON t_resume(analysis_status);
CREATE INDEX idx_resume_created_at ON t_resume(created_at);
CREATE INDEX idx_resume_score ON t_resume(score);

-- 岗位表索引
CREATE INDEX idx_job_hr_id ON t_job(hr_id);
CREATE INDEX idx_job_status ON t_job(status);
CREATE INDEX idx_job_created_at ON t_job(created_at);
CREATE INDEX idx_job_location ON t_job(location);
CREATE INDEX idx_job_type ON t_job(job_type);

-- 匹配记录表索引
CREATE INDEX idx_match_resume_id ON t_match_record(resume_id);
CREATE INDEX idx_match_job_id ON t_match_record(job_id);
CREATE INDEX idx_match_score ON t_match_record(match_score);
CREATE INDEX idx_match_status ON t_match_record(status);
CREATE INDEX idx_match_user_id ON t_match_record(user_id);
CREATE INDEX idx_match_created_at ON t_match_record(created_at);

-- 联合索引（优化常见查询）
CREATE INDEX idx_match_resume_job ON t_match_record(resume_id, job_id);
CREATE INDEX idx_match_user_score ON t_match_record(user_id, match_score DESC);

-- 用户表索引
CREATE INDEX idx_user_status ON t_user(status);
CREATE INDEX idx_user_role ON t_user(role);

-- AI调用日志表索引
CREATE INDEX idx_ai_log_user_id ON t_ai_call_log(user_id);
CREATE INDEX idx_ai_log_created_at ON t_ai_call_log(created_at);

-- 异步任务表索引
CREATE INDEX idx_async_status ON t_async_task(status);
CREATE INDEX idx_async_type ON t_async_task(task_type);

-- ===============================================
-- 查看索引
-- ===============================================
-- SHOW INDEX FROM t_resume;
-- SHOW INDEX FROM t_job;
-- SHOW INDEX FROM t_match_record;
