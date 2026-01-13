package org.cloudnook.mcp.domain.log.repository;

import org.cloudnook.mcp.domain.log.model.McpInvokeLog;

import java.util.List;

/**
 * MCP Invoke 调用日志仓储接口
 * 负责存储和查询 MCP 调用日志
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
public interface McpInvokeLogRepository {

    /**
     * 添加调用日志
     *
     * @param log 调用日志
     */
    void add(McpInvokeLog log);

    /**
     * 获取所有调用日志
     *
     * @return 调用日志列表（最新的在前）
     */
    List<McpInvokeLog> getAll();

    /**
     * 分页获取调用日志
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页后的调用日志
     */
    List<McpInvokeLog> getPage(int page, int size);

    /**
     * 根据来源分页获取调用日志
     *
     * @param source 调用来源
     * @param page   页码（从0开始）
     * @param size   每页大小
     * @return 分页后的调用日志
     */
    List<McpInvokeLog> getPageBySource(McpInvokeLog.InvokeSource source, int page, int size);

    /**
     * 根据服务ID分页获取调用日志
     *
     * @param serverId 服务ID
     * @param page     页码（从0开始）
     * @param size     每页大小
     * @return 分页后的调用日志
     */
    List<McpInvokeLog> getPageByServerId(String serverId, int page, int size);

    /**
     * 清空调用日志
     */
    void clear();

    /**
     * 获取记录数
     *
     * @return 当前记录总数
     */
    int size();
}
