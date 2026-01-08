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
