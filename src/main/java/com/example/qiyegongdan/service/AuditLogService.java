package com.example.qiyegongdan.service;

import com.example.qiyegongdan.entity.AuditLog;
import com.example.qiyegongdan.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String username, Long userId, String module, String action, String description, String ip) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .userId(userId)
                .module(module)
                .action(action)
                .description(description)
                .ip(ip)
                .build();
        auditLogRepository.save(log);
    }

    public Page<AuditLog> getLogs(String module, int page, int size) {
        if (module != null && !module.isEmpty()) {
            return auditLogRepository.findByModuleOrderByCreatedAtDesc(module, PageRequest.of(page - 1, size));
        }
        return auditLogRepository.findAll(PageRequest.of(page - 1, size));
    }
}
