package com.edwin.trial_bank_app.listener;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.event.DepositCompletedEvent;
import com.edwin.trial_bank_app.event.TransferCompletedEvent;
import com.edwin.trial_bank_app.event.WithdrawalCompletedEvent;
import com.edwin.trial_bank_app.service.AuditService;
import com.edwin.trial_bank_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Sends debit/credit alert emails after money has actually moved.
 *
 *
 * @Async moves this onto a separate thread pool entirely (see AsyncConfig),
 * and AFTER_COMMIT means it only runs once the transfer is actually durable.
 * Together: the controller returns TransferResponse the moment transferFunds()
 * returns, and these emails go out afterward, off the request thread.
 */
@Component
@RequiredArgsConstructor
public class TransferNotificationListener {

    private final EmailService emailService;
    private final AuditService auditService;

    private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance(Locale.US);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTransferCompleted(TransferCompletedEvent event) {
        sendAlert(event.sourceUserEmail(), "Debit Alert",
                "Dear Customer\n\n" +
                        "A Debit of ₦" + format(event.amount()) + " has been made from your account.\n" +
                        "If this was not you, please contact support immediately.\n\n" +
                        "Remaining available balance: ₦" + format(event.sourceBalanceAfter()) +
                        "\nTransaction Reference: " + event.transactionReference(),
                event.sourceAccountNumber());

        sendAlert(event.destinationUserEmail(), "Credit Alert",
                "Dear Customer\n\n" +
                        "Your account " + event.destinationAccountNumber() + " has been credited with ₦"
                        + format(event.amount()) +
                        "\nNew available balance: ₦" + format(event.destinationBalanceAfter()) +
                        "\nTransaction Reference: " + event.transactionReference() +
                        "\n\nThank you for banking with us.",
                event.destinationAccountNumber());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onWithdrawalCompleted(WithdrawalCompletedEvent event) {
        sendAlert(event.userEmail(), "Debit Alert",
                "Dear Customer\n\n" +
                        "A withdrawal of ₦" + format(event.amount()) + " was made from your account.\n" +
                        "Remaining available balance: ₦" + format(event.balanceAfter()) +
                        "\nTransaction Reference: " + event.transactionReference(),
                event.accountNumber());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDepositCompleted(DepositCompletedEvent event) {
        sendAlert(event.userEmail(), "Credit Alert",
                "Dear Customer\n\n" +
                        "Your account has been credited with ₦" + format(event.amount()) + ".\n" +
                        "New available balance: ₦" + format(event.balanceAfter()) +
                        "\nTransaction Reference: " + event.transactionReference(),
                event.accountNumber());
    }

    private void sendAlert(String email, String subject, String body, String accountNumber) {
        try {
            emailService.sendEmailAlert(EmailDetails.builder()
                    .recipientEmail(email)
                    .subject(subject)
                    .messageBody(body)
                    .attachment(null)
                    .build());
        } catch (Exception ex) {
            // Best-effort: the money has already moved and been committed,
            // so a failed notification is logged, not propagated anywhere.
            auditService.log("NOTIFICATION_FAILED", email, "WARN",
                    "Email notification failed: " + ex.getMessage(), accountNumber);
        }
    }

    private String format(BigDecimal amount) {
        return FORMATTER.format(amount);
    }
}
