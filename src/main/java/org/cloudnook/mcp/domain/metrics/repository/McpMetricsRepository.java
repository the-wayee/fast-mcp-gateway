package org.cloudnook.mcp.domain.metrics.repository;

import org.cloudnook.mcp.domain.metrics.model.McpServerMetrics;

import java.util.List;

/**
 * MCP 监控指标仓储接口
 * 负责服务监控数据的存储和查询
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
public interface McpMetricsRepository {

    /**
     * 初始化服务监控指标
     * 当服务注册时调用
     *
     * @param serverName 服务名称
     * @param serverId  服务ID
     */
    void initMetrics(String serverName, String serverId);

    /**
     * 记录请求指标
     * 每次 MCP 调用时调用
     *
     * @param serverId 服务ID
     * @param latency  延迟（毫秒）
     * @param success  是否成功
     */
    void recordRequest(String serverId, long latency, boolean success);

    /**
     * 更新心跳时间
     * 健康检查或调用成功时调用
     *
     * @param serverId 服务ID
     */
    void updateHeartbeat(String serverId);

    /**
     * 获取服务监控数据
     *
     * @param serverId 服务ID
     * @return 服务监控指标
     */
    McpServerMetrics getServerMetrics(String serverId);

    /**
     * 获取所有服务监控数据
     *
     * @return 所有服务监控指标列表
     */
    List<McpServerMetrics> getAllServerMetrics();

    /**
     * 移除服务监控数据
     * 服务注销时调用
     *
     * @param serverId 服务ID
     */
    void removeMetrics(String serverId);
}
