package org.cloudnook.mcp.infrastruction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MCP 注册中心配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "mcp.gateway.register")
public class RegisterProperties {

    /**
     * 注册中心类型
     */
    private RegisterType type = RegisterType.MEMORY;

    /**
     * Redis 配置
     */
    private RedisConfig redis = new RedisConfig();

    /**
     * 注册中心类型枚举
     */
    public enum RegisterType {
        MEMORY,
        REDIS
    }

    /**
     * Redis 配置
     */
    @Data
    public static class RedisConfig {
        private String host = "localhost";
        private int port = 6379;
        private String password;
        private int database = 0;
        private String keyPrefix = "mcp:gateway:server:";
    }
}
