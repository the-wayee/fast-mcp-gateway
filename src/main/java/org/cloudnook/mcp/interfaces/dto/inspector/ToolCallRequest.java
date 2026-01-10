package org.cloudnook.mcp.interfaces.dto.inspector;


import lombok.Data;

import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: Tool 调用请求
 */
@Data
public class ToolCallRequest {

    /**
     * Tool 名称
     */
    private String toolName;

    /**
     * 调用参数
     */
    private Map<String, Object> arguments;
}
