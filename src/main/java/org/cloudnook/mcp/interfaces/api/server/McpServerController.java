package org.cloudnook.mcp.interfaces.api.server;

import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.application.service.McpServerAppService;
import org.cloudnook.mcp.domain.server.model.McpServer;
import org.cloudnook.mcp.domain.server.model.McpTransportType;
import org.cloudnook.mcp.infrastructure.common.result.Result;
import org.cloudnook.mcp.interfaces.dto.server.McpServerRegisterReq;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * MCP 服务管理接口
 * 接口层，负责接收 HTTP 请求，调用应用层服务
 */
@RestController
@RequestMapping("/servers")
@RequiredArgsConstructor
public class McpServerController {

    private final McpServerAppService mcpServerAppService;

    /**
     * 注册服务
     */
    @PostMapping
    public Mono<Result<McpServer>> register(@RequestBody McpServerRegisterReq request) {
       return mcpServerAppService.registerServer(request);
    }

    /**
     * 注销服务
     */
    @DeleteMapping
    public Result<McpServer> unregister(@RequestParam String serverName, @RequestParam String serverId) {
        return Result.success(mcpServerAppService.unregisterServer(serverName, serverId));
    }

    /**
     * 获取服务详情
     */
    @GetMapping("/{serverId}")
    public Result<McpServer> getServer(@PathVariable String serverId, @RequestParam String serverName) {
        McpServer server = mcpServerAppService.getServer(serverName, serverId);
        return Result.success(server);
    }

    /**
     * 获取所有服务
     */
    @GetMapping
    public Result<List<McpServer>> getAllServers() {
        List<McpServer> servers = mcpServerAppService.getAllServers();
        return Result.success(servers);
    }

    /**
     * 获取传输类型
     */
    @GetMapping("/protocols")
    public Result<List<McpTransportType>> getTransportTypes() {
        return Result.success(Arrays.asList(McpTransportType.values()));
    }
}

