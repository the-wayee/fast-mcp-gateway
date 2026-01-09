package org.cloudnook.mcp.domain.service.server;


import io.modelcontextprotocol.client.McpAsyncClient;
import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.infrastruction.common.result.Result;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 17:17
 * @Description: Mcp 管理服务
 */
@Service
@RequiredArgsConstructor
public class McpManagerService {

    /**
     * 注册中心
     */
    private final McpRegister mcpRegister;

    /**
     * 连接中心
     */
    private final McpClientManager mcpClientManager;

    /**
     * 注册
     */
    public Mono<Result<McpServer>> register(McpServer mcpServer) {
        // 验证传输
        return mcpClientManager.connect(mcpServer)
                .flatMap(client -> {
                    // client 连接成功后注册
                    try {
                        mcpRegister.register(mcpServer);
                        return Mono.just(Result.<McpServer>success(mcpServer));
                    }catch (Exception e) {
                        return Mono.just(Result.<McpServer>error(mcpServer.getName() + " 注册失败"));
                    }
                })
                .onErrorResume(e -> {
                    // 连接失败
                    return Mono.just(Result.error(mcpServer.getName() + " 连接失败"));
                });
    }
}
