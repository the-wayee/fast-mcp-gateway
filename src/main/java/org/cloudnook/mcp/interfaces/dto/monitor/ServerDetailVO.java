package org.cloudnook.mcp.interfaces.dto.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cloudnook.mcp.domain.model.metrics.McpServerMetrics;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.cloudnook.mcp.domain.model.shared.HealthStatus;
import org.cloudnook.mcp.domain.model.shared.McpServerStatus;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * 服务详情完整版 VO
 * 合并服务基本信息和监控指标，用于详情页展示
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerDetailVO {

    // ==================== 服务基本信息 ====================

    /**
     * 服务ID
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

    /**
     * 版本号（可选）
     */
    private String version;

    /**
     * 注册时间（格式化）
     */
    private String registerTime;

    /**
     * 运行时长（格式化，如 "15d 7h 23m"）
     */
    private String uptime;

    // ==================== 请求指标 ====================

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
     * 成功率（百分比）
     */
    private Double successRate;

    /**
     * 失败率（百分比）
     */
    private Double failureRate;

    // ==================== 延迟指标 ====================

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

    // ==================== 时间指标 ====================

    /**
     * 最后心跳时间（格式化）
     */
    private String lastHeartbeat;

    /**
     * 活跃连接数（可选，暂未实现）
     */
    private Integer activeConnections;

    /**
     * 从 McpServer 和 McpServerMetrics 创建详情 VO
     *
     * @param server  服务信息
     * @param metrics 监控指标（可以为 null）
     * @return ServerDetailVO
     */
    public static ServerDetailVO from(McpServer server, McpServerMetrics metrics) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ServerDetailVO.builder()
                // 服务基本信息
                .serverId(server.getId())
                .serverName(server.getName())
                .description(server.getDescription())
                .status(server.getStatus())
                .healthStatus(metrics != null ? metrics.getHealthStatus() : HealthStatus.UNKNOWN)
                .transportType(server.getTransportType().name())
                .endpoint(server.getEndpoint())
                .version(server.getVersion()) // 从服务信息获取版本号
                .registerTime(metrics != null && metrics.getRegisterTime() != null
                        ? metrics.getRegisterTime().atZone(java.time.ZoneId.systemDefault()).format(formatter)
                        : "N/A")
                .uptime(metrics != null && metrics.getUptime() != null
                        ? formatUptime(metrics.getUptime())
                        : "N/A")

                // 请求指标
                .totalRequests(metrics != null ? metrics.getTotalRequests() : 0L)
                .successRequests(metrics != null ? metrics.getSuccessRequests() : 0L)
                .failedRequests(metrics != null ? metrics.getFailedRequests() : 0L)
                .successRate(metrics != null ? metrics.getSuccessRate() : 0.0)
                .failureRate(metrics != null ? metrics.getFailureRate() : 0.0)

                // 延迟指标
                .avgLatency(metrics != null ? metrics.getAvgLatency() : 0.0)
                .minLatency(metrics != null && metrics.getMinLatency() != null ? metrics.getMinLatency() : 0L)
                .maxLatency(metrics != null && metrics.getMaxLatency() != null ? metrics.getMaxLatency() : 0L)

                // 时间指标
                .lastHeartbeat(metrics != null && metrics.getLastHeartbeat() != null
                        ? metrics.getLastHeartbeat().atZone(java.time.ZoneId.systemDefault()).format(formatter)
                        : "N/A")

                // 连接数（暂未实现）
                .activeConnections(null)

                .build();
    }

    /**
     * 格式化运行时长
     *
     * @param seconds 运行秒数
     * @return 格式化字符串，如 "15d 7h 23m"
     */
    private static String formatUptime(Long seconds) {
        if (seconds == null || seconds == 0) {
            return "0s";
        }

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("h ");
        }
        sb.append(minutes).append("m");

        return sb.toString();
    }
}
