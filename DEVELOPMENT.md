# Fast MCP Gateway 开发文档

## 1. 项目概述

### 1.1 背景与问题

在真实工程环境中,通常存在多个 MCP Server,它们具有以下特点:
- 不同团队维护
- 不同部署方式
- 不同 transport 类型(stdio/SSE/streamable-http)
- 不同能力边界

如果让 LLM 或 Agent 直接连接所有 MCP Server,会带来以下问题:
- 配置和管理复杂
- 无统一治理能力
- 无调用日志和审计
- 无健康检查、失败重试和负载均衡
- 无法作为基础设施长期维护

### 1.2 解决方案

MCP Gateway 是一个 **协议级代理服务(Protocol-level Proxy)**,其核心定位是:
- **对上游**: 表现为一个 MCP Server
- **对下游**: 管理多个 MCP Client
- **中间层**: 负责治理、路由、日志和高可用能力

### 1.3 技术栈

- **框架**: Spring Boot 4.0.1
- **JDK**: Java 17
- **MCP SDK**: io.modelcontextprotocol.sdk:mcp:0.14.1
- **MCP Transport**: mcp-spring-webflux:0.11.2
- **响应式**: Spring WebFlux

---

## 2. 核心功能模块

### 2.1 模块架构

```
┌─────────────────────────────────────────────────────────────┐
│                     MCP Gateway                              │
├─────────────────────────────────────────────────────────────┤
│  1. Upstream MCP Server Layer (对上层暴露)                    │
│     - MCP Protocol Server (SSE/HTTP)                         │
│     - Tool Registration & Discovery                          │
├─────────────────────────────────────────────────────────────┤
│  2. Gateway Core Layer (核心网关能力)                         │
│     - Tool → Server Routing Engine                          │
│     - Request/Response Aggregation                          │
│     - Protocol Transparency Layer                           │
├─────────────────────────────────────────────────────────────┤
│  3. Governance Layer (治理层)                                │
│     - Health Check & Heartbeat                              │
│     - Load Balancing Strategy                               │
│     - Retry & Failover                                      │
│     - Rate Limiting & Circuit Breaker                       │
├─────────────────────────────────────────────────────────────┤
│  4. Observability Layer (可观测性)                           │
│     - Call Logging & Audit                                  │
│     - Metrics & Monitoring                                  │
│     - Distributed Tracing                                   │
├─────────────────────────────────────────────────────────────┤
│  5. Downstream MCP Client Layer (对下游连接)                 │
│     - MCP Client Pool Management                            │
│     - Multi-Transport Support (stdio/SSE/HTTP)              │
│     - Connection Lifecycle Management                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. 功能点详细设计

### 3.1 MCP Server 注册与发现

**目标**: 管理下游 MCP Server 的生命周期

**功能点**:
1. **Server 注册**
   - 静态配置文件注册 (application.yml)
   - 动态 API 注册 (REST API)
   - 服务发现集成 (Nacos/Consul/Eureka)

2. **Server 元数据管理**
   ```java
   McpServer {
       String serverId;
       String serverName;
       String description;
       TransportType transportType;  // STDIO, SSE, STREAMABLE_HTTP
       String endpoint;              // 连接地址
       Map<String, Object> capabilities;
       List<String> supportedTools;
       HealthStatus healthStatus;
       int weight;                   // 负载均衡权重
       Map<String, String> tags;     // 标签 (用于路由策略)
   }
   ```

3. **Server 健康状态**
   - HEALTHY, UNHEALTHY, DISABED, UNKNOWN
   - 心跳检测机制
   - 自动摘除与恢复

**API 设计**:
```java
// 注册 MCP Server
POST /api/v1/servers
{
  "serverId": "weather-server",
  "endpoint": "http://localhost:3000/sse",
  "transportType": "SSE",
  "healthCheck": {
    "enabled": true,
    "interval": "30s",
    "timeout": "5s"
  }
}

// 查询所有 Server
GET /api/v1/servers

// 查询特定 Server
GET /api/v1/servers/{serverId}

// 启用/禁用 Server
PUT /api/v1/servers/{serverId}/status

// 删除 Server
DELETE /api/v1/servers/{serverId}
```

---

### 3.2 多 Transport 支持

**目标**: 支持多种 MCP 传输协议

**功能点**:
1. **支持的 Transport 类型**
   - **stdio**: 本地进程通信 (命令行启动)
   - **SSE**: Server-Sent Events (长连接)
   - **streamable-http**: 流式 HTTP (WebFlux)

2. **Transport 抽象层**
   ```java
   interface McpTransportClient {
       // 连接到 MCP Server
       Mono<McpConnection> connect(McpServer server);

       // 调用 Tool
       Mono<ToolExecutionResult> invokeTool(
           McpConnection connection,
           String toolName,
           Map<String, Object> arguments
       );

       // 列举 Tools
       Mono<List<Tool>> listTools(McpConnection connection);

       // 关闭连接
       Mono<Void> disconnect(McpConnection connection);
   }
   ```

3. **连接池管理**
   - 每个独立的 McpServer 维护一个连接池
   - 连接复用与生命周期管理
   - 连接健康检查与自动重连

**实现类**:
```java
// SSE Transport 实现
class SseMcpTransportClient implements McpTransportClient {
    // 基于 WebFlux SSE 客户端实现
}

// HTTP Streamable Transport 实现
class StreamableHttpMcpTransportClient implements McpTransportClient {
    // 基于 WebFlux HTTP 客户端实现
}

// STDIO Transport 实现
class StdioMcpTransportClient implements McpTransportClient {
    // 基于 ProcessBuilder 实现
}
```

---

### 3.3 MCP 协议透明转发

**目标**: 对上/下游透传 MCP 协议,不做协议转换

**功能点**:
1. **上行转发 (LLM → Gateway → MCP Server)**
   - 接收 LLM 的 MCP 请求
   - 根据路由策略选择目标 Server
   - 转发原始协议请求

2. **下行转发 (MCP Server → Gateway → LLM)**
   - 接收 MCP Server 响应
   - 统一响应格式
   - 返回给 LLM

3. **协议透明性**
   - 保留原始请求参数
   - 保留响应元数据
   - 支持 MCP 协议的所有特性 (prompts/resources/tools)

**核心处理流程**:
```java
@Service
class McpGatewayService {

    public Mono<ToolExecutionResult> invokeTool(
        String toolName,
        Map<String, Object> arguments
    ) {
        // 1. 查找 Tool 所属的 Server
        Mono<McpServer> server = routingStrategy.route(toolName);

        // 2. 获取连接
        return server.flatMap(svr ->
            connectionPool.getConnection(svr)
        );

        // 3. 调用 Tool
        .flatMap(conn ->
            transportClient.invokeTool(conn, toolName, arguments)
        );

        // 4. 记录日志
        .doOnNext(result -> logInvocation(result));
    }
}
```

---

### 3.4 Tool → Server 路由

**目标**: 根据工具名称智能路由到正确的 MCP Server

**功能点**:
1. **路由策略**
   - **精确匹配**: toolName → serverId 映射
   - **前缀匹配**: 按工具名前缀路由 (如 `weather:*` → weather-server)
   - **标签路由**: 按标签选择 Server (如 `region:us` → us-cluster)
   - **负载均衡**: 同一 Tool 在多个 Server 间均衡

2. **路由表管理**
   ```java
   ToolRouteRule {
       String ruleId;
       String toolNamePattern;     // 支持通配符 (*, **)
       RouteType routeType;        // EXACT, PREFIX, REGEX, TAG
       String targetServerId;      // 目标 Server ID
       int priority;               // 优先级 (数字越小优先级越高)
       Map<String, String> conditions; // 额外条件 (标签匹配)
   }
   ```

3. **路由匹配算法**
   ```
   1. 解析 Tool 名称
   2. 按优先级排序路由规则
   3. 依次匹配规则
   4. 找到第一个匹配的规则
   5. 返回目标 Server 列表
   6. 应用负载均衡策略选择最终 Server
   ```

**API 设计**:
```java
// 添加路由规则
POST /api/v1/routes
{
  "toolNamePattern": "weather:*",
  "routeType": "PREFIX",
  "targetServerId": "weather-server",
  "priority": 100
}

// 查询路由表
GET /api/v1/routes

// 删除路由规则
DELETE /api/v1/routes/{ruleId}
```

---

### 3.5 调用日志与审计

**目标**: 记录所有 Tool 调用的完整链路

**功能点**:
1. **调用日志记录**
   ```java
   ToolInvocationLog {
       String invocationId;        // UUID
       String requestId;           // 上游请求 ID
       String toolName;
       Map<String, Object> arguments;
       String serverId;
       String serverEndpoint;
       Instant startTime;
       Instant endTime;
       long durationMs;
       InvocationStatus status;    // SUCCESS, FAILURE, TIMEOUT
       String errorMessage;
       Map<String, Object> metadata;
   }
   ```

2. **日志内容**
   - 请求信息 (toolName, arguments)
   - 路由信息 (selectedServer, routingStrategy)
   - 性能指标 (duration, latency)
   - 错误信息 (errorMessage, stackTrace)
   - 审计信息 (timestamp, caller)

3. **日志存储**
   - 内存存储 (最近 N 条)
   - 数据库持久化 (MySQL/PostgreSQL)
   - 日志文件 (RollingFile)
   - 外部系统 (ELK/Loki)

4. **审计查询**
   ```java
   // 按时间范围查询
   GET /api/v1/audit/logs?startTime=...&endTime=...

   // 按 Tool 名称查询
   GET /api/v1/audit/logs?toolName=weather:get

   // 按 Server ID 查询
   GET /api/v1/audit/logs?serverId=weather-server

   // 按状态查询
   GET /api/v1/audit/logs?status=FAILURE
   ```

---

### 3.6 健康检查

**目标**: 监控 MCP Server 的健康状态,自动摘除故障节点

**功能点**:
1. **健康检查策略**
   - **主动检查**: 定期发送 ping/health 请求
   - **被动检查**: 监控调用失败率
   - **混合模式**: 主动 + 被动结合

2. **检查机制**
   ```java
   @Component
   class McpServerHealthChecker {

       // 定时任务: 每 30s 检查一次
       @Scheduled(fixedRate = 30000)
       public void checkAllServers() {
           mcpServerRegistry.getAllServers()
               .forEach(this::checkServerHealth);
       }

       private void checkServerHealth(McpServer server) {
           // 1. 尝试连接
           // 2. 发送 ping 请求
           // 3. 记录响应时间
           // 4. 更新健康状态
       }
   }
   ```

3. **健康指标**
   - 连接成功率
   - 响应时间
   - 错误率
   - 并发连接数

4. **自动故障转移**
   - UNHEALTHY → 从负载均衡池移除
   - HEALTHY → 恢复到负载均衡池
   - 连续失败 N 次 → 标记为 UNHEALTHY
   - 连续成功 N 次 → 标记为 HEALTHY

**配置示例**:
```yaml
mcp.gateway.health-check:
  enabled: true
  interval: 30s
  timeout: 5s
  failure-threshold: 3    # 连续失败 3 次标记为不健康
  recovery-threshold: 2   # 连续成功 2 次标记为健康
```

---

### 3.7 失败重试与高可用

**目标**: 提高调用成功率,保证服务高可用

**功能点**:
1. **重试策略**
   - **重试条件**: 连接失败、超时、5xx 错误
   - **重试次数**: 可配置 (默认 3 次)
   - **退避策略**:
     - Fixed Delay: 固定延迟
     - Exponential Backoff: 指数退避
     - Random Jitter: 随机抖动

2. **重试算法**
   ```java
   @Component
   class RetryStrategy {

       public Mono<ToolExecutionResult> execute(
           Supplier<Mono<ToolExecutionResult>> supplier
       ) {
           return Mono.defer(supplier)
               .retryWhen(Retry.backoff(maxRetries, Duration.ofMillis(100))
                   .filter(this::isRetryableError)
                   .doAfterRetry(signal -> logRetry(signal))
               );
       }

       private boolean isRetryableError(Throwable e) {
           return e instanceof ConnectException
               || e instanceof TimeoutException
               || e instanceof MCPServerError;
       }
   }
   ```

3. **故障转移 (Failover)**
   - **同 Server 重试**: 同一个 Server 重试 N 次
   - **跨 Server 重试**: 尝试其他健康的 Server
   - **降级策略**: 返回默认值或缓存结果

4. **熔断器 (Circuit Breaker)**
   - 连续失败率达到阈值 → 打开熔断器
   - 熔断器打开 → 快速失败,不发起调用
   - 半开状态 → 尝试恢复

**配置示例**:
```yaml
mcp.gateway.retry:
  max-attempts: 3
  backoff-policy: EXPONENTIAL
  initial-delay: 100ms
  max-delay: 5s
  retry-on-timeout: true
  retry-on-error: true

mcp.gateway.circuit-breaker:
  enabled: true
  failure-threshold: 10     # 失败阈值
  success-threshold: 2      # 成功阈值 (半开状态)
  timeout: 60s              # 熔断器打开后等待时间
```

---

### 3.8 负载均衡

**目标**: 在多个 MCP Server 间均衡分配请求

**功能点**:
1. **负载均衡策略**
   - **Round Robin**: 轮询
   - **Weighted Round Robin**: 加权轮询
   - **Random**: 随机
   - **Weighted Random**: 加权随机
   - **Least Connections**: 最少连接
   - **IP Hash**: 一致性哈希
   - **Response Time**: 响应时间优先

2. **策略选择**
   ```java
   interface LoadBalancingStrategy {
       McpServer select(List<McpServer> servers, ToolContext context);
   }

   // 轮询实现
   class RoundRobinStrategy implements LoadBalancingStrategy {
       private final AtomicInteger counter = new AtomicInteger(0);

       public McpServer select(List<McpServer> servers, ToolContext context) {
           int index = counter.getAndIncrement() % servers.size();
           return servers.get(index);
       }
   }

   // 加权轮询实现
   class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
       public McpServer select(List<McpServer> servers, ToolContext context) {
           // 根据权重选择 Server
       }
   }
   ```

3. **动态权重调整**
   - 根据响应时间自动调整权重
   - 根据成功率自动调整权重
   - 手动配置权重

**配置示例**:
```yaml
mcp.gateway.load-balancing:
  strategy: WEIGHTED_ROUND_ROBIN
  servers:
    - serverId: "weather-server-1"
      weight: 10
    - serverId: "weather-server-2"
      weight: 5
```

---

### 3.9 可扩展策略设计

**目标**: 支持自定义扩展,满足特定业务需求

**功能点**:
1. **策略接口**
   ```java
   // 路由策略扩展
   interface RoutingStrategy {
       Mono<List<McpServer>> route(String toolName, Map<String, Object> context);
   }

   // 负载均衡策略扩展
   interface LoadBalancingStrategy {
       McpServer select(List<McpServer> servers, ToolContext context);
   }

   // 重试策略扩展
   interface RetryStrategy {
       Mono<ToolExecutionResult> execute(Supplier<Mono<ToolExecutionResult>> supplier);
   }

   // 健康检查策略扩展
   interface HealthCheckStrategy {
       Mono<HealthStatus> check(McpServer server);
   }
   ```

2. **SPI 机制**
   - 支持通过 SPI 加载自定义策略
   - 配置文件指定策略实现类

3. **插件系统**
   ```java
   @Component
   @Plugin(name = "customRouting")
   class CustomRoutingStrategy implements RoutingStrategy {
       // 自定义路由逻辑
   }
   ```

4. **配置驱动**
   ```yaml
   mcp.gateway:
     routing-strategy: com.example.CustomRoutingStrategy
     load-balancing-strategy: com.example.CustomLoadBalancingStrategy
   ```

---

## 4. 数据模型设计

### 4.1 核心领域对象

```java
// MCP Server 注册信息
@Data
class McpServer {
    String serverId;
    String serverName;
    String description;
    TransportType transportType;
    String endpoint;
    Map<String, Object> capabilities;
    List<String> supportedTools;
    HealthStatus healthStatus;
    int weight;
    Map<String, String> tags;
    Instant registrationTime;
    Instant lastHealthCheckTime;
}

// Tool 路由规则
@Data
class ToolRouteRule {
    String ruleId;
    String toolNamePattern;
    RouteType routeType;
    String targetServerId;
    int priority;
    Map<String, String> conditions;
    boolean enabled;
}

// Tool 调用日志
@Data
class ToolInvocationLog {
    String invocationId;
    String requestId;
    String toolName;
    Map<String, Object> arguments;
    String selectedServerId;
    Instant startTime;
    Instant endTime;
    long durationMs;
    InvocationStatus status;
    String errorMessage;
    Map<String, Object> metadata;
}

// MCP 连接
@Data
class McpConnection {
    String connectionId;
    McpServer server;
    Object transportConnection;  // 实际的连接对象
    Instant creationTime;
    Instant lastUsedTime;
    ConnectionStatus status;
}

// Tool 上下文
@Data
class ToolContext {
    String toolName;
    Map<String, Object> arguments;
    Map<String, String> headers;
    Map<String, Object> metadata;
}
```

---

## 5. API 设计

### 5.1 MCP Protocol API (对上层暴露)

```
# SSE 方式暴露 MCP Server
GET /mcp/sse

# HTTP 方式调用 Tool
POST /mcp/tools/invoke
{
  "toolName": "weather:get",
  "arguments": {
    "city": "Beijing"
  }
}

# 列举所有 Tools
GET /mcp/tools

# 列举所有 Resources
GET /mcp/resources

# 列举所有 Prompts
GET /mcp/prompts
```

### 5.2 Gateway Management API

```
# Server 管理
POST   /api/v1/servers          # 注册 Server
GET    /api/v1/servers          # 查询所有 Server
GET    /api/v1/servers/{id}     # 查询特定 Server
PUT    /api/v1/servers/{id}     # 更新 Server
DELETE /api/v1/servers/{id}     # 删除 Server
PUT    /api/v1/servers/{id}/status  # 启用/禁用 Server

# 路由规则管理
POST   /api/v1/routes           # 添加路由规则
GET    /api/v1/routes           # 查询路由规则
DELETE /api/v1/routes/{id}      # 删除路由规则

# 审计日志查询
GET    /api/v1/audit/logs       # 查询调用日志
GET    /api/v1/audit/stats      # 查询统计数据

# 健康检查
GET    /health                  # Gateway 健康检查
GET    /health/servers          # 所有 Server 健康状态
GET    /health/servers/{id}     # 特定 Server 健康状态
```

---

## 6. 配置设计

### 6.1 应用配置 (application.yml)

```yaml
server:
  port: 9000

spring:
  application:
    name: fast-mcp-gateway

# MCP Gateway 配置
mcp:
  gateway:
    # 上游 MCP Server 暴露配置
    server:
      transport-type: SSE        # SSE, HTTP
      endpoint: /mcp/sse

    # 连接池配置
    connection-pool:
      max-connections-per-server: 10
      max-idle-time: 60s
      max-lifetime: 300s

    # 路由配置
    routing:
      strategy: PRIORITY_BASED   # PRIORITY_BASED, TAG_BASED
      default-server: null

    # 负载均衡配置
    load-balancing:
      strategy: WEIGHTED_ROUND_ROBIN
      default-weight: 1

    # 重试配置
    retry:
      max-attempts: 3
      backoff-policy: EXPONENTIAL
      initial-delay: 100ms
      max-delay: 5s

    # 健康检查配置
    health-check:
      enabled: true
      interval: 30s
      timeout: 5s
      failure-threshold: 3
      recovery-threshold: 2

    # 熔断器配置
    circuit-breaker:
      enabled: true
      failure-threshold: 10
      success-threshold: 2
      timeout: 60s

    # 日志配置
    logging:
      enabled: true
      level: INFO              # DEBUG, INFO, WARN, ERROR
      storage: MEMORY          # MEMORY, DATABASE, FILE
      retention-days: 7

    # 指标配置
    metrics:
      enabled: true
      export-prometheus: true

# 下游 MCP Server 注册 (静态配置)
mcp-servers:
  - serverId: "weather-server"
    serverName: "Weather Service"
    transportType: "SSE"
    endpoint: "http://localhost:3000/sse"
    weight: 10
    tags:
      region: "us"
      version: "1.0"
    healthCheck:
      enabled: true
      interval: "30s"

  - serverId: "calculator-server"
    serverName: "Calculator Service"
    transportType: "STDIO"
    endpoint: "python /path/to/calculator_server.py"
    weight: 5
```

---

## 7. 实现优先级

### Phase 1: 核心功能 (MVP)
1. ✅ 项目基础架构搭建
2. ⬜ MCP Server 注册 (静态配置)
3. ⬜ 多 Transport 支持 (SSE, HTTP)
4. ⬜ Tool → Server 路由 (精确匹配)
5. ⬜ Tool 调用透明转发
6. ⬜ 基础日志记录

### Phase 2: 高可用能力
7. ⬜ 健康检查机制
8. ⬜ 失败重试策略
9. ⬜ 负载均衡 (Round Robin)
10. ⬜ 熔断器

### Phase 3: 治理与可观测性
11. ⬜ 动态 Server 注册 API
12. ⬜ 路由规则管理 API
13. ⬜ 审计日志查询 API
14. ⬜ 指标监控与告警
15. ⬜ 分布式追踪

### Phase 4: 高级特性
16. ⬜ 多种负载均衡策略
17. ⬜ 自定义策略扩展
18. ⬜ 流量控制与限流
19. ⬜ 灰度发布支持
20. ⬜ Tool 调用链路追踪

---

## 8. 技术挑战与解决方案

### 8.1 Transport 兼容性

**挑战**: MCP 支持多种 Transport,每种 Transport 的连接管理方式不同

**解决方案**:
- 设计统一的 `McpTransportClient` 接口
- 每种 Transport 实现独立的 Client
- 连接池抽象层屏蔽底层差异

### 8.2 协议透明性

**挑战**: 如何保证 MCP 协议的透明转发,不丢失任何信息

**解决方案**:
- 不解析协议内容,直接透传
- 保留所有请求/响应元数据
- 使用泛型对象存储 (Map<String, Object>)

### 8.3 并发控制

**挑战**: 多个 LLM 同时调用,如何管理连接池和限流

**解决方案**:
- 每个 Server 独立连接池
- 使用 Reactor 的背压机制
- 信号量 (Semaphore) 控制并发

### 8.4 性能优化

**挑战**: Gateway 作为中间层,不能引入太多延迟

**解决方案**:
- 响应式编程 (WebFlux) 非阻塞 I/O
- 连接复用,避免频繁建连
- 异步日志记录
- 缓存 Server 列表和路由表

---

## 9. 测试策略

### 9.1 单元测试
- 每个策略的单元测试 (路由/负载均衡/重试)
- Transport Client 的单元测试
- 工具类的单元测试

### 9.2 集成测试
- MCP Server 注册集成测试
- Tool 调用端到端测试
- 健康检查集成测试
- 故障转移集成测试

### 9.3 性能测试
- 并发调用压测
- 连接池压测
- 内存泄漏测试
- 长时间运行稳定性测试

---

## 10. 部署架构

```
┌──────────────┐
│   LLM/Agent  │
└──────┬───────┘
       │
       │ MCP Protocol (SSE/HTTP)
       ▼
┌─────────────────────────────────────┐
│     MCP Gateway Cluster             │
│  ┌─────────┐  ┌─────────┐           │
│  │ Node 1  │  │ Node 2  │  ...      │
│  └─────────┘  └─────────┘           │
└─────────────────────────────────────┘
       │
       │ MCP Protocol (SSE/HTTP/STDIO)
       ▼
┌─────────────────────────────────────┐
│      MCP Server Pool                │
│  ┌──────────┐  ┌──────────┐         │
│  │ Weather  │  │ Calc     │  ...   │
│  │ Server   │  │ Server   │         │
│  └──────────┘  └──────────┘         │
└─────────────────────────────────────┘
```

### 部署模式
- **单机部署**: 开发/测试环境
- **集群部署**: 生产环境 (多实例 + 负载均衡)
- **容器化**: Docker + Kubernetes

---

## 11. 后续扩展方向

1. **安全性增强**
   - API Key 认证
   - JWT Token 支持
   - Tool 调用权限控制

2. **流量治理**
   - 限流与降级
   - 灰度发布
   - A/B 测试

3. **可观测性增强**
   - 分布式追踪 (Sleuth/Zipkin)
   - 指标导出 (Prometheus)
   - 日志聚合 (ELK)

4. **多租户支持**
   - 租户隔离
   - 配额管理
   - 计费支持

---

## 12. 参考资料

- [MCP Protocol Specification](https://modelcontextprotocol.io)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Project Reactor Documentation](https://projectreactor.io/docs)

---

**文档版本**: v1.0
**最后更新**: 2026-01-08
**维护者**: Fast MCP Gateway Team
