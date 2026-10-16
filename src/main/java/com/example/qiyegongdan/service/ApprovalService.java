package com.example.qiyegongdan.service;

import com.example.qiyegongdan.entity.Approval;
import com.example.qiyegongdan.entity.Ticket;
import com.example.qiyegongdan.entity.User;
import com.example.qiyegongdan.enums.ApprovalStatus;
import com.example.qiyegongdan.enums.TicketStatus;
import com.example.qiyegongdan.exception.BusinessException;
import com.example.qiyegongdan.repository.ApprovalRepository;
import com.example.qiyegongdan.repository.TicketRepository;
import com.example.qiyegongdan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public ApprovalService(ApprovalRepository approvalRepository,
                            TicketRepository ticketRepository,
                            UserRepository userRepository) {
        this.approvalRepository = approvalRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public List<Approval> getApprovalsByTicket(Long ticketId) {
        return approvalRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    @Transactional
    public Approval submitForApproval(Long ticketId, Long submitterId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("工单不存在"));

        if (ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new BusinessException("只有处理中的工单才能提交审批");
        }

        ticket.setStatus(TicketStatus.PENDING_APPROVAL);
        ticketRepository.save(ticket);

        Approval approval = Approval.builder()
                .ticketId(ticketId)
                .submitterId(submitterId)
                .status(ApprovalStatus.PENDING)
                .build();

        return approvalRepository.save(approval);
    }

    @Transactional
    public Approval approve(Long approvalId, Long approverId, String comment) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批记录不存在"));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("该审批已处理");
        }

        Ticket ticket = ticketRepository.findById(approval.getTicketId())
                .orElseThrow(() -> new BusinessException("工单不存在"));

        if (approverId.equals(approval.getSubmitterId())) {
            throw new BusinessException("不能审批自己提交的工单");
        }

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setApproverId(approverId);
        approval.setComment(comment);
        approvalRepository.save(approval);

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        return approval;
    }

    @Transactional
    public Approval reject(Long approvalId, Long approverId, String comment) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("审批记录不存在"));

        if (approval.getStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException("该审批已处理");
        }

        Ticket ticket = ticketRepository.findById(approval.getTicketId())
                .orElseThrow(() -> new BusinessException("工单不存在"));

        if (approverId.equals(approval.getSubmitterId())) {
            throw new BusinessException("不能审批自己提交的工单");
        }

        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setApproverId(approverId);
        approval.setComment(comment);
        approvalRepository.save(approval);

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticketRepository.save(ticket);

        return approval;
    }
}