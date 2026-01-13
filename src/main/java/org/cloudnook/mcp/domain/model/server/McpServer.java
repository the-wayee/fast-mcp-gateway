package org.cloudnook.mcp.domain.model.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cloudnook.mcp.domain.model.shared.McpServerStatus;
import org.cloudnook.mcp.domain.model.shared.McpTransportType;

/**
 * MCP 服务实体
 * 只存储服务的基本信息，不包含监控数据
 *
 * @Author: the-way
 * @Date: 2026-01-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpServer {

    /**
     * 服务 id
     */
    private String id;

    /**
     * 服务名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 服务状态
     */
    private McpServerStatus status;

    /**
     * 传输类型
     */
    private McpTransportType transportType;

    /**
     * 连接端点
     */
    private String endpoint;

}
