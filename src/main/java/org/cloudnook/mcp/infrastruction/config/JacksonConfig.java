package org.cloudnook.mcp.infrastruction.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: Jackson 配置
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置 ObjectMapper Bean
     * 提供 JSON 序列化/反序列化能力
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 禁用日期作为时间戳输出
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return objectMapper;
    }
}
