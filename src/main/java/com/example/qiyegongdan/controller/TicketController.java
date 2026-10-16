package com.example.qiyegongdan.controller;

import com.example.qiyegongdan.dto.CommentRequest;
import com.example.qiyegongdan.dto.TicketCreateRequest;
import com.example.qiyegongdan.dto.TicketQueryRequest;
import com.example.qiyegongdan.dto.AssignRequest;
import com.example.qiyegongdan.entity.Ticket;
import com.example.qiyegongdan.entity.TicketAttachment;
import com.example.qiyegongdan.entity.TicketComment;
import com.example.qiyegongdan.security.UserPrincipal;
import com.example.qiyegongdan.service.ApprovalService;
import com.example.qiyegongdan.service.TicketService;
import com.example.qiyegongdan.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "工单管理", description = "工单全生命周期管理")
public class TicketController {

    private final TicketService ticketService;
    private final ApprovalService approvalService;

    public TicketController(TicketService ticketService, ApprovalService approvalService) {
        this.ticketService = ticketService;
        this.approvalService = approvalService;
    }

    @GetMapping
    @Operation(summary = "查询工单列表")
    public ApiResponse<PageResult<TicketVO>> queryTickets(@Valid TicketQueryRequest request) {
        Page<Ticket> page = ticketService.queryTickets(request);
        List<TicketVO> vos = page.getContent().stream().map(this::toVO).toList();
        return ApiResponse.success(new PageResult<>(vos, page.getTotalElements(),
                request.getPage(), request.getSize(), page.getTotalPages()));
    }

    @GetMapping("/my-created")
    @Operation(summary = "我创建的工单")
    public ApiResponse<PageResult<TicketVO>> myCreated(@AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<Ticket> ticketPage = ticketService.getMyCreatedTickets(principal.getId(), page, size);
        List<TicketVO> vos = ticketPage.getContent().stream().map(this::toVO).toList();
        return ApiResponse.success(new PageResult<>(vos, ticketPage.getTotalElements(), page, size, ticketPage.getTotalPages()));
    }

    @GetMapping("/my-assigned")
    @Operation(summary = "分配给我的工单")
    public ApiResponse<PageResult<TicketVO>> myAssigned(@AuthenticationPrincipal UserPrincipal principal,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<Ticket> ticketPage = ticketService.getMyAssignedTickets(principal.getId(), page, size);
        List<TicketVO> vos = ticketPage.getContent().stream().map(this::toVO).toList();
        return ApiResponse.success(new PageResult<>(vos, ticketPage.getTotalElements(), page, size, ticketPage.getTotalPages()));
    }

    @PostMapping
    @Operation(summary = "创建工单")
    public ApiResponse<TicketVO> createTicket(@Valid @RequestBody TicketCreateRequest request,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(toVO(ticketService.createTicket(request, principal.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取工单详情")
    public ApiResponse<TicketVO> getTicket(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicket(id);
        TicketVO vo = toVO(ticket);
        List<TicketComment> comments = ticketService.getComments(id);
        vo.setComments(comments.stream().map(this::toCommentVO).toList());
        List<TicketAttachment> attachments = ticketService.getAttachments(id);
        vo.setAttachments(attachments.stream().map(this::toAttachmentVO).toList());
        vo.setApprovals(approvalService.getApprovalsByTicket(id).stream()
                .map(a -> ApprovalVO.builder()
                        .id(a.getId()).ticketId(a.getTicketId())
                        .submitterId(a.getSubmitterId()).approverId(a.getApproverId())
                        .status(a.getStatus().name()).comment(a.getComment())
                        .createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt()).build())
                .toList());
        return ApiResponse.success(vo);
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "分配工单")
    public ApiResponse<TicketVO> assignTicket(@PathVariable Long id, @Valid @RequestBody AssignRequest request) {
        return ApiResponse.success(toVO(ticketService.assignTicket(id, request.getAssigneeId())));
    }

    @PutMapping("/{id}/start")
    @Operation(summary = "开始处理")
    public ApiResponse<TicketVO> startProcess(@PathVariable Long id) {
        return ApiResponse.success(toVO(ticketService.startProcess(id)));
    }

    @PutMapping("/{id}/resolve")
    @Operation(summary = "标记已解决")
    public ApiResponse<TicketVO> resolveTicket(@PathVariable Long id) {
        return ApiResponse.success(toVO(ticketService.resolveTicket(id)));
    }

    @PutMapping("/{id}/close")
    @Operation(summary = "关闭工单")
    public ApiResponse<TicketVO> closeTicket(@PathVariable Long id) {
        return ApiResponse.success(toVO(ticketService.closeTicket(id)));
    }

    @PutMapping("/{id}/reopen")
    @Operation(summary = "重开工单")
    public ApiResponse<TicketVO> reopenTicket(@PathVariable Long id) {
        return ApiResponse.success(toVO(ticketService.reopenTicket(id)));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "添加评论")
    public ApiResponse<CommentVO> addComment(@PathVariable Long id,
                                               @Valid @RequestBody CommentRequest request,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        TicketComment comment = ticketService.addComment(id, principal.getId(), request);
        String name = principal.getRealName() != null ? principal.getRealName() : principal.getUsername();
        CommentVO vo = CommentVO.builder()
                .id(comment.getId()).userId(comment.getUserId())
                .realName(name).content(comment.getContent())
                .createdAt(comment.getCreatedAt()).build();
        return ApiResponse.success(vo);
    }

    @PostMapping("/{id}/attachments")
    @Operation(summary = "上传附件")
    public ApiResponse<AttachmentVO> uploadAttachment(@PathVariable Long id,
                                                        @AuthenticationPrincipal UserPrincipal principal,
                                                        @RequestParam("file") MultipartFile file) {
        TicketAttachment attachment = ticketService.uploadAttachment(id, principal.getId(), file);
        AttachmentVO vo = AttachmentVO.builder()
                .id(attachment.getId()).fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize()).createdAt(attachment.getCreatedAt()).build();
        return ApiResponse.success(vo);
    }

    @PostMapping("/auto-assign")
    @Operation(summary = "自动分配待处理工单")
    public ApiResponse<TicketVO> autoAssign() {
        Ticket ticket = ticketService.autoAssign();
        if (ticket == null) return ApiResponse.success("无待分配工单", null);
        return ApiResponse.success(toVO(ticket));
    }

    private TicketVO toVO(Ticket ticket) {
        return TicketVO.builder()
                .id(ticket.getId()).title(ticket.getTitle()).description(ticket.getDescription())
                .type(ticket.getType()).priority(ticket.getPriority()).status(ticket.getStatus())
                .category(ticket.getCategory()).customFields(ticket.getCustomFields())
                .creatorId(ticket.getCreatorId()).creatorName(ticketService.getUsername(ticket.getCreatorId()))
                .assigneeId(ticket.getAssigneeId())
                .assigneeName(ticket.getAssigneeId() != null ? ticketService.getUsername(ticket.getAssigneeId()) : null)
                .createdAt(ticket.getCreatedAt()).updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt()).closedAt(ticket.getClosedAt())
                .build();
    }

    private CommentVO toCommentVO(TicketComment comment) {
        return CommentVO.builder()
                .id(comment.getId()).userId(comment.getUserId())
                .username(ticketService.getUsername(comment.getUserId()))
                .content(comment.getContent()).createdAt(comment.getCreatedAt()).build();
    }

    private AttachmentVO toAttachmentVO(TicketAttachment attachment) {
        return AttachmentVO.builder()
                .id(attachment.getId()).fileName(attachment.getFileName())
                .fileSize(attachment.getFileSize()).createdAt(attachment.getCreatedAt()).build();
    }
}