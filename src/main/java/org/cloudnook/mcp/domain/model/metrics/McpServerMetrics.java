package org.cloudnook.mcp.domain.model.metrics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cloudnook.mcp.domain.model.shared.HealthStatus;

import java.time.Instant;

/**
 * 服务监控指标实体
 * 独立存储服务的监控数据，与服务信息解耦
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpServerMetrics {

    /**
     * 服务ID（关联到 McpServer）
     */
    private String serverId;

    /**
     * 服务名称（冗余字段，便于查询）
     */
    private String serverName;

    // ========== 请求指标 ==========

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

    // ========== 延迟指标 ==========

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

    // ========== 时间指标 ==========

    /**
     * 最后心跳时间
     */
    private Instant lastHeartbeat;

    /**
     * 注册时间
     */
    private Instant registerTime;

    /**
     * 运行时长（秒）
     */
    private Long uptime;

    /**
     * 计算成功率
     *
     * @return 成功率百分比（0-100）
     */
    public double getSuccessRate() {
        if (totalRequests == null || totalRequests == 0) {
            return 0.0;
        }
        return (successRequests * 100.0) / totalRequests;
    }

    /**
     * 计算失败率
     *
     * @return 失败率百分比（0-100）
     */
    public double getFailureRate() {
        if (totalRequests == null || totalRequests == 0) {
            return 0.0;
        }
        return (failedRequests * 100.0) / totalRequests;
    }

    /**
     * 计算健康状态
     * 基于成功率和平均延迟判断
     *
     * @return 健康状态
     */
    public HealthStatus getHealthStatus() {
        // 新注册或无数据
        if (totalRequests == null || totalRequests == 0) {
            return HealthStatus.UNKNOWN;
        }

        double successRate = getSuccessRate();
        double avgLatency = this.avgLatency != null ? this.avgLatency : 0.0;

        // 不健康：成功率 < 80% 或 延迟 > 500ms
        if (successRate < 80.0 || avgLatency > 500) {
            return HealthStatus.UNHEALTHY;
        }

        // 降级：成功率 < 95% 或 延迟 > 200ms
        if (successRate < 95.0 || avgLatency > 200) {
            return HealthStatus.DEGRADED;
        }

        // 健康
        return HealthStatus.HEALTHY;
    }
}
