package org.cloudnook.mcp.domain.service.inspector;


import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP Inspector 服务接口
 * 负责调试操作：调用 Tool、读取 Resource、获取 Prompt
 */
public interface McpInspectorService {

    /**
     * 调用 Tool
     *
     * @param serverId  服务ID
     * @param toolName  Tool 名称
     * @param arguments 调用参数
     * @return Tool 调用结果
     */
    Mono<McpSchema.CallToolResult> callTool(String serverId, String toolName, Map<String, Object> arguments);

    /**
     * 读取 Resource
     *
     * @param serverId    服务ID
     * @param resourceUri Resource URI
     * @return Resource 内容
     */
    Mono<McpSchema.ReadResourceResult> readResource(String serverId, String resourceUri);

    /**
     * 获取 Prompt
     *
     * @param serverId   服务ID
     * @param promptName Prompt 名称
     * @param arguments  Prompt 参数
     * @return Prompt 内容
     */
    Mono<McpSchema.GetPromptResult> getPrompt(String serverId, String promptName, Map<String, Object> arguments);
}
