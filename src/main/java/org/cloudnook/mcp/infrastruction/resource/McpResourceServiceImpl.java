package org.cloudnook.mcp.infrastruction.resource;


import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.domain.service.resource.McpResourceService;
import org.cloudnook.mcp.domain.service.server.McpClientManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP 资源查询服务实现
 * 负责查询 MCP Server 的 Tools、Resources、Prompts
 */
@Service
@RequiredArgsConstructor
public class McpResourceServiceImpl implements McpResourceService {

    private final McpClientManager mcpClientManager;

    @Override
    public Mono<McpSchema.ListToolsResult> listTools(String serverId) {
        return mcpClientManager.getClient(serverId)
                .flatMap(McpAsyncClient::listTools);
    }

    @Override
    public Mono<McpSchema.ListResourcesResult> listResources(String serverId) {
        return mcpClientManager.getClient(serverId)
                .flatMap(McpAsyncClient::listResources);
    }

    @Override
    public Mono<McpSchema.ListPromptsResult> listPrompts(String serverId) {
        return mcpClientManager.getClient(serverId)
                .flatMap(McpAsyncClient::listPrompts);
    }
}
