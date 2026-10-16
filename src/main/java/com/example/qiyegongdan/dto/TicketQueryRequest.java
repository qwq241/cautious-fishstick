package com.example.qiyegongdan.dto;

import com.example.qiyegongdan.enums.TicketPriority;
import com.example.qiyegongdan.enums.TicketStatus;
import com.example.qiyegongdan.enums.TicketType;
import lombok.Data;

@Data
public class TicketQueryRequest {
    private String keyword;
    private TicketType type;
    private TicketStatus status;
    private TicketPriority priority;
    private String category;
    private Long creatorId;
    private Long assigneeId;
    private Integer page = 1;
    private Integer size = 10;
}
