package com.example.qiyegongdan.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "璇勮鍐呭涓嶈兘涓虹┖")
    private String content;
}