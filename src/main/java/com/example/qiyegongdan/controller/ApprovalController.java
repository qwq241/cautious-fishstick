package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.dto.ApprovalRequest;
import com.example.qiyegongdan.entity.Approval;
import com.example.qiyegongdan.security.UserPrincipal;
import com.example.qiyegongdan.service.ApprovalService;
import com.example.qiyegongdan.vo.ApiResponse;
import com.example.qiyegongdan.vo.ApprovalVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "审批管理", description = "工单审批操作")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/{id}/approvals")
    @Operation(summary = "获取工单审批记录")
    public ApiResponse<List<ApprovalVO>> getApprovals(@PathVariable Long id) {
        List<Approval> approvals = approvalService.getApprovalsByTicket(id);
        List<ApprovalVO> vos = approvals.stream().map(this::toVO).toList();
        return ApiResponse.success(vos);
    }

    @PostMapping("/{id}/submit-approval")
    @Operation(summary = "提交审批")
    public ApiResponse<ApprovalVO> submitForApproval(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserPrincipal principal) {
        Approval approval = approvalService.submitForApproval(id, principal.getId());
        return ApiResponse.success(toVO(approval));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "通过审批")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ApprovalVO> approve(@PathVariable Long id,
                                            @Valid @RequestBody ApprovalRequest request,
                                            @AuthenticationPrincipal UserPrincipal principal) {
        Approval approval = approvalService.getApprovalsByTicket(id)
                .stream()
                .filter(a -> a.getStatus() == com.example.qiyegongdan.enums.ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new com.example.qiyegongdan.exception.BusinessException("没有待审批的记录"));

        return ApiResponse.success(toVO(approvalService.approve(approval.getId(), principal.getId(), request.getComment())));
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "驳回审批")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<ApprovalVO> reject(@PathVariable Long id,
                                           @Valid @RequestBody ApprovalRequest request,
                                           @AuthenticationPrincipal UserPrincipal principal) {
        Approval approval = approvalService.getApprovalsByTicket(id)
                .stream()
                .filter(a -> a.getStatus() == com.example.qiyegongdan.enums.ApprovalStatus.PENDING)
                .findFirst()
                .orElseThrow(() -> new com.example.qiyegongdan.exception.BusinessException("没有待审批的记录"));

        return ApiResponse.success(toVO(approvalService.reject(approval.getId(), principal.getId(), request.getComment())));
    }

    private ApprovalVO toVO(Approval approval) {
        return ApprovalVO.builder()
                .id(approval.getId())
                .ticketId(approval.getTicketId())
                .submitterId(approval.getSubmitterId())
                .approverId(approval.getApproverId())
                .status(approval.getStatus().name())
                .comment(approval.getComment())
                .createdAt(approval.getCreatedAt())
                .updatedAt(approval.getUpdatedAt())
                .build();
    }
}