-- 测试数据 - 给亮哥的项目添加数据

USE resume_match;

-- 插入更多测试用户 (密码都是 123456，实际 hash 从原 init.sql 获取)
INSERT INTO t_user (username, password, email, phone, role) VALUES 
('liangge', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'liangge@example.com', '13800138000', 'USER'),
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@example.com', '13900139000', 'ADMIN'),
('hr_zhang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKOLMYSGe', 'hr_zhang@example.com', '13700137000', 'HR'),
('hr_li', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKOLMYSGe', 'hr_li@example.com', '13600136000', 'HR'),
('user_wang', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKOLMYSGe', 'wang@example.com', '13500135000', 'USER'),
('user_zhao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKOLMYSGe', 'zhao@example.com', '13400134000', 'USER')
ON DUPLICATE KEY UPDATE username=username;

-- 插入更多岗位（HR的id从3开始）
INSERT INTO t_job (hr_id, title, company, location, salary_range, job_type, description, requirements, required_skills, preferred_skills, status, views) VALUES
-- 字节跳动
(3, 'Python后端开发', '字节跳动', '北京', '25K-50K', 'FULL_TIME', '负责业务后端开发', '3-5年经验', '["Python", "Django", "Flask", "MySQL"]', '["Go", "Redis", "Kafka"]', 'OPEN', 200),
(3, 'Go开发工程师', '字节跳动', '北京', '28K-55K', 'FULL_TIME', '负责基础架构开发', '3-5年经验', '["Go", "Kubernetes", "Docker"]', '["Rust", "Python"]', 'OPEN', 180),
(3, '数据分析师', '字节跳动', '北京', '20K-40K', 'FULL_TIME', '负责数据分析工作', '2-4年经验', '["Python", "SQL", "Excel"]', '["Tableau", "SPSS"]', 'OPEN', 150),

-- 阿里巴巴
(3, 'Java技术专家', '阿里巴巴', '杭州', '30K-60K', 'FULL_TIME', '负责淘宝技术架构', '5年以上经验', '["Java", "Spring Boot", "Dubbo"]', '["K8s", "Service Mesh"]', 'OPEN', 250),
(3, '前端架构师', '阿里巴巴', '杭州', '28K-50K', 'FULL_TIME', '负责前端架构设计', '5年以上经验', '["React", "Vue", "TypeScript"]', '["Node.js", "Webpack"]', 'OPEN', 120),

-- 腾讯
(4, 'C++后台开发', '腾讯', '深圳', '25K-45K', 'FULL_TIME', '负责微信后台开发', '3-5年经验', '["C++", "Linux", "MySQL"]', '["Go", "Redis"]', 'OPEN', 220),
(4, 'AI产品经理', '腾讯', '深圳', '22K-40K', 'FULL_TIME', '负责AI产品规划', '3年以上经验', '["产品设计", "AI知识", "数据分析"]', '["机器学习", "深度学习"]', 'OPEN', 100),

-- 百度
(3, '推荐算法工程师', '百度', '北京', '35K-70K', 'FULL_TIME', '负责推荐系统算法', '3-5年经验', '["Python", "TensorFlow", "推荐系统"]', '["C++", "Spark"]', 'OPEN', 300),
(3, '搜索研发工程师', '百度', '北京', '28K-55K', 'FULL_TIME', '负责搜索引擎研发', '3-5年经验', '["Java", "Elasticsearch", "NLP"]', '["Go", "Hadoop"]', 'OPEN', 180),

-- 美团
(4, 'Python爬虫工程师', '美团', '北京', '18K-35K', 'FULL_TIME', '负责数据采集', '1-3年经验', '["Python", "Scrapy", "Redis"]', '["Playwright", "MongoDB"]', 'OPEN', 90),
(4, '后端开发工程师', '美团', '北京', '20K-40K', 'FULL_TIME', '负责外卖业务开发', '2-4年经验', '["Java", "Go", "MySQL"]', '["Redis", "Kafka"]', 'OPEN', 160),

-- 快手
(3, '大数据开发工程师', '快手', '北京', '25K-45K', 'FULL_TIME', '负责大数据平台开发', '3-5年经验', '["Hadoop", "Spark", "Hive"]', '["Flink", "Kafka"]', 'OPEN', 140),
(3, '短视频后端开发', '快手', '北京', '22K-42K', 'FULL_TIME', '负责短视频后端', '2-4年经验', '["Go", "FFmpeg", "CDN"]', '["Rust", "Python"]', 'OPEN', 110),

-- 拼多多
(4, 'Java开发', '拼多多', '上海', '25K-50K', 'FULL_TIME', '负责电商后端', '3-5年经验', '["Java", "Spring", "MySQL"]', '["Redis", "分库分表"]', 'OPEN', 200),

-- 滴滴
(3, '地图研发工程师', '滴滴', '北京', '22K-42K', 'FULL_TIME', '负责地图服务研发', '2-4年经验', '["Java", "Go", "PostgreSQL"]', '["GeoHash", "Redis"]', 'OPEN', 80)
ON DUPLICATE KEY UPDATE title=title;

-- 为用户1（liangge）插入收藏记录
INSERT INTO t_favorite (user_id, job_id) VALUES 
(1, 1),  -- 收藏 Java开发工程师
(1, 2),  -- 收藏 前端开发工程师  
(1, 3),  -- 收藏 算法工程师
(1, 4),  -- 收藏 Python后端开发
(1, 5),  -- 收藏 Go开发工程师
(1, 8),  -- 收藏 推荐算法工程师
(1, 12), -- 收藏 大数据开发工程师
(1, 14)  -- 收藏 Java开发
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 为用户5（user_wang）插入收藏记录
INSERT INTO t_favorite (user_id, job_id) VALUES 
(5, 2),
(5, 4),
(5, 6)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- 插入一些简历数据
INSERT INTO t_resume (user_id, file_name, file_url, file_type, parsed_data, skills, work_experience, education, analysis_status, score) VALUES
(1, 'Python工程师简历.pdf', '/uploads/resumes/1.pdf', 'PDF', 
 '{"name":"亮哥","email":"liangge@example.com","phone":"13800138000","summary":"5年Python开发经验","projects":[{"name":"AI简历系统","tech":["Python","Vue","Spring"]}]}', 
 '["Python", "Django", "Flask", "MySQL", "Redis", "Vue", "Java"]', 
 '2020-至今 字节跳动 Python开发工程师', 
 '本科 计算机科学', 
 'COMPLETED', 85),

(1, 'Java开发简历.docx', '/uploads/resumes/2.docx', 'DOCX',
 '{"name":"亮哥Java","email":"liangge@example.com","phone":"13800138000","summary":"3年Java开发经验","projects":[{"name":"电商平台","tech":["Java","Spring Boot"]}]}',
 '["Java", "Spring Boot", "MySQL", "Redis", "Dubbo"]',
 '2018-2020 阿里 Java开发工程师',
 '本科 软件工程',
 'COMPLETED', 78),

(5, '王五简历.pdf', '/uploads/resumes/3.pdf', 'PDF',
 '{"name":"王五","email":"wang@example.com","phone":"13500135000","summary":"2年前端经验"}',
 '["Vue", "React", "JavaScript", "TypeScript"]',
 '2021-至今 拼多多 前端开发',
 '本科 前端开发',
 'COMPLETED', 72)
ON DUPLICATE KEY UPDATE file_name=file_name;

-- 验证数据
SELECT '用户数量' as info, COUNT(*) as count FROM t_user
UNION ALL
SELECT '岗位数量', COUNT(*) FROM t_job
UNION ALL
SELECT '简历数量', COUNT(*) FROM t_resume
UNION ALL
SELECT '收藏数量', COUNT(*) FROM t_favorite;

-- 查看用户
SELECT id, username, role FROM t_user;

-- 查看收藏
SELECT f.id, u.username, j.title, j.company 
FROM t_favorite f 
JOIN t_user u ON f.user_id = u.id 
JOIN t_job j ON f.job_id = j.id;
