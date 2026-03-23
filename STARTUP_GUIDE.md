# 一键启动指南（上线部署版）

## 前提条件

### 1. 安装 Docker Desktop
下载地址：https://www.docker.com/products/docker-desktop/

安装后双击打开，等右下角小鲸鱼图标变绿。

---

## 一键启动（复制粘贴运行）

### Windows 用户
打开 PowerShell，复制粘贴这个命令：

```powershell
cd D:\code\ai-resume-match-system; docker-compose up -d
```

### Mac/Linux 用户
打开终端：

```bash
cd ~/code/ai-resume-match-system; docker-compose up -d
```

---

## 验证是否启动成功

```bash
docker ps
```

看到这些容器就成功了：
- mysql-resume
- redis-resume
- nacos
- app (你的Java应用)

---

## 访问地址

| 服务 | 地址 |
|------|------|
| 你的网站 | http://localhost:8080 |
| 数据库管理 | http://localhost:8081 (phpMyAdmin) |
| Redis管理 | http://localhost:8082 (RedisInsight) |
| Nacos监控 | http://localhost:8848/nacos |

---

## 常用命令

```bash
# 查看日志
docker-compose logs -f

# 查看Java应用日志
docker-compose logs -f app

# 停止
docker-compose down

# 重启
docker-compose restart

# 重新构建并启动
docker-compose up -d --build
```

---

## 如果启动失败

### 1. Docker没启动
- 双击打开 Docker Desktop
- 等小鲸鱼变绿

### 2. 端口被占用
```bash
netstat -ano | findstr "8080"
```
关闭占用端口的程序。

### 3. MySQL连接失败
确保MySQL容器健康：
```bash
docker ps
```
看 MySQL 状态是否是 healthy。

---

## 需要安装的软件

只需要安装 **Docker Desktop** 这一个软件就够了！

下载地址：https://www.docker.com/products/docker-desktop/

---

## 项目功能清单

✅ 用户注册/登录
✅ 简历上传（PDF/DOCX）
✅ 简历解析
✅ 岗位发布
✅ AI智能匹配
✅ 匹配结果展示
✅ 用户管理

---

## 技术栈

- Java 21 + Spring Boot 3.4
- MySQL 8.0
- Redis 7.2
- Nacos (服务管理)
- MinIO (文件存储)
- RabbitMQ (消息队列)
