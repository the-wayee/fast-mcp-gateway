package org.cloudnook.mcp.interfaces.dto.inspector;


import lombok.Data;

import java.util.Map;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: Prompt 获取请求
 */
@Data
public class PromptGetRequest {

    /**
     * Prompt 名称
     */
    private String promptName;

    /**
     * Prompt 参数
     */
    private Map<String, Object> arguments;
}
