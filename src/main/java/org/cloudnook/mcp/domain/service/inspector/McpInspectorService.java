package org.cloudnook.mcp.domain.service.inspector;


import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP Inspector 服务接口
 * 负责 MCP 协议的远程调用：列表查询、调用 Tool、读取 Resource、获取 Prompt
 */
public interface McpInspectorService {

    // ==================== 列表查询 ====================

    /**
     * 获取 Server 的 Tools 列表
     *
     * @param serverId 服务ID
     * @return Tools 列表
     */
    Mono<McpSchema.ListToolsResult> listTools(String serverId);

    /**
     * 获取 Server 的 Resources 列表
     *
     * @param serverId 服务ID
     * @return Resources 列表
     */
    Mono<McpSchema.ListResourcesResult> listResources(String serverId);

    /**
     * 获取 Server 的 Prompts 列表
     *
     * @param serverId 服务ID
     * @return Prompts 列表
     */
    Mono<McpSchema.ListPromptsResult> listPrompts(String serverId);

    // ==================== 资源调用 ====================

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
