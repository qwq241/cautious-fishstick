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
public class AttachmentVO {
    private Long id;
    private String fileName;
    private Long fileSize;
    private LocalDateTime createdAt;
}