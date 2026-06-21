package com.edwin.trial_bank_app.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositFailedEvent(
        String accountNumber,
        String userEmail,
        BigDecimal amount,
        String reason,
        LocalDateTime occurredAt
) {
}
