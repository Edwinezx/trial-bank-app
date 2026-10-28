package com.edwin.trial_bank_app.listener;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.event.UserRegisteredEvent;
import com.edwin.trial_bank_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationNotificationListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendWelcomeEmail(UserRegisteredEvent event) {
        try {
            emailService.sendEmailAlert(EmailDetails.builder()
                    .recipientEmail(event.email())
                    .subject("WELCOME")
                    .messageBody(
                            "Hi " + event.firstName()
                                    + ", your registration was successful. "
                                    + "You can now open an account."
                    )
                    .build());
        } catch (Exception exception) {
            log.error(
                    "Welcome email delivery failed for {}",
                    event.email(),
                    exception
            );
        }
    }
}