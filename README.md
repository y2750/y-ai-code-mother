# 智码应用生成平台

一个基于 Spring Boot + LangChain4j + LangGraph4j 的 AI 应用生成后端平台。  
支持通过自然语言创建应用、流式生成代码、部署预览、下载源码，以及管理用户和对话历史。

## 项目特性

- AI 代码生成：根据提示词自动选择生成策略（如 HTML / Vue 项目等）
- 流式返回：基于 SSE / Flux 实现实时代码生成反馈
- 应用全生命周期：创建、更新、删除、分页查询、部署、下载
- 对话历史管理：记录用户与 AI 的消息，支持按应用游标分页查询
- 鉴权与权限：基于登录态 + 角色（管理员 / 普通用户）接口权限控制
- 速率限制：对高频接口做限流保护（基于 Redis + Redisson）
- 可视化调试：集成 Knife4j，便于接口联调

## 技术栈

- Java 21
- Spring Boot 3.5.x
- MyBatis-Flex
- MySQL 8.x
- Redis（Session、缓存、限流）
- LangChain4j / LangGraph4j
- Reactor（Flux）
- Hutool / Lombok
- Knife4j(OpenAPI 3)

## 快速开始

### 1. 环境要求

- JDK 21
- Maven 3.9+
- MySQL 8+
- Redis 6+

### 2. 初始化数据库

执行脚本：

- `sql/create_table.sql`

默认数据库名可使用：`y_ai_code_mother`

### 3. 配置应用参数

项目主配置文件为：`src/main/resources/application.yml`  
建议按本地环境修改以下关键项：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.data.redis.host`
- `spring.data.redis.port`
- `spring.data.redis.password`

如需启用图片检索 / Logo 生成能力，可补充：

- `pexels.api-key`
- `dashscope.api-key`
- `dashscope.image-model`（可选，默认 `wan2.2-t2i-flash`）

### 4. 启动项目

```bash
mvn clean spring-boot:run
```

默认访问地址：

- 服务基址：`http://localhost:8000/api`
- Knife4j 文档：`http://localhost:8000/api/doc.html`

## 核心接口概览

> 以下均基于服务前缀 `/api`

### 用户模块（`/user`）

- `POST /user/register`：注册
- `POST /user/login`：登录
- `GET /user/get/login`：获取当前登录用户
- `POST /user/logout`：退出登录

### 应用模块（`/app`）

- `POST /app/add`：创建应用
- `GET /app/chat/gen/code`：流式对话生成代码（SSE）
- `POST /app/deploy`：部署应用
- `GET /app/download/{appId}`：下载应用代码压缩包
- `POST /app/update`：更新应用
- `POST /app/delete`：删除应用
- `GET /app/get/vo`：获取应用详情
- `POST /app/my/list/page/vo`：我的应用分页
- `POST /app/good/list/page/vo`：精选应用分页
- `POST /app/admin/list/page/vo`：管理员分页查询

### 对话历史（`/chatHistory`）

- `GET /chatHistory/app/{appId}`：按应用游标分页查询消息
- `POST /chatHistory/admin/list/page/vo`：管理员分页查询所有历史

### 工作流调试（`/workflow`）

- `POST /workflow/execute`：同步执行工作流
- `GET /workflow/execute-flux`：Flux 流式执行
- `GET /workflow/execute-sse`：SSE 流式执行

## 目录结构（核心）

```text
src/main/java/com/yy/yaicodemother
├─ ai                # AI 服务工厂、路由、Guardrail、工具
├─ controller        # 接口控制层
├─ service           # 业务服务层
├─ core              # 代码解析、保存、流处理、项目构建
├─ langgraph4j       # 工作流节点与状态编排
├─ mapper            # MyBatis-Flex Mapper
├─ model             # DTO / Entity / VO / Enum
├─ config            # 系统配置（模型、Redis、跨域等）
└─ ratelimiter       # 限流组件
```

## 运行说明

- 代码生成输出目录：`tmp/code_output`
- 部署目录：`tmp/code_deploy`
- 默认部署主机常量：`http://localhost`（见 `AppConstant`）

部署成功后可按部署标识访问静态资源路由：

- `GET /api/static/{deployKey}/`

## 常见问题

- 无法连接数据库：确认 MySQL 已启动、库名正确、账号密码可用
- Redis 报错：确认 Redis 启动且配置与 `application.yml` 一致
- AI 能力不可用：检查模型配置与 API Key 是否正确
- 部署后无页面：确认应用已先生成代码，再执行部署

## License

本项目当前未声明开源许可证，可按团队内部规范补充。

