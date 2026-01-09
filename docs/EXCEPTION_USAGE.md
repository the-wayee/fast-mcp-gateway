# 异常处理使用指南

## 概述

MCP Gateway 使用统一的异常处理机制，包括：
- **业务异常类**: 继承自 `McpGatewayException`
- **全局异常拦截器**: `GlobalExceptionHandler`
- **标准错误响应**: 统一的 JSON 格式

---

## 异常类层次结构

```
McpGatewayException (基类)
├── ServerException (服务相关异常)
│   ├── ServerNotFoundException (服务未找到)
│   ├── ServerAlreadyExistsException (服务已存在)
│   ├── ServerStatusException (服务状态异常)
│   └── ServerConnectionException (服务连接失败)
├── RouteException (路由相关异常)
│   ├── RouteNotFoundException (路由未找到)
│   └── NoAvailableServerException (无可用服务实例)
└── TransportException (传输相关异常)
    ├── UnsupportedTransportException (不支持的传输类型)
    ├── TransportTimeoutException (传输超时)
    └── TransportInitException (传输初始化失败)
```

---

## 使用示例

### 1. 在业务代码中抛出异常

#### 示例 1: 服务未找到
```java
@Service
public class McpServerService {

    @Autowired
    private McpRegisterInterface mcpRegister;

    public McpServer getServer(String serverId) {
        Optional<McpServer> server = mcpRegister.getServer(serverId);

        if (server.isEmpty()) {
            // 抛出服务未找到异常
            throw new ServerException.ServerNotFoundException(serverId);
        }

        return server.get();
    }
}
```

#### 示例 2: 服务已存在
```java
@Override
public boolean register(McpServer mcpServer) {
    String serverId = mcpServer.getId();

    // 检查是否已存在
    if (mcpRegister.getServer(serverId).isPresent()) {
        // 抛出服务已存在异常
        throw new ServerException.ServerAlreadyExistsException(serverId);
    }

    return mcpRegister.register(mcpServer);
}
```

#### 示例 3: 无可用服务实例
```java
public McpServer selectServer(String serviceName) {
    List<McpServer> servers = mcpRegister.getServersByName(serviceName);

    // 过滤健康实例
    List<McpServer> healthyServers = servers.stream()
            .filter(s -> s.getStatus() == McpServerStatus.ACTIVE)
            .collect(Collectors.toList());

    if (healthyServers.isEmpty()) {
        // 抛出无可用服务实例异常
        throw new RouteException.NoAvailableServerException(serviceName, "无健康实例");
    }

    return loadBalancer.select(healthyServers);
}
```

#### 示例 4: 传输连接失败
```java
public Mono<McpConnection> connect(McpServer server) {
    try {
        return transportClient.connect(server.getEndpoint())
                .timeout(Duration.ofSeconds(5));
    } catch (TimeoutException e) {
        // 抛出传输超时异常
        throw new TransportException.TransportTimeoutException(
                server.getEndpoint(),
                5000,
                e
        );
    }
}
```

---

### 2. 自定义异常类

如果需要自定义异常，继承相应的异常基类：

```java
public class CustomException extends McpGatewayException {

    public CustomException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * 自定义异常: 参数验证失败
     */
    public static class ValidationException extends CustomException {
        public ValidationException(String fieldName, String value) {
            super("VALIDATION_FAILED",
                    String.format("参数验证失败: 字段=%s, 值=%s", fieldName, value));
        }
    }

    /**
     * 自定义异常: 权限不足
     */
    public static class PermissionDeniedException extends CustomException {
        public PermissionDeniedException(String resource, String operation) {
            super("PERMISSION_DENIED",
                    String.format("权限不足: 资源=%s, 操作=%s", resource, operation));
        }
    }
}
```

使用自定义异常：
```java
public void deleteServer(String serverId, String userId) {
    if (!hasPermission(userId, "delete", serverId)) {
        throw new CustomException.PermissionDeniedException(serverId, "delete");
    }
}
```

---

### 3. 在 Controller 中处理

Controller 层**不需要**显式捕获异常，全局异常处理器会自动处理：

```java
@RestController
@RequestMapping("/api/v1/servers")
public class McpServerController {

    @Autowired
    private McpServerService mcpServerService;

    @GetMapping("/{serverId}")
    public McpServer getServer(@PathVariable String serverId) {
        // 直接抛出异常，不需要 try-catch
        return mcpServerService.getServer(serverId);
    }

    @PostMapping
    public boolean register(@RequestBody McpServer mcpServer) {
        // 直接抛出异常，不需要 try-catch
        return mcpServerService.register(mcpServer);
    }
}
```

---

### 4. 全局异常处理器返回的标准格式

当抛出异常时，`GlobalExceptionHandler` 会自动返回以下格式：

```json
{
  "success": false,
  "errorCode": "SERVER_NOT_FOUND",
  "errorMessage": "服务未找到: serverId=weather-server-1",
  "httpStatus": 500,
  "timestamp": "2026-01-08T15:30:45"
}
```

**字段说明**：
- `success`: 固定为 `false`
- `errorCode`: 错误码（用于程序判断）
- `errorMessage`: 错误描述（用于展示给用户）
- `httpStatus`: HTTP 状态码
- `timestamp`: 错误发生时间

---

## 常用错误码

### 服务相关 (SERVER_*)
| 错误码 | HTTP 状态 | 说明 |
|--------|----------|------|
| `SERVER_NOT_FOUND` | 500 | 服务未找到 |
| `SERVER_ALREADY_EXISTS` | 400 | 服务已存在 |
| `SERVER_STATUS_ERROR` | 500 | 服务状态异常 |
| `SERVER_CONNECTION_FAILED` | 500 | 服务连接失败 |

### 路由相关 (ROUTE_*)
| 错误码 | HTTP 状态 | 说明 |
|--------|----------|------|
| `ROUTE_NOT_FOUND` | 404 | 路由规则未找到 |
| `NO_AVAILABLE_SERVER` | 404 | 无可用服务实例 |

### Transport 相关 (TRANSPORT_*)
| 错误码 | HTTP 状态 | 说明 |
|--------|----------|------|
| `UNSUPPORTED_TRANSPORT` | 503 | 不支持的传输类型 |
| `TRANSPORT_TIMEOUT` | 503 | 传输连接超时 |
| `TRANSPORT_INIT_FAILED` | 503 | 传输初始化失败 |

---

## 最佳实践

### 1. 使用语义化的异常
```java
// ✅ 好的做法：使用具体的异常类
if (server == null) {
    throw new ServerException.ServerNotFoundException(serverId);
}

// ❌ 不好的做法：使用通用异常
if (server == null) {
    throw new McpGatewayException("ERROR", "服务未找到");
}
```

### 2. 异常信息要详细
```java
// ✅ 好的做法：包含关键信息
throw new ServerException.ServerNotFoundException(serverId);

// ❌ 不好的做法：信息模糊
throw new ServerException("SERVER_NOT_FOUND", "服务未找到");
```

### 3. 不要捕获后直接抛出新异常（会丢失堆栈）
```java
// ❌ 不好的做法：丢失原始异常信息
try {
    connect(server);
} catch (Exception e) {
    throw new ServerException("ERROR", "连接失败");
}

// ✅ 好的做法：保留原始异常
try {
    connect(server);
} catch (Exception e) {
    throw new ServerException.ServerConnectionException(serverId, e.getMessage(), e);
}
```

### 4. 使用异常链
```java
try {
    riskyOperation();
} catch (IOException e) {
    // 保留原始异常，便于排查问题
    throw new ServerException.ServerConnectionException(serverId, "IO 错误", e);
}
```

---

## 前端处理示例

### Axios 示例
```javascript
axios.get('/api/v1/servers/123')
  .then(response => {
    console.log('成功:', response.data);
  })
  .catch(error => {
    if (error.response) {
      const { errorCode, errorMessage, httpStatus } = error.response.data;

      switch (errorCode) {
        case 'SERVER_NOT_FOUND':
          message.error('服务不存在');
          break;
        case 'NO_AVAILABLE_SERVER':
          message.error('无可用服务实例');
          break;
        default:
          message.error(errorMessage);
      }
    }
  });
```

---

**文档版本**: v1.0
**最后更新**: 2026-01-08
