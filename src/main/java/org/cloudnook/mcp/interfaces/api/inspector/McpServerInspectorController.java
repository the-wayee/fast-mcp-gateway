package org.cloudnook.mcp.interfaces.api.inspector;


import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.application.service.McpInspectorAppService;
import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;
import org.cloudnook.mcp.infrastruction.common.result.Result;
import org.cloudnook.mcp.interfaces.dto.inspector.PromptGetRequest;
import org.cloudnook.mcp.interfaces.dto.inspector.ToolCallRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP Server Inspector 调试接口
 * 提供类似 MCP Inspector 的调试能力
 */
@RestController
@RequestMapping("/inspector")
@RequiredArgsConstructor
public class McpServerInspectorController {

    private final McpInspectorAppService mcpInspectorAppService;

    // ==================== 资源列表查询 ====================

    /**
     * 获取 Server 的 Tools 列表
     */
    @GetMapping("/{serverId}/tools/list")
    public Mono<Result<McpSchema.ListToolsResult>> listTools(@PathVariable String serverId) {
        return mcpInspectorAppService.listTools(serverId)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    /**
     * 获取 Server 的 Resources 列表
     */
    @GetMapping("/{serverId}/resources/list")
    public Mono<Result<McpSchema.ListResourcesResult>> listResources(@PathVariable String serverId) {
        return mcpInspectorAppService.listResources(serverId)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    /**
     * 获取 Server 的 Prompts 列表
     */
    @GetMapping("/{serverId}/prompts/list")
    public Mono<Result<McpSchema.ListPromptsResult>> listPrompts(@PathVariable String serverId) {
        return mcpInspectorAppService.listPrompts(serverId)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== Tool 调试 ====================

    /**
     * 调用 Tool
     */
    @PostMapping("/{serverId}/tools/call")
    public Mono<Result<McpSchema.CallToolResult>> callTool(
            @PathVariable String serverId,
            @RequestBody ToolCallRequest request
    ) {
        return mcpInspectorAppService.callTool(serverId, request.getToolName(), request.getArguments())
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== Resource 调试 ====================

    /**
     * 读取 Resource
     * 
     * 示例: GET /inspector/{serverId}/resources/read?uri=greeting://theway
     */
    @GetMapping("/{serverId}/resources/read")
    public Mono<Result<McpSchema.ReadResourceResult>> readResource(
            @PathVariable String serverId,
            @RequestParam String uri
    ) {
        return mcpInspectorAppService.readResource(serverId, uri)
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== Prompt 调试 ====================

    /**
     * 获取 Prompt
     */
    @PostMapping("/{serverId}/prompts/get")
    public Mono<Result<McpSchema.GetPromptResult>> getPrompt(
            @PathVariable String serverId,
            @RequestBody PromptGetRequest request
    ) {
        return mcpInspectorAppService.getPrompt(serverId, request.getPromptName(), request.getArguments())
                .map(Result::success)
                .onErrorResume(e -> Mono.just(Result.error(e.getMessage())));
    }

    // ==================== 调用历史 ====================

    /**
     * 查询调用历史（全局）
     */
    @GetMapping("/history")
    public Result<List<ToolInvocationRecord>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<ToolInvocationRecord> history = mcpInspectorAppService.getHistory(page, size);
        return Result.success(history);
    }

    /**
     * 查询指定服务的调用历史
     */
    @GetMapping("/{serverId}/history")
    public Result<List<ToolInvocationRecord>> getHistoryByServerId(
            @PathVariable String serverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<ToolInvocationRecord> history = mcpInspectorAppService.getHistoryByServerId(serverId, page, size);
        return Result.success(history);
    }

    /**
     * 清空调用历史
     */
    @DeleteMapping("/history")
    public Result<Void> clearHistory() {
        mcpInspectorAppService.clearHistory();
        return Result.success();
    }
}
