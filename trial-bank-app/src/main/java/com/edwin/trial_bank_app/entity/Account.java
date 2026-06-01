package com.edwin.trial_bank_app.entity;

import com.edwin.trial_bank_app.enums.AccountStatus;
import com.edwin.trial_bank_app.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@SuppressWarnings("JpaDataSourceORMInspection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //equality checks
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) && Objects.equals(accountNumber, account.accountNumber) && Objects.equals(accountBalance, account.accountBalance) && Objects.equals(interest, account.interest) && accountType == account.accountType && Objects.equals(status, account.status) && Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, accountBalance, interest, accountType, status, user);
    }

    private String accountNumber;
    private BigDecimal accountBalance;
    private BigDecimal interest;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(length = 25)
    private AccountStatus status;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
