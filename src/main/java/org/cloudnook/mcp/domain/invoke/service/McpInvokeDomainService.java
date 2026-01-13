package org.cloudnook.mcp.domain.invoke.service;

import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * MCP Invoke 领域服务接口
 * 封装 MCP 协议的调用能力，是最后一步发出请求的领域
 *
 * 职责：
 * - 列表查询：Tools、Resources、Prompts
 * - 资源调用：Tool调用、Resource读取、Prompt获取
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
public interface McpInvokeDomainService {

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
