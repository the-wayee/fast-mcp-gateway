package org.cloudnook.mcp.infrastructure.core.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.server.model.McpServer;
import org.cloudnook.mcp.domain.server.service.McpClientManager;
import org.cloudnook.mcp.domain.metrics.repository.McpMetricsRepository;
import org.cloudnook.mcp.domain.server.service.McpRegister;
import org.cloudnook.mcp.domain.server.service.McpServerDomainService;
import org.cloudnook.mcp.infrastructure.common.result.Result;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 17:17
 * @Description: MCP Server 领域服务实现
 * 负责服务注册、注销、查询等核心领域逻辑的具体实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpServerDomainServiceImpl implements McpServerDomainService {

    /**
     * 注册中心
     */
    private final McpRegister mcpRegister;

    /**
     * 连接中心
     */
    private final McpClientManager mcpClientManager;

    /**
     * 监控指标仓储
     */
    private final McpMetricsRepository metricsRepository;

    @Override
    public Mono<Result<McpServer>> register(McpServer mcpServer) {
        // 验证传输
        return mcpClientManager.connect(mcpServer)
                .flatMap(client -> {
                    // client 连接成功后注册
                    try {
                        mcpRegister.register(mcpServer);

                        // 初始化监控指标
                        metricsRepository.initMetrics(mcpServer.getName(), mcpServer.getId());

                        return Mono.just(Result.<McpServer>success(mcpServer));
                    } catch (Exception e) {
                        return Mono.just(Result.<McpServer>error(e.getMessage()));
                    }
                })
                .onErrorResume(e -> {
                    // 连接失败
                    return Mono.just(Result.error(mcpServer.getName() + " 连接失败"));
                });
    }

    @Override
    public McpServer unregister(String serverName, String serverId) {
        // 断开连接
        mcpClientManager.disconnect(serverId);

        // 注销服务
        McpServer unregistered = mcpRegister.unregister(serverName, serverId);

        // 清除监控指标
        if (unregistered != null) {
            metricsRepository.removeMetrics(serverId);
        }

        return unregistered;
    }

    @Override
    public McpServer getServer(String serverName, String serverId) {
        return mcpRegister.getServer(serverName, serverId);
    }

    @Override
    public List<McpServer> getAllServers() {
        return mcpRegister.getAllServers();
    }

    @Override
    public McpServer getServerById(String serverId) {
        return mcpRegister.getServerById(serverId);
    }
}
