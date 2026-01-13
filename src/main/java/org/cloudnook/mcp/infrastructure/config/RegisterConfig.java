package org.cloudnook.mcp.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.server.service.McpRegister;
import org.cloudnook.mcp.infrastructure.core.register.McpMemoryRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * MCP 注册中心配置
 * 根据 YAML 配置动态选择注册中心实现
 */
@Slf4j
@Configuration
public class RegisterConfig {

    @Autowired
    private RegisterProperties registerProperties;

    /**
     * 创建注册中心实例
     * 根据配置动态选择实现
     */
    @Bean
    @Primary
    public McpRegister mcpRegister() {
        RegisterProperties.RegisterType type = registerProperties.getType();

        log.info("初始化 MCP 注册中心，类型: {}", type);

        switch (type) {
            case MEMORY:
                log.info("使用内存注册中心");
                return new McpMemoryRegister();

            case REDIS:
                log.info("使用 Redis 注册中心");
                // TODO: 后续实现 RedisRegister
                // return new RedisRegister(registerProperties.getRedis());
                throw new UnsupportedOperationException("Redis 注册中心暂未实现");

            default:
                log.warn("未知的注册中心类型，使用默认的内存注册中心");
                return new McpMemoryRegister();
        }
    }
}
