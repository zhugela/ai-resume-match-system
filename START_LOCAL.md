# 本地启动指南（不用Docker）

## 前提条件

确保你本地已安装：
- ✅ Java 21
- ✅ MySQL（端口3306，用户root，密码liang）
- ✅ Redis（端口6379）

---

## 第一步：创建数据库

打开 MySQL，执行：
```sql
CREATE DATABASE IF NOT EXISTS resume_match DEFAULT CHARACTER SET utf8mb4;
```

然后执行项目里的初始化脚本：
```sql
USE resume_match;
SOURCE D:/code/ai-resume-match-system/sql/init.sql;
```

---

## 第二步：启动 Redis

如果你没装 Redis，可以用 Docker 只启动 Redis：
```bash
docker run -d --name redis -p 6379:6379 redis:7.2-alpine
```

---

## 第三步：IDEA 打开项目

1. 打开 IDEA
2. File → Open → 选择 `D:\code\ai-resume-match-system`
3. 等右下角 Maven 进度条跑完

---

## 第四步：按顺序启动服务

在 IDEA 里找到这些启动类，**按顺序**右键 Run：

| 顺序 | 服务 | 启动类 | 端口 |
|------|------|--------|------|
| 1 | Gateway | `gateway/.../GatewayApplication.java` | 9000 |
| 2 | Auth | `auth-service/.../AuthApplication.java` | 8081 |
| 3 | Resume | `resume-service/.../ResumeApplication.java` | 8082 |
| 4 | Job | `job-service/.../JobApplication.java` | 8083 |
| 5 | AI | `ai-service/.../AiApplication.java` | 8084 |

---

## 第五步：测试

浏览器访问：
```
http://localhost:9000
```

如果看到页面或者没报错，就成功了！

---

## 常见问题

### Q: 启动报错 "Cannot connect to MySQL"
A: 确保 MySQL 正在运行，用户名密码是 root/liang

### Q: 启动报错 "Cannot connect to Redis"
A: 确保 Redis 正在运行，端口是 6379

### Q: 端口被占用
A: 检查端口是否被其他程序占用，可以改 application.yml 里的端口号

---

## API 接口

| 接口 | 说明 |
|------|------|
| POST /api/auth/register | 注册 |
| POST /api/auth/login | 登录 |
| GET /api/resume/list | 简历列表 |
| POST /api/resume/upload | 上传简历 |
| GET /api/job/list | 岗位列表 |
| POST /api/analysis/match | 匹配分析 |