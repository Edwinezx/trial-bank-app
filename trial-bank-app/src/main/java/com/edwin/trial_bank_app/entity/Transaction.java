package com.edwin.trial_bank_app.entity;

import com.edwin.trial_bank_app.enums.TransactionChannel;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions",
        indexes = {
                @Index(
                        name = "idx_transaction_reference",
                        columnList = "transactionReference"
                )
        }
)
public class Transaction extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String transactionReference;

    private String sourceAccountNumber;

    private String destinationAccountNumber;

    private BigDecimal amount;

    private LocalDateTime transactionDate;

    @Column(nullable = false)
    private String narration;

    private String externalReference;

    private String initiatedBy;

    private String failureReason;

    private boolean reversed;

    private String reversalReference;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    private TransactionChannel channel;


}

