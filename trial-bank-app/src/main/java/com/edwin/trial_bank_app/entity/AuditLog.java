package com.edwin.trial_bank_app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    private String actor;
    private String action;
    private String reference;
    private String ipAddress;
    private String description;

}
