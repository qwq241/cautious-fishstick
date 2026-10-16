package com.example.qiyegongdan.repository;

import com.example.qiyegongdan.entity.Approval;
import com.example.qiyegongdan.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
    Optional<Approval> findTopByTicketIdAndStatusOrderByCreatedAtDesc(Long ticketId, ApprovalStatus status);
}