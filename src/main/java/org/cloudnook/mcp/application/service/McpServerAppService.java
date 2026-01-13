package org.cloudnook.mcp.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.metrics.model.McpServerMetrics;
import org.cloudnook.mcp.domain.server.model.McpServer;
import org.cloudnook.mcp.domain.server.model.McpServerStatus;
import org.cloudnook.mcp.domain.metrics.repository.McpMetricsRepository;
import org.cloudnook.mcp.domain.server.service.McpRegister;
import org.cloudnook.mcp.domain.server.service.McpServerDomainService;
import org.cloudnook.mcp.infrastructure.common.result.Result;
import org.cloudnook.mcp.infrastructure.utils.GeneratorUtil;
import org.cloudnook.mcp.interfaces.dto.monitor.ServerDetailVO;
import org.cloudnook.mcp.interfaces.dto.monitor.ServerMonitorSummaryVO;
import org.cloudnook.mcp.interfaces.dto.server.McpServerRegisterReq;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * MCP Server 应用服务
 * 位于应用层，负责服务管理功能
 * 依赖 domain/server + domain/metrics
 */
@Slf4j
@Service
@AllArgsConstructor
public class McpServerAppService {

    /**
     * MCP Server 领域服务
     */
    private McpServerDomainService mcpServerDomainService;

    /**
     * MCP 监控指标仓储
     */
    private McpMetricsRepository metricsRepository;

    /**
     * MCP 注册中心
     */
    private McpRegister mcpRegister;

    /**
     * 注册服务
     */
    public Mono<Result<McpServer>> registerServer(McpServerRegisterReq request) {
        // 1. 根据 name 和 endpoint 计算 serverId
        String serverId = GeneratorUtil.generateServerId(request.getName(), request.getEndpoint());

        log.info("生成服务ID: name={}, endpoint={}, serverId={}",
                request.getName(), request.getEndpoint(), serverId);

        // 2. 构建 McpServer 对象
        McpServer mcpServer = McpServer.builder()
                .id(serverId)
                .name(request.getName())
                .description(request.getDescription())
                .transportType(request.getTransportType())
                .endpoint(request.getEndpoint())
                .version(request.getVersion()) // 添加版本号
                .status(McpServerStatus.ACTIVE).build();

        // 3. 注册到注册中心
        return mcpServerDomainService.register(mcpServer);
    }

    /**
     * 注销服务
     */
    public McpServer unregisterServer(String serverName, String serverId) {
        return mcpServerDomainService.unregister(serverName, serverId);
    }

    /**
     * 获取服务详情
     */
    public McpServer getServer(String serverName, String serverId) {
        return mcpServerDomainService.getServer(serverName, serverId);
    }

    /**
     * 获取所有服务
     */
    public List<McpServer> getAllServers() {
        return mcpServerDomainService.getAllServers();
    }

    // ==================== 监控数据查询方法 ====================

    /**
     * 获取所有服务监控概览（列表展示）
     *
     * @return 服务监控概览列表
     */
    public List<ServerMonitorSummaryVO> getAllServerSummaries() {
        // 1. 从注册中心获取所有服务
        List<McpServer> allServers = mcpServerDomainService.getAllServers();

        // 2. 遍历每个服务，查询对应的监控指标并组装
        return allServers.stream()
                .map(server -> {
                    McpServerMetrics metrics = metricsRepository.getServerMetrics(server.getId());
                    return ServerMonitorSummaryVO.from(server, metrics);
                })
                .toList();
    }

    /**
     * 获取单个服务完整监控数据（详情页展示）
     *
     * @param serverId 服务ID
     * @return 完整监控指标
     */
    public McpServerMetrics getServerDetail(String serverId) {
        return metricsRepository.getServerMetrics(serverId);
    }

    /**
     * 获取单个服务详情VO（合并服务信息和监控指标）
     *
     * @param serverName 服务名称
     * @param serverId   服务ID
     * @return 服务详情VO
     */
    public ServerDetailVO getServerDetailVO(String serverName, String serverId) {
        // 1. 从注册中心获取服务信息
        McpServer server = mcpServerDomainService.getServer(serverName, serverId);

        if (server == null) {
            return null;
        }

        // 2. 获取监控指标
        McpServerMetrics metrics = metricsRepository.getServerMetrics(serverId);

        // 3. 组装详情VO
        return ServerDetailVO.from(server, metrics);
    }
}

