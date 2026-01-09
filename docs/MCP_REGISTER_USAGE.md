# MCP Memory Register 使用说明

## 概述

`McpMemoryRegister` 是一个基于内存的 MCP 服务注册中心，支持：
- 同一服务名称下注册多个服务实例
- 服务实例的注册与注销
- 基于负载均衡的服务实例选择
- 线程安全的并发访问

## 核心功能

### 1. 注册服务实例

```java
@Autowired
private McpRegisterInterface mcpRegister;

// 创建服务实例
McpServer server1 = new McpServer();
server1.setId("weather-server-1");
server1.setName("weather-service");
server1.setTransportType(McpTransportType.SSE);
server1.setStatus(McpServerStatus.ACTIVE);

McpServer server2 = new McpServer();
server2.setId("weather-server-2");
server2.setName("weather-service");
server2.setTransportType(McpTransportType.SSE);
server2.setStatus(McpServerStatus.ACTIVE);

// 注册服务
boolean result1 = mcpRegister.register(server1);  // true
boolean result2 = mcpRegister.register(server2);  // true
```

**特点**：
- 同一服务名称（如 `weather-service`）可以注册多个实例
- 每个实例必须有唯一的 `serverId`
- 返回值表示注册是否成功

---

### 2. 注销服务实例

```java
// 注销指定服务实例
boolean result = mcpRegister.unregister("weather-service", "weather-server-1");
```

**特点**：
- 当某个服务名称下的所有实例都被注销后，会自动清理相关记录
- 释放内存资源

---

### 3. 查询服务实例

#### 3.1 根据服务名称和 ID 查询

```java
Optional<McpServer> server = mcpRegister.getServer("weather-service", "weather-server-1");

if (server.isPresent()) {
    McpServer mcpServer = server.get();
    System.out.println("Found server: " + mcpServer.getId());
}
```

#### 3.2 根据服务名称查询所有实例

```java
List<McpServer> servers = mcpRegister.getServersByName("weather-service");

System.out.println("Total instances: " + servers.size());
// 输出: Total instances: 2
```

#### 3.3 基于负载均衡查询（核心功能）

```java
Optional<McpServer> selectedServer = mcpRegister.getServerByLoadBalance("weather-service");

if (selectedServer.isPresent()) {
    McpServer server = selectedServer.getServer();
    // 自动选择一个健康的服务实例
    invokeTool(server);
}
```

**负载均衡机制**：
1. 过滤出状态为 `ACTIVE` 的健康实例
2. 使用 **Round Robin（轮询）** 算法选择实例
3. 每次调用会轮询到不同的实例
4. 自动跳过不健康的实例

**调用示例**：
```java
// 第一次调用，返回 weather-server-1
Optional<McpServer> s1 = mcpRegister.getServerByLoadBalance("weather-service");

// 第二次调用，返回 weather-server-2
Optional<McpServer> s2 = mcpRegister.getServerByLoadBalance("weather-service");

// 第三次调用，返回 weather-server-1（循环）
Optional<McpServer> s3 = mcpRegister.getServerByLoadBalance("weather-service");
```

---

### 4. 辅助查询方法

```java
// 检查服务名称是否存在
boolean hasService = mcpRegister.hasServer("weather-service");  // true

// 获取服务实例数量
int count = mcpRegister.getServerCount("weather-service");  // 2

// 获取所有服务
List<McpServer> allServers = mcpRegister.getAllServers();
```

---

## 数据结构

### 内部存储

```java
// 1. 服务名称 -> 服务ID集合
ConcurrentHashMap<String, Set<String>> NAME_IDS_HOLDER
// 示例:
// "weather-service" -> ["weather-server-1", "weather-server-2"]
// "calc-service"    -> ["calc-server-1"]

// 2. 服务ID -> 服务实例
ConcurrentHashMap<String, McpServer> ID_SERVER_HOLDER
// 示例:
// "weather-server-1" -> McpServer{...}
// "weather-server-2" -> McpServer{...}

// 3. 轮询索引计数器
ConcurrentHashMap<String, AtomicInteger> ROUND_ROBIN_INDEX
// 示例:
// "weather-service" -> AtomicInteger(5)
```

---

## 负载均衡策略

### 当前实现：Round Robin（轮询）

**算法特点**：
- 请求平均分配到每个实例
- 简单、公平、无状态
- 适合实例性能相近的场景

**选择过程**：
```
实例列表: [server-1, server-2, server-3]

第1次请求: server-1 (index=0)
第2次请求: server-2 (index=1)
第3次请求: server-3 (index=2)
第4次请求: server-1 (index=0, 循环)
...
```

### 后期扩展

支持其他负载均衡策略：
- **Weighted Round Robin**: 加权轮询（根据权重）
- **Random**: 随机选择
- **Least Connections**: 最少连接优先
- **Response Time**: 响应时间优先

**扩展方式**：
```java
// 实现 LoadBalanceStrategy 接口
@Component
public class WeightedRoundRobinStrategy implements LoadBalanceStrategy {
    @Override
    public McpServer select(List<McpServer> servers) {
        // 根据权重选择实例
    }
}

// 在配置中指定策略
mcp.gateway.load-balancing.strategy: WEIGHTED_ROUND_ROBIN
```

---

## 线程安全性

**保证**：
- 使用 `ConcurrentHashMap` 保证并发安全
- 使用 `AtomicInteger` 保证原子操作
- 无锁竞争，高并发性能好

**适用场景**：
- 多线程同时注册/注销服务
- 高并发请求负载均衡选择

---

## 使用示例：完整场景

```java
@Service
public class McpGatewayService {

    @Autowired
    private McpRegisterInterface mcpRegister;

    /**
     * 启动时注册服务实例
     */
    @PostConstruct
    public void init() {
        // 注册多个 weather-service 实例
        for (int i = 1; i <= 3; i++) {
            McpServer server = new McpServer();
            server.setId("weather-server-" + i);
            server.setName("weather-service");
            server.setStatus(McpServerStatus.ACTIVE);
            mcpRegister.register(server);
        }
    }

    /**
     * 处理 Tool 调用请求
     */
    public Mono<ToolResult> invokeTool(String serviceName, String toolName, Map<String, Object> args) {
        // 通过负载均衡选择实例
        Optional<McpServer> serverOpt = mcpRegister.getServerByLoadBalance(serviceName);

        if (serverOpt.isEmpty()) {
            return Mono.error(new ServerNotFoundException("No available server for: " + serviceName));
        }

        McpServer server = serverOpt.get();

        // 调用 MCP Server
        return mcpTransport.invokeTool(server, toolName, args);
    }
}
```

---

## 监控与统计

```java
@Autowired
private McpMemoryRegister mcpMemoryRegister;

/**
 * 获取注册统计信息
 */
public Map<String, Object> getStatistics() {
    return mcpMemoryRegister.getStatistics();
}

// 输出示例:
// {
//   "totalServerNames": 5,          // 5个不同的服务名称
//   "totalServers": 12,             // 总共12个服务实例
//   "serverCounts": {
//     "weather-service": 3,         // weather-service 有3个实例
//     "calc-service": 2,            // calc-service 有2个实例
//     "file-service": 1             // file-service 有1个实例
//   }
// }
```

---

## 注意事项

1. **内存存储**：重启后数据丢失，生产环境建议结合数据库持久化
2. **健康状态过滤**：负载均衡只会选择 `ACTIVE` 状态的实例
3. **空列表处理**：当没有可用实例时，返回 `Optional.empty()`
4. **线程安全**：所有方法都是线程安全的，可以并发调用

---

## 后续优化方向

1. **持久化支持**：结合数据库/Redis 实现持久化注册中心
2. **健康检查**：定期检查服务健康状态，自动更新 `McpServer.status`
3. **故障转移**：当某个实例不可用时，自动切换到其他实例
4. **服务发现**：支持从 Nacos/Consul 等注册中心自动同步服务
5. **权重支持**：支持为不同实例配置权重，实现加权负载均衡

---

**文档版本**: v1.0
**最后更新**: 2026-01-08
