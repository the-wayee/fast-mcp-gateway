package org.cloudnook.mcp.domain.metrics.service;

import org.cloudnook.mcp.domain.log.model.McpInvokeContext;

/**
 * MCP Metrics 领域服务接口
 * 负责统一的监控流程编排：
 * 1. 记录调用历史
 * 2. 收集指标数据
 * 3. 更新健康状态
 * 4. 其他监控逻辑
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
public interface McpMetricsDomainService {

    /**
     * 记录成功的 MCP 调用
     *
     * @param context 监控上下文
     * @param result 调用结果
     */
    void recordSuccess(McpInvokeContext context, Object result);

    /**
     * 记录失败的 MCP 调用
     *
     * @param context 监控上下文
     * @param error 异常信息
     */
    void recordFailure(McpInvokeContext context, Throwable error);
}
