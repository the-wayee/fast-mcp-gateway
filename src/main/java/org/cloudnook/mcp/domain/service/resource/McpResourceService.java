package org.cloudnook.mcp.domain.service.resource;


import io.modelcontextprotocol.spec.McpSchema;
import reactor.core.publisher.Mono;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP 资源查询服务接口
 * 负责查询 MCP Server 的 Tools、Resources、Prompts
 */
public interface McpResourceService {

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
}
