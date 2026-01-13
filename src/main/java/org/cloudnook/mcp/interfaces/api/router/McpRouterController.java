package org.cloudnook.mcp.interfaces.api.router;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.application.service.McpRouterAppService;
import org.cloudnook.mcp.infrastructure.common.result.Result;
import org.cloudnook.mcp.interfaces.dto.inspector.PromptGetRequest;
import org.cloudnook.mcp.interfaces.dto.inspector.ToolCallRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * MCP Router 接口
 * 真实请求的入口，负责将请求路由到具体的 MCP Server 实例
 *
 * 核心功能：
 * 1. 接收客户端请求（使用 serverName 而不是 serverId）
 * 2. 通过负载均衡算法选择具体的 serverId
 * 3. 调用 McpInvokeAppService 执行具体操作
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@RestController
@RequestMapping("/router")
@RequiredArgsConstructor
public class McpRouterController {

    private final McpRouterAppService mcpRouterAppService;

    // ==================== 列表查询 ====================

    /**
     * 获取 Server 的 Tools 列表
     *
     * @param serverName 服务名称（不是serverId）
     */
    @GetMapping("/{serverName}/tools/list")
    public Mono<Result<McpSchema.ListToolsResult>> listTools(@PathVariable String serverName) {
        return mcpRouterAppService.listTools(serverName)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    /**
     * 获取 Server 的 Resources 列表
     */
    @GetMapping("/{serverName}/resources/list")
    public Mono<Result<McpSchema.ListResourcesResult>> listResources(@PathVariable String serverName) {
        return mcpRouterAppService.listResources(serverName)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    /**
     * 获取 Server 的 Prompts 列表
     */
    @GetMapping("/{serverName}/prompts/list")
    public Mono<Result<McpSchema.ListPromptsResult>> listPrompts(@PathVariable String serverName) {
        return mcpRouterAppService.listPrompts(serverName)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== Tool 调用 ====================

    /**
     * 调用 Tool
     *
     * @param serverName 服务名称
     * @param clientId   客户端ID（从请求头获取）
     * @param request    调用请求
     * @return 调用结果
     */
    @PostMapping("/{serverName}/tools/call")
    public Mono<Result<McpSchema.CallToolResult>> callTool(
            @PathVariable String serverName,
            @RequestHeader(value = "X-Client-ID", required = false) String clientId,
            @RequestBody ToolCallRequest request
    ) {
        return mcpRouterAppService.callTool(
                    serverName,
                    request.getToolName(),
                    request.getArguments(),
                    clientId
                )
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== Resource 调用 ====================

    /**
     * 读取 Resource
     *
     * @param serverName    服务名称
     * @param resourceUri  Resource URI
     * @return Resource 内容
     */
    @GetMapping("/{serverName}/resources/read")
    public Mono<Result<McpSchema.ReadResourceResult>> readResource(
            @PathVariable String serverName,
            @RequestParam String resourceUri
    ) {
        return mcpRouterAppService.readResource(serverName, resourceUri)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== Prompt 调用 ====================

    /**
     * 获取 Prompt
     *
     * @param serverName 服务名称
     * @param request    获取请求
     * @return Prompt 内容
     */
    @PostMapping("/{serverName}/prompts/get")
    public Mono<Result<McpSchema.GetPromptResult>> getPrompt(
            @PathVariable String serverName,
            @RequestBody PromptGetRequest request
    ) {
        return mcpRouterAppService.getPrompt(
                    serverName,
                    request.getPromptName(),
                    request.getArguments()
                )
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }
}
