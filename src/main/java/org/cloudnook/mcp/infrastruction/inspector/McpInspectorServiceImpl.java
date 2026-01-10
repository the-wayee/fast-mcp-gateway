package org.cloudnook.mcp.infrastruction.inspector;


import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.domain.service.inspector.McpInspectorService;
import org.cloudnook.mcp.domain.service.server.McpClientManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: MCP Inspector 服务实现
 */
@Service
@RequiredArgsConstructor
public class McpInspectorServiceImpl implements McpInspectorService {

    private final McpClientManager mcpClientManager;

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
