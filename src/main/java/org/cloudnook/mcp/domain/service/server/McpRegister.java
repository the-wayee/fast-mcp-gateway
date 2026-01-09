package org.cloudnook.mcp.domain.service.server;

import org.cloudnook.mcp.domain.model.server.McpServer;

import java.util.List;

/**
 * MCP 服务注册接口
 * 负责服务的注册、注销、查询等管理功能
 */
public interface McpRegister {

    /**
     * 注册服务
     *
     * @param mcpServer 服务实例
     * @return 注册是否成功
     */
    boolean register(McpServer mcpServer);

    /**
     * 注销服务
     *
     * @param serverName 服务名称
     * @param serverId   服务ID
     * @return 注销是否成功
     */
    boolean unregister(String serverName, String serverId);

    /**
     * 根据服务名称和服务ID查询
     *
     * @param serverName 服务名称
     * @param serverId   服务ID
     */
    McpServer getServer(String serverName, String serverId);

    /**
     * 获取所有服务信息
     *
     * @return 所有服务实例列表
     */
    List<McpServer> getAllServers();

}
