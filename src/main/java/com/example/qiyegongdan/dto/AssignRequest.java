package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRequest {
    @NotNull(message = "澶勭悊浜篒D涓嶈兘涓虹┖")
    private Long assigneeId;
}