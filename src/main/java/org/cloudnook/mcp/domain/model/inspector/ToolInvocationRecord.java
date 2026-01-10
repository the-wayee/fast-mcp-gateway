package org.cloudnook.mcp.domain.model.inspector;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: 调用记录实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolInvocationRecord {

    /**
     * 调用ID
     */
    private String invocationId;

    /**
     * 服务ID
     */
    private String serverId;

    /**
     * 操作类型
     */
    private InvocationType type;

    /**
     * 请求名称（toolName / resourceUri / promptName）
     */
    private String requestName;

    /**
     * 请求参数
     */
    private Map<String, Object> requestArgs;

    /**
     * 响应结果（JSON 字符串）
     */
    private String response;

    /**
     * 耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 状态
     */
    private InvocationStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 时间戳
     */
    private Instant timestamp;

    /**
     * 操作类型枚举
     */
    public enum InvocationType {
        TOOL_CALL,
        RESOURCE_READ,
        PROMPT_GET
    }

    /**
     * 状态枚举
     */
    public enum InvocationStatus {
        SUCCESS,
        FAILURE
    }
}
