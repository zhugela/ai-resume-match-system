# 项目结构说明

## 一句话介绍
这是一个 **AI简历匹配系统**，用微服务架构拆分成多个独立服务。

---

## 目录结构

```
ai-resume-match-system/
│
├── gateway/              # API网关 - 所有请求的入口
├── auth-service/         # 鉴权服务 - 登录注册
├── resume-service/       # 简历服务 - 简历上传解析
├── job-service/          # 岗位服务 - 岗位发布管理
├── ai-service/           # AI服务 - 简历匹配分析
├── common/               # 公共模块 - 共用代码
│
├── web/                  # 前端 Vue3 代码
├── sql/                  # 数据库初始化脚本
├── docker-compose.yml    # Docker一键部署配置
└── pom.xml               # Maven父工程配置
```

---

## 各模块详解

### 1. gateway（API网关）
**端口：9000**

所有请求先到这里，网关负责：
- 路由转发（把请求分发给对应服务）
- JWT鉴权（检查token是否有效）
- 请求日志（记录所有请求）

**关键文件：**
- `GatewayApplication.java` - 启动类
- `AuthFilter.java` - 鉴权过滤器
- `application.yml` - 路由配置

---

### 2. auth-service（鉴权服务）
**端口：8081**

负责用户登录注册：
- 用户注册
- 用户登录 → 返回JWT token
- Token刷新

**关键文件：**
- `AuthController.java` - 登录注册接口
- `AuthService.java` - 业务逻辑
- `User.java` - 用户实体

---

### 3. resume-service（简历服务）
**端口：8082**

负责简历管理：
- 上传简历（PDF/DOCX）
- 解析简历内容
- 简历版本管理
- 存储到MinIO

**关键文件：**
- `ResumeController.java` - 简历接口
- `ResumeService.java` - 业务逻辑
- `ResumeParser.java` - 解析PDF/DOCX

---

### 4. job-service（岗位服务）
**端口：8083**

负责岗位管理：
- 发布岗位
- 编辑岗位
- 查询岗位列表

**关键文件：**
- `JobController.java` - 岗位接口
- `JobService.java` - 业务逻辑
- `Job.java` - 岗位实体

---

### 5. ai-service（AI分析服务）
**端口：8084**

负责AI匹配分析：
- 简历 vs 岗位匹配分析
- 批量匹配（一份简历匹配多个岗位）
- 语义匹配（用向量相似度）
- AI调用（MiniMax/OpenAI）

**关键文件：**
- `AnalysisController.java` - 分析接口
- `AIService.java` - AI调用
- `BatchMatchService.java` - 批量匹配
- `SemanticMatchService.java` - 语义匹配
- `CacheService.java` - 缓存

---

### 6. common（公共模块）
**无端口**

存放所有服务共用的代码：
- `Result.java` - 统一返回格式
- `BusinessException.java` - 业务异常
- `JwtUtil.java` - JWT工具类
- `UserInfoDTO.java` - 用户信息DTO

---

## 前端（web/）

Vue3 + Element Plus + Vite

**关键文件：**
- `src/views/` - 页面组件
- `src/api/` - API调用
- `src/router/` - 路由配置

---

## 数据库（sql/）

- `init.sql` - 初始化表结构
- `upgrade.sql` - 升级脚本

**主要表：**
- `user` - 用户表
- `resume` - 简历表
- `job` - 岗位表
- `match_record` - 匹配记录表
- `match_analysis` - 分析结果表

---

## 启动顺序

1. 启动基础设施：MySQL、Redis、Nacos
2. 启动 gateway
3. 启动 auth-service
4. 启动 resume-service
5. 启动 job-service
6. 启动 ai-service

---

## 访问地址

| 服务 | 地址 |
|------|------|
| API网关 | http://localhost:9000 |
| Nacos控制台 | http://localhost:8848/nacos |

---

## API接口示例

```
POST /api/auth/register    # 注册
POST /api/auth/login       # 登录
GET  /api/resume/list      # 简历列表
POST /api/resume/upload    # 上传简历
GET  /api/job/list         # 岗位列表
POST /api/analysis/match   # 匹配分析
```