package com.example.qiyegongdan.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsVO {
    private long totalTickets;
    private long pendingTickets;
    private long inProgressTickets;
    private long pendingApprovalTickets;
    private long resolvedTickets;
    private long closedTickets;
    private Map<String, Long> ticketsByType;
    private Map<String, Long> ticketsByPriority;
    private List<DailyStats> dailyTrend;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DailyStats {
    private String date;
    private long created;
    private long resolved;
}