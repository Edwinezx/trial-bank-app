package com.edwin.trial_bank_app.service;

public interface AuditService {

    void log(
            String actor,
            String action,
            String reference,
            String description
    );
}
