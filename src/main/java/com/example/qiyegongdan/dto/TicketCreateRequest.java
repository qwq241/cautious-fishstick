package com.example.qiyegongdan.dto;

import com.example.qiyegongdan.enums.TicketPriority;
import com.example.qiyegongdan.enums.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "工单创建请求")
public class TicketCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长200个字符")
    @Schema(description = "工单标题", example = "系统登录异常")
    private String title;

    @NotBlank(message = "描述不能为空")
    @Schema(description = "工单描述", example = "用户反馈无法正常登录")
    private String description;

    @NotNull(message = "工单类型不能为空")
    @Schema(description = "工单类型", example = "BUG")
    private TicketType type;

    @NotNull(message = "优先级不能为空")
    @Schema(description = "优先级", example = "HIGH")
    private TicketPriority priority;

    @Schema(description = "工单分类", example = "系统管理")
    private String category;

    @Schema(description = "自定义字段")
    private String customFields;

    @Schema(description = "指定处理人ID")
    private Long assigneeId;
}