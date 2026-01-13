package org.cloudnook.mcp.application.service;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.log.model.McpInvokeLog;
import org.cloudnook.mcp.domain.routing.LoadBalanceStrategy;
import org.cloudnook.mcp.domain.server.model.McpServer;
import org.cloudnook.mcp.domain.server.service.McpRegister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * MCP Router 应用服务
 * 路由服务，负责将请求路由到具体的 MCP Server 实例
 *
 * 核心功能：
 * 1. 根据负载均衡策略选择 serverId
 * 2. 调用 McpInvokeAppService 执行具体操作
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpRouterAppService {

    private final McpInvokeAppService mcpInvokeAppService;
    private final McpRegister mcpRegister;
    private final LoadBalanceStrategy loadBalanceStrategy;

    // ==================== 列表查询（不经过路由，直接返回所有服务的信息）====================

    /**
     * 获取 Server 的 Tools 列表
     * 注意：Router模式下，list操作可能需要聚合多个服务的结果
     * 这里暂时简化为返回第一个可用服务的结果
     */
    public Mono<McpSchema.ListToolsResult> listTools(String serverName) {
        log.info("Router: 查询 Tools 列表 - serverName={}", serverName);

        // 获取服务的所有实例
        List<String> serverIds = mcpRegister.getServerIdsByName(serverName);
        if (serverIds == null || serverIds.isEmpty()) {
            log.error("服务不存在: {}", serverName);
            return Mono.error(new RuntimeException("服务不存在: " + serverName));
        }

        // 简化处理：使用第一个实例
        String serverId = serverIds.get(0);

        return mcpInvokeAppService.listTools(
                serverId,
                McpInvokeLog.InvokeSource.ROUTER
        );
    }

    /**
     * 获取 Server 的 Resources 列表
     */
    public Mono<McpSchema.ListResourcesResult> listResources(String serverName) {
        log.info("Router: 查询 Resources 列表 - serverName={}", serverName);

        List<String> serverIds = mcpRegister.getServerIdsByName(serverName);
        if (serverIds == null || serverIds.isEmpty()) {
            log.error("服务不存在: {}", serverName);
            return Mono.error(new RuntimeException("服务不存在: " + serverName));
        }

        String serverId = serverIds.get(0);

        return mcpInvokeAppService.listResources(
                serverId,
                McpInvokeLog.InvokeSource.ROUTER
        );
    }

    /**
     * 获取 Server 的 Prompts 列表
     */
    public Mono<McpSchema.ListPromptsResult> listPrompts(String serverName) {
        log.info("Router: 查询 Prompts 列表 - serverName={}", serverName);

        List<String> serverIds = mcpRegister.getServerIdsByName(serverName);
        if (serverIds == null || serverIds.isEmpty()) {
            log.error("服务不存在: {}", serverName);
            return Mono.error(new RuntimeException("服务不存在: " + serverName));
        }

        String serverId = serverIds.get(0);

        return mcpInvokeAppService.listPrompts(
                serverId,
                McpInvokeLog.InvokeSource.ROUTER
        );
    }

    // ==================== 资源调用（经过负载均衡）====================

    /**
     * 通过路由调用 Tool
     *
     * @param serverName 服务名称（可能对应多个实例）
     * @param toolName Tool名称
     * @param arguments 调用参数
     * @param clientId 客户端ID
     * @return 调用结果
     */
    public Mono<McpSchema.CallToolResult> callTool(
            String serverName,
            String toolName,
            Map<String, Object> arguments,
            String clientId
    ) {
        log.info("Router: 调用 Tool - serverName={}, toolName={}, clientId={}",
                serverName, toolName, clientId);

        // 1. 获取服务的所有实例
        List<McpServer> servers = mcpRegister.getServersByName(serverName);
        if (servers == null || servers.isEmpty()) {
            log.error("服务不存在: {}", serverName);
            return Mono.error(new RuntimeException("服务不存在: " + serverName));
        }

        // 2. 根据负载均衡策略选择服务实例
        McpServer selectedServer = loadBalanceStrategy.select(servers);
        String serverId = selectedServer.getId();

        log.info("Router: 选择服务实例 - serverName={}, serverId={}", serverName, serverId);

        // 3. 调用统一的能力服务
        return mcpInvokeAppService.callTool(
                serverId,
                toolName,
                arguments,
                McpInvokeLog.InvokeSource.ROUTER,  // 标记为路由来源
                clientId
        );
    }

    /**
     * 通过路由读取 Resource
     *
     * @param serverName    服务名称
     * @param resourceUri Resource URI
     * @return Resource 内容
     */
    public Mono<McpSchema.ReadResourceResult> readResource(
            String serverName,
            String resourceUri
    ) {
        log.info("Router: 读取 Resource - serverName={}, uri={}", serverName, resourceUri);

        List<McpServer> servers = mcpRegister.getServersByName(serverName);
        if (servers == null || servers.isEmpty()) {
            log.error("服务不存在: {}", serverName);
            return Mono.error(new RuntimeException("服务不存在: " + serverName));
        }

        McpServer selectedServer = loadBalanceStrategy.select(servers);
        String serverId = selectedServer.getId();

        return mcpInvokeAppService.readResource(
                serverId,
                resourceUri,
                McpInvokeLog.InvokeSource.ROUTER
        );
    }

    /**
     * 通过路由获取 Prompt
     *
     * @param serverName  服务名称
     * @param promptName Prompt名称
     * @param arguments  Prompt参数
     * @return Prompt 内容
     */
    public Mono<McpSchema.GetPromptResult> getPrompt(
            String serverName,
            String promptName,
            Map<String, Object> arguments
    ) {
        log.info("Router: 获取 Prompt - serverName={}, promptName={}", serverName, promptName);

        List<McpServer> servers = mcpRegister.getServersByName(serverName);
        if (servers == null || servers.isEmpty()) {
            log.error("服务不存在: {}", serverName);
            return Mono.error(new RuntimeException("服务不存在: " + serverName));
        }

        McpServer selectedServer = loadBalanceStrategy.select(servers);
        String serverId = selectedServer.getId();

        return mcpInvokeAppService.getPrompt(
                serverId,
                promptName,
                arguments,
                McpInvokeLog.InvokeSource.ROUTER
        );
    }
}
