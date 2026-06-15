package com.edwin.trial_bank_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    private String action;

    private String username;

    private String status;

    @Column(length = 1000)
    private String details;
}