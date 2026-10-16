package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.service.TicketService;
import com.example.qiyegongdan.vo.ApiResponse;
import com.example.qiyegongdan.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "统计报表", description = "工单数据统计")
public class StatisticsController {

    private final TicketService ticketService;

    public StatisticsController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/overview")
    @Operation(summary = "获取工单概览统计")
    public ApiResponse<StatisticsVO> overview() {
        return ApiResponse.success(ticketService.getStatistics());
    }
}