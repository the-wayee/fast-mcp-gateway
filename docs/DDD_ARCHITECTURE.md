# DDD 分层架构说明

## 架构层次

```
┌─────────────────────────────────────────────────────────────┐
│                    Interface Layer (接口层)                  │
│  ├─ McpServerController (REST API)                          │
│  └─ 负责：接收 HTTP 请求，参数校验，返回响应                │
└────────────────────┬────────────────────────────────────────┘
                     │ 调用
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                  Application Layer (应用层)                  │
│  ├─ McpServerAppService                                     │
│  └─ 负责：业务编排，事务控制，调用领域服务                  │
└────────────────────┬────────────────────────────────────────┘
                     │ 调用
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    Domain Layer (领域层)                     │
│  ├─ McpRegisterInterface (接口)                             │
│  ├─ McpServer (实体)                                        │
│  └─ 负责：核心业务逻辑，业务规则                             │
└────────────────────┬────────────────────────────────────────┘
                     │ 被实现
                     ▼
┌─────────────────────────────────────────────────────────────┐
│               Infrastructure Layer (基础设施层)              │
│  ├─ McpMemoryRegister (内存实现)                            │
│  ├─ McpRedisRegister (Redis 实现)                           │
│  └─ 负责：技术实现，数据持久化，外部系统集成                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 各层职责

### 1. Interface Layer (接口层)
**位置**: `interfaces/api/`

**职责**:
- 接收 HTTP 请求
- 参数校验
- 调用应用层服务
- 返回统一格式响应 (Result<T>)

**示例**:
```java
@RestController
@RequestMapping("/api/v1/servers")
public class McpServerController {

    private final McpServerAppService mcpServerAppService;

    @PostMapping
    public Result<Boolean> register(@RequestBody McpServer server) {
        // 只负责接收请求和返回响应
        boolean result = mcpServerAppService.registerServer(server);
        return Result.success(result);
    }
}
```

---

### 2. Application Layer (应用层)
**位置**: `application/service/`

**职责**:
- 业务流程编排
- 调用多个领域服务
- 事务控制
- 不包含核心业务逻辑

**示例**:
```java
@Service
public class McpServerAppService {

    @Autowired
    private McpRegisterInterface mcpRegister;

    public boolean registerServer(McpServer server) {
        // 编排业务流程
        // 1. 参数校验
        // 2. 调用领域服务
        // 3. 发布事件（可选）
        return mcpRegister.register(server);
    }
}
```

---

### 3. Domain Layer (领域层)
**位置**: `domain/`

**职责**:
- 核心业务逻辑
- 业务规则
- 领域模型
- 定义仓储接口

**示例**:
```java
// 领域接口（领域层定义）
public interface McpRegisterInterface {
    boolean register(McpServer mcpServer);
    boolean unregister(String serverName, String serverId);
    McpServer getServer(String serverName, String serverId);
    List<McpServer> getAllServers();
}

// 领域实体
@Data
public class McpServer {
    private String id;
    private String name;
    private McpServerStatus status;
    private McpTransportType mcpTransportType;
}
```

---

### 4. Infrastructure Layer (基础设施层)
**位置**: `infrastructure/`

**职责**:
- 实现领域层定义的接口
- 数据持久化
- 外部系统集成
- 技术实现细节

**示例**:
```java
// 内存实现
@Service
public class McpMemoryRegister implements McpRegisterInterface {
    @Override
    public boolean register(McpServer mcpServer) {
        // 具体技术实现
    }
}

// Redis 实现
public class McpRedisRegister implements McpRegisterInterface {
    @Override
    public boolean register(McpServer mcpServer) {
        // Redis 存储实现
    }
}
```

---

## 动态选择注册中心实现

### 配置文件 (application.yml)
```yaml
mcp:
  gateway:
    register:
      # 注册中心类型: MEMORY, REDIS
      type: MEMORY
      redis:
        host: localhost
        port: 6379
```

### 配置类 (RegisterConfig)
```java
@Configuration
public class RegisterConfig {

    @Bean
    @Primary
    public McpRegisterInterface mcpRegister() {
        RegisterType type = registerProperties.getType();

        switch (type) {
            case MEMORY:
                return new McpMemoryRegister();
            case REDIS:
                return new McpRedisRegister(redisTemplate, keyPrefix);
            default:
                return new McpMemoryRegister();
        }
    }
}
```

### 应用层注入
```java
@Service
public class McpServerAppService {

    @Autowired
    private McpRegisterInterface mcpRegister;  // 自动注入配置的实现
}
```

---

## 调用链路

### 用户请求流程
```
1. HTTP Request
   ↓
2. McpServerController (接口层)
   - 接收请求
   - 参数校验
   ↓
3. McpServerAppService (应用层)
   - 业务编排
   - 事务控制
   ↓
4. McpRegisterInterface (领域层接口)
   - 定义业务能力
   ↓
5. McpMemoryRegister / McpRedisRegister (基础设施层)
   - 具体技术实现
   - 数据持久化
   ↓
6. Return Result<T>
```

---

## 依赖规则

**重要**: 依赖方向必须从外向内

```
Interface → Application → Domain ← Infrastructure
```

- **Interface 层** 可以调用 Application 层
- **Application 层** 可以调用 Domain 层
- **Infrastructure 层** 实现 Domain 层定义的接口
- **Domain 层** 不依赖任何外层（核心层）

---

## 目录结构

```
org.cloudnook.mcp
├── interfaces/                    # 接口层
│   ├── api/
│   │   └── McpServerController.java
│   └── facade/                   # 门面（可选）
│
├── application/                   # 应用层
│   ├── service/
│   │   └── McpServerAppService.java
│   ├── command/                  # 命令对象（可选）
│   └── query/                    # 查询对象（可选）
│
├── domain/                        # 领域层
│   ├── model/
│   │   ├── server/
│   │   │   └── McpServer.java
│   │   └── shared/
│   │       ├── McpServerStatus.java
│   │       └── McpTransportType.java
│   └── service/
│       └── server/
│           └── McpRegisterInterface.java
│
├── infrastructure/                # 基础设施层
│   ├── config/
│   │   ├── RegisterProperties.java
│   │   └── RegisterConfig.java
│   ├── register/
│   │   ├── McpMemoryRegister.java
│   │   └── McpRedisRegister.java
│   └── common/                   # 基础设施通用组件
│       ├── exception/
│       └── result/
│
└── common/                        # 通用组件（跨层使用）
    ├── exception/
    │   └── BusinessException.java
    └── result/
        └── Result.java
```

---

## 优势

1. **清晰的职责划分**: 每层有明确的职责
2. **易于测试**: 各层可独立测试
3. **灵活替换**: 基础设施实现可轻松替换（内存 → Redis）
4. **核心业务稳定**: 领域层不依赖技术实现
5. **配置驱动**: 通过 YAML 配置选择实现

---

**文档版本**: v1.0
**最后更新**: 2026-01-08
