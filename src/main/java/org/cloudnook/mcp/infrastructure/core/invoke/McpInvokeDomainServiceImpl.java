package org.cloudnook.mcp.infrastructure.core.invoke;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.domain.invoke.service.McpInvokeDomainService;
import org.cloudnook.mcp.domain.server.service.McpClientManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * MCP Invoke 领域服务实现
 * 封装 MCP 协议的调用，是最后一步发出请求的实现
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Service
@RequiredArgsConstructor
public class McpInvokeDomainServiceImpl implements McpInvokeDomainService {

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

    @Override
    public Mono<McpSchema.CallToolResult> callTool(String serverId, String toolName, Map<String, Object> arguments) {
        return mcpClientManager.getClient(serverId)
                .flatMap(client -> client.callTool(new McpSchema.CallToolRequest(toolName, arguments)));
    }

    @Override
    public Mono<McpSchema.ReadResourceResult> readResource(String serverId, String resourceUri) {
        return mcpClientManager.getClient(serverId)
                .flatMap(client -> client.readResource(new McpSchema.ReadResourceRequest(resourceUri)));
    }

    @Override
    public Mono<McpSchema.GetPromptResult> getPrompt(String serverId, String promptName, Map<String, Object> arguments) {
        return mcpClientManager.getClient(serverId)
                .flatMap(client -> client.getPrompt(new McpSchema.GetPromptRequest(promptName, arguments)));
    }
}
