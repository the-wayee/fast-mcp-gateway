# 新架构设计 - Invoke 统一调用能力

## 设计理念

### 核心思想

**Inspector 和 Router 都是入口，不是独立领域**

```
Inspector（调试入口）───┐
                        ├──→ McpInvokeAppService（编排层）
Router（真实请求入口）───┘       ↓
                            调用 Domain 层的各个领域
                                ├── invoke（发出请求）✨
                                ├── metrics（记录指标）
                                └── call（记录日志）
```

### 领域划分

去掉 `inspector` 领域，重新划分为：

```
domain/
├── server/          # 服务管理（包括客户端管理）
├── invoke/          # 调用能力（最后一步发出请求）✨ 核心
├── metrics/         # 监控指标
├── call/            # 调用记录
└── routing/         # 路由策略
```

### invoke 领域的作用

**invoke 领域是最后一步发出请求的领域**

```
Application层（McpInvokeAppService）
    ↓ 编排
Domain层
    ├── invoke.McpInvokeDomainService      ✅ 封装MCP协议调用
    ├── metrics.McpMetricsDomainService    ✅ 记录监控指标
    └── call.McpCallRecordRepository       ✅ 记录调用日志
```

**invoke 领域的职责**：
- 封装 MCP 协议的具体调用细节
- 负责 Tool调用、Resource读取、Prompt获取等操作
- 是整个调用链路的最后一步

## 详细架构

### 1. Domain 层

#### invoke/ 领域（核心调用能力）

```
domain/invoke/
└── service/
    └── McpInvokeDomainService.java    (接口)
```

**职责**：提供MCP协议的核心调用能力
- 列表查询：Tools、Resources、Prompts
- 资源调用：Tool调用、Resource读取、Prompt获取

#### call/ 领域（调用记录）

```
domain/call/
├── model/
│   ├── McpCallRecord.java          (调用记录模型)
│   └── McpCallContext.java         (调用上下文)
└── repository/
    └── McpCallRecordRepository.java  (仓储接口)
```

**职责**：记录所有对MCP Server的调用
- 支持来源标记：INSPECTOR（调试）/ ROUTER（真实请求）
- 支持按来源、服务ID查询

### 2. Application 层

```
application/service/
├── McpInvokeAppService.java         ✨ 新增（统一调用能力）
├── McpRouterAppService.java         ✨ 新增（路由服务）
├── McpServerAppService.java         (服务管理)
└── McpMetricsAppService.java        ✨ 新增（监控查询）
```

#### McpInvokeAppService（核心）

```java
@Service
@RequiredArgsConstructor
public class McpInvokeAppService {

    private final McpInvokeDomainService mcpInvokeDomainService;
    private final McpMetricsDomainService mcpMetricsDomainService;
    private final McpCallRecordRepository mcpCallRecordRepository;

    /**
     * 调用 Tool
     *
     * @param serverId 服务ID
     * @param toolName Tool名称
     * @param arguments 调用参数
     * @param source 调用来源（INSPECTOR/ROUTER）
     * @param clientId 客户端ID（可选，仅ROUTER来源）
     */
    public Mono<McpSchema.CallToolResult> callTool(
        String serverId,
        String toolName,
        Map<String, Object> arguments,
        McpCallRecord.CallSource source,
        String clientId
    ) {
        // 1. 构建调用上下文
        McpCallContext context = buildCallContext(
            serverId, toolName, arguments, source, clientId
        );

        // 2. 执行调用
        return mcpInvokeDomainService.callTool(serverId, toolName, arguments)
            .doOnSuccess(result -> {
                // 3. 记录监控指标
                mcpMetricsDomainService.recordSuccess(context, result);

                // 4. 记录调用日志
                mcpCallRecordRepository.add(buildCallRecord(context, result));
            })
            .doOnError(error -> {
                // 记录失败
                mcpMetricsDomainService.recordFailure(context, error);
                mcpCallRecordRepository.add(buildCallRecord(context, error));
            });
    }

    // 其他方法：listTools, listResources, callTool, readResource, getPrompt 等
}
```

#### McpRouterAppService（路由服务）

```java
@Service
@RequiredArgsConstructor
public class McpRouterAppService {

    private final McpInvokeAppService mcpInvokeAppService;
    private final McpRouterDomainService mcpRouterDomainService;

    /**
     * 通过路由调用 Tool
     *
     * @param serverName 服务名称（可能对应多个实例）
     * @param toolName Tool名称
     * @param arguments 调用参数
     * @param clientId 客户端ID
     * @return 调用结果
     */
    public Mono<McpSchema.CallToolResult> routedCallTool(
        String serverName,
        String toolName,
        Map<String, Object> arguments,
        String clientId
    ) {
        // 1. 根据负载均衡算法选择 serverId
        String serverId = mcpRouterDomainService.selectServer(serverName);

        // 2. 调用统一的能力服务
        return mcpInvokeAppService.callTool(
            serverId,
            toolName,
            arguments,
            McpCallRecord.CallSource.ROUTER,  // 标记为路由来源
            clientId
        );
    }
}
```

### 3. Controller 层

#### McpInspectorController（调试入口）

```java
@RestController
@RequestMapping("/inspector")
@RequiredArgsConstructor
public class McpInspectorController {

    private final McpInvokeAppService mcpInvokeAppService;

    /**
     * 调用 Tool（调试入口）
     *
     * 用户直接指定 serverId
     */
    @PostMapping("/{serverId}/tools/call")
    public Mono<Result<McpSchema.CallToolResult>> callTool(
        @PathVariable String serverId,
        @RequestBody ToolCallRequest request
    ) {
        return mcpInvokeAppService.callTool(
            serverId,
            request.getToolName(),
            request.getArguments(),
            McpCallRecord.CallSource.INSPECTOR,  // 标记为调试来源
            null                                // 调试入口没有clientId
        )
        .map(Result::success)
        .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // 其他方法：listTools, listResources, readResource, getPrompt 等
}
```

#### McpRouterController（真实请求入口）

```java
@RestController
@RequestMapping("/router")
@RequiredArgsConstructor
public class McpRouterController {

    private final McpRouterAppService mcpRouterAppService;

    /**
     * 调用 Tool（真实请求入口）
     *
     * 用户只指定 serverName，由路由选择 serverId
     */
    @PostMapping("/{serverName}/tools/call")
    public Mono<Result<McpSchema.CallToolResult>> callTool(
        @PathVariable String serverName,
        @RequestHeader("X-Client-ID") String clientId,
        @RequestBody ToolCallRequest request
    ) {
        return mcpRouterAppService.routedCallTool(
            serverName,                      // 服务名称
            request.getToolName(),
            request.getArguments(),
            clientId                         // 客户端ID
        )
        .map(Result::success)
        .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // 其他方法：listTools, listResources, callTool, readResource, getPrompt 等
}
```

### 4. Infrastructure 层

```
infrastructure/
├── invoke/
│   └── McpInvokeDomainServiceImpl.java   ✨ 新增
├── call/
│   └── McpCallRecordMemoryRepository.java ✨ 新增
├── server/
│   └── McpServerDomainServiceImpl.java
├── metrics/
│   ├── McpMetricsDomainServiceImpl.java
│   └── McpMemoryMetrics.java
└── routing/
    └── McpRouterDomainServiceImpl.java    (待实现)
```

## 依赖关系图

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│  ┌──────────────────┐         ┌──────────────────┐         │
│  │ Inspector        │         │ Router           │         │
│  │ Controller       │         │ Controller       │         │
│  │ (调试入口)        │         │ (真实请求入口)    │         │
│  └────────┬─────────┘         └────────┬─────────┘         │
└───────────┼────────────────────────────┼────────────────────┘
            │                            │
            ↓                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                          │
│  ┌────────────────────────────────────────────────────┐     │
│  │ McpRouterAppService                                 │     │
│  │ - routedCallTool()  (路由选择serverId)             │     │
│  └────────┬───────────────────────────────────────────┘     │
│           │                                                  │
│           ↓                                                  │
│  ┌────────────────────────────────────────────────────┐     │
│  │ McpInvokeAppService  ✨ 统一的调用能力               │     │
│  │ - callTool(serverId, ..., source, clientId)        │     │
│  │ - listTools(), listResources(), ...               │     │
│  └────────┬───────────────────────────────────────────┘     │
└───────────┼──────────────────────────────────────────────────┘
            │
            ↓
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer (接口)                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ invoke/                                              │   │
│  │ - McpInvokeDomainService        ✨ 核心调用能力       │   │
│  │                                                      │   │
│  │ call/                                                │   │
│  │ - McpCallRecordRepository       ✨ 调用记录仓储       │   │
│  │                                                      │   │
│  │ metrics/                                             │   │
│  │ - McpMetricsDomainService                           │   │
│  │                                                      │   │
│  │ routing/                                             │   │
│  │ - McpRouterDomainService                            │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                          ▲
                          │ implements
                          │
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer (实现)                 │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ invoke/                                              │   │
│  │ - McpInvokeDomainServiceImpl    ✨                   │   │
│  │                                                      │   │
│  │ call/                                                │   │
│  │ - McpCallRecordMemoryRepository ✨                   │   │
│  │                                                      │   │
│  │ metrics/                                             │   │
│  │ - McpMetricsDomainServiceImpl                       │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 对比旧架构

### 旧架构问题

```
❌ Inspector 被当作独立领域
❌ InspectorAppService 包含调试逻辑
❌ 没有路由层
❌ 调用记录在 Inspector 领域（不合适）
```

### 新架构优势

```
✅ Inspector 和 Router 都是入口，不是领域
✅ Invoke 是核心能力，统一调用接口
✅ 调用记录独立为 call 领域
✅ 支持来源标记，便于区分调试和真实请求
✅ 支持负载均衡和路由
✅ 职责清晰，符合DDD
```

## 后续实现步骤

1. ✅ 创建 invoke 领域
2. ✅ 创建 call 领域
3. ⏳ 创建 McpInvokeAppService
4. ⏳ 创建 McpRouterAppService
5. ⏳ 重命名 InspectorController
6. ⏳ 创建 RouterController
7. ⏳ 删除旧的 inspector 领域
8. ⏳ 更新所有引用
