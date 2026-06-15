package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.exception.FraudSuspectedException;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.AuditService;
import com.edwin.trial_bank_app.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Rule-based fraud detection foundation.
 * Rules:
 *  1. Single transaction exceeds ₦5,000,000 (large amount flag)
 *  2. More than 10 debit transactions within the last 60 seconds (velocity check)
 *  3. Transaction amount exceeds 90% of available balance (near-empty drain)
 */
@Service
@RequiredArgsConstructor
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private static final BigDecimal LARGE_AMOUNT_THRESHOLD  = new BigDecimal("5000000");
    private static final BigDecimal DRAIN_THRESHOLD_PERCENT = new BigDecimal("0.90");
    private static final int        VELOCITY_LIMIT          = 10;
    private static final int        VELOCITY_WINDOW_SECONDS = 60;

    private final TransactionRepository transactionRepository;
    private final AuditService          auditService;

    @Override
    public void assess(Account account, BigDecimal amount) {
        checkLargeAmount(account, amount);
        checkVelocity(account);
        checkBalanceDrain(account, amount);
    }

    // Rule 1 — single large transaction
    private void checkLargeAmount(Account account, BigDecimal amount) {
        if (amount.compareTo(LARGE_AMOUNT_THRESHOLD) > 0) {
            flag(account, "Large transaction: ₦" + amount);
        }
    }

    // Rule 2 — rapid successive debits
    private void checkVelocity(Account account) {
        LocalDateTime windowStart = LocalDateTime.now().minusSeconds(VELOCITY_WINDOW_SECONDS);
        long recentCount = transactionRepository.countRecentDebits(
                account.getAccountNumber(), windowStart);

        if (recentCount >= VELOCITY_LIMIT) {
            flag(account, "Velocity limit exceeded: " + recentCount
                    + " transactions in " + VELOCITY_WINDOW_SECONDS + "s");
        }
    }

    // Rule 3 — near-complete balance drain
    private void checkBalanceDrain(Account account, BigDecimal amount) {
        if (account.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal ratio = amount.divide(account.getAvailableBalance(), 4,
                    java.math.RoundingMode.HALF_UP);
            if (ratio.compareTo(DRAIN_THRESHOLD_PERCENT) >= 0) {
                flag(account, "Balance drain: " + ratio.multiply(new BigDecimal("100"))
                        .setScale(1, java.math.RoundingMode.HALF_UP) + "% of balance");
            }
        }
    }

    private void flag(Account account, String reason) {
        auditService.log("FRAUD_ALERT", account.getUser().getEmail(), "BLOCKED",
                reason, account.getAccountNumber());
        throw new FraudSuspectedException(reason);
    }
}
