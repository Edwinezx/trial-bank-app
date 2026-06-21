package com.edwin.trial_bank_app.listener;

import com.edwin.trial_bank_app.event.*;
import com.edwin.trial_bank_app.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Single home for all transaction-related audit logging.
 *
 * Previously, auditService.log(...) was called inline inside TransferServiceImpl,
 * WithdrawServiceImpl and DepositServiceImpl — once for success, once for failure,
 * in every single one of them. That meant every new money-movement feature had to
 * remember to copy-paste those calls correctly, and auditing logic was scattered
 * across business logic instead of living in one place.
 *
 * Phase choice matters here:
 *  - *CompletedEvent listeners use AFTER_COMMIT: the transfer/withdrawal/deposit
 *    already committed successfully, so it's safe to log it as a fact. If this
 *    listener itself throws, it cannot undo the already-committed money movement.
 *  - *FailedEvent listeners use AFTER_ROLLBACK: the originating transaction is
 *    rolling back (no commit will ever happen), so AFTER_COMMIT would never fire.
 *    AFTER_ROLLBACK fires specifically in that case.
 *
 * AuditServiceImpl.log(...) still uses @Transactional(REQUIRES_NEW) under the hood,
 * which remains correct and harmless here — it just means each audit write gets its
 * own short transaction rather than being tangled up with anything else.
 */
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        auditService.log("TRANSFER", event.sourceUserEmail(), "SUCCESS",
                "Transferred ₦" + event.amount() + " to " + event.destinationAccountNumber(),
                event.sourceAccountNumber());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onTransferFailed(TransferFailedEvent event) {
        auditService.log("TRANSFER", event.sourceUserEmail(), "FAILED",
                event.reason(), event.sourceAccountNumber());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onWithdrawalCompleted(WithdrawalCompletedEvent event) {
        auditService.log("WITHDRAWAL", event.userEmail(), "SUCCESS",
                "Withdrew ₦" + event.amount(), event.accountNumber());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onWithdrawalFailed(WithdrawalFailedEvent event) {
        auditService.log("WITHDRAWAL", event.userEmail(), "FAILED",
                event.reason(), event.accountNumber());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDepositCompleted(DepositCompletedEvent event) {
        auditService.log("DEPOSIT", event.userEmail(), "SUCCESS",
                "Deposited ₦" + event.amount(), event.accountNumber());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onDepositFailed(DepositFailedEvent event) {
        auditService.log("DEPOSIT", event.userEmail(), "FAILED",
                event.reason(), event.accountNumber());
    }
}
