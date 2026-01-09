package org.cloudnook.mcp.domain.service.routing;

import lombok.extern.slf4j.Slf4j;
import org.cloudnook.mcp.domain.model.server.McpServer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询（Round Robin）负载均衡策略
 * 按顺序依次选择每个服务实例，实现请求的平均分配
 */
@Slf4j
@Component
public class RoundRobinLoadBalanceStrategy implements LoadBalanceStrategy {

    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public McpServer select(List<McpServer> servers) {
        if (servers == null || servers.isEmpty()) {
            log.warn("No servers available for round robin selection");
            return null;
        }

        // 只有一个实例时直接返回
        if (servers.size() == 1) {
            return servers.get(0);
        }

        // 使用轮询算法选择实例
        int selectedIndex = Math.abs(index.getAndIncrement()) % servers.size();
        McpServer selectedServer = servers.get(selectedIndex);

        log.debug("Round robin selected server: index={}, serverId={}",
                selectedIndex, selectedServer.getId());

        return selectedServer;
    }

    @Override
    public String getStrategyName() {
        return "ROUND_ROBIN";
    }
}
