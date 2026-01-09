package org.cloudnook.mcp.domain.model.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum McpTransportType {

    STDIO,
    SSE,
    STREAMABLE_HTTP
}
