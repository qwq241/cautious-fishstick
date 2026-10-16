package com.example.qiyegongdan.aspect;

import com.example.qiyegongdan.entity.AuditLog;
import com.example.qiyegongdan.repository.AuditLogRepository;
import com.example.qiyegongdan.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class OperationLogAspect {

    private final AuditLogRepository auditLogRepository;

    public OperationLogAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @AfterReturning("execution(* com.example.qiyegongdan.controller.*.*(..)) && !execution(* com.example.qiyegongdan.controller.AuditLogController.*(..))")
    public void logOperation(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getSignature().getDeclaringTypeName();

            Long userId = null;
            String username = "system";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                userId = principal.getId();
                username = principal.getUsername();
            }

            String ip = "unknown";
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                ip = request.getRemoteAddr();
                String forwarded = request.getHeader("X-Forwarded-For");
                if (forwarded != null && !forwarded.isEmpty()) {
                    ip = forwarded.split(",")[0].trim();
                }
            }

            AuditLog log = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .module(extractModule(className))
                    .action(methodName)
                    .description("执行操作: " + methodName)
                    .ip(ip)
                    .build();

            auditLogRepository.save(log);
        } catch (Exception e) {
            // ignored
        }
    }

    private String extractModule(String className) {
        if (className.contains("TicketController")) return "工单管理";
        if (className.contains("UserController")) return "用户管理";
        if (className.contains("AuthController")) return "认证管理";
        return "其他";
    }
}