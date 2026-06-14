package com.edwin.trial_bank_app.entity;

import com.edwin.trial_bank_app.enums.AccountStatus;
import com.edwin.trial_bank_app.enums.AccountType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof Account account))
            return false;

        return Objects.equals(
                accountNumber,
                account.accountNumber
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Version
    private Long version;


    @Column(unique = true)
    private String accountNumber;
    private BigDecimal availableBalance;
    private BigDecimal ledgerBalance;
    private BigDecimal interest;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private AccountStatus status;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
}
