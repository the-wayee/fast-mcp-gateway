/**
 * 服务状态（服务生命周期状态）
 */
export type McpServerStatus = "ACTIVE" | "INACTIVE" | "CONNECTING" | "DISCONNECTED"

/**
 * 健康状态（基于监控指标计算）
 */
export type HealthStatus = "HEALTHY" | "DEGRADED" | "UNHEALTHY" | "UNKNOWN"

/**
 * 传输类型
 */
export type TransportType = "STDIO" | "SSE" | "STREAMABLE_HTTP"

/**
 * 服务监控摘要（列表展示）
 * 对应后端 ServerMonitorSummaryVO
 */
export interface ServerMonitorSummary {
    serverId: string
    serverName: string
    description: string
    status: McpServerStatus
    healthStatus: HealthStatus
    transportType: TransportType
    endpoint: string

    // 监控指标
    totalRequests: number
    avgLatency: number
    uptime: number
    successRate: number
}

/**
 * 服务完整监控数据（详情页展示）
 * 对应后端 McpServerMetrics
 */
export interface ServerMonitorDetail {
    serverId: string
    serverName: string

    // 请求指标
    totalRequests: number
    successRequests: number
    failedRequests: number

    // 延迟指标
    avgLatency: number
    minLatency: number
    maxLatency: number

    // 时间指标
    lastHeartbeat: string
    registerTime: string
    uptime: number
}
