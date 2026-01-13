package org.cloudnook.mcp.application.service;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.log.model.McpInvokeContext;
import org.cloudnook.mcp.domain.log.model.McpInvokeLog;
import org.cloudnook.mcp.domain.log.repository.McpInvokeLogRepository;
import org.cloudnook.mcp.domain.invoke.service.McpInvokeDomainService;
import org.cloudnook.mcp.domain.metrics.service.McpMetricsDomainService;
import org.cloudnook.mcp.infrastructure.utils.GeneratorUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * MCP Invoke 应用服务
 * 统一的 MCP 调用能力编排层
 *
 * 职责：
 * 1. 编排多个 Domain 服务
 * 2. invoke（发出请求）
 * 3. metrics（记录指标）
 * 4. log（记录日志）
 *
 * 两个入口都会调用此服务：
 * - Inspector（调试入口）：直接指定 serverId
 * - Router（真实请求入口）：经过路由选择 serverId
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpInvokeAppService {

    private final McpInvokeDomainService mcpInvokeDomainService;
    private final McpMetricsDomainService mcpMetricsDomainService;
    private final McpInvokeLogRepository mcpInvokeLogRepository;

    // ==================== 列表查询 ====================

    /**
     * 获取 Server 的 Tools 列表
     *
     * @param serverId 服务ID
     * @param source   调用来源
     * @return Tools 列表
     */
    public Mono<McpSchema.ListToolsResult> listTools(
            String serverId,
            McpInvokeLog.InvokeSource source
    ) {
        log.info("Invoke: 查询 Tools 列表 - serverId={}, source={}", serverId, source);

        McpInvokeContext context = buildCallContext(
                serverId,
                McpInvokeLog.InvokeType.TOOL_LIST,
                "list_tools",
                null,
                source,
                null
        );

        return mcpInvokeDomainService.listTools(serverId)
                .doOnSuccess(result -> recordSuccess(context, result))
                .doOnError(error -> recordFailure(context, error));
    }

    /**
     * 获取 Server 的 Resources 列表
     *
     * @param serverId 服务ID
     * @param source   调用来源
     * @return Resources 列表
     */
    public Mono<McpSchema.ListResourcesResult> listResources(
            String serverId,
            McpInvokeLog.InvokeSource source
    ) {
        log.info("Invoke: 查询 Resources 列表 - serverId={}, source={}", serverId, source);

        McpInvokeContext context = buildCallContext(
                serverId,
                McpInvokeLog.InvokeType.RESOURCE_LIST,
                "list_resources",
                null,
                source,
                null
        );

        return mcpInvokeDomainService.listResources(serverId)
                .doOnSuccess(result -> recordSuccess(context, result))
                .doOnError(error -> recordFailure(context, error));
    }

    /**
     * 获取 Server 的 Prompts 列表
     *
     * @param serverId 服务ID
     * @param source   调用来源
     * @return Prompts 列表
     */
    public Mono<McpSchema.ListPromptsResult> listPrompts(
            String serverId,
            McpInvokeLog.InvokeSource source
    ) {
        log.info("Invoke: 查询 Prompts 列表 - serverId={}, source={}", serverId, source);

        McpInvokeContext context = buildCallContext(
                serverId,
                McpInvokeLog.InvokeType.PROMPT_LIST,
                "list_prompts",
                null,
                source,
                null
        );

        return mcpInvokeDomainService.listPrompts(serverId)
                .doOnSuccess(result -> recordSuccess(context, result))
                .doOnError(error -> recordFailure(context, error));
    }

    // ==================== 资源调用 ====================

    /**
     * 调用 Tool
     *
     * @param serverId  服务ID
     * @param toolName  Tool名称
     * @param arguments 调用参数
     * @param source    调用来源
     * @param clientId  客户端ID（可选，仅ROUTER来源）
     * @return Tool 调用结果
     */
    public Mono<McpSchema.CallToolResult> callTool(
            String serverId,
            String toolName,
            Map<String, Object> arguments,
            McpInvokeLog.InvokeSource source,
            String clientId
    ) {
        log.info("Invoke: 调用 Tool - serverId={}, toolName={}, source={}, clientId={}",
                serverId, toolName, source, clientId);

        McpInvokeContext context = buildCallContext(
                serverId,
                McpInvokeLog.InvokeType.TOOL_CALL,
                toolName,
                arguments,
                source,
                clientId
        );

        return mcpInvokeDomainService.callTool(serverId, toolName, arguments)
                .doOnSuccess(result -> recordSuccess(context, result))
                .doOnError(error -> recordFailure(context, error));
    }

    /**
     * 读取 Resource
     *
     * @param serverId    服务ID
     * @param resourceUri Resource URI
     * @param source      调用来源
     * @return Resource 内容
     */
    public Mono<McpSchema.ReadResourceResult> readResource(
            String serverId,
            String resourceUri,
            McpInvokeLog.InvokeSource source
    ) {
        log.info("Invoke: 读取 Resource - serverId={}, uri={}, source={}", serverId, resourceUri, source);

        McpInvokeContext context = buildCallContext(
                serverId,
                McpInvokeLog.InvokeType.RESOURCE_READ,
                resourceUri,
                null,
                source,
                null
        );

        return mcpInvokeDomainService.readResource(serverId, resourceUri)
                .doOnSuccess(result -> recordSuccess(context, result))
                .doOnError(error -> recordFailure(context, error));
    }

    /**
     * 获取 Prompt
     *
     * @param serverId   服务ID
     * @param promptName Prompt名称
     * @param arguments  Prompt参数
     * @param source     调用来源
     * @return Prompt 内容
     */
    public Mono<McpSchema.GetPromptResult> getPrompt(
            String serverId,
            String promptName,
            Map<String, Object> arguments,
            McpInvokeLog.InvokeSource source
    ) {
        log.info("Invoke: 获取 Prompt - serverId={}, promptName={}, source={}", serverId, promptName, source);

        McpInvokeContext context = buildCallContext(
                serverId,
                McpInvokeLog.InvokeType.PROMPT_GET,
                promptName,
                arguments,
                source,
                null
        );

        return mcpInvokeDomainService.getPrompt(serverId, promptName, arguments)
                .doOnSuccess(result -> recordSuccess(context, result))
                .doOnError(error -> recordFailure(context, error));
    }

    // ==================== 私有方法 ====================

    /**
     * 记录成功调用
     */
    private void recordSuccess(McpInvokeContext context, Object result) {
        // 记录监控指标
        mcpMetricsDomainService.recordSuccess(context, result);

        // 记录调用日志
        McpInvokeLog log = buildInvokeLog(context, result, null);
        mcpInvokeLogRepository.add(log);
    }

    /**
     * 记录失败调用
     */
    private void recordFailure(McpInvokeContext context, Throwable error) {
        // 记录监控指标
        mcpMetricsDomainService.recordFailure(context, error);

        // 记录调用日志
        McpInvokeLog log = buildInvokeLog(context, null, error.getMessage());
        mcpInvokeLogRepository.add(log);
    }

    /**
     * 构建调用上下文
     */
    private McpInvokeContext buildCallContext(
            String serverId,
            McpInvokeLog.InvokeType type,
            String targetName,
            Map<String, Object> arguments,
            McpInvokeLog.InvokeSource source,
            String clientId
    ) {
        return McpInvokeContext.builder()
                .callId(GeneratorUtil.generateInvocationId())
                .serverId(serverId)
                .type(type)
                .targetName(targetName)
                .arguments(arguments)
                .source(source)
                .clientId(clientId)
                .startTime(Instant.now())
                .build();
    }

    /**
     * 构建调用记录
     */
    private McpInvokeLog buildInvokeLog(
            McpInvokeContext context,
            Object result,
            String errorMessage
    ) {
        long durationMs = Instant.now().toEpochMilli() - context.getStartTime().toEpochMilli();

        String responseJson = null;
        if (result != null) {
            responseJson = result.toString();
        }

        return McpInvokeLog.builder()
                .callId(context.getCallId())
                .source(context.getSource())
                .serverId(context.getServerId())
                .type(context.getType())
                .targetName(context.getTargetName())
                .arguments(context.getArguments())
                .response(responseJson)
                .status(errorMessage == null ? McpInvokeLog.InvokeStatus.SUCCESS : McpInvokeLog.InvokeStatus.FAILURE)
                .errorMessage(errorMessage)
                .durationMs(durationMs)
                .timestamp(context.getStartTime())
                .clientId(context.getClientId())
                .build();
    }
}
