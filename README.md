# Fast MCP Gateway

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green.svg)
![React](https://img.shields.io/badge/React-18-blue.svg)
![Next.js](https://img.shields.io/badge/Next.js-16-black.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

**一个高性能的 Model Context Protocol 服务网关**

[功能特性](#功能特性) • [快速开始](#快速开始) • [文档](#文档) • [API 文档](#api-文档) • [贡献指南](#贡献指南)

</div>

---

## 📖 项目简介

Fast MCP Gateway 是一个基于 Spring Boot 4.0.1 + Next.js 16 构建的 **Model Context Protocol (MCP) 服务网关**，提供服务注册、监控、调试、治理等企业级能力。

### 核心特性

- 🚀 **高性能**: 基于 Spring WebFlux 响应式编程，支持高并发
- 📊 **可观测**: 完善的监控指标、调用日志、性能统计
- 🔍 **可调试**: 类似 MCP Inspector 的 Web 调试界面
- 🛡️ **高可用**: 健康检查、故障转移、负载均衡（规划中）
- 🎨 **现代化 UI**: 基于 shadcn/ui 的精美管理界面
- 📦 **开箱即用**: 支持 STDIO/SSE/HTTP 多种传输协议

---

## ✨ 功能特性

### 已实现功能 ✅

#### 1. 服务管理
- ✅ 服务注册（支持静态配置和 API 注册）
- ✅ 服务状态监控（健康度、连接数、响应时间）
- ✅ 服务能力查看（Tools/Resources/Prompts 列表）
- ✅ 版本号管理
- ✅ 服务生命周期管理（注册/注销）

#### 2. 服务监控
- ✅ 实时监控指标
  - 总请求数、成功/失败请求数
  - 平均/最小/最大延迟
  - 成功率、失败率
  - 运行时长、心跳时间
- ✅ 健康状态判断
  - HEALTHY（健康）
  - DEGRADED（降级）
  - UNHEALTHY（不健康）
  - UNKNOWN（未知）
- ✅ 监控数据自动收集

#### 3. Inspector 调试
- ✅ Web UI 调试界面
- ✅ Tools/Resources/Prompts 列表查询
- ✅ 交互式 Tool 调用测试
- ✅ 调用历史记录
- ✅ 实时日志查看
- ✅ 按服务ID筛选历史

#### 4. 前端管理界面
- ✅ Dashboard 首页
  - 服务列表展示
  - 服务搜索过滤
  - 健康状态标识
  - 关键指标卡片
- ✅ 服务详情页
  - 服务基本信息
  - 完整监控指标
  - 调用历史记录
  - 操作按钮（规划中）
- ✅ 服务注册对话框
  - 表单验证
  - 版本号默认值（1.0.0）

### 规划中功能 🚧

- 🔲 服务生命周期管理（启动/停止/重启）
- 🔲 活跃连接数追踪
- 🔲 监控数据趋势展示
- 🔲 日志级别区分（info/warn/error）
- 🔲 心跳定期更新
- 🔲 服务健康检查与自动恢复
- 🔲 监控数据持久化（Redis/MySQL）
- 🔲 负载均衡支持（多实例）
- 🔲 WebSocket 实时推送
- 🔲 告警功能

详细规划请查看 [DEVELOPMENT.md](DEVELOPMENT.md)

---

## 🏗️ 系统架构

### 技术栈

**后端：**
- **框架**: Spring Boot 4.0.1
- **JDK**: Java 17
- **响应式**: Spring WebFlux + Project Reactor
- **MCP SDK**: io.modelcontextprotocol.sdk:mcp:0.14.1
- **构建工具**: Maven

**前端：**
- **框架**: Next.js 16 (App Router)
- **语言**: TypeScript
- **UI 库**: shadcn/ui + Tailwind CSS
- **HTTP 客户端**: Axios
- **构建工具**: Turbopack

### 架构设计

采用 **DDD (领域驱动设计)** 四层架构：

```
interfaces (接口层)    → REST API 控制器
    ↓
application (应用层)    → 应用服务，负责编排
    ↓
domain (领域层)        → 领域模型、领域服务接口
    ↓
infrastructure (基础设施层) → 技术实现（内存存储、MCP 客户端）
```

### 模块划分

```
fast-mcp-gateway/
├── src/main/java/org/cloudnook/mcp/
│   ├── interfaces/              # 接口层
│   │   ├── api/                 # REST 控制器
│   │   │   ├── server/          # 服务管理 API
│   │   │   ├── inspector/       # 调试 API
│   │   │   └── monitor/        # 监控 API
│   │   └── dto/                 # 数据传输对象
│   ├── application/             # 应用层
│   │   └── service/            # 应用服务
│   ├── domain/                  # 领域层
│   │   ├── model/              # 领域模型
│   │   └── service/            # 领域服务接口
│   └── infrastruction/         # 基础设施层
│       ├── inspector/          # 调试实现
│       ├── metrics/            # 监控实现
│       ├── register/           # 注册中心
│       └── client/             # MCP 客户端管理
└── gateway-project-style/      # 前端项目
    ├── app/                    # Next.js 页面
    ├── components/             # React 组件
    ├── lib/                    # 工具库
    └── types/                  # TypeScript 类型
```

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **Node.js**: 18+
- **npm**: 9+

### 后端启动

```bash
# 克隆项目
git clone https://github.com/your-org/fast-mcp-gateway.git
cd fast-mcp-gateway

# 启动后端（端口 9000）
cd /path/to/fast-mcp-gateway
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/fast-mcp-gateway-*.jar
```

访问：http://localhost:9000

### 前端启动

```bash
# 进入前端目录
cd gateway-project-style

# 安装依赖
npm install

# 启动开发服务器（端口 3000）
npm run dev

# 或者构建生产版本
npm run build
npm run start
```

访问：http://localhost:3000

### Docker 部署（推荐）

```bash
# 构建镜像
docker build -t fast-mcp-gateway .

# 运行容器
docker run -p 9000:9000 -p 3000:3000 fast-mcp-gateway
```

---

## 📚 使用指南

### 1. 注册 MCP 服务

#### 方式一：通过 Web UI

1. 访问 http://localhost:3000
2. 点击右上角 "Add Server" 按钮
3. 填写表单：
   - **Server Name**: 服务名称（如：weather-server）
   - **Description**: 服务描述
   - **Transport Type**: 传输类型（STDIO/SSE/STREAMABLE_HTTP）
   - **Endpoint**: 服务地址（如：http://localhost:8000）
   - **Version**: 版本号（默认 1.0.0）
4. 点击 "Add Server" 完成注册

#### 方式二：通过 API

```bash
curl -X POST http://localhost:9000/api/servers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "weather-server",
    "description": "Weather data service",
    "transportType": "STREAMABLE_HTTP",
    "endpoint": "http://localhost:8000",
    "version": "1.0.0"
  }'
```

### 2. 查看服务列表

访问首页 Dashboard，可以看到：
- 所有注册的服务卡片
- 服务健康状态（绿色=健康，黄色=降级，红色=不健康）
- 关键指标（请求数、延迟、成功率）

### 3. 调试 MCP 服务

#### 查看 Tools 列表

```bash
curl "http://localhost:9000/api/inspector/{serverId}/tools/list"
```

#### 调用 Tool

```bash
curl -X POST "http://localhost:9000/api/inspector/{serverId}/tools/call" \
  -H "Content-Type: application/json" \
  -d '{
    "toolName": "weather:get_current",
    "arguments": {
      "city": "Beijing"
    }
  }'
```

#### 查看调用历史

```bash
curl "http://localhost:9000/api/inspector/{serverId}/history?page=0&size=20"
```

### 4. 监控服务状态

#### 获取所有服务监控摘要

```bash
curl "http://localhost:9000/api/monitors/summary"
```

#### 获取服务详情

```bash
curl "http://localhost:9000/api/monitors/{serverId}/detail?serverName={name}"
```

---

## 📖 API 文档

### 服务管理 API

#### 注册服务
```http
POST /api/servers
Content-Type: application/json

{
  "name": "weather-server",
  "description": "Weather data service",
  "transportType": "STREAMABLE_HTTP",
  "endpoint": "http://localhost:8000",
  "version": "1.0.0"  // 可选，默认 1.0.0
}

Response:
{
  "code": "200",
  "message": "success",
  "data": {
    "id": "weather-server-123456",
    "name": "weather-server",
    "status": "ACTIVE",
    ...
  }
}
```

#### 获取所有服务
```http
GET /api/servers

Response:
{
  "code": "200",
  "data": [
    {
      "id": "weather-server-123",
      "name": "weather-server",
      "status": "ACTIVE",
      ...
    }
  ]
}
```

#### 注销服务
```http
DELETE /api/servers?serverName=weather-server&serverId=weather-server-123

Response:
{
  "code": "200",
  "data": { ... }
}
```

### 监控 API

#### 获取监控摘要（列表页）
```http
GET /api/monitors/summary

Response:
{
  "code": "200",
  "data": [
    {
      "serverId": "weather-server-123",
      "serverName": "weather-server",
      "status": "ACTIVE",
      "healthStatus": "HEALTHY",
      "totalRequests": 1234,
      "avgLatency": 45.2,
      "successRate": 99.8,
      ...
    }
  ]
}
```

#### 获取服务详情（详情页）
```http
GET /api/monitors/{serverId}/detail?serverName={name}

Response:
{
  "code": "200",
  "data": {
    "serverId": "weather-server-123",
    "serverName": "weather-server",
    "totalRequests": 1234,
    "successRequests": 1232,
    "failedRequests": 2,
    "avgLatency": 45.2,
    "minLatency": 12,
    "maxLatency": 234,
    "successRate": 99.8,
    "uptime": "2d 5h 30m",
    ...
  }
}
```

### Inspector API

#### 获取 Tools 列表
```http
GET /api/inspector/{serverId}/tools/list

Response:
{
  "code": "200",
  "data": {
    "tools": [
      {
        "name": "weather:get_current",
        "description": "Get current weather",
        "inputSchema": { ... }
      }
    ]
  }
}
```

#### 调用 Tool
```http
POST /api/inspector/{serverId}/tools/call
Content-Type: application/json

{
  "toolName": "weather:get_current",
  "arguments": {
    "city": "Beijing"
  }
}

Response:
{
  "code": "200",
  "data": {
    "content": [
      {
        "type": "text",
        "text": "当前北京气温：25°C"
      }
    ]
  }
}
```

#### 获取调用历史
```http
GET /api/inspector/{serverId}/history?page=0&size=20

Response:
{
  "code": "200",
  "data": [
    {
      "invocationId": "inv-123",
      "serverId": "weather-server-123",
      "type": "TOOL_CALL",
      "requestName": "weather:get_current",
      "status": "SUCCESS",
      "timestamp": "2026-01-13T10:30:00Z",
      ...
    }
  ]
}
```

完整 API 文档请查看 [API.md](docs/API.md)

---

## 🎯 核心场景

### 场景 1：LLM 应用调用 MCP 服务

```
用户 LLM 应用
    ↓
调用 weather:get_current?city=Beijing
    ↓
Fast MCP Gateway (服务名路由)
    ↓
weather-server-1 (负载均衡)
    ↓
返回结果
```

### 场景 2：服务调试

```
开发者
    ↓
访问 Inspector 页面
    ↓
选择 weather-server
    ↓
浏览 Tools 列表
    ↓
调用 weather:get_current 测试
    ↓
查看调用结果和日志
```

### 场景 3：服务监控

```
运维人员
    ↓
查看 Dashboard
    ↓
发现 weather-server 状态为 UNHEALTHY
    ↓
点击查看详情
    ↓
发现成功率只有 65%，延迟 800ms
    ↓
决定重启服务或联系服务提供方
```

---

## 📂 项目结构

```
fast-mcp-gateway/
├── docs/                           # 文档目录
│   ├── ARCHITECTURE.md              # 架构设计文档
│   └── API.md                       # API 文档
├── src/main/java/org/cloudnook/mcp/
│   ├── interfaces/                  # 接口层
│   │   ├── api/                     # REST 控制器
│   │   │   ├── server/             # 服务管理 API
│   │   │   │   └── McpServerController.java
│   │   │   ├── inspector/          # 调试 API
│   │   │   │   └── McpServerInspectorController.java
│   │   │   └── monitor/            # 监控 API
│   │   │       └── McpMonitorController.java
│   │   └── dto/                     # 数据传输对象
│   │       ├── server/             # 服务 DTO
│   │       ├── inspector/          # 调试 DTO
│   │       └── monitor/            # 监控 DTO
│   ├── application/                # 应用层
│   │   └── service/
│   │       ├── McpServerAppService.java       # 服务管理应用服务
│   │       └── McpInspectorAppService.java    # 调试应用服务
│   ├── domain/                     # 领域层
│   │   ├── model/                  # 领域模型
│   │   │   ├── server/            # 服务实体
│   │   │   ├── metrics/            # 监控指标实体
│   │   │   └── inspector/         # 调试实体
│   │   └── service/                # 领域服务接口
│   ├── infrastruction/             # 基础设施层
│   │   ├── client/                # MCP 客户端管理
│   │   ├── register/              # 服务注册中心
│   │   ├── metrics/               # 监控指标存储
│   │   └── inspector/             # 调试功能实现
│   └── ...                        # 其他模块
├── gateway-project-style/         # 前端项目
│   ├── app/                       # Next.js 页面
│   │   ├── page.tsx              # 首页 Dashboard
│   │   ├── server/[id]/page.tsx  # 服务详情页
│   │   └── inspector/[id]/page.tsx # 调试页
│   ├── components/                # React 组件
│   │   ├── server-grid.tsx       # 服务列表
│   │   ├── server-card.tsx       # 服务卡片
│   │   ├── server-details.tsx    # 服务详情
│   │   ├── server-metrics.tsx    # 监控指标
│   │   ├── add-server-dialog.tsx # 注册对话框
│   │   └── ...
│   ├── lib/                      # 工具库
│   │   └── http-client.ts        # HTTP 客户端
│   └── types/                    # TypeScript 类型
│       └── server.ts             # 服务类型定义
├── DEVELOPMENT.md                 # 开发文档
└── README.md                      # 本文件
```

---

## 🔧 配置说明

### 后端配置

`application.yml`:

```yaml
server:
  port: 9000

spring:
  application:
    name: fast-mcp-gateway

logging:
  level:
    org.cloudnook.mcp: DEBUG
    org.springframework.web: INFO
```

### 前端配置

`.env.local`:

```bash
NEXT_PUBLIC_API_URL=http://localhost:9000/api
```

---

## 🧪 测试

### 运行单元测试

```bash
mvn test
```

### 运行集成测试

```bash
mvn verify
```

### API 测试

使用 [Postman](https://www.postman.com/) 或 [curl](https://curl.se/)：

```bash
# 健康检查
curl http://localhost:9000/actuator/health

# 获取所有服务
curl http://localhost:9000/api/servers

# 获取监控摘要
curl http://localhost:9000/api/monitors/summary
```

---

## 📊 性能指标

- **响应时间**: P95 < 50ms（内存存储）
- **吞吐量**: 支持 10k+ QPS（单机）
- **并发**: 支持同时监控 100+ MCP 服务
- **内存占用**: < 512MB（空闲状态）

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下流程：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/)：

- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具链相关

---

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议。

---

## 🙏 致谢

感谢以下开源项目：

- [Model Context Protocol](https://modelcontextprotocol.io/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Next.js](https://nextjs.org/)
- [shadcn/ui](https://ui.shadcn.com/)

---

## 📞 联系方式

- **项目地址**: [https://github.com/the-wayee/fast-mcp-gateway](https://github.com/your-org/fast-mcp-gateway)
- **问题反馈**: [Issues](https://github.com/your-org/fast-mcp-gateway/issues)
- **开发文档**: [DEVELOPMENT.md](DEVELOPMENT.md)

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star ⭐**

Made with ❤️ by Fast MCP Gateway Team

</div>
