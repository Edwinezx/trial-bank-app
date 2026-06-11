package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.service.EmailService;
import com.edwin.trial_bank_app.service.TransactionNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionEmailNotificationServiceImpl implements TransactionNotificationService {

    private final EmailService emailService;

    @Override
    public void sendDebitAlert(Account account, BigDecimal amount, String reference) {

        EmailDetails email =
                EmailDetails.builder()
                        .recipientEmail(account.getUser().getEmail())
                        .messageBody("Amount: ₦" + amount +
                                "\nReference: " + reference +
                                "\nBalance:  ₦" + account.getAccountBalance())
                        .subject("Debit Alert")
                        .attachment(null)
                        .build();

        emailService.sendEmailAlert(email);

    }

    @Override
    public void sendCreditAlert(Account account, BigDecimal amount, String reference) {

        EmailDetails email =
                EmailDetails.builder()
                        .recipientEmail(account.getUser().getEmail())
                        .messageBody("Amount: ₦" + amount +
                                "\nReference: " + reference +
                                "\nBalance:  ₦" + account.getAccountBalance())
                        .subject("Credit Alert")
                        .attachment(null)
                        .build();

        emailService.sendEmailAlert(email);
    }
}
