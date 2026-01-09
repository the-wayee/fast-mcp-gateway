package org.cloudnook.mcp.infrastruction.connect;


import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.domain.model.shared.McpTransportType;
import org.cloudnook.mcp.domain.service.server.McpClientManager;
import org.cloudnook.mcp.infrastruction.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 19:29
 * @Description: MCP client 缓存管理
 */
@Service
public class McpClientCache implements McpClientManager {

    /**
     * 客户端缓存
     */
    private static final ConcurrentHashMap<String, Mono<McpAsyncClient>> CLIENTS_HOLDER = new ConcurrentHashMap<>();


    @Override
    public Mono<McpAsyncClient> connect(McpServer mcpServer) {
        return Mono.<McpAsyncClient>defer(() ->
            CLIENTS_HOLDER.computeIfAbsent(mcpServer.getId(), id -> createMcpClient(mcpServer).cache())
        );
    }

    @Override
    public void disconnect(String serverId) {
        Mono<McpAsyncClient> mono = CLIENTS_HOLDER.remove(serverId);
        if (mono != null) {
            mono.subscribe(McpAsyncClient::close,
                    e ->{} // 忽略关闭异常
            );
        }
    }

    @Override
    public void shutdown() {
        CLIENTS_HOLDER.values().forEach(mono -> {
            mono.subscribe(McpAsyncClient::close,
                    e -> {});
        });
        CLIENTS_HOLDER.clear();
    }

    private Mono<McpAsyncClient> createMcpClient(McpServer mcpServer) {
        return Mono.<McpAsyncClient>defer(() -> {
            final McpAsyncClient client;
            if (McpTransportType.STREAMABLE_HTTP == mcpServer.getTransportType()) {
                McpClientTransport transport = HttpClientStreamableHttpTransport
                        .builder(mcpServer.getEndpoint())
                        .build();
                client = McpClient.async(transport)
                        .build();
            } else if (McpTransportType.STDIO == mcpServer.getTransportType()) {
                // todo 未来支持
                return Mono.error(new BusinessException("stdio 协议暂未支持"));
            } else {
                return Mono.error(new BusinessException("未支持的协议"));
            }

            return client.initialize()
                    .thenReturn(client)
                    .doOnError(t -> {
                        try {
                            client.closeGracefully().subscribe();
                        } catch (Exception e) {

                        }
                    });
        });
    }
}
