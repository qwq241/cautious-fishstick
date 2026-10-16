package com.example.qiyegongdan.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalVO {
    private Long id;
    private Long ticketId;
    private Long submitterId;
    private Long approverId;
    private String status;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}