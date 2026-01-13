package org.cloudnook.mcp.infrastruction.inspector;


import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;
import org.cloudnook.mcp.domain.service.metrics.McpInvocationLogRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 调用日志仓储 - 内存实现
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
 * @Verson: v1.0
 * @Date: 2026-01-10
 */
@Component
public class McpInvocationLogMemoryRepository implements McpInvocationLogRepository {

    /**
     * 最大记录数
     */
    private static final int MAX_SIZE = 100;

    /**
     * 日志记录（使用 Deque 保持 LIFO 顺序）
     */
    private final ConcurrentLinkedDeque<ToolInvocationRecord> logs = new ConcurrentLinkedDeque<>();

    /**
     * 添加日志记录
     */
    public void add(ToolInvocationRecord record) {
        logs.addFirst(record);

        // 超出限制时删除最旧的记录
        while (logs.size() > MAX_SIZE) {
            logs.removeLast();
        }
    }

    /**
     * 获取所有日志记录
     *
     * @return 日志记录列表（最新的在前）
     */
    public List<ToolInvocationRecord> getAll() {
        return new ArrayList<>(logs);
    }

    /**
     * 分页获取日志记录
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页后的日志记录
     */
    public List<ToolInvocationRecord> getPage(int page, int size) {
        List<ToolInvocationRecord> all = new ArrayList<>(logs);
        int start = page * size;
        if (start >= all.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    /**
     * 清空日志
     */
    public void clear() {
        logs.clear();
    }

    /**
     * 获取记录数
     */
    public int size() {
        return logs.size();
    }
}
