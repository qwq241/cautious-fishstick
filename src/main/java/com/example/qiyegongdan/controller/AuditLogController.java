package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.entity.AuditLog;
import com.example.qiyegongdan.service.AuditLogService;
import com.example.qiyegongdan.vo.ApiResponse;
import com.example.qiyegongdan.vo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "审计日志", description = "操作日志查询")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "查询审计日志")
    public ApiResponse<PageResult<AuditLog>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AuditLog> logPage = auditLogService.getLogs(module, page, size);
        return ApiResponse.success(new PageResult<>(logPage.getContent(), logPage.getTotalElements(),
                page, size, logPage.getTotalPages()));
    }
}