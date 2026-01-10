package org.cloudnook.mcp.infrastruction.inspector;


import org.cloudnook.mcp.domain.model.inspector.ToolInvocationRecord;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author: the-way
 * @Verson: v1.0
 * @Date: 2026-01-10
 * @Description: 调用历史内存存储
 */
@Component
public class InvocationHistoryMemoryStore {

    /**
     * 最大记录数
     */
    private static final int MAX_SIZE = 100;

    /**
     * 历史记录（使用 Deque 保持 LIFO 顺序）
     */
    private final ConcurrentLinkedDeque<ToolInvocationRecord> history = new ConcurrentLinkedDeque<>();

    /**
     * 添加记录
     */
    public void add(ToolInvocationRecord record) {
        history.addFirst(record);
        
        // 超出限制时删除最旧的记录
        while (history.size() > MAX_SIZE) {
            history.removeLast();
        }
    }

    /**
     * 获取所有历史记录
     *
     * @return 历史记录列表（最新的在前）
     */
    public List<ToolInvocationRecord> getAll() {
        return new ArrayList<>(history);
    }

    /**
     * 分页获取历史记录
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页后的历史记录
     */
    public List<ToolInvocationRecord> getPage(int page, int size) {
        List<ToolInvocationRecord> all = new ArrayList<>(history);
        int start = page * size;
        if (start >= all.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, all.size());
        return all.subList(start, end);
    }

    /**
     * 清空历史
     */
    public void clear() {
        history.clear();
    }

    /**
     * 获取记录数
     */
    public int size() {
        return history.size();
    }
}
