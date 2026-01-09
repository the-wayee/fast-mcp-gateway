package org.cloudnook.mcp.application.service;

import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.domain.model.shared.McpServerStatus;
import org.cloudnook.mcp.domain.service.server.McpManagerService;
import org.cloudnook.mcp.domain.service.server.McpRegister;
import org.cloudnook.mcp.infrastruction.common.result.Result;
import org.cloudnook.mcp.infrastruction.utils.GeneratorUtil;
import org.cloudnook.mcp.interfaces.dto.server.McpServerRegisterReq;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * MCP 服务管理应用服务
 * 位于应用层，负责编排领域服务
 */
@Slf4j
@Service
@AllArgsConstructor
public class McpServerAppService {

    /**
     * 注入注册中心接口（通过配置动态选择实现）
     */
    private McpRegister mcpRegister;

    private McpManagerService mcpManagerService;

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
                .transportType(request.getTransportType())
                .endpoint(request.getEndpoint())
                .status(McpServerStatus.ACTIVE).build();

        // 3. 注册到注册中心
        return mcpManagerService.register(mcpServer);
    }

    /**
     * 注销服务
     */
    public boolean unregisterServer(String serverName, String serverId) {
        return mcpRegister.unregister(serverName, serverId);
    }

    /**
     * 获取服务详情
     */
    public McpServer getServer(String serverName, String serverId) {
        return mcpRegister.getServer(serverName, serverId);
    }

    /**
     * 获取所有服务
     */
    public List<McpServer> getAllServers() {
        return mcpRegister.getAllServers();
    }


}
