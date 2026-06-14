package com.edwin.trial_bank_app.dto;

import com.edwin.trial_bank_app.enums.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private String transactionReference;
    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private TransactionStatus status;
    private LocalDateTime timestamp;
}



