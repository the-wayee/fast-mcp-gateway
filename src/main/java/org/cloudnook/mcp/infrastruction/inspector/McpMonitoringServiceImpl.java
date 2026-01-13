package org.cloudnook.mcp.infrastruction.inspector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.model.inspector.McpInvocationContext;
import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;
import org.cloudnook.mcp.domain.service.metrics.McpInvocationLogRepository;
import org.cloudnook.mcp.domain.service.metrics.McpMonitoringService;
import org.cloudnook.mcp.domain.service.server.McpMetricsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * MCP 监控服务实现
 * 负责统一的监控流程编排：
 * 1. 构建调用记录
 * 2. 存储调用历史
 * 3. 收集监控指标
 * 4. 更新健康状态
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class McpMonitoringServiceImpl implements McpMonitoringService {

    private final McpInvocationLogRepository mcpLogRepository;
    private final McpMetricsRepository metricsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void recordSuccess(McpInvocationContext context, Object result) {
        log.debug("记录成功调用: serverId={}, operation={}, target={}",
                context.getServerId(), context.getOperationName(), context.getTargetName());

        long latency = calculateLatency(context.getStartTime());

        // 1. 构建调用记录
        ToolInvocationRecord record = buildRecord(
                context,
                result,
                ToolInvocationRecord.InvocationStatus.SUCCESS,
                null
        );

        // 2. 存储调用日志
        mcpLogRepository.add(record);

        // 3. 收集监控指标
        metricsRepository.recordRequest(context.getServerId(), latency, true);

        log.debug("监控数据处理完成: latency={}ms", latency);
    }

    @Override
    public void recordFailure(McpInvocationContext context, Throwable error) {
        log.warn("记录失败调用: serverId={}, operation={}, target={}, error={}",
                context.getServerId(), context.getOperationName(), context.getTargetName(), error.getMessage());

        long latency = calculateLatency(context.getStartTime());

        // 1. 构建调用记录
        ToolInvocationRecord record = buildRecord(
                context,
                null,
                ToolInvocationRecord.InvocationStatus.FAILURE,
                error.getMessage()
        );

        // 2. 存储调用日志
        mcpLogRepository.add(record);

        // 3. 收集监控指标
        metricsRepository.recordRequest(context.getServerId(), latency, false);

        log.debug("监控数据处理完成: latency={}ms", latency);
    }

    /**
     * 计算调用延迟
     */
    private long calculateLatency(Instant startTime) {
        return Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    /**
     * 构建调用记录
     */
    private ToolInvocationRecord buildRecord(
            McpInvocationContext context,
            Object result,
            ToolInvocationRecord.InvocationStatus status,
            String errorMessage
    ) {
        long durationMs = calculateLatency(context.getStartTime());

        // 序列化响应结果
        String responseJson = null;
        if (result != null) {
            try {
                responseJson = objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                log.warn("序列化响应结果失败: {}", e.getMessage());
                responseJson = result.toString();
            }
        }

        return ToolInvocationRecord.builder()
                .invocationId(context.getInvocationId())
                .serverId(context.getServerId())
                .type(context.getOperationType())
                .requestName(context.getTargetName())
                .requestArgs(context.getArguments())
                .response(responseJson)
                .durationMs(durationMs)
                .status(status)
                .errorMessage(errorMessage)
                .timestamp(context.getStartTime())
                .build();
    }
}
