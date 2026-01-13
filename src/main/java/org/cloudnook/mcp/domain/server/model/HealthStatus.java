package org.cloudnook.mcp.domain.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MCP 服务健康状态
 * 基于监控指标判断的服务健康状态
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Getter
@AllArgsConstructor
public enum HealthStatus {

    /**
     * 健康
     * 条件：成功率高（≥95%）、延迟正常（<200ms）、无大量错误
     */
    HEALTHY,

    /**
     * 降级
     * 条件：成功率偏低（80%-95%）或延迟偏高（200-500ms）或少量错误
     */
    DEGRADED,

    /**
     * 不健康
     * 条件：成功率低（<80%）或延迟过高（>500ms）或大量错误
     */
    UNHEALTHY,

    /**
     * 未知
     * 新注册或长时间无数据
     */
    UNKNOWN
}
