package org.cloudnook.mcp.domain.log.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

/**
 * MCP Invoke 调用上下文
 * 用于在调用过程中传递上下文信息
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Data
@Builder
public class McpInvokeContext {

    /**
     * 调用ID
     */
    private String callId;

    /**
     * 调用来源
     */
    private McpInvokeLog.InvokeSource source;

    /**
     * 服务ID
     */
    private String serverId;

    /**
     * 调用类型
     */
    private McpInvokeLog.InvokeType type;

    /**
     * 目标名称（toolName/resourceUri/promptName）
     */
    private String targetName;

    /**
     * 调用参数
     */
    private Map<String, Object> arguments;

    /**
     * 客户端ID（可选）
     */
    private String clientId;

    /**
     * 开始时间
     */
    private Instant startTime;
}
