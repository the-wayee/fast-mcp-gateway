package org.cloudnook.mcp.interfaces.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cloudnook.mcp.domain.model.metrics.McpServerMetrics;

/**
 * 服务监控数据详情版 VO
 * 用于详情页展示，包含完整的监控指标
 * 注意：不包含 McpServer，只通过 serverId 关联
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerMonitorDetailVO {

    /**
     * 服务ID（用于关联）
     */
    private String serverId;

    /**
     * 服务名称
     */
    private String serverName;

    // ========== 完整监控指标 ==========

    /**
     * 总请求数
     */
    private Long totalRequests;

    /**
     * 成功请求数
     */
    private Long successRequests;

    /**
     * 失败请求数
     */
    private Long failedRequests;

    /**
     * 平均延迟（毫秒）
     */
    private Double avgLatency;

    /**
     * 最小延迟（毫秒）
     */
    private Long minLatency;

    /**
     * 最大延迟（毫秒）
     */
    private Long maxLatency;

    /**
     * 运行时长（秒）
     */
    private Long uptime;

    /**
     * 注册时间
     */
    private String registerTime;

    /**
     * 最后心跳时间
     */
    private String lastHeartbeat;

    /**
     * 成功率（百分比）
     */
    private Double successRate;

    /**
     * 失败率（百分比）
     */
    private Double failureRate;

    /**
     * 从 McpServerMetrics 转换
     */
    public static ServerMonitorDetailVO fromMetrics(McpServerMetrics metrics) {
        return ServerMonitorDetailVO.builder()
                .serverId(metrics.getServerId())
                .serverName(metrics.getServerName())
                .totalRequests(metrics.getTotalRequests())
                .successRequests(metrics.getSuccessRequests())
                .failedRequests(metrics.getFailedRequests())
                .avgLatency(metrics.getAvgLatency())
                .minLatency(metrics.getMinLatency())
                .maxLatency(metrics.getMaxLatency())
                .uptime(metrics.getUptime())
                .registerTime(metrics.getRegisterTime() != null ? metrics.getRegisterTime().toString() : null)
                .lastHeartbeat(metrics.getLastHeartbeat() != null ? metrics.getLastHeartbeat().toString() : null)
                .successRate(metrics.getSuccessRate())
                .failureRate(metrics.getFailureRate())
                .build();
    }
}
