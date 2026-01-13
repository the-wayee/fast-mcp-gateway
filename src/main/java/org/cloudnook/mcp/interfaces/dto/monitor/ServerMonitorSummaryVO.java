package org.cloudnook.mcp.interfaces.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cloudnook.mcp.domain.model.metrics.McpServerMetrics;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.domain.model.shared.HealthStatus;
import org.cloudnook.mcp.domain.model.shared.McpServerStatus;

/**
 * 服务监控数据精简版 VO
 * 用于列表展示，只包含关键指标
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerMonitorSummaryVO {

    /**
     * 服务ID（用于关联详情）
     */
    private String serverId;

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 服务描述
     */
    private String description;

    /**
     * 服务状态
     */
    private McpServerStatus status;

    /**
     * 健康状态
     */
    private HealthStatus healthStatus;

    /**
     * 传输类型
     */
    private String transportType;

    /**
     * 连接端点
     */
    private String endpoint;

    // ========== 关键监控指标 ==========

    /**
     * 总请求数
     */
    private Long totalRequests;

    /**
     * 平均延迟（毫秒）
     */
    private Double avgLatency;

    /**
     * 运行时长（秒）
     */
    private Long uptime;

    /**
     * 成功率（百分比）
     */
    private Double successRate;

    /**
     * 从 McpServer 和 McpServerMetrics 创建 VO
     *
     * @param server  服务信息
     * @param metrics 监控指标（可以为 null）
     * @return ServerMonitorSummaryVO
     */
    public static ServerMonitorSummaryVO from(McpServer server, McpServerMetrics metrics) {
        return ServerMonitorSummaryVO.builder()
                .serverId(server.getId())
                .serverName(server.getName())
                .description(server.getDescription())
                .status(server.getStatus())
                .healthStatus(metrics != null ? metrics.getHealthStatus() : HealthStatus.UNKNOWN)
                .transportType(server.getTransportType().name())
                .endpoint(server.getEndpoint())
                // 监控指标（处理 null）
                .totalRequests(metrics != null ? metrics.getTotalRequests() : 0L)
                .avgLatency(metrics != null ? metrics.getAvgLatency() : 0.0)
                .uptime(metrics != null ? metrics.getUptime() : 0L)
                .successRate(metrics != null ? metrics.getSuccessRate() : 0.0)
                .build();
    }

}
