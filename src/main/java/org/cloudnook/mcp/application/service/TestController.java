package org.cloudnook.mcp.application.service;


import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 测试 controller
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final McpAsyncClient client;

    public TestController() {
        // ⚠️ 测试阶段先写死，后面你会从 registry / manager 拿
        McpClientTransport transport =
                HttpClientStreamableHttpTransport
                        .builder("http://localhost:8000")
                        .build();

        this.client = McpClient.async(transport).build();

        // 初始化 MCP session
        this.client.initialize().block();
    }

    @GetMapping("/mcp/tools")
    public Mono<McpSchema.ListToolsResult> listTools() {
        return client.listTools();
    }
}
