package com.example.qiyegongdan.repository;

import com.example.qiyegongdan.entity.Ticket;
import com.example.qiyegongdan.enums.TicketStatus;
import com.example.qiyegongdan.enums.TicketPriority;
import com.example.qiyegongdan.enums.TicketType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    Page<Ticket> findByCreatorId(Long creatorId, Pageable pageable);
    Page<Ticket> findByAssigneeId(Long assigneeId, Pageable pageable);
    List<Ticket> findByStatus(TicketStatus status);
    long countByStatus(TicketStatus status);
    List<Ticket> findByAssigneeIdAndStatusIn(Long assigneeId, List<TicketStatus> statuses);
    long countByType(TicketType type);
    long countByPriority(TicketPriority priority);
}