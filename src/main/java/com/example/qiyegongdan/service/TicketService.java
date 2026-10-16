package com.example.qiyegongdan.service;

import com.example.qiyegongdan.dto.CommentRequest;
import com.example.qiyegongdan.dto.TicketCreateRequest;
import com.example.qiyegongdan.dto.TicketQueryRequest;
import com.example.qiyegongdan.entity.*;
import com.example.qiyegongdan.enums.TicketPriority;
import com.example.qiyegongdan.enums.TicketStatus;
import com.example.qiyegongdan.enums.TicketType;
import com.example.qiyegongdan.exception.BusinessException;
import com.example.qiyegongdan.repository.*;
import com.example.qiyegongdan.vo.StatisticsVO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;
    private final TicketAttachmentRepository attachmentRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final ApprovalRepository approvalRepository;

    public TicketService(TicketRepository ticketRepository,
                          TicketCommentRepository commentRepository,
                          TicketAttachmentRepository attachmentRepository,
                          UserRepository userRepository,
                          FileService fileService,
                          ApprovalRepository approvalRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.fileService = fileService;
        this.approvalRepository = approvalRepository;
    }

    public Page<Ticket> queryTickets(TicketQueryRequest request) {
        Specification<Ticket> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String kw = "%" + request.getKeyword() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("title"), kw),
                        cb.like(root.get("description"), kw)
                ));
            }
            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), request.getPriority()));
            }
            if (request.getCategory() != null && !request.getCategory().isEmpty()) {
                predicates.add(cb.equal(root.get("category"), request.getCategory()));
            }
            if (request.getCreatorId() != null) {
                predicates.add(cb.equal(root.get("creatorId"), request.getCreatorId()));
            }
            if (request.getAssigneeId() != null) {
                predicates.add(cb.equal(root.get("assigneeId"), request.getAssigneeId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return ticketRepository.findAll(spec,
                PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public Ticket createTicket(TicketCreateRequest request, Long creatorId) {
        userRepository.findById(creatorId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .priority(request.getPriority())
                .status(TicketStatus.PENDING)
                .category(request.getCategory())
                .customFields(request.getCustomFields())
                .creatorId(creatorId)
                .assigneeId(request.getAssigneeId())
                .build();

        if (request.getAssigneeId() != null) {
            ticket.setStatus(TicketStatus.ASSIGNED);
        }

        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new BusinessException("工单不存在"));
    }

    @Transactional
    public Ticket assignTicket(Long ticketId, Long assigneeId) {
        Ticket ticket = getTicket(ticketId);
        if (ticket.getStatus() != TicketStatus.PENDING && ticket.getStatus() != TicketStatus.REOPENED) {
            throw new BusinessException("当前状态不允许分配");
        }
        userRepository.findById(assigneeId)
                .orElseThrow(() -> new BusinessException("处理人不存在"));

        ticket.setAssigneeId(assigneeId);
        ticket.setStatus(TicketStatus.ASSIGNED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket startProcess(Long ticketId) {
        Ticket ticket = getTicket(ticketId);
        if (!ticket.getStatus().canTransitionTo(TicketStatus.IN_PROGRESS)) {
            throw new BusinessException("当前状态不允许开始处理");
        }
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket resolveTicket(Long ticketId) {
        Ticket ticket = getTicket(ticketId);
        if (!ticket.getStatus().canTransitionTo(TicketStatus.RESOLVED)) {
            throw new BusinessException("当前状态不允许标记为已解决");
        }
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket closeTicket(Long ticketId) {
        Ticket ticket = getTicket(ticketId);
        if (!ticket.getStatus().canTransitionTo(TicketStatus.CLOSED)) {
            throw new BusinessException("当前状态不允许关闭");
        }
        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket reopenTicket(Long ticketId) {
        Ticket ticket = getTicket(ticketId);
        if (!ticket.getStatus().canTransitionTo(TicketStatus.REOPENED)) {
            throw new BusinessException("当前状态不允许重开");
        }
        ticket.setStatus(TicketStatus.REOPENED);
        ticket.setResolvedAt(null);
        ticket.setClosedAt(null);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public TicketComment addComment(Long ticketId, Long userId, CommentRequest request) {
        getTicket(ticketId);
        TicketComment comment = TicketComment.builder()
                .ticketId(ticketId)
                .userId(userId)
                .content(request.getContent())
                .build();
        return commentRepository.save(comment);
    }

    public List<TicketComment> getComments(Long ticketId) {
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    @Transactional
    public TicketAttachment uploadAttachment(Long ticketId, Long userId, MultipartFile file) {
        getTicket(ticketId);
        String storedFilename = fileService.storeFile(file);

        TicketAttachment attachment = TicketAttachment.builder()
                .ticketId(ticketId)
                .userId(userId)
                .fileName(file.getOriginalFilename())
                .filePath(storedFilename)
                .fileSize(file.getSize())
                .build();
        return attachmentRepository.save(attachment);
    }

    public List<TicketAttachment> getAttachments(Long ticketId) {
        return attachmentRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
    }

    public StatisticsVO getStatistics() {
        long total = ticketRepository.count();
        long pending = ticketRepository.countByStatus(TicketStatus.PENDING);
        long inProgress = ticketRepository.countByStatus(TicketStatus.IN_PROGRESS);
        long pendingApproval = ticketRepository.countByStatus(TicketStatus.PENDING_APPROVAL);
        long resolved = ticketRepository.countByStatus(TicketStatus.RESOLVED);
        long closed = ticketRepository.countByStatus(TicketStatus.CLOSED);

        Map<String, Long> byType = new HashMap<>();
        for (TicketType type : TicketType.values()) {
            byType.put(type.name(), ticketRepository.countByType(type));
        }

        Map<String, Long> byPriority = new HashMap<>();
        for (TicketPriority priority : TicketPriority.values()) {
            byPriority.put(priority.name(), ticketRepository.countByPriority(priority));
        }

        return StatisticsVO.builder()
                .totalTickets(total)
                .pendingTickets(pending)
                .inProgressTickets(inProgress)
                .pendingApprovalTickets(pendingApproval)
                .resolvedTickets(resolved)
                .closedTickets(closed)
                .ticketsByType(byType)
                .ticketsByPriority(byPriority)
                .dailyTrend(Collections.emptyList())
                .build();
    }

    public Page<Ticket> getMyCreatedTickets(Long userId, int page, int size) {
        return ticketRepository.findByCreatorId(userId,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public Page<Ticket> getMyAssignedTickets(Long userId, int page, int size) {
        return ticketRepository.findByAssigneeId(userId,
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public Ticket autoAssign() {
        List<Ticket> pendingTickets = ticketRepository.findByStatus(TicketStatus.PENDING);
        if (pendingTickets.isEmpty()) return null;

        List<User> engineers = userRepository.findAll().stream()
                .filter(User::getEnabled)
                .filter(u -> u.getRole().name().equals("ENGINEER") || u.getRole().name().equals("ADMIN"))
                .toList();

        if (engineers.isEmpty()) return null;

        Ticket ticket = pendingTickets.get(0);
        User engineer = engineers.get((int) (Math.random() * engineers.size()));
        ticket.setAssigneeId(engineer.getId());
        ticket.setStatus(TicketStatus.ASSIGNED);
        return ticketRepository.save(ticket);
    }

    public String getUsername(Long userId) {
        return userRepository.findById(userId)
                .map(User::getRealName)
                .orElse("未知用户");
    }
}