package org.cloudnook.mcp.domain.server.service;


import org.cloudnook.mcp.domain.server.model.McpServer;
import org.cloudnook.mcp.infrastructure.common.result.Result;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 17:17
 * @Description: MCP Server 领域服务接口
 * 负责服务注册、注销、查询等核心领域逻辑
 */
public interface McpServerDomainService {

    /**
     * 注册服务
     *
     * @param mcpServer 服务信息
     * @return 注册结果
     */
    Mono<Result<McpServer>> register(McpServer mcpServer);

    /**
     * 注销服务
     *
     * @param serverName 服务名称
     * @param serverId   服务ID
     * @return 被注销的服务
     */
    McpServer unregister(String serverName, String serverId);

    /**
     * 获取服务详情
     *
     * @param serverName 服务名称
     * @param serverId   服务ID
     * @return 服务实例
     */
    McpServer getServer(String serverName, String serverId);

    /**
     * 获取所有服务
     *
     * @return 所有服务实例列表
     */
    List<McpServer> getAllServers();

    /**
     * 根据服务ID获取服务
     *
     * @param serverId 服务ID
     * @return 服务实例
     */
    McpServer getServerById(String serverId);
}

