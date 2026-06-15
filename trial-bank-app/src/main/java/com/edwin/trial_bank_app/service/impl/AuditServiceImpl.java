package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.AuditLog;
import com.edwin.trial_bank_app.repository.AuditLogRepository;
import com.edwin.trial_bank_app.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuditServiceImpl
        implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(
            String action,
            String username,
            String status,
            String details
    ) {

        AuditLog auditLog =
                AuditLog.builder()
                        .action(action)
                        .username(username)
                        .status(status)
                        .details(details)
                        .build();

        auditLogRepository.save(auditLog);
    }
}