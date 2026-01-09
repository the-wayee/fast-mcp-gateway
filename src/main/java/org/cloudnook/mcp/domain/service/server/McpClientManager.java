package org.cloudnook.mcp.domain.service.server;


import io.modelcontextprotocol.client.McpAsyncClient;
import org.cloudnook.mcp.domain.model.server.McpServer;
import reactor.core.publisher.Mono;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 19:23
 * @Description: Mcp 客户端统一管理
 */
public interface McpClientManager {

    /**
     * 获取 MCP 客户端连接
     */
    Mono<McpAsyncClient> connect(McpServer mcpServer);

    /**
     * 断开连接
     */
    void disconnect(String serverId);

    /**
     * 关闭连接池
     */
    void shutdown();
}
