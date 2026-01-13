package org.cloudnook.mcp.infrastructure.core.register;

import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.infrastructure.common.exception.BusinessException;
import org.cloudnook.mcp.domain.server.model.McpServer;
import org.cloudnook.mcp.domain.server.service.McpRegister;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * 内存级别的 MCP 服务注册中心
 * 支持同一服务名称下多个服务实例的注册与管理
 */
@Slf4j
@Service
public class McpMemoryRegister implements McpRegister {

    /**
     * 存储 服务名称 -> 服务实例列表 的映射
     * Key: 服务名称
     * Value: 该服务名称下的所有服务实例列表
     */
    private static final ConcurrentHashMap<String, List<McpServer>> MCP_SERVER_HOLDER = new ConcurrentHashMap<>();

    @Override
    public boolean register(McpServer mcpServer) {
        if (mcpServer == null || mcpServer.getName() == null || mcpServer.getId() == null) {
            throw new BusinessException("服务信息无效");
        }

        String serverName = mcpServer.getName();
        String serverId = mcpServer.getId();

        // 使用 compute 保证原子性操作
        MCP_SERVER_HOLDER.compute(serverName, (key, servers) -> {
            if (servers == null) {
                servers = new ArrayList<>();
            }

            // 检查是否已存在相同 serverId 的实例
            boolean exists = servers.stream()
                    .anyMatch(server -> serverId.equals(server.getId()));

            if (exists) {
                throw new BusinessException("服务已存在: " + serverId);
            }

            // 添加新实例
            servers.add(mcpServer);
            log.info("成功注册服务: 服务名称={}, 服务ID={}", serverName, serverId);
            return servers;
        });

        return true;
    }

    @Override
    public McpServer unregister(String serverName, String serverId) {
        if (serverName == null || serverId == null) {
            throw new BusinessException("服务名称或服务ID不能为空");
        }

        // 使用 AtomicReference 保存被移除的服务实例
        final McpServer[] removedServer = {null};

        MCP_SERVER_HOLDER.computeIfPresent(serverName, (key, servers) -> {
            // 先查找要移除的服务
            Optional<McpServer> serverToRemove = servers.stream()
                    .filter(server -> serverId.equals(server.getId()))
                    .findFirst();

            if (serverToRemove.isPresent()) {
                removedServer[0] = serverToRemove.get();
                servers.remove(removedServer[0]);
                log.info("成功注销服务: 服务名称={}, 服务ID={}", serverName, serverId);
                // 如果列表为空，返回 null 以移除该 key
                return servers.isEmpty() ? null : servers;
            }

            throw new BusinessException("服务未找到: " + serverId);
        });

        // 如果服务名称不存在或服务未找到
        if (removedServer[0] == null) {
            throw new BusinessException("服务未找到: " + serverName);
        }

        return removedServer[0];
    }

    @Override
    public McpServer getServer(String serverName, String serverId) {
        if (serverName == null || serverId == null) {
            throw new BusinessException("服务名称或服务ID不能为空");
        }

        List<McpServer> servers = MCP_SERVER_HOLDER.get(serverName);
        if (servers == null || servers.isEmpty()) {
            throw new BusinessException("服务未找到: " + serverName);
        }

        return servers.stream()
                .filter(server -> serverId.equals(server.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("服务未找到: " + serverId));
    }

    @Override
    public List<McpServer> getAllServers() {
        return MCP_SERVER_HOLDER.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public McpServer getServerById(String serverId) {
        if (serverId == null) {
            throw new BusinessException("服务ID不能为空");
        }

        return MCP_SERVER_HOLDER.values().stream()
                .flatMap(List::stream)
                .filter(server -> serverId.equals(server.getId()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("服务未找到: " + serverId));
    }

    /**
     * 清空所有注册信息（主要用于测试）
     */
    public void clear() {
        MCP_SERVER_HOLDER.clear();
        log.info("已清空所有服务注册信息");
    }

    /**
     * 获取当前注册统计信息
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalServerNames", MCP_SERVER_HOLDER.size());

        int totalServers = MCP_SERVER_HOLDER.values().stream()
                .mapToInt(List::size)
                .sum();
        stats.put("totalServers", totalServers);

        Map<String, Integer> serverCounts = new HashMap<>();
        MCP_SERVER_HOLDER.forEach((name, servers) -> serverCounts.put(name, servers.size()));
        stats.put("serverCounts", serverCounts);

        return stats;
    }

    /**
     * 根据服务名称获取所有服务实例
     * 提供给负载均衡模块使用
     */
    @Override
    public List<McpServer> getServersByName(String serverName) {
        if (serverName == null) {
            return Collections.emptyList();
        }

        List<McpServer> servers = MCP_SERVER_HOLDER.get(serverName);
        return servers == null ? Collections.emptyList() : new ArrayList<>(servers);
    }

    @Override
    public List<String> getServerIdsByName(String serverName) {
        List<McpServer> servers = getServersByName(serverName);
        return servers.stream()
                .map(McpServer::getId)
                .collect(Collectors.toList());
    }

}
