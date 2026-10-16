package com.example.qiyegongdan.vo;

import com.example.qiyegongdan.enums.TicketPriority;
import com.example.qiyegongdan.enums.TicketStatus;
import com.example.qiyegongdan.enums.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVO {
    private Long id;
    private String title;
    private String description;
    private TicketType type;
    private TicketPriority priority;
    private TicketStatus status;
    private String category;
    private String customFields;
    private Long creatorId;
    private String creatorName;
    private Long assigneeId;
    private String assigneeName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private List<CommentVO> comments;
    private List<AttachmentVO> attachments;
    private List<ApprovalVO> approvals;
}