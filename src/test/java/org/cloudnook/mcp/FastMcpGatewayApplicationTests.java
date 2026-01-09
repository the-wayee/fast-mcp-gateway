package org.cloudnook.mcp;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * MCP 连接测试
 * 先用最简单的 HTTP 请求测试 MCP Server 是否可用
 */
@Slf4j
@SpringBootTest
class FastMcpGatewayApplicationTests {

    @Test
    void textStreamable(){
        McpClientTransport transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8000")
                .build();

        McpAsyncClient client = McpClient.async(transport)
                .build();

        client.initialize().block();

    }
}
