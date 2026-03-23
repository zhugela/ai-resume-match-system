-- 面试题库表
CREATE TABLE IF NOT EXISTS t_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL COMMENT '分类：Java/Python/前端/数据库等',
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM' COMMENT '难度',
    question TEXT NOT NULL COMMENT '题目',
    answer TEXT COMMENT '答案',
    tags JSON COMMENT '标签',
    views INT DEFAULT 0 COMMENT '浏览次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试题库';

-- 简历模板表
CREATE TABLE IF NOT EXISTS t_resume_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '模板名称',
    description VARCHAR(500) COMMENT '描述',
    file_url VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(20) COMMENT '文件类型',
    preview_url VARCHAR(500) COMMENT '预览图',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    is_premium BOOLEAN DEFAULT FALSE COMMENT '是否付费',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历模板';

-- 求职进度表
CREATE TABLE IF NOT EXISTS t_application_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_id BIGINT COMMENT '岗位ID',
    company VARCHAR(200) COMMENT '公司名称',
    position VARCHAR(200) COMMENT '职位名称',
    status ENUM('APPLIED', 'REVIEWING', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN') DEFAULT 'APPLIED' COMMENT '状态',
    salary VARCHAR(50) COMMENT '薪资',
    interview_time DATETIME COMMENT '面试时间',
    notes TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='求职进度';

-- 插入面试题库初始数据
INSERT INTO t_question (category, difficulty, question, answer, tags) VALUES
('Python', 'EASY', 'Python中list和tuple的区别是什么？', 'list是可变的，tuple是不可变的。list用[]表示，tuple用()表示。tuple可以作为字典的key，而list不行。', '["基础", "数据类型"]'),
('Python', 'EASY', 'Python中的深拷贝和浅拷贝区别？', '浅拷贝只拷贝引用地址，深拷贝递归拷贝所有内容。使用copy模块的copy()和deepcopy()函数。', '["基础", "内存"]'),
('Python', 'MEDIUM', 'Python中的装饰器是什么？', '装饰器是一个函数，用于在不修改原函数的情况下增强其功能。语法是@decorator放在函数定义上方。', '["进阶", "装饰器"]'),
('Python', 'MEDIUM', 'Python中的生成器是什么？', '生成器是一种特殊的迭代器，使用yield关键字来返回值。每次调用只生成一个值，节省内存。', '["进阶", "生成器"]'),
('Python', 'HARD', 'Python中的GIL是什么？', 'GIL是全局解释器锁，导致Python多线程无法真正并行执行。适用于IO密集型任务，不适用于CPU密集型任务。', '["进阶", "并发"]'),
('Java', 'EASY', 'Java中==和equals的区别？', '==比较的是引用地址，equals默认比较引用但通常被重写比较内容。String类的equals被重写为比较字符串内容。', '["基础", "对比"]'),
('Java', 'MEDIUM', 'Spring Boot的自动配置原理？', '通过@EnableAutoConfiguration注解，基于classpath中的依赖自动配置Bean。使用条件注解如@ConditionalOnClass判断是否配置。', '["框架", "Spring"]'),
('Java', 'HARD', 'HashMap的底层实现原理？', 'JDK8之前是数组+链表，JDK8之后是数组+链表+红黑树。当链表长度>8且数组长度>64时转换为红黑树。', '["进阶", "集合"]'),
('前端', 'EASY', 'CSS中display:none和visibility:hidden的区别？', 'display:none不占用空间，visibility:hidden占据空间。display影响布局，visibility不影响。', '["基础", "CSS"]'),
('前端', 'MEDIUM', 'Vue的生命周期有哪些？', 'beforeCreate, created, beforeMount, mounted, beforeUpdate, updated, beforeUnmount, unmounted。常用的是created和mounted。', '["框架", "Vue"]'),
('数据库', 'EASY', 'MySQL中innodb和myisam的区别？', 'innodb支持事务和行级锁，myisam不支持事务只支持表级锁。innodb支持外键，myisam不支持。', '["基础", "存储引擎"]'),
('数据库', 'MEDIUM', '什么是索引？索引的优缺点？', '索引是数据结构，提高查询速度。优点：加快检索速度。缺点：占用磁盘空间，增加写操作时间。', '["进阶", "索引"]'),
('计算机基础', 'EASY', 'HTTP和HTTPS的区别？', 'HTTPS是HTTP+SSL/TLS加密传输，更安全。HTTPS默认端口443，HTTP是80。', '["基础", "网络"]'),
('计算机基础', 'MEDIUM', 'TCP三次握手和四次挥手？', '三次握手：SYN->SYN+ACK->ACK。四次挥手：FIN->ACK->FIN->ACK。', '["进阶", "网络"]');

-- 插入简历模板初始数据
INSERT INTO t_resume_template (name, description, file_url, file_type) VALUES
('简约大气版', '适合技术岗位，简洁清晰', '/templates/resume1.docx', 'DOCX'),
('互联网大厂版', '适合BAT等大厂，结构化强', '/templates/resume2.docx', 'DOCX'),
('经典稳重版', '适合传统企业，成熟稳重', '/templates/resume3.docx', 'DOCX'),
('创意设计版', '适合设计岗位，视觉效果好', '/templates/resume4.docx', 'DOCX');

-- 插入薪资查询初始数据
CREATE TABLE IF NOT EXISTS t_salary_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    company VARCHAR(100) NOT NULL COMMENT '公司',
    position VARCHAR(100) NOT NULL COMMENT '职位',
    location VARCHAR(50) COMMENT '城市',
    experience VARCHAR(20) COMMENT '经验要求',
    salary_low INT COMMENT '最低薪资(K)',
    salary_high INT COMMENT '最高薪资(K)',
    salary_mid INT COMMENT '中位薪资(K)',
    sample_count INT DEFAULT 0 COMMENT '样本数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_company (company),
    INDEX idx_position (position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪资数据';

INSERT INTO t_salary_data (company, position, location, experience, salary_low, salary_high, salary_mid, sample_count) VALUES
('字节跳动', 'Python后端', '北京', '1-3年', 20, 40, 30, 150),
('字节跳动', 'Python后端', '北京', '3-5年', 30, 55, 42, 200),
('阿里巴巴', 'Java开发', '杭州', '1-3年', 18, 35, 26, 180),
('阿里巴巴', 'Java开发', '杭州', '3-5年', 28, 50, 38, 220),
('腾讯', 'Go开发', '深圳', '3-5年', 25, 48, 36, 160),
('百度', '算法工程师', '北京', '3-5年', 30, 60, 45, 140),
('美团', '后端开发', '北京', '1-3年', 18, 32, 25, 120),
('快手', 'Python开发', '北京', '3-5年', 25, 45, 35, 100),
('拼多多', 'Java开发', '上海', '3-5年', 25, 50, 37, 90),
('网易', '前端开发', '杭州', '1-3年', 15, 28, 22, 110);

SELECT 'Tables created successfully!' as result;
