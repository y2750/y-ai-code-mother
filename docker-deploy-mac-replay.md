# Docker 部署复现文档（Mac / Linux 通用）

本文档用于完整复现本项目通过 Docker 在服务器上运行的过程，包含：

- 必须创建的文件
- 必须修改的文件
- 每个文件的完整内容
- 首次启动命令
- 数据库重建与排错要点

适用场景：

- 在 Mac 上准备代码并提交
- 在 Linux 服务器上通过 `docker compose` 启动
- 临时测试环境
- 密码、密钥继续保存在 `application.yml` 和 `application-local.yml`

当前部署目标地址：

- `http://118.195.128.174/`

---

## 1. 项目目录要求

项目根目录下必须包含：

```text
y-ai-code-mother/
├─ .dockerignore
├─ Dockerfile.backend
├─ docker-compose.yml
├─ pom.xml
├─ sql/
│  └─ create_table.sql
├─ src/
│  └─ main/
│     ├─ java/
│     └─ resources/
├─ .mvn/
├─ mvnw
└─ y-ai-code-mother-frontend/
   ├─ .dockerignore
   ├─ Dockerfile.frontend
   ├─ nginx.conf
   ├─ package.json
   ├─ package-lock.json
   └─ src/
```

注意：

- 前端目录名必须是 `y-ai-code-mother-frontend`
- 如果服务器上的前端目录名不同，必须同步修改 `docker-compose.yml`

---

## 2. 必须修改的已有文件

### 2.1 `src/main/resources/application.yml`

作用：

- 后端连接 Docker 内部的 MySQL 和 Redis

完整内容：

```yaml
spring:
  application:
    name: y-ai-code-mother-backend
  session:
    store-type: redis
    timeout: 2592000
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://mysql:3306/y_ai_code_mother?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: a600523
  profiles:
    active: local
  data:
    redis:
      host: redis
      port: 6379
      password:
      ttl: 3600
      database: 0

server:
  port: 8000
  servlet:
    context-path: /api
    session:
      cookie:
        max-age: 2592000

springdoc:
  group-configs:
    - group: 'default'
      paths-to-match: '/**'
      packages-to-scan: com.yy.yaicodemother.controller

knife4j:
  enable: true
  setting:
    language: zh_cn
```

关键改动：

- MySQL 地址从 `localhost` 改成 `mysql`
- Redis 地址从 `localhost` 改成 `redis`

---

### 2.2 `src/main/resources/application-local.yml`

作用：

- 保留临时测试环境使用的 AI 密钥、COS 密钥等

说明：

- 这个文件继续保留在项目中
- 不额外迁移到环境变量
- 内容按你自己当前有效密钥为准

当前文件中已经包含：

- DeepSeek API Key
- DashScope API Key
- COS SecretId / SecretKey
- Pexels API Key

如果明天换机器复现，只需要确保这个文件仍然存在即可。

---

### 2.3 `src/main/java/com/yy/yaicodemother/constant/AppConstant.java`

作用：

- 设置部署后生成链接时使用的主机地址

完整内容：

```java
package com.yy.yaicodemother.constant;

public interface AppConstant {

    /**
     * 精选应用的优先级
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署目录
     */
    String CODE_DEPLOY_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_deploy";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://118.195.128.174";

}
```

---

### 2.4 `pom.xml`

作用：

- 修复后端 Docker 构建时报错

必须确认 `spring-boot-maven-plugin` 里的 `exclude` **不要包含 `<version>`**

正确写法：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
            </exclude>
        </excludes>
    </configuration>
</plugin>
```

错误写法示例：

```xml
<exclude>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.36</version>
</exclude>
```

如果保留 `<version>`，后端镜像构建时会报：

```text
Unable to parse configuration of mojo ... for parameter version
```

---

### 2.5 `y-ai-code-mother-frontend/src/config/env.ts`

作用：

- 前端默认请求服务器 IP，不再指向 localhost

完整内容：

```ts
/**
 * 环境变量配置
 */
import {CodeGenTypeEnum} from "@/utils/codeGenTypes.ts";

// 应用部署域名
export const DEPLOY_DOMAIN = import.meta.env.VITE_DEPLOY_DOMAIN || 'http://118.195.128.174'

// API 基础地址
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://118.195.128.174/api'

// 静态资源地址
export const STATIC_BASE_URL = `${API_BASE_URL}/static`

// 获取部署应用的完整 URL
export const getDeployUrl = (deployKey: string) => {
  return `${DEPLOY_DOMAIN}/${deployKey}`
}

// 获取静态资源预览 URL
export const getStaticPreviewUrl = (codeGenType: string, appId: string) => {
  const baseUrl = `${STATIC_BASE_URL}/${codeGenType}_${appId}/`
  if (codeGenType === CodeGenTypeEnum.VUE_PROJECT) {
    return `${baseUrl}dist/index.html`
  }
  return baseUrl
}
```

---

### 2.6 `y-ai-code-mother-frontend/src/pages/admin/UserManagePage.vue`

作用：

- 修复前端 Docker 构建时的 TypeScript 报错

必须修改这一行：

```ts
const doDelete = async (id?: number) => {
```

不要写成：

```ts
const doDelete = async (id: string) => {
```

否则构建时报：

```text
Type 'string' is not assignable to type 'number'
```

---

## 3. 必须新建的文件

### 3.1 根目录 `.dockerignore`

路径：

- `.dockerignore`

内容：

```dockerignore
.git
.idea
target
node_modules
tmp
y-ai-code-mother-frontend/node_modules
y-ai-code-mother-frontend/dist
```

作用：

- 减少后端构建上下文
- 防止无关目录进入镜像构建过程

---

### 3.2 后端 Dockerfile

路径：

- `Dockerfile.backend`

内容：

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /app/tmp/code_output /app/tmp/code_deploy

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### 3.3 `docker-compose.yml`

路径：

- `docker-compose.yml`

内容：

```yaml
version: "3.9"

services:
  mysql:
    image: mysql:8.4
    container_name: yai-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: a600523
      MYSQL_DATABASE: y_ai_code_mother
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/create_table.sql:/docker-entrypoint-initdb.d/create_table.sql

  redis:
    image: redis:7.4-alpine
    container_name: yai-redis
    restart: unless-stopped
    command: redis-server --appendonly yes
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  backend:
    build:
      context: .
      dockerfile: Dockerfile.backend
    container_name: yai-backend
    restart: unless-stopped
    depends_on:
      - mysql
      - redis
    ports:
      - "8000:8000"
    volumes:
      - ./tmp:/app/tmp

  frontend:
    build:
      context: ./y-ai-code-mother-frontend
      dockerfile: Dockerfile.frontend
    container_name: yai-frontend
    restart: unless-stopped
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  mysql_data:
  redis_data:
```

说明：

- `version: "3.9"` 会有过时警告，但不影响运行
- 想消除警告，可删除 `version:` 这一行

---

### 3.4 前端 `.dockerignore`

路径：

- `y-ai-code-mother-frontend/.dockerignore`

内容：

```dockerignore
node_modules
dist
.git
.idea
```

作用：

- 避免宿主机上的前端 `node_modules` 覆盖容器内 `npm ci` 安装的依赖
- 避免构建时报 `run-p: Permission denied`

---

### 3.5 前端 Dockerfile

路径：

- `y-ai-code-mother-frontend/Dockerfile.frontend`

内容：

```dockerfile
FROM node:22-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

RUN npm run build

FROM nginx:1.27-alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
```

---

### 3.6 前端 nginx 配置

路径：

- `y-ai-code-mother-frontend/nginx.conf`

内容：

```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    client_max_body_size 50m;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8000/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 3600;
        proxy_send_timeout 3600;
    }
}
```

---

## 4. SQL 文件要求

路径：

- `sql/create_table.sql`

必须包含：

- `app` 表
- `chat_history` 表
- `user` 表

并且如果你需要初始化测试账号，`user` 表下面还要带 `INSERT INTO user ...`

这次实际踩到的问题是：

- 旧 SQL 里没有 `user` 表
- 导致登录接口 `/api/user/login` 报 `50000 系统错误`

---

## 5. Mac 上复现时的建议步骤

### 5.1 本地准备代码

在 Mac 上确认上述文件都已存在并更新到最新内容。

建议先检查：

```bash
ls
ls y-ai-code-mother-frontend
```

---

### 5.2 提交或打包代码

如果你通过 Git 推送：

```bash
git add .
git commit -m "chore: add docker deployment files"
git push
```

如果你通过压缩包上传：

- 确保把整个项目根目录都传上去
- 尤其不要漏掉：
  - `y-ai-code-mother-frontend`
  - `Dockerfile.backend`
  - `docker-compose.yml`
  - `sql/create_table.sql`

---

### 5.3 服务器首次启动

进入项目目录：

```bash
cd /root/y-ai-code-mother
mkdir -p tmp
```

启动：

```bash
docker compose up -d --build
```

查看状态：

```bash
docker compose ps
```

查看日志：

```bash
docker compose logs --tail=100 backend
docker compose logs --tail=100 frontend
```

---

## 6. 如果数据库初始化不完整怎么办

MySQL 容器会只在“首次初始化数据目录时”执行：

```text
/docker-entrypoint-initdb.d/create_table.sql
```

如果你改了 SQL，但数据库卷已经存在，新的 SQL 不会自动重新执行。

### 解决方法：重建数据卷

```bash
cd /root/y-ai-code-mother
docker compose down
docker volume ls
docker volume rm y-ai-code-mother_mysql_data
docker volume rm y-ai-code-mother_redis_data
docker compose up -d
```

然后检查表：

```bash
docker exec -it yai-mysql mysql -uroot -pa600523 -e "USE y_ai_code_mother; SHOW TABLES;"
```

---

## 7. 这次实际遇到过的问题清单

### 7.1 后端构建失败

现象：

```text
Unable to parse configuration of mojo ... for parameter version
```

原因：

- `pom.xml` 中 `spring-boot-maven-plugin` 的 `exclude` 写了 `<version>`

解决：

- 删除 `<version>`

---

### 7.2 前端目录不存在

现象：

```text
unable to prepare context: path ".../y-ai-code-mother-frontend" not found
```

原因：

- 前端目录没上传
- 或前端目录名和 compose 不一致

解决：

- 保证目录存在
- 或修改 `docker-compose.yml`

---

### 7.3 前端构建时报 `run-p: Permission denied`

原因：

- 宿主机前端 `node_modules` 被 `COPY . .` 带进镜像，覆盖了容器内执行文件权限

解决：

- 增加 `y-ai-code-mother-frontend/.dockerignore`

---

### 7.4 前端构建时报 TS 类型错误

现象：

```text
Type 'string' is not assignable to type 'number'
```

位置：

- `y-ai-code-mother-frontend/src/pages/admin/UserManagePage.vue`

解决：

- 把 `doDelete` 参数改成 `id?: number`

---

### 7.5 登录时报 `50000 系统错误`

原因：

- 数据库里没有 `user` 表

解决：

- 更新 `sql/create_table.sql`
- 重建 MySQL 数据卷

---

## 8. 启动成功后的访问地址

- 前端首页：`http://118.195.128.174/`
- 后端接口文档：`http://118.195.128.174/api/doc.html`

---

## 9. 最终复现清单

在 Mac 上复现时，必须确认以下项目全部完成：

- 已修改 `src/main/resources/application.yml`
- 已保留 `src/main/resources/application-local.yml`
- 已修改 `src/main/java/com/yy/yaicodemother/constant/AppConstant.java`
- 已修复 `pom.xml` 中 `spring-boot-maven-plugin` 配置
- 已修改 `y-ai-code-mother-frontend/src/config/env.ts`
- 已修改 `y-ai-code-mother-frontend/src/pages/admin/UserManagePage.vue`
- 已创建根目录 `.dockerignore`
- 已创建 `Dockerfile.backend`
- 已创建 `docker-compose.yml`
- 已创建前端 `.dockerignore`
- 已创建 `y-ai-code-mother-frontend/Dockerfile.frontend`
- 已创建 `y-ai-code-mother-frontend/nginx.conf`
- 已确认 `sql/create_table.sql` 包含 `user` 表
- 已确认上传时没有漏掉前端目录

只要上面这份清单全部满足，明天在 Mac 上准备好代码后，再传到 Linux 服务器，按文档执行即可复现。
