package org.cloudnook.mcp.domain.mcp.model;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class ToolInvocationContext {
    String requestId;
    String toolName;
    Map<String, Object> args;
    String selectedServerId;
    Instant startTime;
}