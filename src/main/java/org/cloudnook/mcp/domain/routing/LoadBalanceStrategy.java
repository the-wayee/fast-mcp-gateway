package org.cloudnook.mcp.domain.routing;

import org.cloudnook.mcp.domain.server.model.McpServer;

import java.util.List;

/**
 * 负载均衡策略接口
 * 定义从多个服务实例中选择一个的算法
 */
public interface LoadBalanceStrategy {

    /**
     * 从服务实例列表中选择一个实例
     *
     * @param servers 候选服务实例列表
     * @return 被选中的服务实例，如果列表为空则返回 null
     */
    McpServer select(List<McpServer> servers);

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    String getStrategyName();
}
