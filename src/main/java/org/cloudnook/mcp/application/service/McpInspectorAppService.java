package org.cloudnook.mcp.application.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;
import org.cloudnook.mcp.domain.service.inspector.McpInspectorService;
import org.cloudnook.mcp.infrastruction.inspector.InvocationHistoryMemoryStore;
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
    private final InvocationHistoryMemoryStore historyStore;
    private final ObjectMapper objectMapper;

    /**
     * 调用 Tool
     */
    public Mono<McpSchema.CallToolResult> callTool(String serverId, String toolName, Map<String, Object> arguments) {
        String invocationId = GeneratorUtil.generateInvocationId();
        Instant startTime = Instant.now();

        log.info("Inspector: 调用 Tool - serverId={}, toolName={}", serverId, toolName);

        return mcpInspectorService.callTool(serverId, toolName, arguments)
                .doOnSuccess(result -> {
                    // 记录成功
                    ToolInvocationRecord record = buildRecord(
                            invocationId, serverId, ToolInvocationRecord.InvocationType.TOOL_CALL,
                            toolName, arguments, result, startTime,
                            ToolInvocationRecord.InvocationStatus.SUCCESS, null
                    );
                    historyStore.add(record);
                })
                .doOnError(error -> {
                    // 记录失败
                    ToolInvocationRecord record = buildRecord(
                            invocationId, serverId, ToolInvocationRecord.InvocationType.TOOL_CALL,
                            toolName, arguments, null, startTime,
                            ToolInvocationRecord.InvocationStatus.FAILURE, error.getMessage()
                    );
                    historyStore.add(record);
                });
    }

    /**
     * 读取 Resource
     */
    public Mono<McpSchema.ReadResourceResult> readResource(String serverId, String resourceUri) {
        String invocationId = GeneratorUtil.generateInvocationId();
        Instant startTime = Instant.now();

        log.info("Inspector: 读取 Resource - serverId={}, uri={}", serverId, resourceUri);

        return mcpInspectorService.readResource(serverId, resourceUri)
                .doOnSuccess(result -> {
                    ToolInvocationRecord record = buildRecord(
                            invocationId, serverId, ToolInvocationRecord.InvocationType.RESOURCE_READ,
                            resourceUri, null, result, startTime,
                            ToolInvocationRecord.InvocationStatus.SUCCESS, null
                    );
                    historyStore.add(record);
                })
                .doOnError(error -> {
                    ToolInvocationRecord record = buildRecord(
                            invocationId, serverId, ToolInvocationRecord.InvocationType.RESOURCE_READ,
                            resourceUri, null, null, startTime,
                            ToolInvocationRecord.InvocationStatus.FAILURE, error.getMessage()
                    );
                    historyStore.add(record);
                });
    }

    /**
     * 获取 Prompt
     */
    public Mono<McpSchema.GetPromptResult> getPrompt(String serverId, String promptName, Map<String, Object> arguments) {
        String invocationId = GeneratorUtil.generateInvocationId();
        Instant startTime = Instant.now();

        log.info("Inspector: 获取 Prompt - serverId={}, promptName={}", serverId, promptName);

        return mcpInspectorService.getPrompt(serverId, promptName, arguments)
                .doOnSuccess(result -> {
                    ToolInvocationRecord record = buildRecord(
                            invocationId, serverId, ToolInvocationRecord.InvocationType.PROMPT_GET,
                            promptName, Map.of("arguments", arguments), result, startTime,
                            ToolInvocationRecord.InvocationStatus.SUCCESS, null
                    );
                    historyStore.add(record);
                })
                .doOnError(error -> {
                    ToolInvocationRecord record = buildRecord(
                            invocationId, serverId, ToolInvocationRecord.InvocationType.PROMPT_GET,
                            promptName, Map.of("arguments", arguments), null, startTime,
                            ToolInvocationRecord.InvocationStatus.FAILURE, error.getMessage()
                    );
                    historyStore.add(record);
                });
    }

    /**
     * 获取调用历史（分页）
     */
    public List<ToolInvocationRecord> getHistory(int page, int size) {
        return historyStore.getPage(page, size);
    }

    /**
     * 清空调用历史
     */
    public void clearHistory() {
        historyStore.clear();
        log.info("Inspector: 调用历史已清空");
    }

    /**
     * 构建调用记录
     */
    private ToolInvocationRecord buildRecord(
            String invocationId,
            String serverId,
            ToolInvocationRecord.InvocationType type,
            String requestName,
            Map<String, ?> requestArgs,
            Object response,
            Instant startTime,
            ToolInvocationRecord.InvocationStatus status,
            String errorMessage
    ) {
        long durationMs = Instant.now().toEpochMilli() - startTime.toEpochMilli();

        String responseJson = null;
        if (response != null) {
            try {
                responseJson = objectMapper.writeValueAsString(response);
            } catch (JsonProcessingException e) {
                responseJson = response.toString();
            }
        }

        return ToolInvocationRecord.builder()
                .invocationId(invocationId)
                .serverId(serverId)
                .type(type)
                .requestName(requestName)
                .requestArgs(requestArgs != null ? Map.copyOf((Map<String, Object>) requestArgs) : null)
                .response(responseJson)
                .durationMs(durationMs)
                .status(status)
                .errorMessage(errorMessage)
                .timestamp(startTime)
                .build();
    }
}
