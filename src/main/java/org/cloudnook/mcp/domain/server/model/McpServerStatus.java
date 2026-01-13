package org.cloudnook.mcp.domain.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum McpServerStatus {

    /**
     * 可用
     */
    ACTIVE,

    /**
     * 未激活
     */
    INACTIVE,

    /**
     * 不可用
     */
    UNHEALTHY
}
