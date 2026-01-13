package org.cloudnook.mcp.infrastructure.core.metrics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.log.model.McpInvokeContext;
import org.cloudnook.mcp.domain.metrics.repository.McpMetricsRepository;
import org.cloudnook.mcp.domain.metrics.service.McpMetricsDomainService;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * MCP Metrics 领域服务实现
 * 负责监控指标的收集和更新
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpMetricsDomainServiceImpl implements McpMetricsDomainService {

    private final McpMetricsRepository metricsRepository;

    @Override
    public void recordSuccess(McpInvokeContext context, Object result) {
        log.debug("记录成功调用: serverId={}, type={}, target={}",
                context.getServerId(), context.getType(), context.getTargetName());

        long latency = calculateLatency(context.getStartTime());

        // 收集监控指标
        metricsRepository.recordRequest(context.getServerId(), latency, true);

        log.debug("监控指标记录完成: latency={}ms", latency);
    }

    @Override
    public void recordFailure(McpInvokeContext context, Throwable error) {
        log.warn("记录失败调用: serverId={}, type={}, target={}, error={}",
                context.getServerId(), context.getType(), context.getTargetName(), error.getMessage());

        long latency = calculateLatency(context.getStartTime());

        // 收集监控指标
        metricsRepository.recordRequest(context.getServerId(), latency, false);

        log.debug("监控指标记录完成: latency={}ms", latency);
    }

    /**
     * 计算调用延迟
     */
    private long calculateLatency(Instant startTime) {
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }
}
