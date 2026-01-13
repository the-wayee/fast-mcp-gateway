# Fast MCP Gateway 开发文档

## 1. 项目愿景

### 1.1 核心定位

Fast MCP Gateway 是一个**双角色系统**，既是可独立部署的微服务，也是可集成的组件库：

**作为服务**
- 独立部署运行，提供完整的 MCP Server 治理能力
- 提供 Web UI 管理界面（类似 MCP Inspector）
- REST API 管理接口
- 支持 SSE/HTTP MCP 协议接入

**作为组件**
- 可作为 Java Library 集成到其他应用
- 提供 SDK 供其他服务调用
- 支持嵌入式部署

### 1.2 核心价值

#### 对 LLM/Agent 客户端
- 统一接入点：通过服务名调用，无需关心底层 Server 细节
- 高可用保障：故障转移、负载均衡、重试机制
- 透明转发：完全兼容 MCP 协议

#### 对 MCP Server 提供方
- 统一管理平台：查看 Server 状态、配置、监控
- 调试工具：交互式测试 Tools/Resources/Prompts
- 生命周期管理：注册、启用/禁用、健康检查

#### 对运维团队
- 可观测性：调用日志、性能指标、审计追踪
- 灵活配置：动态路由规则、负载均衡策略
- 生产级特性：健康检查、熔断降级、流量控制

---

## 2. 系统架构

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端层                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐    │
│  │   LLM    │  │  Agent   │  │ Web UI   │  │  REST Client │    │
│  └─────┬────┘  └─────┬────┘  └─────┬────┘  └──────┬───────┘    │
└────────┼─────────────┼─────────────┼───────────────┼───────────┘
         │             │             │               │
         │ MCP 协议     │ HTTP API    │ WebSocket     │ REST API
         ▼             ▼             ▼               ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Fast MCP Gateway                              │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              1. 协议接入层 (MCP Server)                    │  │
│  │     - SSE Transport                                       │  │
│  │     - HTTP Transport                                      │  │
│  │     - 服务名 → Server 路由                                 │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │              2. 管理能力层 (Management)                    │  │
│  │     - Server 状态监控                                     │  │
│  │     - Tools/Resources/Prompts 浏览                        │  │
│  │     - 调试界面 (类 Inspector)                              │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │              3. 协议转发层 (Gateway Core)                  │  │
│  │     - 请求路由 (服务名 → Server)                           │  │
│  │     - 协议透明转发                                        │  │
│  │     - 响应聚合                                            │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │              4. 治理层 (Governance)                        │  │
│  │     - 健康检查                                            │  │
│  │     - 故障转移                                            │  │
│  │     - 负载均衡                                            │  │
│  │     - 重试机制                                            │  │
│  │     - 熔断降级                                            │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │              5. 可观测层 (Observability)                   │  │
│  │     - 调用日志                                            │  │
│  │     - 性能指标                                            │  │
│  │     - 链路追踪                                            │  │
│  ├───────────────────────────────────────────────────────────┤  │
│  │              6. 持久化层 (Persistence)                     │  │
│  │     - Server 注册信息                                     │  │
│  │     - 路由规则                                            │  │
│  │     - 调用日志                                            │  │
│  │     - 调试会话                                            │  │
│  └───────────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     │ MCP 协议 (stdio/SSE/HTTP)
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                    MCP Server Pool                              │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌─────────────┐  │
│  │  Weather  │  │ Calculator│  │   File    │  │   Database  │  │
│  │  Server   │  │  Server   │  │  Server   │  │   Server    │  │
│  └───────────┘  └───────────┘  └───────────┘  └─────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 核心模块

#### 1. MCP Server 管理模块
- Server 注册（静态配置 + 动态 API）
- Server 状态监控（健康度、连接数、响应时间）
- Server 能力查看（Tools/Resources/Prompts 列表）
- Server 生命周期管理（启用/禁用/删除）

#### 2. 协议转发模块
- 基于服务名的智能路由
- MCP 协议透明转发
- 多 Transport 支持（stdio/SSE/HTTP）
- 连接池管理

#### 3. 调试模块（类似 MCP Inspector）
- Web UI 调试界面
- 交互式 Tool 调用测试
- Resource 内容浏览
- Prompt 模板预览
- 实时调用日志查看

#### 4. 治理模块
- 健康检查（主动/被动/混合）
- 故障转移（自动摘除故障节点）
- 负载均衡（Round Robin/加权/最少连接等）
- 失败重试（指数退避/跨 Server 重试）
- 熔断器（连续失败阈值/半开状态恢复）

#### 5. 可观测模块
- 调用日志（请求/响应/性能指标）
- 审计日志（who/when/what）
- 统计分析（成功率/平均响应时间/调用量）
- 分布式追踪（trace ID 传递）

#### 6. 持久化模块
- Server 注册信息持久化
- 路由规则配置持久化
- 调用日志持久化
- 调试会话记录持久化

---

## 3. 核心功能

### 3.1 MCP Server 管理

#### 功能清单
- 查看 Server 列表及状态
- 注册新 Server（支持静态配置和 API 注册）
- 查看 Server 详情（endpoint、transport、capabilities）
- 启用/禁用 Server
- 删除 Server
- 查看 Server 的 Tools/Resources/Prompts
- 批量导入/导出 Server 配置

#### 需要持久化的数据
- Server 基本信息（serverId、name、endpoint、transportType）
- Server 元数据（capabilities、tags、权重）
- Server 配置（健康检查配置、连接池配置）
- Server 状态（healthStatus、lastHealthCheckTime）

---

### 3.2 服务名路由转发

#### 功能清单
- 基于服务名的路由（如 `weather:get_current` → `weather-server`）
- 支持路由规则配置（精确匹配、前缀匹配、通配符）
- 负载均衡（同一服务名对应多个 Server 实例）
- MCP 协议透明转发（Tools/Resources/Prompts）
- 连接池管理与复用

#### 路由示例
```
客户端调用: weather:get_current?city=Beijing
         ↓
Gateway 解析服务名: weather
         ↓
查找路由规则: weather:* → weather-server-group
         ↓
负载均衡选择: weather-server-1 (权重 10)
         ↓
转发请求: http://weather-server-1/sse
         ↓
返回结果给客户端
```

#### 需要持久化的数据
- 路由规则（ruleId、pattern、targetServerId、priority）
- Server 分组信息（groupId、members）
- 负载均衡配置（strategy、weights）

---

### 3.3 MCP Inspector 调试能力

#### 功能清单
- Web UI 调试界面
- 查看 Server 状态及能力
- 浏览 Tools 列表及 Schema
- 浏览 Resources 列表及内容
- 浏览 Prompts 列表及模板
- 交互式调用 Tool 并查看结果
- 实时查看调用日志
- 调用历史回放

#### UI 模块
```
┌────────────────────────────────────────────────────┐
│  MCP Inspector                                     │
├────────────────────────────────────────────────────┤
│  Server List          │  Tool Explorer             │
│  ├─ weather-server    │  ├─ weather:get_current   │
│  ├─ calc-server       │  ├─ weather:forecast      │
│  └─ file-server       │  └─ calc:add              │
│                       │                            │
│  [Status Panel]       │  [Test Tool]              │
│  ├─ Health: ✓         │  Tool: weather:get        │
│  ├─ Connections: 3    │  Arguments:               │
│  └─ Response: 45ms    │    city: Beijing          │
│                       │                            │
│                       │  [Execute]                │
│                       │                            │
│                       │  Result:                  │
│                       │  {                        │
│                       │    "temp": 25,            │
│                       │    "condition": "Sunny"   │
│                       │  }                        │
└────────────────────────────────────────────────────┘
```

#### 需要持久化的数据
- 调试会话记录（sessionId、userId、operations）
- 调用历史（invocationId、request、response、timestamp）

---

### 3.4 高可用能力

#### 健康检查
- 主动检查：定期发送 ping/initialize 请求
- 被动检查：监控调用失败率
- 混合模式：主动 + 被动结合
- 自动摘除：不健康 Server 从负载均衡池移除
- 自动恢复：健康 Server 重新加入负载均衡池

#### 故障转移
- 同 Server 重试：连接失败时重试 N 次
- 跨 Server 重试：Server 故障时切换到其他健康实例
- 降级策略：返回缓存结果或默认值
- 熔断器：连续失败达到阈值后快速失败

#### 负载均衡策略
- Round Robin（轮询）
- Weighted Round Robin（加权轮询）
- Random（随机）
- Least Connections（最少连接）
- Response Time（响应时间优先）
- IP Hash（一致性哈希）

#### 需要持久化的数据
- 健康检查历史（serverId、checkTime、status、responseTime）
- 故障转移记录（fromServer、toServer、reason、timestamp）
- 负载均衡配置（strategy、weights、serverGroups）

---

### 3.5 日志与审计

#### 调用日志
- 请求信息（toolName、arguments、timestamp）
- 路由信息（selectedServer、routingDecision）
- 性能指标（duration、latency、throughput）
- 错误信息（errorMessage、stackTrace）

#### 审计日志
- 操作类型（注册 Server、修改路由、调用 Tool）
- 操作者（userId、clientIp）
- 操作时间（timestamp）
- 操作结果（success/failure）

#### 日志查询
- 按时间范围查询
- 按 Server 查询
- 按 Tool 名称查询
- 按状态筛选（success/failure/timeout）
- 支持分页和排序

#### 需要持久化的数据
- 调用日志（invocationId、toolName、arguments、result、duration）
- 审计日志（auditId、operationType、operator、timestamp、result）

---

## 4. 数据模型

### 4.1 核心实体

#### McpServer（MCP Server）
- serverId: 唯一标识
- serverName: 显示名称
- description: 描述信息
- transportType: 传输类型（STDIO/SSE/HTTP）
- endpoint: 连接地址
- capabilities: 能力声明
- healthStatus: 健康状态（HEALTHY/UNHEALTHY/UNKNOWN）
- weight: 负载均衡权重
- tags: 标签（用于路由和分组）
- createdAt: 注册时间
- updatedAt: 更新时间

#### ToolRouteRule（工具路由规则）
- ruleId: 规则唯一标识
- pattern: 匹配模式（支持通配符）
- routeType: 路由类型（EXACT/PREFIX/WILDCARD）
- targetServerId: 目标 Server ID
- targetServerGroup: 目标 Server 组
- priority: 优先级（数字越小优先级越高）
- enabled: 是否启用
- createdAt: 创建时间

#### ToolInvocationLog（工具调用日志）
- invocationId: 调用唯一标识
- requestId: 请求 ID
- toolName: 工具名称
- arguments: 调用参数
- selectedServerId: 选中的 Server
- startTime: 开始时间
- endTime: 结束时间
- durationMs: 耗时（毫秒）
- status: 调用状态（SUCCESS/FAILURE/TIMEOUT）
- errorMessage: 错误信息
- createdAt: 创建时间

#### ServerHealthRecord（Server 健康检查记录）
- recordId: 记录唯一标识
- serverId: Server ID
- checkTime: 检查时间
- status: 健康状态
- responseTimeMs: 响应时间（毫秒）
- errorMessage: 错误信息

#### DebugSession（调试会话）
- sessionId: 会话唯一标识
- userId: 用户 ID
- serverId: 关联的 Server ID
- operations: 操作记录（JSON）
- createdAt: 创建时间
- expiresAt: 过期时间

---

## 5. API 设计

### 5.1 MCP Protocol API（对客户端暴露）

```
# SSE 方式接入
GET /mcp/sse

# 调用 Tool
POST /mcp/tools/call
{
  "serviceName": "weather",
  "toolName": "get_current",
  "arguments": {
    "city": "Beijing"
  }
}

# 列举 Tools
GET /mcp/tools?serviceName=weather

# 列举 Resources
GET /mcp/resources?serviceName=weather

# 列举 Prompts
GET /mcp/prompts?serviceName=weather
```

### 5.2 Gateway Management API（管理接口）

```
# Server 管理
POST   /api/v1/servers              # 注册 Server
GET    /api/v1/servers              # 查询 Server 列表
GET    /api/v1/servers/:id          # 查询 Server 详情
PUT    /api/v1/servers/:id          # 更新 Server
DELETE /api/v1/servers/:id          # 删除 Server
PUT    /api/v1/servers/:id/enable   # 启用 Server
PUT    /api/v1/servers/:id/disable  # 禁用 Server
GET    /api/v1/servers/:id/tools    # 获取 Server 的 Tools
GET    /api/v1/servers/:id/resources # 获取 Server 的 Resources
GET    /api/v1/servers/:id/prompts  # 获取 Server 的 Prompts

# 路由规则管理
POST   /api/v1/routes              # 添加路由规则
GET    /api/v1/routes              # 查询路由规则
PUT    /api/v1/routes/:id          # 更新路由规则
DELETE /api/v1/routes/:id          # 删除路由规则

# 日志查询
GET    /api/v1/logs/invocations    # 查询调用日志
GET    /api/v1/logs/audit          # 查询审计日志
GET    /api/v1/logs/health         # 查询健康检查记录

# 统计分析
GET    /api/v1/stats/overview      # 总体统计
GET    /api/v1/stats/servers/:id   # Server 统计
GET    /api/v1/stats/tools/:name   # Tool 统计

# 健康检查
GET    /health                     # Gateway 健康状态
GET    /health/servers             # 所有 Server 健康状态
GET    /health/servers/:id         # 特定 Server 健康状态
```

### 5.3 Debug API（调试接口）

```
# 调试会话
POST   /api/v1/debug/sessions      # 创建调试会话
GET    /api/v1/debug/sessions/:id  # 获取会话信息
DELETE /api/v1/debug/sessions/:id  # 删除会话

# 调试操作
POST   /api/v1/debug/call-tool     # 调用 Tool
GET    /api/v1/debug/list-tools    # 列举 Tools
GET    /api/v1/debug/get-resource  # 获取 Resource
GET    /api/v1/debug/history       # 调用历史
```

---

## 6. 持久化方案

### 6.1 数据库选型
- **主数据库**: MySQL / PostgreSQL（存储核心配置和日志）
- **缓存层**: Redis（缓存 Server 状态、路由表）
- **时序数据**: InfluxDB / TimescaleDB（存储性能指标、健康检查记录）

### 6.2 表设计

#### mcp_servers（Server 注册信息）
- id (PK)
- server_id (UK)
- server_name
- description
- transport_type
- endpoint
- capabilities (JSON)
- health_status
- weight
- tags (JSON)
- enabled
- created_at
- updated_at

#### tool_route_rules（路由规则）
- id (PK)
- rule_id (UK)
- pattern
- route_type
- target_server_id
- target_server_group
- priority
- enabled
- created_at
- updated_at

#### tool_invocation_logs（调用日志）
- id (PK)
- invocation_id (UK)
- request_id
- tool_name
- arguments (JSON)
- selected_server_id
- start_time
- end_time
- duration_ms
- status
- error_message
- created_at

#### server_health_records（健康检查记录）
- id (PK)
- server_id
- check_time
- status
- response_time_ms
- error_message

#### debug_sessions（调试会话）
- id (PK)
- session_id (UK)
- user_id
- server_id
- operations (JSON)
- created_at
- expires_at

---

## 7. 开发 TODO

### Phase 1: 核心基础（2-3 周）
- [ ] 项目结构搭建
- [ ] 数据库表设计及创建
- [ ] MCP Server 注册功能（静态配置 + API）
- [ ] 多 Transport 支持（SSE、HTTP）
- [ ] 基础的路由转发（服务名 → Server）
- [ ] 简单的 Web UI（Server 列表、状态查看）

### Phase 2: 调试能力（1-2 周）
- [ ] Inspector UI 开发
- [ ] Tools/Resources/Prompts 浏览
- [ ] 交互式 Tool 调用
- [ ] 调用历史记录
- [ ] 实时日志查看

### Phase 3: 高可用（2-3 周）
- [ ] 健康检查机制（主动 + 被动）
- [ ] 故障转移（自动摘除/恢复）
- [ ] 负载均衡（Round Robin、加权）
- [ ] 失败重试（指数退避）
- [ ] 熔断器

### Phase 4: 可观测性（1-2 周）
- [ ] 调用日志记录
- [ ] 审计日志
- [ ] 性能指标统计
- [ ] 日志查询 API
- [ ] 统计分析 Dashboard

### Phase 5: 高级特性（2-3 周）
- [ ] 多种负载均衡策略
- [ ] 路由规则动态管理
- [ ] 流量控制与限流
- [ ] 链路追踪（trace ID）
- [ ] 配置热更新

### Phase 6: 优化与测试（1-2 周）
- [ ] 性能优化
- [ ] 单元测试覆盖
- [ ] 集成测试
- [ ] 压力测试
- [ ] 文档完善

---

## 8. 技术栈

### 后端
- **框架**: Spring Boot 4.0.1
- **JDK**: Java 17
- **响应式**: Spring WebFlux
- **MCP SDK**: io.modelcontextprotocol.sdk:mcp:0.14.1
- **数据库**: MySQL / PostgreSQL
- **缓存**: Redis
- **ORM**: Spring Data R2DBC（响应式）

### 前端
- **框架**: Vue.js 3 / React
- **UI 库**: Element Plus / Ant Design
- **构建**: Vite

### 运维
- **容器化**: Docker
- **编排**: Kubernetes（可选）
- **监控**: Prometheus + Grafana
- **日志**: ELK / Loki

---

## 9. 部署模式

### 9.1 独立服务部署
```
┌──────────────┐
│   Nginx/LB   │
└──────┬───────┘
       │
       ▼
┌─────────────────────────────┐
│  Fast MCP Gateway (Cluster) │
│  ┌─────────┐  ┌─────────┐   │
│  │ Node 1  │  │ Node 2  │   │
│  └─────────┘  └─────────┘   │
└─────────────────────────────┘
```

### 9.2 嵌入式部署
```
┌─────────────────────────────────┐
│   你的 Spring Boot 应用         │
│  ┌───────────────────────────┐  │
│  │  Fast MCP Gateway Library │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

---

## 10. 参考项目

- [MCP Inspector](https://github.com/modelcontextprotocol/inspector) - 调试界面参考
- [MCP SDK](https://github.com/modelcontextprotocol/java-sdk) - MCP 协议实现
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) - 网关设计参考

---

**文档版本**: v2.0
**最后更新**: 2026-01-08
**维护者**: Fast MCP Gateway Team

---

## 11. 已实现功能清单 (2026-01-13)

### 11.1 服务注册与管理 ✅

#### 实现的接口
- `POST /api/servers` - 注册服务
  - 支持参数：name, description, transportType, endpoint, version (默认1.0.0)
  - 返回：McpServer 对象
  - 功能：验证连接、注册到中心、初始化监控指标

- `DELETE /api/servers` - 注销服务
  - 参数：serverName, serverId
  - 功能：断开连接、移除注册、清除监控数据

- `GET /api/servers` - 获取所有服务
  - 返回：List<McpServer>

- `GET /api/servers/protocols` - 获取支持的传输类型
  - 返回：STDIO, SSE, STREAMABLE_HTTP

#### 数据模型
**McpServer** (服务基本信息)
- id (serverId) - 唯一标识
- name - 服务名称
- description - 描述
- status - ACTIVE/INACTIVE/CONNECTING/DISCONNECTED
- transportType - 传输类型
- endpoint - 连接地址
- **version - 版本号 (新增)**

### 11.2 服务监控 ✅

#### 实现的接口
- `GET /api/monitors/summary` - 服务监控摘要
  - 用途：首页列表展示
  - 返回：List<ServerMonitorSummaryVO>
  - 包含：基本信息 + 关键指标

- `GET /api/monitors/{serverId}/detail?serverName={name}` - 服务详情
  - 用途：详情页展示
  - 返回：ServerDetailVO
  - 包含：完整服务信息 + 完整监控指标

#### 监控指标 (McpServerMetrics)

**请求指标：**
- totalRequests - 总请求数
- successRequests - 成功请求数
- failedRequests - 失败请求数

**延迟指标：**
- avgLatency - 平均延迟（毫秒）
- minLatency - 最小延迟（毫秒）
- maxLatency - 最大延迟（毫秒）

**时间指标：**
- registerTime - 注册时间
- lastHeartbeat - 最后心跳时间
- uptime - 运行时长（秒）

**计算指标：**
- successRate - 成功率 (%)
- failureRate - 失败率 (%)
- healthStatus - 健康状态 (自动计算)

#### 健康状态判断逻辑
```java
HEALTHY: 成功率 ≥ 95% 且延迟 < 200ms
         或：刚注册（lastHeartbeat < 60秒）

DEGRADED: 成功率 80%-95% 或延迟 200-500ms

UNHEALTHY: 成功率 < 80% 或延迟 > 500ms

UNKNOWN: 无数据或心跳超过1分钟
```

### 11.3 Inspector 调试功能 ✅

#### 实现的接口

**资源列表查询：**
- `GET /api/inspector/{serverId}/tools/list`
- `GET /api/inspector/{serverId}/resources/list`
- `GET /api/inspector/{serverId}/prompts/list`

**资源调用调试：**
- `POST /api/inspector/{serverId}/tools/call`
- `GET /api/inspector/{serverId}/resources/read?uri={uri}`
- `POST /api/inspector/{serverId}/prompts/get`

**调用历史：**
- `GET /api/inspector/history?page=0&size=20` - 全局历史
- `GET /api/inspector/{serverId}/history?page=0&size=20` - 指定服务历史
- `DELETE /api/inspector/history` - 清空历史

#### 监控集成
所有 Inspector 操作都会记录：
- 成功/失败状态
- 延迟统计
- 调用历史
- 自动更新监控指标

### 11.4 前端页面 ✅

#### Dashboard (首页)
- **路由**: `/`
- **组件**: `ServerGrid`
- **功能**: 
  - 展示所有服务卡片
  - 显示健康状态、请求数、延迟、成功率
  - 支持搜索过滤
  - 点击卡片跳转详情页

#### 服务详情页
- **路由**: `/server/{serverId}?serverName={name}`
- **组件**:
  - `ServerDetails` - 服务基本信息
    - 服务名称、描述、状态
    - 端点、协议、版本、运行时长
  
  - `ServerMetrics` - 监控指标
    - 4个关键指标：Total Requests, Avg Latency, Success Rate, Active Connections
    - 6个详细指标：Success/Failed Requests, Min/Max Latency, Failure Rate, Uptime
  
  - `ServerLogs` - 调用历史 (待对接)
  - `ServerActions` - 操作按钮 (待实现)

#### 服务注册
- **组件**: `AddServerDialog`
- **功能**: 
  - 表单输入：name, description, transportType, endpoint, version
  - 默认版本：1.0.0
  - 注册成功后刷新页面

### 11.5 数据流 ✅

#### 注册流程
```
前端表单 
  → POST /api/servers 
  → McpServerAppService.registerServer()
  → McpManagerService.register()
  → McpClientManager.connect() [连接验证]
  → McpRegister.register() [注册到中心]
  → McpMetricsRepository.initMetrics() [初始化监控]
  → 返回成功
```

#### 监控流程
```
McpInspectorAppService 调用
  → mcpInspectorService.xxx() [执行调用]
  → .doOnSuccess() 
  → monitoringService.recordSuccess() 
  → 记录日志 + 更新指标
```

#### 查询流程
```
前端请求
  → GET /api/monitors/summary
  → McpServerAppService.getAllServerSummaries()
  → 查询所有服务
  → 查询每个服务的监控指标
  → 组装 ServerMonitorSummaryVO
  → 返回前端
```

---

## 12. 待开发功能 (按优先级) 🚧

### 12.1 高优先级 (P0)

#### 1. ServerLogs 组件对接 ⭐⭐⭐
**需求**: 显示服务调用历史记录

**实现方案**:
```typescript
// components/server-logs.tsx
useEffect(() => {
  httpClient.get<ActionResult<ToolInvocationRecord[]>>(
    `/inspector/${serverId}/history?page=0&size=20`
  )
  .then(response => {
    setLogs(response.data.data)
  })
}, [serverId])
```

**待添加功能**:
- 日志级别显示 (info/warn/error)
- 时间格式化
- 分页加载
- 实时刷新

#### 2. ServerActions 功能实现 ⭐⭐⭐
**需求**: 服务操作按钮功能

**需要实现的接口**:
- `POST /api/servers/{serverId}/start` - 启动服务
- `POST /api/servers/{serverId}/stop` - 停止服务
- `POST /api/servers/{serverId}/restart` - 重启服务
- `DELETE /api/servers/{serverId}` - 删除服务

**实现要点**:
- 调用 `McpClientManager.connect()` 和 `disconnect()`
- 更新服务状态 (ACTIVE ↔ INACTIVE)
- 前端确认对话框
- 操作成功后刷新页面

#### 3. 活跃连接数追踪 ⭐⭐
**需求**: 实时追踪服务活跃连接数

**实现方案**:
```java
// McpServerMetrics 新增
private Integer activeConnections;

// McpClientManager 维护连接计数
private ConcurrentHashMap<String, AtomicInteger> connectionCounter;

public void incrementConnections(String serverId) {
    connectionCounter.computeIfAbsent(serverId, k -> new AtomicInteger(0)).incrementAndGet();
}

public void decrementConnections(String serverId) {
    connectionCounter.computeIfPresent(serverId, (k, v) -> {
        int count = v.decrementAndGet();
        return count <= 0 ? null : v;
    });
}

// 获取当前连接数
public int getConnections(String serverId) {
    AtomicInteger counter = connectionCounter.get(serverId);
    return counter != null ? counter.get() : 0;
}
```

### 12.2 中优先级 (P1)

#### 4. 心跳更新服务 ⭐⭐
**需求**: 定期更新服务心跳和运行时长

**实现方案**:
```java
@Component
public class HeartbeatScheduler {
    
    @Scheduled(fixedRate = 30000) // 每30秒
    public void updateHeartbeat() {
        List<McpServer> servers = mcpRegister.getAllServers();
        for (McpServer server : servers) {
            if (server.getStatus() == McpServerStatus.ACTIVE) {
                metricsRepository.updateHeartbeat(server.getId());
                
                // 更新运行时长
                McpServerMetrics metrics = metricsRepository.getServerMetrics(server.getId());
                if (metrics != null && metrics.getRegisterTime() != null) {
                    long uptime = Instant.now().getEpochSecond() - metrics.getRegisterTime().getEpochSecond();
                    metrics.setUptime(uptime);
                }
            }
        }
    }
}
```

#### 5. 监控数据趋势 ⭐
**需求**: 显示指标变化趋势 (如 "+12.5%")

**实现方案**:
- 新增 `MetricsSnapshot` 实体存储历史快照
- 定时任务（每分钟）保存快照
- 计算当前值与上个快照的变化率

```java
@Entity
public class MetricsSnapshot {
    private String snapshotId;
    private String serverId;
    private Long snapshotTime;
    private Long totalRequests;
    private Double avgLatency;
    private Double successRate;
}

// 计算变化率
public MetricsTrend calculateTrend(String serverId) {
    MetricsSnapshot current = getLatestSnapshot(serverId);
    MetricsSnapshot previous = getPreviousSnapshot(serverId);
    
    double requestChange = calculateChange(
        current.getTotalRequests(), 
        previous.getTotalRequests()
    );
    
    return new MetricsTrend(requestChange, /* ... */);
}
```

#### 6. 日志级别区分 ⭐
**需求**: 为调用记录添加日志级别 (info/warn/error)

**实现方案**:
```java
// ToolInvocationRecord 新增
public enum LogLevel {
    INFO, WARN, ERROR
}

// 自动判断级别
private LogLevel determineLevel(Throwable error, long latency) {
    if (error != null) return LogLevel.ERROR;
    if (latency > 500) return LogLevel.WARN;
    return LogLevel.INFO;
}

// ServerLogs 组件按级别显示不同颜色
const levelColors = {
  info: "text-blue-500",
  warn: "text-yellow-500", 
  error: "text-red-500"
};
```

### 12.3 低优先级 (P2)

#### 7. 服务健康检查 ⭐
**需求**: 定期健康检查，自动剔除不健康的服务

**实现方案**:
```java
@Scheduled(fixedRate = 60000) // 每分钟
public void healthCheck() {
    List<McpServer> servers = mcpRegister.getAllServers();
    for (McpServer server : servers) {
        McpServerMetrics metrics = metricsRepository.getServerMetrics(server.getId());
        HealthStatus status = metrics.getHealthStatus();
        
        if (status == HealthStatus.UNHEALTHY) {
            // 尝试重连
            mcpManagerService.reconnect(server.getId());
        }
    }
}
```

#### 8. 监控数据持久化 ⭐
**需求**: 将监控数据存储到 Redis/MySQL

**实现方案**:
```java
@Repository
public class McpRedisMetrics implements McpMetricsRepository {
    @Autowired
    private RedisTemplate<String, McpServerMetrics> redisTemplate;
    
    private static final String KEY_PREFIX = "mcp:metrics:";
    
    public void saveMetrics(String serverId, McpServerMetrics metrics) {
        redisTemplate.opsForValue().set(
            KEY_PREFIX + serverId, 
            metrics, 
            Duration.ofDays(7) // 7天过期
        );
    }
}
```

#### 9. 负载均衡支持 ⭐
**需求**: 同一服务名支持多实例，自动负载均衡

**实现方案**:
```java
public interface LoadBalancer {
    McpServer select(String serverName);
}

// 轮询实现
public class RoundRobinLoadBalancer implements LoadBalancer {
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
    
    public McpServer select(String serverName) {
        List<McpServer> servers = mcpRegister.getServersByName(serverName);
        int index = counters.computeIfAbsent(serverName, k -> new AtomicInteger(0))
                          .getAndIncrement() % servers.size();
        return servers.get(index);
    }
}
```

#### 10. 实时数据推送 (WebSocket) ⭐
**需求**: 使用 WebSocket 推送实时监控数据

**实现方案**:
```java
@Controller
public class McpWebSocketController {
    
    @MessageMapping("/subscribe/monitors")
    @SendTo("/topic/monitors")
    public List<ServerMonitorSummaryVO> broadcastMetrics() {
        return mcpServerAppService.getAllServerSummaries();
    }
}

// 前端订阅
const ws = new WebSocket('ws://localhost:9000/ws')
ws.onmessage = (message) => {
    const metrics = JSON.parse(message.data)
    setServers(metrics)
}
```

#### 11. 告警功能 ⭐
**需求**: 监控指标异常时发送告警

**实现方案**:
- 定义告警规则（成功率 < 80%，延迟 > 500ms）
- 支持多种告警渠道（邮件、钉钉、Slack）
- 告警去重和限流

---

## 13. 技术债务与改进建议

### 13.1 当前限制
1. **内存存储**: 监控指标、日志都存在内存中，重启丢失
2. **无认证授权**: API 没有认证机制
3. **错误处理不完善**: 部分异常情况未处理
4. **测试覆盖不足**: 缺少单元测试和集成测试
5. **文档待完善**: API 文档、部署文档需要补充

### 13.2 性能优化建议
1. **连接池管理**: 实现连接复用，避免频繁建立连接
2. **缓存层**: 添加 Redis 缓存热点数据
3. **异步处理**: 日志记录、指标更新使用异步队列
4. **分页优化**: 大数据量查询使用游标分页

### 13.3 安全建议
1. **API 认证**: 添加 JWT 或 API Key 认证
2. **权限控制**: RBAC 角色权限控制
3. **输入验证**: 严格验证所有用户输入
4. **敏感数据加密**: endpoint、密钥等敏感信息加密存储

---

**文档版本**: v3.0  
**最后更新**: 2026-01-13  
**维护者**: Fast MCP Gateway Team
