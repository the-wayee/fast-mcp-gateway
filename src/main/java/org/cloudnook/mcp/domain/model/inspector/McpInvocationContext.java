package org.cloudnook.mcp.domain.model.inspector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * 监控上下文对象
 * 封装一次 MCP 调用的所有监控信息
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpInvocationContext {

    /**
     * 调用唯一ID
     */
    private String invocationId;

    /**
     * 服务器ID
     */
    private String serverId;

    /**
     * 操作类型
     * TOOL_CALL, RESOURCE_READ, PROMPT_GET
     */
    private ToolInvocationRecord.InvocationType operationType;

    /**
     * 目标名称
     * toolName, resourceUri, promptName
     */
    private String targetName;

    /**
     * 调用参数
     */
    private Map<String, Object> arguments;

    /**
     * 调用开始时间
     */
    private Instant startTime;

    /**
     * 操作名称（字符串形式，便于日志记录）
     */
    public String getOperationName() {
        return operationType != null ? operationType.name() : "UNKNOWN";
    }
}