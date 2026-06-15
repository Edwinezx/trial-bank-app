package com.edwin.trial_bank_app.service;

public interface AuditService {

    void log(
            String action,
            String username,
            String status,
            String details
    );
}
