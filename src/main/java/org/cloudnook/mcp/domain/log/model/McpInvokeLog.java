package org.cloudnook.mcp.domain.log.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * MCP Invoke 调用日志
 * 记录所有对 MCP Server 的操作（Tool调用、Resource读取、Prompt获取等）
 *
 * 来源：
 * - INSPECTOR: 调试入口（开发者通过Inspector界面操作）
 * - ROUTER: 路由入口（真实的AI客户端通过Router调用）
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpInvokeLog {

    /**
     * 调用ID
     */
    private String callId;

    /**
     * 调用来源
     */
    private InvokeSource source;

    /**
     * 服务ID
     */
    private String serverId;

    /**
     * 服务名称（冗余字段，便于查询）
     */
    private String serverName;

    /**
     * 调用类型
     */
    private InvokeType type;

    /**
     * 目标名称（toolName/resourceUri/promptName）
     */
    private String targetName;

    /**
     * 调用参数
     */
    private Map<String, Object> arguments;

    /**
     * 响应结果（JSON格式）
     */
    private String response;

    /**
     * 调用状态
     */
    private InvokeStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 调用时间
     */
    private Instant timestamp;

    /**
     * 客户端ID（仅ROUTER来源时有值）
     */
    private String clientId;

    /**
     * 调用来源枚举
     */
    public enum InvokeSource {
        INSPECTOR,  // 调试入口
        ROUTER      // 路由入口（真实请求）
    }

    /**
     * 调用类型枚举
     */
    public enum InvokeType {
        TOOL_LIST,      // 查询Tools列表
        TOOL_CALL,      // 调用Tool
        RESOURCE_LIST,  // 查询Resources列表
        RESOURCE_READ,  // 读取Resource
        PROMPT_LIST,    // 查询Prompts列表
        PROMPT_GET      // 获取Prompt
    }

    /**
     * 调用状态枚举
     */
    public enum InvokeStatus {
        SUCCESS,  // 成功
        FAILURE   // 失败
    }
}
