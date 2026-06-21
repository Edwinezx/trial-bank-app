package com.edwin.trial_bank_app.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Published when a transfer fails after the source/destination accounts were resolved.
 * Note: since the surrounding @Transactional method rolls back on exception, this is
 * published via a plain (non-transactional) ApplicationEventPublisher call so the
 * audit listener still records the failure even though nothing was committed.
 */
public record TransferFailedEvent(
        String sourceAccountNumber,
        String sourceUserEmail,
        String destinationAccountNumber,
        BigDecimal amount,
        String reason,
        LocalDateTime occurredAt
) {
}
