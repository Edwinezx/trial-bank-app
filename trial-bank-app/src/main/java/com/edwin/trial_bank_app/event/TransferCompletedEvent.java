package com.edwin.trial_bank_app.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Published after a transfer has been persisted successfully.
 * Carries plain values (not entities) so listeners running AFTER_COMMIT,
 * possibly on a different thread, never touch a detached JPA object.
 */
public record TransferCompletedEvent(
        String transactionReference,
        String sourceAccountNumber,
        String sourceUserEmail,
        BigDecimal sourceBalanceAfter,
        String destinationAccountNumber,
        String destinationUserEmail,
        BigDecimal destinationBalanceAfter,
        BigDecimal amount,
        LocalDateTime occurredAt
) {
}
