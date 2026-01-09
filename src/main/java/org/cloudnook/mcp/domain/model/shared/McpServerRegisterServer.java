package org.cloudnook.mcp.domain.model.shared;

import lombok.Data;

@Data
public class McpServerRegisterServer {

    private String name;

    private McpTransportType mcpTransportType;

}
