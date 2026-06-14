package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.service.EmailService;
import com.edwin.trial_bank_app.service.TransactionNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TransactionEmailNotificationServiceImpl implements TransactionNotificationService {

    private final EmailService emailService;

    @Override
    public void sendDebitAlert(Account account, BigDecimal amount, String reference) {

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        String formattedAmount = formatter.format(amount);
        String formattedBalance = formatter.format(account.getAvailableBalance());

        EmailDetails email =
                EmailDetails.builder()
                        .recipientEmail(account.getUser().getEmail())
                        .messageBody("Dear Customer\n\n" +
                                "A Debit of ₦" + formattedAmount + " has been made from your account.\n" +
                                "If this was not you, please contact support immediately.\n\n" +
                                "Best regards" +
                                "\nRemaining available balance:  ₦" + formattedBalance + "\nTransaction Reference: " + reference)
                .subject("Debit Alert")
                        .attachment(null)
                        .build();

        emailService.sendEmailAlert(email);

    }

    @Override
    public void sendCreditAlert(Account account, BigDecimal amount, String reference) {

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        String formattedAmount = formatter.format(amount);
        String formattedBalance = formatter.format(account.getAvailableBalance());

        EmailDetails email =
                EmailDetails.builder()
                        .recipientEmail(account.getUser().getEmail())
                        .messageBody("Dear Customer\n\n" +
                                "Your account " +account.getAccountNumber() +" has been credited with ₦" + formattedAmount +
                                "\nNew available balance:  ₦" + formattedBalance + "\nTransaction Reference: " + reference +
                                "\nYou can log in to your mobile or internet banking platform to view details.\n\n" +
                                "Thank you for banking with us.\n\n" +
                                "Best regards.")
                        .subject("Credit Alert")
                        .attachment(null)
                        .build();

        emailService.sendEmailAlert(email);
    }
}
