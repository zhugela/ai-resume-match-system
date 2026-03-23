@echo off
mysql -u root -pliang resume_match -e "CREATE TABLE IF NOT EXISTS t_resume_version (id BIGINT PRIMARY KEY AUTO_INCREMENT, resume_id BIGINT NOT NULL, version_number INT NOT NULL, original_content TEXT, optimized_content TEXT, optimization_suggestions TEXT, interview_questions TEXT, match_score DECIMAL(5,2), target_job_id BIGINT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, INDEX idx_resume_id (resume_id), INDEX idx_version_number (version_number), FOREIGN KEY (resume_id) REFERENCES t_resume(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"

mysql -u root -pliang resume_match -e "CREATE TABLE IF NOT EXISTS t_export_record (id BIGINT PRIMARY KEY AUTO_INCREMENT, resume_id BIGINT NOT NULL, version_id BIGINT, export_type VARCHAR(20), file_url VARCHAR(500), file_name VARCHAR(255), created_at DATETIME DEFAULT CURRENT_TIMESTAMP, INDEX idx_resume_id (resume_id), FOREIGN KEY (resume_id) REFERENCES t_resume(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"

mysql -u root -pliang resume_match -e "CREATE TABLE IF NOT EXISTS t_match_analysis (id BIGINT PRIMARY KEY AUTO_INCREMENT, resume_id BIGINT NOT NULL, job_id BIGINT NOT NULL, overall_score DECIMAL(5,2), skill_match_details JSON, experience_match_details JSON, education_match_details JSON, missing_skills JSON, suggested_improvements TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, UNIQUE KEY uk_resume_job (resume_id, job_id), FOREIGN KEY (resume_id) REFERENCES t_resume(id) ON DELETE CASCADE, FOREIGN KEY (job_id) REFERENCES t_job(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"

echo Tables created successfully!
pause
