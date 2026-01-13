package org.cloudnook.mcp.infrastructure.core.log;

import org.cloudnook.mcp.domain.log.model.McpInvokeLog;
import org.cloudnook.mcp.domain.log.repository.McpInvokeLogRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * MCP Invoke 调用日志仓储 - 内存实现
 * 使用 ConcurrentLinkedDeque 存储，支持高并发写入
 *
 * 特点：
 * - 容量限制：最多 100 条记录（LRU 淘汰）
 * - 顺序：LIFO（最新的在前）
 * - 线程安全：使用并发队列
 *
 * 适用场景：
 * - 开发测试环境
 * - 调试和问题排查
 * - 不需要持久化的场景
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@Component
public class McpInvokeLogMemoryRepository implements McpInvokeLogRepository {

    /**
     * 最大记录数
     */
    private static final int MAX_SIZE = 100;

    /**
     * 日志记录（使用 Deque 保持 LIFO 顺序）
     */
    private final ConcurrentLinkedDeque<McpInvokeLog> logs = new ConcurrentLinkedDeque<>();

    @Override
    public void add(McpInvokeLog log) {
        logs.addFirst(log);

        // 超出限制时删除最旧的记录
        while (logs.size() > MAX_SIZE) {
            logs.removeLast();
        }
    }

    @Override
    public List<McpInvokeLog> getAll() {
        return new ArrayList<>(logs);
    }

    @Override
    public List<McpInvokeLog> getPage(int page, int size) {
        List<McpInvokeLog> all = new ArrayList<>(logs);
        int start = page * size;
        if (start >= all.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    @Override
    public List<McpInvokeLog> getPageBySource(McpInvokeLog.InvokeSource source, int page, int size) {
        // 过滤出指定来源的日志记录
        List<McpInvokeLog> filtered = logs.stream()
                .filter(log -> source.equals(log.getSource()))
                .collect(Collectors.toList());

        // 分页
        int start = page * size;
        if (start >= filtered.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, filtered.size());
        return filtered.subList(start, end);
    }

    @Override
    public List<McpInvokeLog> getPageByServerId(String serverId, int page, int size) {
        // 过滤出指定服务的日志记录
        List<McpInvokeLog> filtered = logs.stream()
                .filter(log -> serverId.equals(log.getServerId()))
                .collect(Collectors.toList());

        // 分页
        int start = page * size;
        if (start >= filtered.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, filtered.size());
        return filtered.subList(start, end);
    }

    @Override
    public void clear() {
        logs.clear();
    }

    @Override
    public int size() {
        return logs.size();
    }
}
