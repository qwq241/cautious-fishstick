package com.example.qiyegongdan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "审批操作请求")
public class ApprovalRequest {

    @Size(max = 500, message = "审批意见最长500个字符")
    @Schema(description = "审批意见", example = "修复方案合理，同意关闭")
    private String comment;
}