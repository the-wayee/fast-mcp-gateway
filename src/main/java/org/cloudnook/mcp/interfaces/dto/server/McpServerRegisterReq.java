package org.cloudnook.mcp.interfaces.dto.server;


import lombok.Data;
import org.cloudnook.mcp.domain.server.model.McpTransportType;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-09 17:07
 * @Description: Mcp 注册请求
 */
@Data
public class McpServerRegisterReq {

    /**
     * 服务名
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 协议类型
     */
    private McpTransportType transportType;

    /**
     * 接入点
     */
    private String endpoint;

    /**
     * 版本号（可选）
     */
    private String version;
}
