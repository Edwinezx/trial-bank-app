package com.edwin.trial_bank_app.dto.response;

import com.edwin.trial_bank_app.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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



