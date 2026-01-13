package org.cloudnook.mcp.infrastruction.metrics;

import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.model.metrics.McpServerMetrics;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.domain.service.server.McpRegister;
import org.cloudnook.mcp.domain.service.server.McpMetricsRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP 监控指标仓储 - 内存实现
 * 使用 ConcurrentHashMap 存储服务监控数据
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Slf4j
@Repository
public class McpMemoryMetrics implements McpMetricsRepository {

    /**
     * Key: serverId, Value: 服务监控指标
     */
    private static final ConcurrentHashMap<String, McpServerMetrics> METRICS_HOLDER = new ConcurrentHashMap<>();

    private final McpRegister mcpRegister;

    public McpMemoryMetrics(McpRegister mcpRegister) {
        this.mcpRegister = mcpRegister;
    }

    @Override
    public void initMetrics(String serverName, String serverId) {
        if (METRICS_HOLDER.containsKey(serverId)) {
            log.warn("监控指标已存在，跳过初始化: serverId={}", serverId);
            return;
        }

        // 从注册中心获取基础信息
        McpServer baseServer = mcpRegister.getServer(serverName, serverId);
        if (baseServer == null) {
            log.error("服务不存在，无法初始化监控指标: serverId={}", serverId);
            return;
        }

        // 构建监控指标对象
        McpServerMetrics metrics = McpServerMetrics.builder()
                .serverId(baseServer.getId())
                .serverName(baseServer.getName())
                // 初始化监控指标
                .totalRequests(0L)
                .successRequests(0L)
                .failedRequests(0L)
                .avgLatency(0.0)
                .minLatency(null)
                .maxLatency(null)
                .registerTime(Instant.now())
                .lastHeartbeat(Instant.now())
                .uptime(0L)
                .build();

        METRICS_HOLDER.put(serverId, metrics);
        log.info("监控指标初始化成功: serverId={}, name={}", serverId, baseServer.getName());
    }

    @Override
    public void recordRequest(String serverId, long latency, boolean success) {
        METRICS_HOLDER.computeIfPresent(serverId, (id, metrics) -> {
            // 更新请求数
            metrics.setTotalRequests(metrics.getTotalRequests() + 1);

            if (success) {
                metrics.setSuccessRequests(metrics.getSuccessRequests() + 1);
            } else {
                metrics.setFailedRequests(metrics.getFailedRequests() + 1);
            }

            // 更新延迟指标
            updateLatencyMetrics(metrics, latency);

            // 更新心跳
            metrics.setLastHeartbeat(Instant.now());

            return metrics;
        });

        if (log.isDebugEnabled()) {
            log.debug("记录请求指标: serverId={}, latency={}ms, success={}", serverId, latency, success);
        }
    }

    @Override
    public void updateHeartbeat(String serverId) {
        METRICS_HOLDER.computeIfPresent(serverId, (id, metrics) -> {
            metrics.setLastHeartbeat(Instant.now());
            return metrics;
        });
    }

    @Override
    public McpServerMetrics getServerMetrics(String serverId) {
        return METRICS_HOLDER.get(serverId);
    }

    @Override
    public List<McpServerMetrics> getAllServerMetrics() {
        return new ArrayList<>(METRICS_HOLDER.values());
    }

    @Override
    public void removeMetrics(String serverId) {
        McpServerMetrics removed = METRICS_HOLDER.remove(serverId);
        if (removed != null) {
            log.info("监控指标已移除: serverId={}, name={}", serverId, removed.getServerName());
        }
    }

    /**
     * 更新延迟指标
     * 使用 EWMA (Exponentially Weighted Moving Average) 算法计算平均延迟
     *
     * @param metrics 服务监控指标
     * @param latency 当前延迟
     */
    private void updateLatencyMetrics(McpServerMetrics metrics, long latency) {
        // 更新最大最小值
        if (metrics.getMinLatency() == null || latency < metrics.getMinLatency()) {
            metrics.setMinLatency(latency);
        }
        if (metrics.getMaxLatency() == null || latency > metrics.getMaxLatency()) {
            metrics.setMaxLatency(latency);
        }

        // 使用 EWMA 更新平均延迟
        // alpha 是平滑因子，0.2 表示给当前值 20% 权重，历史值 80% 权重
        double alpha = 0.2;
        double currentAvg = metrics.getAvgLatency() != null ? metrics.getAvgLatency() : latency;
        double newAvg = alpha * latency + (1 - alpha) * currentAvg;
        metrics.setAvgLatency(newAvg);
    }
}
