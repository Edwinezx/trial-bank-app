package com.edwin.trial_bank_app.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositCompletedEvent(
        String transactionReference,
        String accountNumber,
        String userEmail,
        BigDecimal amount,
        BigDecimal balanceAfter,
        LocalDateTime occurredAt
) {
}
