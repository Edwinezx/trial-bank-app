package com.edwin.trial_bank_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@Data
@Table(name = "account_types")
public class AccountType extends BaseEntity {

    @Column(unique = true)
    private String typeName;

    private String description;

    private BigDecimal minimumBalance;

    private BigDecimal dailyTransferLimit;

    private BigDecimal withdrawalLimit;

    private boolean active;
}