package org.cloudnook.mcp.domain.service.metrics;

import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;

import java.util.List;

/**
 * 调用日志仓储接口
 * 负责存储和查询 MCP 调用的详细日志记录
 *
 * 支持多种实现方式：
 * - 内存存储（Memory）
 * - 数据库存储（MySQL、PostgreSQL、MongoDB 等）
 * - 消息队列发送（Kafka、RabbitMQ 等）
 * - 远程日志服务（ELK、Loki 等）
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
public interface McpInvocationLogRepository {

    /**
     * 添加调用日志
     *
     * @param record 调用日志记录
     */
    void add(ToolInvocationRecord record);

    /**
     * 获取所有调用日志
     *
     * @return 调用日志列表（最新的在前）
     */
    List<ToolInvocationRecord> getAll();

    /**
     * 分页获取调用日志
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页后的调用日志
     */
    List<ToolInvocationRecord> getPage(int page, int size);

    /**
     * 根据服务ID分页获取调用日志
     *
     * @param serverId 服务ID
     * @param page     页码（从0开始）
     * @param size     每页大小
     * @return 分页后的调用日志
     */
    List<ToolInvocationRecord> getPageByServerId(String serverId, int page, int size);

    /**
     * 清空调用日志
     */
    void clear();

    /**
     * 获取记录数
     *
     * @return 当前日志总数
     */
    int size();
}
