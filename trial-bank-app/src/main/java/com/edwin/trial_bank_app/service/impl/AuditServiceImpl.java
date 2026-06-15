package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.AuditLog;
import com.edwin.trial_bank_app.repository.AuditLogRepository;
import com.edwin.trial_bank_app.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void log(String action, String username, String status, String details) {
        log(action, username, status, details, null);
    }

    @Override
    public void log(String action, String username, String status, String details, String accountNumber) {
        auditLogRepository.save(
                AuditLog.builder()
                        .action(action)
                        .username(username)
                        .status(status)
                        .details(details)
                        .accountNumber(accountNumber)
                        .ipAddress(resolveClientIp())
                        .build()
        );
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "SYSTEM";
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            return (forwarded != null && !forwarded.isBlank())
                    ? forwarded.split(",")[0].trim()
                    : request.getRemoteAddr();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
