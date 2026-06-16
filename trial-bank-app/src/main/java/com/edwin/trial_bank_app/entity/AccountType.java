package com.edwin.trial_bank_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Getter @Setter
@Table(name = "account_types")
public class AccountType extends BaseEntity {

    @Column(unique = true)
    private String typeName;

    private String description;

    private BigDecimal minimumBalance;

    private BigDecimal dailyTransferLimit;

    private BigDecimal withdrawalLimit;

    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountType that)) return false;
        return Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName);
    }
}