package org.cloudnook.mcp.domain.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum McpTransportType {

    STDIO,
    SSE,
    STREAMABLE_HTTP
}
