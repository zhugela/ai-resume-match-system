# AI简历分析 + 岗位匹配系统

> 基于 Spring Boot + Vue3 的智能简历分析全栈项目

## 🏠 项目介绍

一个能够智能分析简历、匹配岗位的系统。用户上传简历后，AI会自动提取结构化信息（技能、工作经历、教育背景等），并与岗位JD进行多维度匹配，生成匹配度评分和优化建议。

## 🛠 技术栈

### 后端
- **框架**: Spring Boot 3.2
- **数据库**: MySQL 8.x
- **缓存**: Redis
- **ORM**: MyBatis Plus
- **认证**: JWT

### 前端
- **框架**: Vue 3 + Composition API
- **UI库**: Element Plus
- **路由**: Vue Router
- **状态管理**: Pinia
- **构建工具**: Vite

## 📁 项目结构

```
ai-resume-match-system/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/liangge/resumematch/
│   │       ├── config/         # 配置类
│   │       ├── controller/    # 控制器
│   │       ├── service/       # 业务逻辑
│   │       ├── mapper/        # 数据访问
│   │       ├── entity/        # 实体类
│   │       ├── dto/           # 数据传输对象
│   │       └── security/      # 安全认证
│   └── resources/
│       └── application.yml    # 配置文件
│
├── web/                       # Vue3 前端
│   ├── src/
│   │   ├── api/               # API 接口
│   │   ├── views/            # 页面组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # Pinia 状态管理
│   │   └── styles/           # 全局样式
│   └── package.json
│
├── sql/
│   └── init.sql              # 数据库初始化脚本
│
└── README.md
```

## 🚀 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 16+
- MySQL 8.x
- Redis 6.x

### 2. 初始化数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行初始化脚本
source sql/init.sql
```

### 3. 启动后端

```bash
cd backend

# 修改配置文件，配置 MySQL 和 Redis
# 编辑 src/main/resources/application.yml

# 编译运行
mvn clean install
mvn spring-boot:run
```

后端启动成功后，访问 http://localhost:8080

### 4. 启动前端

```bash
cd web

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动成功后，访问 http://localhost:3000

### 5. 测试账号

```
用户名: test_user
密码: password123
```

## 📱 功能演示

### 用户模块
- [x] 用户注册/登录
- [x] JWT 认证
- [x] 个人信息管理
- [x] 修改密码

### 简历模块
- [x] 简历上传 (支持 PDF/DOCX/TXT)
- [x] 简历列表查看
- [x] AI 简历分析
- [x] 简历删除

### 岗位模块
- [x] 岗位列表查询
- [x] 岗位详情查看
- [x] 岗位发布/编辑
- [x] 岗位搜索

### 匹配模块
- [x] 智能岗位匹配
- [x] 匹配度评分
- [x] 技能对比分析
- [x] 优化建议生成

## �接口文档

| 模块 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 认证 | POST | /api/auth/register | 用户注册 |
| 认证 | POST | /api/auth/login | 用户登录 |
| 简历 | POST | /api/resume/upload | 上传简历 |
| 简历 | GET | /api/resume/list | 简历列表 |
| 简历 | POST | /api/resume/{id}/analyze | AI分析 |
| 岗位 | GET | /api/job/list | 岗位列表 |
| 岗位 | POST | /api/job | 发布岗位 |
| AI | POST | /api/ai/match-job | 岗位匹配 |

## 🎯 项目亮点

1. **AI 智能分析**: 接入大模型 API，实现简历结构化提取
2. **多维匹配**: 基于技能、经验、学历等多维度计算匹配度
3. **完整业务流**: 从简历上传到匹配结果，形成闭环
4. **工程化设计**: 分层架构、缓存优化、异步处理

## 📝 简历写法参考

```
AI简历分析与岗位匹配系统 | Spring Boot | Vue3 | MySQL | Redis | JWT

- 独立设计并实现 AI 简历分析系统，支持简历文本解析、岗位 JD 分析、候选人匹配评分与优化建议生成
- 封装大模型调用层，统一处理 Prompt 构造、JSON 结果解析、异常重试与结果落库
- 基于多维评分规则输出匹配结果，增强分析可解释性
- 针对 AI 分析耗时问题，引入异步任务机制与 Redis 缓存优化性能
```

## 📄 License

MIT License
