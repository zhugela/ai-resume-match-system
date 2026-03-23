# 快速启动指南

## 1. 安装 MySQL 和 Redis（如果没安装）

### 使用 Docker（推荐）
```bash
# 启动 MySQL
docker run -d --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=liang -e MYSQL_DATABASE=resume_match mysql:8

# 启动 Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

### 或者直接安装
- MySQL: https://dev.mysql.com/downloads/mysql/
- Redis: https://github.com/tporadowski/redis/releases

---

## 2. 初始化数据库

打开 MySQL 客户端（如 Navicat、MySQL Workbench），执行：

```sql
-- 执行初始化脚本
SOURCE D:/code/ai-resume-match-system/sql/init.sql;

-- 执行测试数据
SOURCE D:/code/ai-resume-match-system/sql/test_data.sql;
```

---

## 3. 启动后端

用 IDEA 打开项目，找到 `ResumeMatchApplication.java`，右键 Run

---

## 4. 启动前端

```bash
cd D:\code\ai-resume-match-system\web
npm run dev
```

---

## 5. 测试账号

| 账号 | 密码 | 角色 |
|------|------|------|
| liangge | 123456 | 普通用户 |
| admin | 123456 | 管理员 |
| hr_zhang | 123456 | HR |

---

## 测试数据已准备

`sql/test_data.sql` 包含：
- 6个测试用户
- 15个岗位
- 8条收藏记录（liangge 用户）
- 3份简历
