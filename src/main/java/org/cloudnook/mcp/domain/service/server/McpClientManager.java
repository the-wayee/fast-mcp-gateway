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
     * 根据 serverId 获取已连接的 MCP 客户端
     *
     * @param serverId 服务ID
     * @return 已连接的 MCP 客户端
     */
    Mono<McpAsyncClient> getClient(String serverId);

    /**
     * 断开连接
     */
    void disconnect(String serverId);

    /**
     * 关闭连接池
     */
    void shutdown();
}
