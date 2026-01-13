package org.cloudnook.mcp.application.service;


import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.model.inspector.McpInvocationContext;
import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;
import org.cloudnook.mcp.domain.service.metrics.McpInvocationLogRepository;
import org.cloudnook.mcp.domain.service.inspector.McpInspectorService;
import org.cloudnook.mcp.domain.service.metrics.McpMonitoringService;
import org.cloudnook.mcp.infrastruction.utils.GeneratorUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP Inspector 应用服务
 * 位于应用层，负责编排调试操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpInspectorAppService {

    private final McpInspectorService mcpInspectorService;
    private final McpInvocationLogRepository mcpInvocationLogRepository;
    private final McpMonitoringService monitoringService;

    // ==================== 资源列表查询 ====================

    /**
     * 获取 Server 的 Tools 列表
     *
     * @param serverId 服务ID
     * @return Tools 列表
     */
    public Mono<McpSchema.ListToolsResult> listTools(String serverId) {
        log.info("Inspector: 查询 Tools 列表 - serverId={}", serverId);

        McpInvocationContext context = buildInvocationContext(
                serverId,
                ToolInvocationRecord.InvocationType.TOOL_LIST,
                "list_tools",
                null
        );

        return mcpInspectorService.listTools(serverId)
                .doOnSuccess(result -> monitoringService.recordSuccess(context, result))
                .doOnError(error -> monitoringService.recordFailure(context, error));
    }

    /**
     * 获取 Server 的 Resources 列表
     *
     * @param serverId 服务ID
     * @return Resources 列表
     */
    public Mono<McpSchema.ListResourcesResult> listResources(String serverId) {
        log.info("Inspector: 查询 Resources 列表 - serverId={}", serverId);

        McpInvocationContext context = buildInvocationContext(
                serverId,
                ToolInvocationRecord.InvocationType.RESOURCE_LIST,
                "list_resources",
                null
        );

        return mcpInspectorService.listResources(serverId)
                .doOnSuccess(result -> monitoringService.recordSuccess(context, result))
                .doOnError(error -> monitoringService.recordFailure(context, error));
    }

    /**
     * 获取 Server 的 Prompts 列表
     *
     * @param serverId 服务ID
     * @return Prompts 列表
     */
    public Mono<McpSchema.ListPromptsResult> listPrompts(String serverId) {
        log.info("Inspector: 查询 Prompts 列表 - serverId={}", serverId);

        McpInvocationContext context = buildInvocationContext(
                serverId,
                ToolInvocationRecord.InvocationType.PROMPT_LIST,
                "list_prompts",
                null
        );

        return mcpInspectorService.listPrompts(serverId)
                .doOnSuccess(result -> monitoringService.recordSuccess(context, result))
                .doOnError(error -> monitoringService.recordFailure(context, error));
    }

    // ==================== 资源调用调试 ====================

    /**
     * 调用 Tool
     */
    public Mono<McpSchema.CallToolResult> callTool(String serverId, String toolName, Map<String, Object> arguments) {
        log.info("Inspector: 调用 Tool - serverId={}, toolName={}", serverId, toolName);

        McpInvocationContext context = buildInvocationContext(
                serverId,
                ToolInvocationRecord.InvocationType.TOOL_CALL,
                toolName,
                arguments
        );

        return mcpInspectorService.callTool(serverId, toolName, arguments)
                .doOnSuccess(result -> monitoringService.recordSuccess(context, result))
                .doOnError(error -> monitoringService.recordFailure(context, error));
    }

    /**
     * 读取 Resource
     */
    public Mono<McpSchema.ReadResourceResult> readResource(String serverId, String resourceUri) {
        log.info("Inspector: 读取 Resource - serverId={}, uri={}", serverId, resourceUri);

        McpInvocationContext context = buildInvocationContext(
                serverId,
                ToolInvocationRecord.InvocationType.RESOURCE_READ,
                resourceUri,
                null
        );

        return mcpInspectorService.readResource(serverId, resourceUri)
                .doOnSuccess(result -> monitoringService.recordSuccess(context, result))
                .doOnError(error -> monitoringService.recordFailure(context, error));
    }

    /**
     * 获取 Prompt
     */
    public Mono<McpSchema.GetPromptResult> getPrompt(String serverId, String promptName, Map<String, Object> arguments) {
        log.info("Inspector: 获取 Prompt - serverId={}, promptName={}", serverId, promptName);

        McpInvocationContext context = buildInvocationContext(
                serverId,
                ToolInvocationRecord.InvocationType.PROMPT_GET,
                promptName,
                arguments
        );

        return mcpInspectorService.getPrompt(serverId, promptName, arguments)
                .doOnSuccess(result -> monitoringService.recordSuccess(context, result))
                .doOnError(error -> monitoringService.recordFailure(context, error));
    }

    /**
     * 获取调用历史（分页）
     */
    public List<ToolInvocationRecord> getHistory(int page, int size) {
        return mcpInvocationLogRepository.getPage(page, size);
    }

    /**
     * 根据服务ID获取调用历史（分页）
     *
     * @param serverId 服务ID
     * @param page     页码（从0开始）
     * @param size     每页大小
     * @return 调用历史记录
     */
    public List<ToolInvocationRecord> getHistoryByServerId(String serverId, int page, int size) {
        return mcpInvocationLogRepository.getPageByServerId(serverId, page, size);
    }

    /**
     * 清空调用历史
     */
    public void clearHistory() {
        mcpInvocationLogRepository.clear();
        log.info("Inspector: 调用历史已清空");
    }

    /**
     * 构建监控上下文
     *
     * @param serverId     服务ID
     * @param operationType 操作类型
     * @param targetName   目标名称（toolName/resourceUri/promptName）
     * @param arguments    调用参数
     * @return 监控上下文
     */
    private McpInvocationContext buildInvocationContext(
            String serverId,
            ToolInvocationRecord.InvocationType operationType,
            String targetName,
            Map<String, Object> arguments
    ) {
        return McpInvocationContext.builder()
                .invocationId(GeneratorUtil.generateInvocationId())
                .serverId(serverId)
                .operationType(operationType)
                .targetName(targetName)
                .arguments(arguments)
                .startTime(Instant.now())
                .build();
    }
}
