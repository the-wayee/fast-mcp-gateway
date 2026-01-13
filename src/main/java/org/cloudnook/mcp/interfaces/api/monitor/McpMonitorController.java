package org.cloudnook.mcp.interfaces.api.monitor;

import lombok.RequiredArgsConstructor;
import org.cloudnook.mcp.application.service.McpServerAppService;
import org.cloudnook.mcp.domain.model.metrics.McpServerMetrics;
import org.cloudnook.mcp.infrastruction.common.result.Result;
import org.cloudnook.mcp.interfaces.dto.monitor.ServerMonitorSummaryVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MCP 监控数据查询接口
 * 提供服务监控数据的查询能力
 *
 * @Author: the-way
 * @Date: 2026-01-13
 */
@RestController
@RequestMapping("/monitors")
@RequiredArgsConstructor
public class McpMonitorController {

    private final McpServerAppService mcpServerAppService;

    /**
     * 获取所有服务监控概览（列表展示）
     * 只返回关键指标，不包含详细数据
     *
     * @return 服务监控概览列表
     */
    @GetMapping("/summary")
    public Result<List<ServerMonitorSummaryVO>> getAllSummaries() {
        List<ServerMonitorSummaryVO> summaries = mcpServerAppService.getAllServerSummaries();
        return Result.success(summaries);
    }

    /**
     * 获取单个服务完整监控数据（详情页展示）
     *
     * @param serverId 服务ID
     * @return 完整监控数据
     */
    @GetMapping("/{serverId}/detail")
    public Result<McpServerMetrics> getMonitorDetail(@PathVariable String serverId) {
        McpServerMetrics metrics = mcpServerAppService.getServerDetail(serverId);

        if (metrics == null) {
            return Result.error("服务监控数据不存在: " + serverId);
        }

        return Result.success(metrics);
    }

}
