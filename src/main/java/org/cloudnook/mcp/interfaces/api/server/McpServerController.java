package org.cloudnook.mcp.interfaces.api.server;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.application.service.McpServerAppService;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.domain.model.shared.McpTransportType;
import org.cloudnook.mcp.infrastruction.common.result.Result;
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

    // ==================== 资源查询接口 ====================

    /**
     * 获取 Server 的 Tools 列表
     */
    @GetMapping("/{serverId}/tools")
    public Mono<Result<McpSchema.ListToolsResult>> listTools(@PathVariable String serverId) {
        return mcpServerAppService.listServerTools(serverId)
                .map(Result::success);
    }

    /**
     * 获取 Server 的 Resources 列表
     */
    @GetMapping("/{serverId}/resources")
    public Mono<Result<McpSchema.ListResourcesResult>> listResources(@PathVariable String serverId) {
        return mcpServerAppService.listServerResources(serverId)
                .map(Result::success);
    }

    /**
     * 获取 Server 的 Prompts 列表
     */
    @GetMapping("/{serverId}/prompts")
    public Mono<Result<McpSchema.ListPromptsResult>> listPrompts(@PathVariable String serverId) {
        return mcpServerAppService.listServerPrompts(serverId)
                .map(Result::success);
    }

    /**
     * 获取传输类型
     */
    @GetMapping("/protocols")
    public Result<List<McpTransportType>> getTransportTypes() {
        return Result.success(Arrays.asList(McpTransportType.values()));
    }
}

